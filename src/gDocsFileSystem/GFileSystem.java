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
	public boolean insertEntry(Entry file)
	{
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


		try
		{
			//See if it exists first
			Map<String, ArrayList<String>> results = db.select(table, columns, idColumn + " = " + fileId);
			if(results.size() > 0)
			{
				//Already exists
				return false;
			}

			//Now get the next available id
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
	public List<Pair<String,String>> getChildrenFiles(DocumentGroup folder) throws Exception
	{
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("gfid");
		columns.add("filename");
		columns.add("parentfolder");

		Map<String, ArrayList<String>> results = db.select("files", columns, "parentfolder == " + folder.getId());

		ArrayList<String> fileIds = results.get("gfid");
		ArrayList<String> fileNames = results.get("filename");
		
		List<Pair<String,String>> resultPairs = new ArrayList<Pair<String,String>>();
		
		for(int i=0; i<fileIds.size(); i++){
			String id = fileIds.get(i);
			String name = fileNames.get(i);
			resultPairs.add(new Pair<String,String>(id,name));
		}
		
		return resultPairs;
	}

	/**
	 * Gets a list of folders in the document group
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public List<Pair<String,String>> getChildrenFolders(DocumentGroup folder) throws Exception
	{
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("folderid");
		columns.add("foldername");
		columns.add("parentfolder");

		Map<String, ArrayList<String>> results = db.select("folders", columns, "parentfolder == " + folder.getId());

		ArrayList<String> folderIds = results.get("folderid");
		List<String> folderNames = results.get("foldername");
		
		List<Pair<String,String>> resultPairs = new ArrayList<Pair<String,String>>();
		
		for(int i=0; i<folderIds.size(); i++){
			String id = folderIds.get(i);
			String name = folderNames.get(i);
			resultPairs.add(new Pair<String,String>(id,name));
		}
		
		return resultPairs;
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
	
	public List<Pair<String,String>> getRootFiles() throws Exception{
		List<Pair<String,String>> results = new ArrayList<Pair<String,String>>();
		
		List<String> columns = Arrays.asList(new String[]{
				"fileid",
				"gfid",
				"parentfolder",
				"filename"
		});
		Map<String, ArrayList<String>> files = db.select("files", columns, "parentfolder = 0");
		ArrayList<String> fileIds = files.get("gfid");
		ArrayList<String> fileNames = files.get("filename");
		for(int i=0; i<fileIds.size(); i++){
			String id = fileIds.get(i);
			id = id.substring(docIdPrefix.length());
			results.add(new Pair<String,String>(fileIds.get(i),fileNames.get(i)));
		}
		return results;
	}
	
	public List<Pair<String,String>> getRootFolders() throws Exception{
		List<Pair<String,String>> results = new ArrayList<Pair<String,String>>();
		
		List<String> columns = Arrays.asList(new String[]{
				"folderid",
				"parentfolder",
				"foldername"
		});
		Map<String, ArrayList<String>> folders = db.select("folders", columns, "parentfolder = 0");
		ArrayList<String> folderIds = folders.get("folderid");
		ArrayList<String> folderNames = folders.get("foldername");
		for(int i=0; i<folderIds.size(); i++){
			results.add(new Pair<String,String>(folderIds.get(i),folderNames.get(i)));
		}
		return results;
	}
}
