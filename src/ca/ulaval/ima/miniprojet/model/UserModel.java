package ca.ulaval.ima.miniprojet.model;

import org.json.JSONException;
import org.json.JSONObject;


public class UserModel {
	private int id;
	private String mEmail;
	private String mPassword;
	private String mPhone;
	
	public UserModel(String email, String password, String phone){
		this.id = 0;
		this.mEmail = email;
		this.mPassword = password;
		this.mPhone = phone;
	}
	
	public UserModel(JSONObject inObject){
		if(inObject!=null){
			try {
				this.id=inObject.getInt("id");
				this.mEmail=inObject.getString("email");
				this.mPassword=inObject.getString("password");
				this.mPhone=inObject.getString("phone");
				
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
	public String getmEmail() {
		return mEmail;
	}
	public void setmEmail(String mEmail) {
		this.mEmail = mEmail;
	}
	public String getmPassword() {
		return mPassword;
	}
	public void setmPassword(String mPassword) {
		this.mPassword = mPassword;
	}
	public String getmPhone() {
		return mPhone;
	}
	public void setmPhone(String mPhone) {
		this.mPhone = mPhone;
	}
}
