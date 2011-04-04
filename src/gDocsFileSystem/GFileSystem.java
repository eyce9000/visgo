package gDocsFileSystem;

import java.util.ArrayList;
import java.util.Map;

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
	public boolean setParent(Entry file, DocumentGroup parent, boolean isFile)
	{
		String fileId = docIdPrefix + file.getDocId();
		String parentId = docIdPrefix + parent.getDocId();
		String idColumn;
		String table;

		if(isFile)
		{
			idColumn = "gfid";
			table = "files";
		}
		else
		{
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
		String fileId = docIdPrefix + file.getDocId();
		String parentId;
		String idColumn;
		String table;
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();



		if(file.hasParent())
		{
			parentId = docIdPrefix + file.getParent().getDocId();
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
		String fileId = docIdPrefix + file.getDocId();
		String parentId;
		String idColumn;
		String table;
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();



		if(file.hasParent())
		{
			parentId = docIdPrefix + file.getParent().getDocId();
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
	public ArrayList<String> getChildrenFiles(DocumentGroup folder) throws Exception
	{
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("fileid");
		columns.add("filename");
		columns.add("parentfolder");

		Map<String, ArrayList<String>> results = db.select("files", columns, "parentfolder == " + docIdPrefix + folder.getDocId());

		ArrayList<String> fileIds = results.get("fileid");
		return fileIds;
	}

	/**
	 * Gets a list of folders in the document group
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getChildrenFolders(DocumentGroup folder) throws Exception
	{
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("folderid");
		columns.add("foldername");
		columns.add("parentfolder");

		Map<String, ArrayList<String>> results = db.select("folders", columns, "parentfolder == " + docIdPrefix + folder.getDocId());

		ArrayList<String> fileIds = results.get("fileid");
		return fileIds;
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
		String fileId = docIdPrefix + file.getDocId();
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
	
	/**
	 * Returns a list of root level folders [0] and the root level files [1]
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ArrayList<String>> getWorkspaceStructure() throws Exception
	{
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("fileid");
		columns.add("gfid");
		columns.add("parentfolder");
		columns.add("filename");
		
		Map<String, ArrayList<String>> files = db.select("files", columns, null);
		
		columns.clear();
		columns.add("folderid");
		columns.add("parentfolder");
		columns.add("foldername");
		
		Map<String, ArrayList<String>> folders = db.select("folders", columns, null);

		ArrayList<String> rootFolders = new ArrayList<String>();
		ArrayList<String> rootFiles = new ArrayList<String>();

		//Get the root folders
		ArrayList<Integer> rootFolderIds = new ArrayList<Integer>();
		int i = 0;
		for(String folderId : folders.get("parentfolder"))
		{
			if(Integer.parseInt(folderId) == 0)
			{
				rootFolderIds.add(i);
			}
			i++;
		}
		
		//Pull out their names
		for(int j = 0; j < rootFolderIds.size(); j++)
		{
			int id = rootFolderIds.get(j).intValue();
			rootFolders.add(folders.get("foldername").get(id));
		}
		
		//Get the root files
		ArrayList<Integer> rootFileIds = new ArrayList<Integer>();
		int ii = 0;
		for(String fileId : files.get("parentfolder"))
		{
			if(Integer.parseInt(fileId) == 0)
			{
				rootFileIds.add(ii);
			}
			ii++;
		}
		
		//Pull out their names
		for(Integer j = 0; j < rootFileIds.size(); j++)
		{
			int id = rootFileIds.get(j).intValue();
			rootFiles.add(files.get("filename").get(id));
		}
		
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		results.add(rootFolders);
		results.add(rootFiles);
		
		return results;
	}
}
