package gDocsFileSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gdata.util.common.base.Pair;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Entry;

public class GFileSystem
{
	private GDatabase db;
	private static final String docIdPrefix = "GDFS_"; //Because Google hates queries starting with a digit but uses doc IDs that start with digits

	/**
	 * File System abstraction on top of GDatabase
	 * @param workspace Name of the workspace spreadsheet (Database)
	 * @throws Exception The user is not authenticated to access the files
	 */
	public GFileSystem(GDatabase database) throws Exception
	{
		db = database;
	}

	/**
	 * Updates a file/folder's parent
	 * @param file File to update
	 * @param parent Parent group to assign to
	 * @param isFile Whether it's a file or not (document group)
	 * @return Update success or failure
	 */
	public boolean setParent(Document file, DocumentGroup parent, boolean isFile)
	{
		String fileId = docIdPrefix + file.getId();
		String parentId = docIdPrefix + parent.getId();
		String idColumn;
		String table;

		if(isFile)
		{
			idColumn = "gfid";
			table = "files";
		}
		else
		{
			parentId = parent.getId();
			idColumn = "folderid";
			table = "folders";
		}

		ArrayList<String> columns = new ArrayList<String>();
		columns.add("parentfolder");

		ArrayList<String> values = new ArrayList<String>();
		values.add(parentId);

		try
		{
			Integer rows = db.update(table, columns, values, idColumn + " = " + fileId);
			if(rows > 0)
			{
				return true;
			}
			else
			{
				//How'd you get a file without it being the files table?
				return false;
			}
		}
		catch (Exception e)
		{
			//TODO: Do something with this?
			return false;
		}
	}

	/**
	 * Indicates whether the entry is already added
	 * @param file
	 * @return
	 */
	public boolean containsEntry(Entry file){
		String fileId = docIdPrefix + file.getId();
		String parentId;
		String idColumn;
		String table;
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();



		if(file.hasParent())
		{
			parentId = docIdPrefix + file.getParent().getId();
		}
		else
		{
			parentId = "";
		}

		if(file instanceof Document)
		{
			idColumn = "gfid";
			table = "files";
			columns.add("fileid");
			columns.add("gfid");
			columns.add("parentfolder");
			columns.add("filename");
			values.add(fileId);
			values.add(parentId);
			values.add(file.getName());
		}
		else
		{
			idColumn = "folderid";
			table = "folders";
			columns.add("parentid");
			columns.add("parentfolder");
			columns.add("foldername");
			values.add(parentId);
			values.add(file.getName());
		}
		try{
			Map<String, ArrayList<String>> results = db.select(table, columns, idColumn + " = " + fileId);
			if(results.size() > 0)
			{
				//Already exists
				return true;
			}
			else{
				return false;
			}
		}
		catch (Exception e){
			return false;
		}
	}


	/**
	 * Inserts a document or folder into the workspace
	 * @param file File to insert
	 * @param parent Parent group of file/folder
	 * @param isFile Whether it's a file or not (document group)
	 * @return Insertion success/failure
	 */
	public boolean insertEntry(Entry file){
		return insertEntry(file,false);
	}
	private boolean insertEntry(Entry file, boolean overwrite)
	{
		String fileId = docIdPrefix + file.getId();
		String parentId;
		String idColumn;
		String table;
		List<String> columns = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		if(file.hasParent())
		{
			parentId = docIdPrefix + file.getParent().getId();
		}
		else
		{
			parentId = "";
		}

		if(file instanceof Document)
		{
			idColumn = "fileid";
			table = "files";

			columns = Arrays.asList(new String[]{
					"fileid",
					"gfid",
					"parentfolder",
					"filename",
					"offsetX",
					"offsetY"
			});
			values = Arrays.asList(new String[]{
					file.getId(),
					parentId,
					file.getName(),
					file.getOffsetX()+"",
					file.getOffsetY()+"",
			});
		}
		else
		{
			idColumn = "folderid";
			table = "folders";
			columns = Arrays.asList(new String[]{
					"parentfolder",
					"foldername",
					"offsetX",
					"offsetY"
			});
			values = Arrays.asList(new String[]{
					parentId,
					file.getName(),
					file.getOffsetX()+"",
					file.getOffsetY()+"",
			});
		}


		try
		{
			//See if it exists first
			Map<String, ArrayList<String>> results = db.select(table, columns, idColumn + " = " + fileId);
			if(overwrite){
				if(results.size() > 0){
					db.update(table, columns, values, idColumn + " = "+fileId);
					return true;
				}
			}
			else{
				if(results.size() > 0){
					return false;
				}
			}

			//DOES NOT EXIST, INSERT
			Integer id = db.getNextId(table, idColumn);
			columns.add(0, id.toString());

			db.insert(table, columns, values);
			return true;


		}
		catch (Exception e)
		{
			//TODO: Do something with this?
			return false;
		}
	}

	/**
	 * Gets a list of files in the document group
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public List<Document> getChildrenFiles(DocumentGroup folder) throws Exception
	{
		List<String> columns = Arrays.asList(new String[]{
				"fileid",
				"gfid",
				"filename",
				"parentfolder",
				"offsetX",
				"offsetY",
				"offsetScale"
		});

		Map<String, ArrayList<String>> results = db.select("files", columns, "parentfolder == " + folder.getId());

		ArrayList<String> fileIds = results.get("fileid");
		ArrayList<String> gfids = results.get("gfid");
		ArrayList<String> fileNames = results.get("filename");
		ArrayList<String> fileOffsetXs = results.get("offsetX");
		ArrayList<String> fileOffsetYs = results.get("offsetY");

		List<Document> resultDocs = new ArrayList<Document>();

		for(int i=0; i<fileIds.size(); i++){
			String id = fileIds.get(i);
			String gid = gfids.get(i);
			String name = fileNames.get(i);
			Document doc = new Document(name,id,gid);
			doc.setOffsetX(Double.parseDouble(fileOffsetXs.get(i)));
			doc.setOffsetY(Double.parseDouble(fileOffsetYs.get(i)));
			resultDocs.add(doc);
		}

		return resultDocs;
	}

	/**
	 * Gets a list of folders in the document group
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public List<DocumentGroup> getChildrenFolders(DocumentGroup folder) throws Exception
	{
		List<String> columns = Arrays.asList(new String[]{
				"folderid",
				"foldername",
				"parentfolder",
				"offsetX",
				"offsetY"
		});

		Map<String, ArrayList<String>> results = db.select("folders", columns, "parentfolder == " + folder.getId());

		ArrayList<String> folderIds = results.get("folderid");
		List<String> folderNames = results.get("foldername");
		ArrayList<String> fileOffsetXs = results.get("offsetX");
		ArrayList<String> fileOffsetYs = results.get("offsetY");

		List<DocumentGroup> resultGroups = new ArrayList<DocumentGroup>();

		for(int i=0; i<folderIds.size(); i++){
			String id = folderIds.get(i);
			String name = folderNames.get(i);
			DocumentGroup group = new DocumentGroup(name,id);
			group.setOffsetX(Double.parseDouble(fileOffsetXs.get(i)));
			group.setOffsetY(Double.parseDouble(fileOffsetYs.get(i)));
			resultGroups.add(group);
		}

		return resultGroups;
	}

	/**
	 * Gets a list of open files and their positions (fileid, posx, posy)
	 * @return
	 * @throws Exception
	 */
	public Map<String, ArrayList<String>> getOpenFiles() throws Exception
	{
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("fileid");
		columns.add("posx");
		columns.add("posy");

		Map<String, ArrayList<String>> results = db.select("open", columns, null);

		return results;
	}

	/**
	 * Marks a file as open in the workspace
	 * @param file The document to open
	 * @param posX Its X coordinate
	 * @param posY Its Y coordinate
	 */
	public void setFileOpen(Document file, Integer posX, Integer posY)
	{
		String fileId = docIdPrefix + file.getId();
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();

		//columns.add("fileid");
		columns.add("gfid");
		columns.add("filename");
		columns.add("posx");
		columns.add("posy");
		values.add(fileId);
		values.add(file.getName());
		values.add(posX.toString());
		values.add(posY.toString());

		try
		{
			//See if it exists first
			Map<String, ArrayList<String>> results = db.select("open", columns, "gfid = " + fileId);
			if(results.size() > 0)
			{
				return;
			}

			db.insert("open", columns, values);
		}
		catch (Exception e)
		{
			//TODO: Do something with this?
			return;
		}
	}

	public List<Document> getRootFiles() throws Exception{
		List<Document> results = new ArrayList<Document>();
		List<String> columns = Arrays.asList(new String[]{
				"fileid",
				"gfid",
				"parentfolder",
				"filename",
				"offsetX",
				"offsetY"
		});
		Map<String, ArrayList<String>> files = db.select("files", columns, "parentfolder = 0");
		ArrayList<String> fileIds = files.get("fileid");
		ArrayList<String> gfids = files.get("gfid");
		ArrayList<String> fileNames = files.get("filename");
		ArrayList<String> fileOffsetXs = files.get("offsetX");
		ArrayList<String> fileOffsetYs = files.get("offsetY");
		for(int i=0; i<fileIds.size(); i++){
			String name = fileNames.get(i);
			String id = fileIds.get(i);
			String gfid = gfids.get(i);
			Document doc = new Document(name,id,gfid);
			doc.setOffsetX(Double.parseDouble(fileOffsetXs.get(i)));
			doc.setOffsetY(Double.parseDouble(fileOffsetYs.get(i)));
			results.add(doc);
		}
		return results;
	}

	public List<DocumentGroup> getRootFolders() throws Exception{
		List<DocumentGroup> results = new ArrayList<DocumentGroup>();

		List<String> columns = Arrays.asList(new String[]{
				"folderid",
				"parentfolder",
				"foldername",
				"offsetX",
				"offsetY"
		});
		Map<String, ArrayList<String>> folders = db.select("folders", columns, "parentfolder = 0");
		ArrayList<String> folderIds = folders.get("folderid");
		ArrayList<String> folderNames = folders.get("foldername");
		ArrayList<String> fileOffsetXs = folders.get("offsetX");
		ArrayList<String> fileOffsetYs = folders.get("offsetY");
		for(int i=0; i<folderIds.size(); i++){
			DocumentGroup group = new DocumentGroup(folderNames.get(i),folderIds.get(i));
			group.setOffsetX(Double.parseDouble(fileOffsetXs.get(i)));
			group.setOffsetY(Double.parseDouble(fileOffsetYs.get(i)));
			results.add(group);
		}
		return results;
	}

	public void store(Entry entry){

	}
}
