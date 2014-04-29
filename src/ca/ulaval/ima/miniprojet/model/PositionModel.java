package ca.ulaval.ima.miniprojet.model;

import org.json.JSONException;
import org.json.JSONObject;


public class PositionModel {
	private double mLatitude;
	private double mLongitude;
	
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
