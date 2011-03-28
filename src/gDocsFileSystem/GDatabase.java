package gDocsFileSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import srl.visgo.gui.Login;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;

/**
 * Google Docs Relational Database
 * @author Patrick Webster
 * 
 * Spreadsheet = database
 * Worksheet = table
 *
 */
public class GDatabase
{
	private String databaseName;
	private SpreadsheetService service;
	private FeedURLFactory factory;
	private SpreadsheetEntry database;
	private boolean isAuthed = false;
	
	/**
	 * Constructor
	 * Checks to see if the user is authenticated through the Google services
	 */
	public GDatabase()
	{
		try
		{
			service = new SpreadsheetService("SRL-VISGO-v1");
			Login.authenticateService(service);
			factory = FeedURLFactory.getDefault();
			isAuthed = true;	//Well, maybe
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the database to be used
	 * 
	 * @param name Name of the database to use
	 * @throws Exception
	 */
	public void setDatabase(String name) throws Exception
	{
		if(!isAuthed)
		{
			throw new Exception("User is not authenticated. Cannot connect to spreadsheet services.");
		}
		
		if(name.length() == 0)
		{
			throw new Exception("No database specified.");
		}
		databaseName = name;
		
		SpreadsheetQuery spreadsheetQuery = new SpreadsheetQuery(factory.getSpreadsheetsFeedUrl());
		spreadsheetQuery.setTitleQuery(databaseName);
		SpreadsheetFeed spreadsheetFeed = service.query(spreadsheetQuery, SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = spreadsheetFeed.getEntries();
		if (spreadsheets.isEmpty())
		{
			throw new Exception("No databases with that name");
		}
		
		database = spreadsheets.get(0);
	}
	
	private WorksheetEntry getTable(String table) throws Exception
	{
		WorksheetQuery worksheetQuery = new WorksheetQuery(database.getWorksheetFeedUrl());

	    worksheetQuery.setTitleQuery(table);
	    WorksheetFeed worksheetFeed = service.query(worksheetQuery, WorksheetFeed.class);
	    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
	    if (worksheets.isEmpty())
	    {
    		throw new Exception("No tables with that name");
	    }

	    return worksheets.get(0);
	}
	
	/**
	 * Retrieves the columns headers from the cell feed of the worksheet
	 * entry.
	 *
	 * @param WorksheetEntry worksheet entry containing the cell feed in question
	 * @return a list of column headers and their column numbers
	 * @throws Exception if error in retrieving the spreadsheet information
	 */
	private HashMap<String, Integer> getColumnHeaders(WorksheetEntry worksheet) throws Exception
	{
		HashMap<String, Integer> headers = new HashMap<String, Integer>();

		// Create a query for the top row of cells only (1-based)
		CellQuery cellQuery = new CellQuery(worksheet.getCellFeedUrl());
		cellQuery.setMaximumRow(1);

		// Get the cell feed matching the query
		CellFeed topRowCellFeed = service.query(cellQuery, CellFeed.class);

		// Get the cell entries from the feed
		List<CellEntry> cellEntries = topRowCellFeed.getEntries();
		for (CellEntry entry : cellEntries)
		{
			// Get the cell element from the entry
			Cell cell = entry.getCell();
			headers.put(cell.getValue(), cell.getCol());
		}

		return headers;
	}
	
	/**
	 * Retrieves the contents of the given column(s)
	 * 
	 * @param table The table to query against
	 * @param columns The desired columns
	 * @param conditions Optional conditions to be applied
	 * @return a map of column values, mapped by column name
	 * @throws Exception if error in retrieving the spreadsheet information
	 */
	public Map<String, ArrayList<String>> select(String table, ArrayList<String> columns, String conditions) throws Exception
	{
		WorksheetEntry worksheet = getTable(table);
		HashMap<String, Integer> headers = getColumnHeaders(worksheet);
		HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();

		if(conditions == null || conditions.length() == 0)
		{
			CellQuery cellQuery = new CellQuery(worksheet.getCellFeedUrl());
			for(String column : columns)
			{
				cellQuery.setMinimumRow(2);	//Start of data rows
				cellQuery.setMinimumCol(headers.get(column));
				cellQuery.setMaximumCol(headers.get(column));
	
				CellFeed cellFeed = service.query(cellQuery, CellFeed.class);
				List<CellEntry> cellEntries = cellFeed.getEntries();
				ArrayList<String> values = new ArrayList<String>();
				for (CellEntry entry : cellEntries)
				{
					// Get the cell element from the entry
					Cell cell = entry.getCell();
					values.add(cell.getValue());
				}
				results.put(column, values);
			}
		}
		else
		{
			ListQuery query = new ListQuery(worksheet.getListFeedUrl());
			query.setSpreadsheetQuery(conditions);
			ListFeed listFeed = service.query(query, ListFeed.class);

			//Get the requested columns
			for(String column : columns)
			{
				ArrayList<String> values = new ArrayList<String>();
				for (ListEntry entry : listFeed.getEntries())
				{
					values.add(entry.getCustomElements().getValue(column));
				}
				results.put(column, values);
			}
		}
		return results;
		
	}
	
	/**
	 * Inserts a new row into the table
	 * 
	 * @param table The table to query against
	 * @param columns The desired columns
	 * @param values The values to insert
	 * @return a map of column values, mapped by column name
	 * @throws Exception if error in retrieving the spreadsheet information
	 */
	public void insert(String table, ArrayList<String> columns, ArrayList<String> values) throws Exception
	{
		if(columns.size() != values.size())
		{
			throw new Exception("Incorrect column count.");
		}

		WorksheetEntry worksheet = getTable(table);
		ListEntry newEntry = new ListEntry();

		for (int i = 0; i < columns.size(); i++)
		{
		  newEntry.getCustomElements().setValueLocal(columns.get(i), values.get(i));
		}
		
		service.insert(worksheet.getListFeedUrl(), newEntry);
	}
	
	/**
	 * Updates a row in the table
	 * 
	 * @param table The table to query against
	 * @param columns The desired columns
	 * @param values The values to insert
	 * @param conditions The conditions to find the row to update
	 * @return The number of affected rows
	 * @throws Exception if error in retrieving the spreadsheet information
	 */
	public Integer update(String table, ArrayList<String> columns, ArrayList<String> values, String conditions) throws Exception
	{
		if(columns.size() != values.size())
		{
			throw new Exception("Incorrect column count.");
		}
		
		if(conditions.length() == 0)
		{
			throw new Exception("No update conditions specified.");
		}

		WorksheetEntry worksheet = getTable(table);
		ListQuery query = new ListQuery(worksheet.getListFeedUrl());
		query.setSpreadsheetQuery(conditions);
		ListFeed listFeed = service.query(query, ListFeed.class);

		//Update the entries
		Integer affected = 0;	//The number of rows affected
		for (ListEntry entry : listFeed.getEntries())
		{
			for(int i = 0; i < columns.size(); i++)
			{
				entry.getCustomElements().setValueLocal(columns.get(i), values.get(i));
			}
			entry.update();
			affected++;
		}
		
		return affected;
	}
	
	/**
	 * Gets the next available primary key
	 * @param table The desired table
	 * @param key The primary key for that table (You MUST have one if you're calling this!)
	 * @return The next id
	 */
	public Integer getNextId(String table, String key) throws Exception
	{
		final String column = key;

		Map<String, ArrayList<String>> results = select(table, new ArrayList<String>() {{ add(column); }}, "orderby=column:" + key + "&reverse=true");
		if(results.size() > 0)
		{
			return Integer.parseInt(results.get(key).get(0));
		}
		else
		{
			//No rows, start the index at 1
			return 1;
		}
	}
}
