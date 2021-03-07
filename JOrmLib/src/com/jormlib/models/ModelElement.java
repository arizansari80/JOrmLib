package com.jormlib.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jormlib.interfaces.ModelElementCategory;

public class ModelElement<T> implements ModelElementCategory {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
     * The name of model element.
     * <P>Type: STRING (String)</P>
     */
	private String columnName;
	
	/**
     * The value of model element.
     * <P>Type: Generic (Generic)</P>
     */
	private T value;
	
	/**
     * The category of model element.
     * <P>Type: INTEGER (long) the value will be -1 if user think that category is insignificant for the object</P>
     */
	private int category;
	
	public ModelElement(final String columnName, final int category) {
		this.columnName = columnName;
		this.value = null;
		this.category = category;
	}
	
	/**
     * Pass -1 in category if you think that category is insignificant for the object
     */
	public ModelElement(final String columnName, final int category, final T value) {
		this.columnName = columnName;
		this.value = value;
		this.category = category;
	}
	
	@SuppressWarnings("unchecked")
	public ModelElement(final String columnName, final int category, final ResultSet resultSet) {
		/*super(columnName,category,resultSet);*/
		this.columnName = columnName;
		try {
			switch (category) {
				case MODEL_BYTE:
					value = (T)((Byte)resultSet.getByte(columnName));
					break;
				case MODEL_INT:
					value = (T)((Integer)resultSet.getInt(columnName));
					break;
				case MODEL_SHORT:
					value = (T)((Short)resultSet.getShort(columnName));
					break;
				case MODEL_LONG:
					value = (T)((Long)resultSet.getLong(columnName));
					break;
				case MODEL_FLOAT:
					value = (T)((Float)resultSet.getFloat(columnName));
					break;
				case MODEL_DOUBLE:
					value = (T)((Double)resultSet.getDouble(columnName));
					break;
				case MODEL_STRING:
					value = (T)((String)resultSet.getString(columnName));
					break;
				case MODEL_BYTE_ARRAY:
					value = (T)((byte [])resultSet.getBytes(columnName));
					break;
				case MODEL_DATE_TIME:
					value = (T)((Date)resultSet.getDate(columnName));
					break;
			}
		} catch (SQLException e) {
			value = null;
			e.printStackTrace();
		}
		this.category = category;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ModelElement(final ModelElement elem) {
		/*super(elem.columnName,(T)elem.value);*/
		this.columnName = elem.columnName;
		this.value = (T)elem.value;
		this.category = elem.category;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ModelElement(final ModelElement elem, final ResultSet resultSet) {
		/*super(elem.columnName,elem.category,resultSet);*/
		this.columnName = elem.columnName;
		try {
			switch (category) {
				case MODEL_BYTE:
					value = (T)((Byte)resultSet.getByte(elem.columnName));
					break;
				case MODEL_INT:
					value = (T)((Integer)resultSet.getInt(elem.columnName));
					break;
				case MODEL_SHORT:
					value = (T)((Short)resultSet.getShort(elem.columnName));
					break;
				case MODEL_LONG:
					value = (T)((Long)resultSet.getLong(elem.columnName));
					break;
				case MODEL_FLOAT:
					value = (T)((Float)resultSet.getFloat(elem.columnName));
					break;
				case MODEL_DOUBLE:
					value = (T)((Double)resultSet.getDouble(elem.columnName));
					break;
				case MODEL_STRING:
					value = (T)((String)resultSet.getString(elem.columnName));
					break;
				case MODEL_BYTE_ARRAY:
					value = (T)((byte [])resultSet.getBytes(columnName));
					break;
			}
		} catch (SQLException e) {
			value = null;
			e.printStackTrace();
		}
		this.category = elem.category;
	}
	
	public void setValue(final T newValue) {
		value = newValue;
	}
	
	public T getValue() {
		return value;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	@Override
	public String toString() {
		switch (category) {
			case MODEL_BYTE:
			case MODEL_INT:
			case MODEL_SHORT:
			case MODEL_LONG:
			case MODEL_FLOAT:
			case MODEL_DOUBLE:
				if (value == null)
					return "-1";
				else
					return value.toString();
			case MODEL_STRING:
				if (value == null)
					return "\'\'";
				else
					return "\'" + value.toString() + "\'";
			case MODEL_BYTE_ARRAY:
				if (value == null)
					return "\'\'";
				else
					return "\'" + new String((byte [])value) + "\'";
			default:
				return "";
		}
	}
}