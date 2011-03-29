package gDocsFileSystem;

import java.util.ArrayList;
import java.util.Map;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Entry;

public class GFileSystem
{
	private GDatabase db;
	
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
		String fileId = file.getDocId();
		String parentId = parent.getDocId();
		String idColumn;
		String table;
		
		if(isFile)
		{
			idColumn = "gfid";
			table = "files";
		}
		else
		{
			idColumn = "folder_id";
			table = "folders";
		}
		
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("parent_folder");
		
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
	 * Inserts a document or folder into the workspace
	 * @param file File to insert
	 * @param parent Parent group of file/folder
	 * @param isFile Whether it's a file or not (document group)
	 * @return Insertion success/failure
	 */
	public boolean insertEntry(Entry file, DocumentGroup parent, boolean isFile)
	{
		String fileId = file.getDocId();
		String parentId;
		String idColumn;
		String table;
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		
		if(parent != null)
		{
			parentId = parent.getDocId();
		}
		else
		{
			parentId = "";
		}
		
		if(isFile)
		{
			idColumn = "gfid";
			table = "files";
			columns.add("file_id");
			columns.add("gfid");
			columns.add("parent_folder");
			columns.add("file_name");
			values.add(fileId);
			values.add(parentId);
			values.add(file.getName());
		}
		else
		{
			idColumn = "folder_id";
			table = "folders";
			columns.add("parent_id");
			columns.add("parent_folder");
			columns.add("folder_name");
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
		columns.add("file_id");
		columns.add("file_name");
		columns.add("parent_folder");

		Map<String, ArrayList<String>> results = db.select("files", columns, "parent_folder = " + folder.getName());
		
		ArrayList<String> fileIds = results.get("file_id");
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
		columns.add("folder_id");
		columns.add("folder_name");
		columns.add("parent_folder");

		Map<String, ArrayList<String>> results = db.select("folders", columns, "parent_folder = " + folder.getName());
		
		ArrayList<String> fileIds = results.get("file_id");
		return fileIds;
	}
}
