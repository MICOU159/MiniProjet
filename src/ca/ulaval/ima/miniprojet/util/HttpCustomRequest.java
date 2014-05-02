package ca.ulaval.ima.miniprojet.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

public class HttpCustomRequest {

	private String mUri;
	private String mMethod="GET";
	private List<NameValuePair> mParameters;
	private List<NameValuePair> mHeaders;
	private String mBody;
	private Context mContext;

	public HttpCustomRequest(Context inContext,String inUrl){
		this.setContext(inContext);
		this.mUri = inUrl;
		this.mParameters = new ArrayList<NameValuePair>();
		this.mHeaders = new ArrayList<NameValuePair>();
		this.mBody = null;
	}
	
	public void setHeaders(List<NameValuePair> inHeaders){
		this.mHeaders = inHeaders;
	}
	
	public void setBody(String inBody){
		this.mBody = inBody;
	}
	public byte[] getBodyEncoded(){
		byte[] encodedValue=null;
		
		if(this.mBody==null) return encodedValue;
		
		try {
			encodedValue= this.mBody.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encodedValue;
	}
	public void setPairValue(List<NameValuePair> content){
		this.mParameters = content;
	}

	public void setMethod(String inMethod) { 
		this.mMethod = inMethod;
		}
	public String getMethod(){
		return this.mMethod;
		}

	public URL getURL() throws MalformedURLException{
		if(this.mParameters.size()>0){
			try {
				return new URL(this.mUri+"?"+this.getQueryParmeters());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else return new URL(this.mUri);
		return new URL(this.mUri);
	}
	public List<NameValuePair> getHeaders(){ 
		return this.mHeaders;
	}

	private String getQueryParmeters() throws UnsupportedEncodingException
	{

		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : this.mParameters)
		{
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

}
