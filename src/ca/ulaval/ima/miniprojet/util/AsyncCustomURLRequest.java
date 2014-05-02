package ca.ulaval.ima.miniprojet.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class AsyncCustomURLRequest extends AsyncTask<HttpCustomRequest, Void, String> {

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
	
	
	public String loadURL(HttpCustomRequest request) throws IOException{
		URL urlToLoad=null;
		
		try {
			urlToLoad = request.getURL();
		} catch (MalformedURLException e3) {
			e3.printStackTrace();
		}
		
		HttpURLConnection.setFollowRedirects(true);
		connection = (HttpURLConnection)urlToLoad.openConnection();

		
		connection.setDoInput(true);
		
		//if POST we have to send the body message as output
		if(request.getMethod() == "POST"){
			connection.setDoOutput(true);
			connection.setChunkedStreamingMode(0);
		}
		
		connection.setRequestMethod(request.getMethod());
		
		//header infos
		connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
		
		/*connection.setRequestProperty("User-Agent", ASyncURLRequest.getUserAgent(request.getContext()));
		for(int i=0;i<request.getHeaders().size();i++){
			NameValuePair currentKeyValue = request.getHeaders().get(i);
			connection.setRequestProperty(currentKeyValue.getName(), currentKeyValue.getValue());
		}*/
		
        
        //start query
        connection.connect();
        
		//write encoded body message in OuputStream
		if(request.getBodyEncoded()!=null){
			OutputStream os = new BufferedOutputStream(connection.getOutputStream());
			//OutputStream os = connection.getOutputStream();
			os.write(request.getBodyEncoded()); 
			os.flush();
			os.close();
		}
		
		int response = connection.getResponseCode();
        Log.d("debug", "The responseCode is: " + response);


		//Convert the response InputStream into a string and return it
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

}

