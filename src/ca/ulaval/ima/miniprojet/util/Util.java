package ca.ulaval.ima.miniprojet.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import ca.ulaval.ima.miniprojet.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;



public class Util {
	public static String getFormatedAPIURL(Context inContext,String inMethod,Map<String, String> inParams){
		String API_BASE = inContext.getResources().getString(R.string.API_BASE);
		String API_format = inContext.getResources().getString(R.string.API_FORMAT);
		if(inParams==null){
			return API_BASE+API_format+inMethod;
		}else{
			return API_BASE+API_format+inMethod;
		}
	}
	public static String getFormatedAPIURL(Context inContext,String inMethod){
		return Util.getFormatedAPIURL(inContext, inMethod,null);
	}
	
	
	public interface BitmapDownloadListener
	{
		void onBitmapDownloaded(Bitmap bitmap);
	}
	
	
	
	public static void downloadBitmap(final String url, final BitmapDownloadListener listener)
	{
		new AsyncTask<Void, Void, Bitmap>()
		{
			@Override
			protected Bitmap doInBackground(Void... params) {
				try
				{
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					if (downloadFile(url, stream))
					{
						byte[] buffer = stream.toByteArray();
						return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
					}					
				}
				catch (Throwable e)
				{
					
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Bitmap bitmap)
			{
				if (bitmap != null)
				{
					listener.onBitmapDownloaded(bitmap);
				}
			}
			
		}.execute();
	}	
	
	public static boolean downloadFile(String url, OutputStream output)
	{
	    HttpURLConnection connection = null;
	    InputStream input = null;
	    try
	    {
	        connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setRequestProperty("Connection", "close");
	        connection.setDoInput(true);
	        connection.connect();
	        if (connection.getResponseCode() == 200)
	        {
		        input = connection.getInputStream();
	        	byte[] buffer = new byte[4096];
	        	int bytesRead = 0;
	        	while ((bytesRead = input.read(buffer)) > 0)
	        	{
	        		output.write(buffer, 0, bytesRead);
	        	}
	        	return true;
	        }
	        else
	        {
	        	Log.e("CarRetailer", "Error " + String.valueOf(connection.getResponseCode()) + " when access resource " + url);
	        }
	    }
	    catch (IOException e)
	    {
	    	Log.wtf("CarRetailer", e);
	    }
	    finally
	    {
	    	closeQuietly(input);
	    	if (connection != null)
	    	{
	    		connection.disconnect();
	    	}
	    }
	    
	    return false;
	}
	
	static void closeQuietly(Closeable obj)
	{
		try
		{
			obj.close();
		}
		catch (Exception e)
		{
			
		}
	}
	

}
