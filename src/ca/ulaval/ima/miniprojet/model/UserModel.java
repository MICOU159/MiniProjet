package ca.ulaval.ima.miniprojet.model;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
	private int id;
	private String mUsername;
	private String mPassword;

	public UserModel(String username, String password) {
		this.id = 0;
		this.mUsername = username;
		this.mPassword = password;
	}

	public UserModel(JSONObject inObject) {
		if (inObject != null) {
			try {
				this.id = inObject.getInt("id");
				this.mUsername = inObject.getString("username");
				this.mPassword = inObject.getString("password");

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public UserModel(Parcel in) {
		if (in.dataSize() > 0) {
			this.id = in.readInt();
			this.mUsername = in.readString();
			this.mPassword = in.readString();
		}
	}

	// convert to JSON object
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();

		try {
			obj.put("id", this.id);
			obj.put("username", this.mUsername);
			obj.put("password", this.mPassword);

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
		return mUsername;
	}

	public void setmEmail(String mEmail) {
		this.mUsername = mEmail;
	}

	public String getmPassword() {
		return mPassword;
	}

	public void setmPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
		public UserModel createFromParcel(Parcel in) {
			return new UserModel(in);
		}

		public UserModel[] newArray(int size) {
			return new UserModel[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		out.writeInt(this.id);
		out.writeString(this.mUsername);
		out.writeString(this.mPassword);
	}
}
