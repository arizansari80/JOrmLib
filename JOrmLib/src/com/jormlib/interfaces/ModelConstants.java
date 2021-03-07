package com.jormlib.interfaces;

public interface ModelConstants {
	static final byte EXECUTE_CREATE = 0;
	static final byte EXECUTE_INSERT = 1;
	static final byte EXECUTE_UPDATE = 2;
	static final byte EXECUTE_DELETE = 3;
	static final byte EXECUTE_QUERY = 4;
	
	static final byte EXECUTE_CREATE_TRUE = -1;
	static final byte EXECUTE_CREATE_FALSE = -2;
	
	static final Byte EXECUTE_NONE = -3;
	static final Byte EXECUTE_SQL_EXCEPTION = -4;
}
