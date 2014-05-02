package ca.ulaval.ima.miniprojet.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;


public class PositionModel implements Parcelable{
	private double mLatitude;
	private double mLongitude;
	
	//create from JSON
	public PositionModel(JSONObject inObject){
		if(inObject!=null){
			try {
				this.mLatitude = inObject.getDouble("latitude");
				this.mLongitude = inObject.getDouble("longitude");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public PositionModel(Parcel in) {
		if (in.dataSize() > 0) {
			this.mLatitude = in.readDouble();
			this.mLongitude = in.readDouble();
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

	public static final Parcelable.Creator<PositionModel> CREATOR = new Parcelable.Creator<PositionModel>() {
		public PositionModel createFromParcel(Parcel in) {
			return new PositionModel(in);
		}

		public PositionModel[] newArray(int size) {
			return new PositionModel[size];
		}
	};
	
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeDouble(this.mLatitude);
		out.writeDouble(this.mLongitude);
		
	}
}
