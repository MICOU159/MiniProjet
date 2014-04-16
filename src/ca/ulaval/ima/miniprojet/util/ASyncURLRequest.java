package ca.ulaval.ima.miniprojet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class ASyncURLRequest extends AsyncTask<HttpCustomRequest, Void, String> {
	boolean error;
	ProgressDialog progressDialog;
	HttpURLConnection connection;


	@Override
	protected String doInBackground(HttpCustomRequest... request) {

		
			String result = null;

			try {
				return loadURL(request[0]);
			} catch (IllegalStateException e) {

				Log.d("asyncHttp","asynchttp catch error : " + e);
				e.printStackTrace();
			} catch (IOException e) {

				Log.d("asyncHttp","asynchttp catch error : " + e);
				e.printStackTrace();
			}  
			return result;

	}

	public int getResponseCode(){
		if(connection != null){
			try {
				return connection.getResponseCode();
			} catch (Exception e) {
				Log.d("asyncHttp", "error get response code :" + e);
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public void abort() {
		connection.disconnect();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String answer) {
		super.onPostExecute(answer);

	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		connection.disconnect();
	} 
	
	public String loadURL(HttpCustomRequest request) throws IOException{
		URL urlToLoad=null;
		try {
			urlToLoad = request.getURL();
		} catch (MalformedURLException e3) {
			e3.printStackTrace();
		}
		
		HttpURLConnection.setFollowRedirects(true);
		connection = (HttpURLConnection)urlToLoad.openConnection();

		CookieSyncManager.createInstance(request.getContext()); 
		CookieManager cookieManager = CookieManager.getInstance();
		String cookie = cookieManager.getCookie(connection.getURL().getHost());
		if (cookie != null) {
			connection.setRequestProperty("Cookie", cookie);
	    }
		
		connection.setDoInput(true);
		
		connection.setRequestMethod(request.getMethod());
		connection.setRequestProperty("User-Agent", ASyncURLRequest.getUserAgent(request.getContext()));
		for(int i=0;i<request.getHeaders().size();i++){
			NameValuePair currentKeyValue = request.getHeaders().get(i);
			connection.setRequestProperty(currentKeyValue.getName(), currentKeyValue.getValue());
		}
		if(request.getBodyEncoded()!=null){
			OutputStream os = connection.getOutputStream();
			os.write(request.getBodyEncoded());    
			os.close();
		}
		
		//connection.setDoInput(true);
		
		connection.connect();

		List<String> cookieList = connection.getHeaderFields().get("Set-Cookie");

	    if (cookieList != null) {
	        for (String cookieTemp : cookieList) {
	            cookieManager.setCookie(connection.getURL().getHost(), cookieTemp);
	        }
	    }


		StringBuilder sb = new StringBuilder();
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (Exception e1) {
			reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			Log.d("asyncHttp", "bufferedreader :"+e1 + " " + reader.readLine());
			e1.printStackTrace();
		} 
		
		if(reader != null) {		

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (Exception e) {
				Log.d("asyncHttp", "line :"+e);
				e.printStackTrace();
			} finally {
				try {
					connection.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.d("asyncHttp","answer"+ sb.toString());
		}
		
		return sb.toString();
	}

	
	
    public static String getUserAgent(Context inContext){
    	final PackageManager packageManager = inContext.getApplicationContext().getPackageManager();
    	ApplicationInfo appInfo;
    	try {
    		appInfo = packageManager.getApplicationInfo( inContext.getPackageName(), 0);
    	} catch (final NameNotFoundException e) {
    		appInfo = null;
    	}
    	String applicationName= (String) (appInfo != null ? packageManager.getApplicationLabel(appInfo) : "(unknown)");
    	
    	PackageInfo pInfo;
		try {
			pInfo = inContext.getPackageManager().getPackageInfo(inContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			pInfo = null;
			e.printStackTrace();
		}
    	String versionName= pInfo.versionName;
    	
    	String customUserAgent =applicationName + "/" + versionName + " (" + android.os.Build.MODEL + "; Android " + android.os.Build.VERSION.RELEASE + ")"; 
	    
    	return customUserAgent;
    }

}
