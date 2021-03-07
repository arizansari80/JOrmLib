package com.jormlib.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.jormlib.exception.DataInsufficientException;
import com.jormlib.exception.DeletingAllException;
import com.jormlib.exception.MethodNotOverriddenException;
import com.jormlib.interfaces.BaseColumns;
import com.jormlib.interfaces.ModelConstants;
import com.jormlib.interfaces.ModelElementCategory;

/**
 * User Please call the super constructor
 * For Insert Statement to run
 * super(final String tableName)
 * 
 * For Update Delete element to run
 * super(final String tableName, final ArrayList<ModelElement> modelElements)
 */
public abstract class Model implements BaseColumns, ModelElementCategory, ModelConstants {
	private byte executionType;
	
	protected String tableName;
	
	@SuppressWarnings("rawtypes")
	protected ArrayList<ModelElement> modelElements;
	
	// Constructor for Insert Statement
	/**
     * The Constructor to make object for Insert Statement
     */
	public Model(final String tableName) {
		this.tableName = tableName;
		executionType = EXECUTE_INSERT;
		modelElements = new ArrayList<>();
		buildDefaults();
	}
	
	// Constructor for Update Statement or Delete Statement
	/**
     * The Constructor to make object for Update Statement
     */
	@SuppressWarnings("rawtypes")
	public Model(final String tableName, final ArrayList<ModelElement> modelElements) {
		this.tableName = tableName;
		executionType = EXECUTE_UPDATE;
		this.modelElements = new ArrayList<>(modelElements);
	}
	
	// Query Statement
	@SuppressWarnings("rawtypes")
	/**
     * The Insert Query Filter
     */
	public static ResultSet filter(final Connection conn, final String tableName, final ArrayList<ModelElement> projection, final String selection, final String sortOrder) {
		try {
			final String where = selection == null ? "" : " where " + selection;
			
			String select = projection == null ? "*" : "";
			if (projection != null) {
				for (ModelElement elem : projection)
					select += elem.getColumnName() + ", ";
				select = select.substring(0,select.length() - 2);
			}
			
			final String sort = sortOrder == null ? "" : " " + sortOrder.trim();
			
			final PreparedStatement stmt = conn.prepareStatement("select " + select +  " from " + tableName + where + sort);
			
			try {
				final ResultSet rset = stmt.executeQuery();
				return rset;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Method return constructed basic query string
	private final String getQueryString(final byte option) {
		String retVal = "";
		switch (option) {
			case EXECUTE_CREATE:
				break;
			case EXECUTE_INSERT: {
					retVal = "insert into " + tableName + " values (";
					for (int i = 0; i < modelElements.size(); i++)
						if (i != modelElements.size() - 1)
							retVal += modelElements.get(i).toString() + ", ";
						else
							retVal += modelElements.get(i).toString() + " );";
				}
				break;
			default:
				retVal = null;
		}
		return retVal;
	}
	
	// User will override this to execute create query
	/**
     * The Override this method to run table create statement
     */
	protected abstract Boolean executeCreate();
	
	// Method will run insert query
	private final Integer executeUpdate(final PreparedStatement stmt) {
		int numOfRows = 0;
		try {
			numOfRows = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numOfRows;
	}
	
	// Method will run update query for a particular object
	@SuppressWarnings("rawtypes")
	private final Integer executeUpdate(final Connection conn) throws DataInsufficientException, MethodNotOverriddenException, SQLException {
		String where = buildWhereClauseForDeleteAndUpdate();
		String query = "update " + tableName + " set ";
		for (ModelElement e : modelElements)
			query += e.getColumnName() + " = " + e.toString() + " , ";
		query = query.substring(0,query.length() - 3) + where + ";";
		
		/*Backup task*/
		new ModelBackupTask(conn, tableName, "select * from " + tableName + where, "via update with object").start();
		/*End*/
		
		final PreparedStatement stmt = conn.prepareStatement(query);
		return stmt.executeUpdate();
	}
	
	// Method will save the instance and will run insert and update query
	@SuppressWarnings({ "unchecked" })
	/**
     * The Method will save the object
     */
	public final <T> T save(final Connection conn) throws DataInsufficientException, MethodNotOverriddenException, SQLException {
		switch (executionType) {
			case EXECUTE_CREATE:
				return (T)EXECUTE_NONE;
			case EXECUTE_INSERT: {
				final PreparedStatement stmt = conn.prepareStatement(getQueryString(executionType));
				return (T)executeUpdate(stmt);
			}
			case EXECUTE_UPDATE:
				return (T)executeUpdate(conn);
			default:
				return (T)EXECUTE_NONE;
		}
	}
	
	// Deleting Rows from the table with the selection
	/**
     * The Method Deleting Rows from the table with the selection
     */
	public final static int delete(final Connection conn, final String tableName, final String selection) throws DeletingAllException, SQLException {
		String where = " where";
		String exceptionMessage = "";
		if (selection == null) {
			exceptionMessage = "selection argument not passed and is null\n";
		}
		else {
			where += " " + selection;
		}
		if (where.compareTo(" where") == 0)
			throw new DeletingAllException("Attempting to delete entire table with this method is not safe\n" + exceptionMessage
					+ "\n\nTo delete an entire table, please use deleteAll() method of the Library");
		
		/*Backup task*/
		new ModelBackupTask(conn, tableName, "select * from " + tableName + where, "via delete with selection").start();
		/*End*/
		
		final PreparedStatement stmt = conn.prepareStatement("delete from " + tableName + where + ";");
		return stmt.executeUpdate();
	}
	
	// Updating Rows from the table with the selection
	/**
     * The Method Updating Rows of the table with the selection
     */
	public final static int update(final Connection conn, final String tableName, final String selection) throws DeletingAllException, SQLException {
		String where = " where";
		String exceptionMessage = "";
		if (selection == null) {
			exceptionMessage = "selection argument not passed and is null\n";
		}
		else {
			where += " " + selection;
		}
		if (where.compareTo(" where") == 0)
			throw new DeletingAllException("Attempting to update entire table with this method is not safe\n" + exceptionMessage
					+ "\n\nTo update an entire table, please use updateAll() method of the Library");
		
		/*Backup task*/
		new ModelBackupTask(conn, tableName, "select * from " + tableName + where, "via update with selection").start();
		/*End*/
		
		final PreparedStatement stmt = conn.prepareStatement("delete from " + tableName + where + ";");
		return stmt.executeUpdate();
	}
	
	@SuppressWarnings({ "rawtypes" })
	private final String buildWhereClauseForDeleteAndUpdate() throws MethodNotOverriddenException, DataInsufficientException {
		String retVal = " where";
		int i = 0;
		ArrayList<String> tempSel = getPrimaryKeys();
		if (tempSel == null)
			throw new MethodNotOverriddenException("getPrimaryKeys() method is not overridden");
		for (String t : tempSel) {
			one: for (ModelElement e : modelElements) {
				if (t.compareTo(e.getColumnName()) == 0) {
					i++;
					retVal += " " + e.getColumnName() + " = " + e.toString() + " and";
					break one;
				}
			}
		}
		if (i != tempSel.size())
			throw new DataInsufficientException("the modelElements memeber of this object is not having enough data that getPrimaryKeys() method has defined!");
		if (retVal.compareTo(" where") != 0)
			retVal = retVal.substring(0,retVal.length() - 4);
		return retVal;
	}
	
	// For Deleting Particular Object
	/**
     * The Method Delete Particular Object
     */
	public final int delete(final Connection conn) throws DataInsufficientException, MethodNotOverriddenException, SQLException {
		String where = buildWhereClauseForDeleteAndUpdate();
		/*Backup task*/
		new ModelBackupTask(conn, tableName, "select * from " + tableName + where, "via delete with object").start();
		/*End*/
		final PreparedStatement stmt = conn.prepareStatement("delete from " + tableName + where + ";");
		return stmt.executeUpdate();
	}
	
	// This method is updating value in model elements array list with the value provided by user
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected final void updateModelElements(final ArrayList<ModelElement> addOnElementPair) {
		if (addOnElementPair != null && addOnElementPair.size() > 0) {
			HashMap<String,Integer> tempMap = getModelElementsIndexes();
			for (ModelElement tempPair : addOnElementPair)
				modelElements.get(tempMap.get(tempPair.getColumnName())).setValue(tempPair.getValue());
			tempMap.clear();
			tempMap = null;
		}
	}
	
	/**
     * The Method sets Element Value
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final <T> void setElementValue(final String columnName, final T value) {
		for (ModelElement e : modelElements) {
			if (columnName.compareTo(e.getColumnName()) == 0) {
				e.setValue(value);
				return;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
     * The Method gets Element Value
     */
	public final <T> T getElementValue(final String columnName) {
		T retVal = null;
		for (ModelElement e : modelElements) {
			if (columnName.compareTo(e.getColumnName()) == 0) {
				retVal = (T) e.getValue();
				return retVal;
			}
		}
		return retVal;
	}
	
	/**
     * The Method Convert ResultSet to JSON
     */
	protected static final JSONObject resultSetToJSON(final ResultSet res) {
		final JSONObject retVal = new JSONObject();
		try {
			final ResultSetMetaData resMeta = res.getMetaData();
			int count = resMeta.getColumnCount();
			for (int i = 1; i <= count; i++) {
				retVal.put(resMeta.getColumnName(i), res.getString(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}
	
	// This will return HashMap with Column Name as key and it's index(zero based in schema) as value
	/**
     * Override this method to get column name to column index map
     */
	protected abstract HashMap<String,Integer> getModelElementsIndexes();
	
	// build modelElements with default values and then update in constructor
	/**
     * Override this method to set default value to the parameter user's think
     */
	protected abstract void buildDefaults();
	
	/**
     * Override this method to give ArrayList of primary keys elements
     * The Value of any element is insignificant in this method
     */
	protected abstract ArrayList<String> getPrimaryKeys();
	
	@SuppressWarnings("rawtypes")
	@Override
	public String toString() {
		String retVal = "{";
		if (modelElements == null || modelElements.size() == 0)
			retVal += " }";
		else {
			retVal += "\n";
			for (ModelElement e : modelElements)
				retVal += "\t" + e.getColumnName() + " : " + e.toString() + ",\n";
			retVal = retVal.substring(0,retVal.length() - 2) + "\n}";
		}
			
		return retVal;
	}
	
	
	@Override
	protected void finalize() {
		if (modelElements != null)
			modelElements.clear();
	}
	
	public void destroy() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
