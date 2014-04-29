package ca.ulaval.ima.miniprojet.model;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestModel {

	private int id;
	private int mUserID;
	private PositionModel mPosition;
	private String mDestination;
	private int mPersonsCount;
	//private messagesList
	private int mStatus;
	
	public RequestModel(JSONObject inObject){
		if(inObject!=null){
			try {
				this.id = inObject.getInt("id");
				this.mUserID = inObject.getInt("userID");
				this.mPosition = new PositionModel(inObject.getJSONObject("position"));
				this.mDestination = inObject.getString("destination");
				//messages...à faire
				this.mStatus = inObject.getInt("status");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getmUserID() {
		return mUserID;
	}
	public void setmUserID(int mUserID) {
		this.mUserID = mUserID;
	}
	public PositionModel getmPosition() {
		return mPosition;
	}
	public void setmPosition(PositionModel mPosition) {
		this.mPosition = mPosition;
	}
	public String getmDestination() {
		return mDestination;
	}
	public void setmDestination(String mDestination) {
		this.mDestination = mDestination;
	}
	public int getmPersonsCount() {
		return mPersonsCount;
	}
	public void setmPersonsCount(int mPersonsCount) {
		this.mPersonsCount = mPersonsCount;
	}
	public int getmStatus() {
		return mStatus;
	}
	public void setmStatus(int mStatus) {
		this.mStatus = mStatus;
	}
	
}

