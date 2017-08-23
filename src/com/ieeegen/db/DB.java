package com.ieeegen.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DB {
	
	private static final Logger LOGGER = Logger.getLogger(DB.class.getName());

	private static final String DB_URL = "jdbc:mysql://localhost/ieeegen";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "span";
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	
	private Statement statement = null; // WARNING! Check init return before using to avoid NPE.
	private static String SELECT_STATEMENT = "select * from ";
	
;	public  DB() throws Exception{
		try {
			Class.forName(DB_DRIVER).newInstance();		
			final Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			statement = conn.createStatement();
		} catch(Exception e){ // sql exception and class not found exception
			LOGGER.log(Level.SEVERE, "exception occured while making connection to DB: ", e);
			throw e;
			
		}
	}
	
	public ResultSet readTable(String tableName, String criteria, String... columnNames) throws SQLException{
		StringBuilder query = new StringBuilder("select ");
		int count = 0;
		for (String column : columnNames){
		query.append(column );
			if(++count != columnNames.length){
				query.append(", ");
			}
		}
		query.append(" from " + tableName + " " + criteria + ";");
		ResultSet rs = statement.executeQuery(query.toString());
		return rs;
	}
	
	public ResultSet readTable(String tableName, String criteria) throws SQLException{
		ResultSet rs = statement.executeQuery(SELECT_STATEMENT + tableName + " " +  criteria + ";" );
		return rs;
	}
	
}
