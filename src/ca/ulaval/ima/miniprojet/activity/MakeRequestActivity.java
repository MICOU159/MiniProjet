package ca.ulaval.ima.miniprojet.activity;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.miniprojet.R;
import ca.ulaval.ima.miniprojet.activity.LoginActivity.UserLoginTask;
import ca.ulaval.ima.miniprojet.model.PositionModel;
import ca.ulaval.ima.miniprojet.model.RequestModel;
import ca.ulaval.ima.miniprojet.model.UserModel;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MakeRequestActivity extends Activity {
	private UserModel currentUser = null;
	private RequestModel request = null;
	private SendRequestTask mRequestTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_request);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		this.currentUser = this.getIntent().getExtras().getParcelable(MainActivity.CURRENT_USER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_request, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_make_request,
					container, false);
			return rootView;
		}
	}
	
	public void summit(View view){
		//recupérer val
		EditText edit= (EditText) findViewById(R.id.inputDestination);
		String destination = edit.getText().toString();
		
		edit= (EditText) findViewById(R.id.inputPersonsCount);
		String personsCountText = edit.getText().toString();
		
		edit= (EditText) findViewById(R.id.inputMessage);
		String message = edit.getText().toString();
		
		if(!destination.isEmpty() && !personsCountText.isEmpty()){
			
			//create request
			String username = this.currentUser.getmUsername();
			PositionModel position = this.getPosition();
			int personsCount = Integer.parseInt(personsCountText);
			
			request = new RequestModel(username, position, destination, personsCount, message);
			
			//send request...
			mRequestTask = new SendRequestTask();
			String url = "http://relaybit.com:2222/users";
			mRequestTask.execute(url);
		}
	}
	
	private PositionModel getPosition(){
		
		PositionModel pos = null;
		
		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if(lastKnownLocation !=null){
        	double latitude = lastKnownLocation.getLatitude(); 
            double longitude = lastKnownLocation.getLongitude();
            pos = new PositionModel(latitude, longitude);
        }
     
		return pos;
	}
	
	/**
	 * Represents an asynchronous task
	 */
	public class SendRequestTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			 try {
	                return downloadUrl(urls[0]);
	            } catch (IOException e) {
	                return "Unable to retrieve web page. URL may be invalid.";
	            }
		}
		
		@Override
		protected void onPostExecute(String response) {
			mRequestTask = null;
			finish(); //<-------test

		}
		
		private String downloadUrl(String myurl) throws IOException {
		    InputStream is = null;
		        
		    try {
		        URL url = new URL(myurl);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        
		        //pour un GET
		        /*conn.setRequestMethod("GET");
		        conn.setDoInput(true);*/
		        
		        //pour un POST
		        conn.setDoOutput(true);
		        conn.setRequestProperty("Content-Type", "application/json");
		        conn.setRequestProperty("Accept", "application/json");
		        conn.setRequestMethod("POST");
		        conn.setChunkedStreamingMode(0);


		     // Starts the query
		        conn.connect();
		        
		        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
		        writeStream(out);
		        
		        Log.d("debug", "Asking for responseCode");
		        int response = conn.getResponseCode();
		        Log.d("debug", "The responseCode is: " + response);
		        is = conn.getInputStream();
		        
		        
		        StringBuilder sb = new StringBuilder();
				BufferedReader reader=null;
				try {
					reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				} catch (Exception e1) {
					reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
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
							conn.getInputStream().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Log.d("asyncHttp","answer"+ sb.toString());
				}
				
				return sb.toString();
		        
		    // Makes sure that the InputStream is closed after the app is
		    // finished using it.
		    } finally {
		        if (is != null) {
		            is.close();
		        } 
		    }
		}
		
		private void writeStream(OutputStream out) throws IOException {
			// TODO Auto-generated method stub
			
			
			String body = request.toString();
			
			Log.d("LoginActivity", "Trying to send string" + body);
			
			byte[] encodedValue = null;
			try {
				encodedValue= body.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(encodedValue != null){
				try {
					Log.d("LoginActivity", "encoded value isnt null!" + encodedValue.toString());
					out.write(encodedValue);
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("LoginActivity", "encoded value is null!");
					e.printStackTrace();
				} finally{
					/*if (out != null) {
			            out.close();
			        }*/ 
				}
			}
			
		}
		
	}

}
