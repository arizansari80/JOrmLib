package com.jormlib.models;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.jormlib.models.Model;

public class ModelBackupTask extends Thread {
	private ResultSet res;
	private Connection conn;
	private String tableName;
	private String category;
	
	protected ModelBackupTask(final Connection co, final String name, final String query, final String category) {
		conn = co;
		tableName = name;
		this.category = category;
		try {
			final PreparedStatement stmtB = conn.prepareStatement(query);
			res = stmtB.executeQuery();
		} catch(SQLException e) {
			System.out.println("Exception While taking backup, query is " + query);
			res = null;
		}
	}
	
	@Override
	public String toString() {
		JSONObject retVal = new JSONObject();
		try {
			int i = 1;
			String key = "object_";
			JSONObject objJson = new JSONObject();
			while (res.next())
				objJson.put(key + i++, Model.resultSetToJSON(res));
			retVal.put("Object", objJson);
			retVal.put("Connection", conn.toString());
			retVal.put("Category", category);
			retVal.put("TableName", tableName);
		} catch (JSONException | SQLException e) {
			System.out.println("Exception While taking backup");
		}
		return retVal.toString();
	}
	
	@Override
	public void run() {
		makeBackup(toString());
	}
	
	private synchronized void makeBackup(final String str) {
		try {
			final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/home/gulameraza/kedormlib_backup.json", true), "utf-8"));
			final JSONObject tempJSON = new JSONObject();
			tempJSON.put("time", new Date().toString());
			tempJSON.put("message", str);
			bw.write(tempJSON.toString());
			bw.newLine();
			bw.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			System.out.println("Exception While taking backup");
		} catch (IOException e) {
			System.out.println("Exception While taking backup");
		} catch (JSONException e) {
			System.out.println("Exception While taking backup");
		}
	}
}
