package ca.ulaval.ima.miniprojet.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestModel implements Parcelable{

	private int id;
	private String mUsername;
	private PositionModel mPosition;
	private String mDestination;
	private int mPersonsCount;
	private ArrayList<String> mMessages;
	private int mStatus;

	public RequestModel(String username, PositionModel position,
			String destination, int personsCount, String message) {
		this.id = 0;
		this.mUsername = username;
		this.mPosition = position;
		this.mDestination = destination;
		this.mPersonsCount = personsCount;
		this.mMessages = new ArrayList<String>();
		if (!message.isEmpty()) {
			this.mMessages.add(message);
		}
		this.mStatus = 0;
	}

	// create request from JSON
	public RequestModel(JSONObject inObject) {
		if (inObject != null) {
			try {
				this.id = inObject.getInt("id");
				this.mUsername = inObject.getString("username");
				this.mPosition = new PositionModel(
						inObject.getJSONObject("position"));
				this.mDestination = inObject.getString("destination");

				this.mMessages = new ArrayList<String>();
				JSONArray jArray = inObject.getJSONArray("messages");
				if (jArray != null) {
					for (int i = 0; i < jArray.length(); i++) {
						this.mMessages.add(jArray.get(i).toString());
					}
				}
				this.mStatus = inObject.getInt("status");

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
	
	
	public RequestModel(Parcel in) {
		if (in.dataSize() > 0) {
			this.id = in.readInt();
			this.mUsername = in.readString();
			this.mDestination = in.readString();
			this.mPosition = in.readParcelable(PositionModel.class.getClassLoader());
			this.mPersonsCount = in.readInt();
			this.mMessages = (ArrayList<String>) in.readSerializable();
			this.mStatus = in.readInt();
		}
	}

	// convert to JSON object
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();

		try {
			obj.put("id", this.id);
			obj.put("username", this.mUsername);
			obj.put("destination", this.mDestination);
			obj.put("position", this.mPosition.toJSON());
			obj.put("persons_count", this.mPersonsCount);

			JSONArray jArray = new JSONArray();
			for (int i = 0; i < this.mMessages.size(); i++) {
				jArray.put(this.mMessages.get(i));
			}
			obj.put("messages", jArray);
			obj.put("status", this.mStatus);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getmUsername() {
		return this.mUsername;
	}

	public void setmUsername(String username) {
		this.mUsername = username;
	}

	public PositionModel getmPosition() {
		return mPosition;
	}

	public void setmPosition(PositionModel position) {
		this.mPosition = position;
	}

	public String getmDestination() {
		return mDestination;
	}

	public void setmDestination(String destination) {
		this.mDestination = destination;
	}

	public int getmPersonsCount() {
		return mPersonsCount;
	}

	public void setmPersonsCount(int personsCount) {
		this.mPersonsCount = personsCount;
	}

	public int getmStatus() {
		return mStatus;
	}

	public void setmStatus(int status) {
		this.mStatus = status;
	}
	
	public ArrayList<String> getmMessages() {
		return mMessages;
	}

	public void setmMessages(ArrayList<String> messages) {
		this.mMessages = messages;
	}
	
	public static final Parcelable.Creator<RequestModel> CREATOR = new Parcelable.Creator<RequestModel>() {
		public RequestModel createFromParcel(Parcel in) {
			return new RequestModel(in);
		}

		public RequestModel[] newArray(int size) {
			return new RequestModel[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeInt(this.id);
		out.writeString(this.mUsername);
		out.writeString(this.mDestination);
		out.writeParcelable(this.mPosition, arg1);
		out.writeInt(this.mPersonsCount);
		out.writeSerializable(this.mMessages);
		out.writeInt(this.mStatus);
	}

}
