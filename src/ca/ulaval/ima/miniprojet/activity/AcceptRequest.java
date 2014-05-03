package ca.ulaval.ima.miniprojet.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import ca.ulaval.ima.miniprojet.R;
import ca.ulaval.ima.miniprojet.model.RequestModel;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.Fragment;
import android.content.Intent;

public class AcceptRequest extends Activity{
	private final static String preUrl = "http://relaybit.com:2222/requests/";
	private final static String postUrlStatus = "/pick";
	private final static String postUrlMessage = "/addMessage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_accept_request);

		if (savedInstanceState != null) {
			getFragmentManager().beginTransaction()
					.add(R.id.map, new PlaceholderFragment()).commit();
		}
		
		RequestModel reqModel = getIntent().getExtras().getParcelable("request_info");
		
        String username = reqModel.getmUsername();
        String destination = reqModel.getmDestination();
        int passengers = reqModel.getmPersonsCount();
        
        Double latitude = reqModel.getmPosition().getmLatitude();
        Double longitude = reqModel.getmPosition().getmLongitude();
        
		TextView tvUsername = (TextView) findViewById(R.id.tvReqUsername);
		TextView tvPassengers = (TextView) findViewById(R.id.tvReqPassengers);
        TextView tvDestination = (TextView) findViewById(R.id.tvReqDestination);
        TextView tvAddress = (TextView) findViewById(R.id.tvReqAddress);
       
        tvUsername.setText(username);
        tvPassengers.setText(""+passengers);
        tvDestination.setText(destination);
        
        Geocoder geocoder = new Geocoder(this, Locale.CANADA);
        
        try{
        	List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
        	
        	if(addresses != null) {
        		   Address returnedAddress = addresses.get(0);
        		   StringBuilder strReturnedAddress = new StringBuilder("");
        		   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
        			   strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
        		   }
        		   		tvAddress.setText(strReturnedAddress.toString());
        	}
        		  else{
        			  tvAddress.setText("No Address returned!");
        		  }
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        	tvAddress.setText("Cannot get an address from the GPS localisation!");
        }
        
	}
	
	public void notifyButton(View v) {

		postMessage("bannnnane");
		postNotification();
    	Log.d("AcceptRequest", "Notifying of success");
    	setResult(RESULT_OK);
    	finish();
	}
	
	private void postMessage(String message){
	    
		final String msg = "\""+message+"\"";
		Thread t = new Thread() {
			public void run() {
			    // Create a new HttpClient and Post Header
				RequestModel reqModel = getIntent().getExtras().getParcelable("request_info");
				Looper.prepare();
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(preUrl+reqModel.getId()+postUrlMessage);
			    HttpResponse response;


			    try {
					StringEntity se = new StringEntity("{\"message\":"+msg+"}");
			        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			        httppost.setEntity(se);
			        response = httpclient.execute(httppost);
			    	Log.d("Notifying", "Trying to contact server");
			    	
			        if(response!=null){
		                InputStream in = response.getEntity().getContent();
				    	Log.d("Notifying", "Reponse : " + in.toString());
		            }

			    } catch (ClientProtocolException e) {
			        // TODO Auto-generated catch block
			    } catch (IOException e) {
			        // TODO Auto-generated catch block
			    }
			    Looper.loop();
			}
		};
		t.start();
	}
	
	private void postNotification(){
		Thread t = new Thread() {
			public void run() {
			    // Create a new HttpClient and Post Header
				RequestModel reqModel = getIntent().getExtras().getParcelable("request_info");
				Looper.prepare();
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(preUrl+reqModel.getId()+postUrlStatus);
			    HttpResponse response;

			    try {
					StringEntity se = new StringEntity("{\"status\": 1}");
			        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			        httppost.setEntity(se);
			        response = httpclient.execute(httppost);
			    	Log.d("Notifying", "Trying to contact server");
			    	
			        if(response!=null){
		                InputStream in = response.getEntity().getContent();
				    	Log.d("Notifying", "Reponse : " + in.toString());
		            }

			    } catch (ClientProtocolException e) {
			        // TODO Auto-generated catch block
			    } catch (IOException e) {
			        // TODO Auto-generated catch block
			    }
			    Looper.loop();
			}
		};
		t.start();
	}
	
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_accept_request,
					container, false);
			return rootView;
		}
	}
}

