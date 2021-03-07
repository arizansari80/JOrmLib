package com.jormlib.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jormlib.sample.model.OrmModelSample;
import com.jormlib.exception.DataInsufficientException;
import com.jormlib.exception.MethodNotOverriddenException;
import com.jormlib.models.ModelElement;

public class Main {
	public static void main(String[] args) {
		Connection gConn = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			gConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/OrmModelTest",
					"ariz",
					"myAriz@123");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Running Insert Query
		/*OrmModelSample ormModelTest1 = new OrmModelSample(19, 19, "8800257124","this is new sample".getBytes(), null);
		if (gConn != null)
			try {
				ormModelTest1.save(gConn);
			} catch (DataInsufficientException | MethodNotOverriddenException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		ArrayList<ModelElement> passedExtra = new ArrayList<>();
		passedExtra.add(new ModelElement<String>(OrmModelSample.PRODUCT_KEY, -1, "KKELR"));
		OrmModelSample ormModelTest2 = new OrmModelSample(214, 214, "7827242918","this is sample".getBytes(), passedExtra);
		if (gConn != null)
			try {
				ormModelTest2.save(gConn);
			} catch (DataInsufficientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MethodNotOverriddenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		
		// Running Update Query
		ArrayList<OrmModelSample> objects = OrmModelSample.filter(gConn, null, OrmModelSample.CONTACT_NUMBER + "=\'7827242918\' or " + OrmModelSample.CONTACT_NUMBER + "=\'8800257124\'","ORDER BY " + OrmModelSample.ID + " desc");
		try {
			objects.get(0).setElementValue(OrmModelSample.IMAGE, "myImage_1256.png");
			objects.get(1).setElementValue(OrmModelSample.IMAGE, "myImage_12589.png");
			byte[] arr = (byte [])objects.get(0).getElementValue(OrmModelSample.ARR); 
			objects.get(0).save(gConn);
			objects.get(1).save(gConn);
		} catch (DataInsufficientException e) {
			e.printStackTrace();
		} catch (MethodNotOverriddenException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		objects.clear();
		
		// Running Delete Query
		/*ArrayList<OrmModelTest1> objects1 = OrmModelTest1.filter(gConn, null, OrmModelTest1.CONTACT_NUMBER + "=\'7827242918\'");
		try {
			objects1.get(0).delete(gConn);
			objects1.get(1).delete(gConn);
		} catch (DataInsufficientException e) {
			e.printStackTrace();
		} catch (MethodNotOverriddenException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		objects1.clear();*/
	}
}
