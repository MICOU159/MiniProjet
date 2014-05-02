package ca.ulaval.ima.miniprojet.model;

import org.json.JSONException;
import org.json.JSONObject;


public class PositionModel {
	private double mLatitude;
	private double mLongitude;
	
	//create from JSON
	public PositionModel(JSONObject inObject){
		if(inObject!=null){
			try {
				this.mLatitude = inObject.getDouble("latitude");
				this.mLatitude = inObject.getDouble("longitude");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public PositionModel(double latitude, double longitude) {
		// TODO Auto-generated constructor stub
		this.mLatitude = latitude;
		this.mLongitude = longitude;
	}

	//convert to JSON object
	public JSONObject toJSON(){
		JSONObject obj = new JSONObject();
		
		try {
			obj.put("latitude", this.mLatitude);
			obj.put("longitude", this.mLongitude);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	
	public double getmLatitude() {
		return mLatitude;
	}
	public void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}
	public double getmLongitude() {
		return mLongitude;
	}
	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
}
