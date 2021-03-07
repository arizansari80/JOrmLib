package com.jormlib.sample.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jormlib.models.ModelElement;
import com.jormlib.models.Model;

public class OrmModelSample extends Model {
	private static final String TABLE_NAME = "OrmModelTest_1"; 
	public static final String ID = "id";
	public static final String IMAGE = "image";
	public static final String USER_ID = "user_id";
	public static final String PRODUCT_KEY = "product_key";
	public static final String CONTACT_NUMBER = "contact_number";
	public static final String ARR = "arr";
	
	@SuppressWarnings("rawtypes")
	public OrmModelSample(
			final int id,
			final int userId,
			final String contactNumber,
			final byte[] arr,
			final ArrayList<ModelElement> modelElementsPair) {
		super(TABLE_NAME);
		setElementValue(ID, id);
		setElementValue(USER_ID, userId);
		setElementValue(CONTACT_NUMBER, contactNumber);
		setElementValue(ARR, arr);
		if (modelElementsPair != null)			
			updateModelElements(modelElementsPair);
	}
	
	@SuppressWarnings("rawtypes")
	public OrmModelSample(final ArrayList<ModelElement> temp) {
		super(TABLE_NAME,temp);
	}

	@Override
	protected Boolean executeCreate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HashMap<String, Integer> getModelElementsIndexes() {
		// TODO Auto-generated method stub
		HashMap<String, Integer> retVal = new HashMap<>();
		retVal.put(ID,0);
		retVal.put(IMAGE,1);
		retVal.put(USER_ID,2);
		retVal.put(PRODUCT_KEY,3);
		retVal.put(CONTACT_NUMBER,4);
		retVal.put(ARR,5);
		return retVal;
	}

	@Override
	protected void buildDefaults() {
		// TODO Auto-generated method stub
		modelElements.add(0,new ModelElement<Integer>(ID, MODEL_INT, -1));
		modelElements.add(1,new ModelElement<String>(IMAGE, MODEL_STRING, "default.jpg"));
		modelElements.add(2,new ModelElement<Integer>(USER_ID, MODEL_INT, -1));
		modelElements.add(3,new ModelElement<Integer>(PRODUCT_KEY, MODEL_STRING));
		modelElements.add(4,new ModelElement<Integer>(CONTACT_NUMBER, MODEL_STRING));
		modelElements.add(5,new ModelElement<byte[]>(ARR, MODEL_BYTE_ARRAY));
	}
	
	@SuppressWarnings("rawtypes")
	public static ArrayList<OrmModelSample> filter(final Connection conn, final ArrayList<ModelElement> projection, final String selection, final String sortOrder) {
		final ResultSet resultSet = Model.filter(conn,TABLE_NAME,projection,selection,sortOrder);
		if (resultSet != null) {
			try {
				ArrayList<OrmModelSample> retVal = new ArrayList<>();
				ArrayList<ModelElement> temp = null;
				while (resultSet.next()) {
					temp = new ArrayList<>();
					if (projection != null) {
						for (ModelElement proj : projection)
							temp.add(new ModelElement<>(proj,resultSet));
					}
					else {
						temp.add(new ModelElement<Integer>(ID, MODEL_INT, resultSet));
						temp.add(new ModelElement<String>(IMAGE, MODEL_STRING, resultSet));
						temp.add(new ModelElement<Integer>(USER_ID, MODEL_INT, resultSet));
						temp.add(new ModelElement<Integer>(PRODUCT_KEY, MODEL_STRING, resultSet));
						temp.add(new ModelElement<Integer>(CONTACT_NUMBER, MODEL_STRING, resultSet));
						temp.add(new ModelElement<byte[]>(ARR, MODEL_BYTE_ARRAY, resultSet));
					}
					retVal.add(new OrmModelSample(temp));
				}
				
				return retVal;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected ArrayList<String> getPrimaryKeys() {
		ArrayList<String> temp = new ArrayList<>();
		temp.add(ID);
		return temp;
	}

	/*@Override
	public JSONObject resultSetToJSON(ResultSet res) {
		JSONObject retVal = new JSONObject();
		try {
			retVal.put(ID, res.getInt(ID));
			retVal.put(IMAGE, res.getString(IMAGE));
			retVal.put(USER_ID, res.getInt(USER_ID));
			retVal.put(PRODUCT_KEY, res.getString(PRODUCT_KEY));
			retVal.put(CONTACT_NUMBER, res.getString(CONTACT_NUMBER));
		} catch (JSONException | SQLException e) {
			e.printStackTrace();
		}
		return retVal;
	}*/
}
