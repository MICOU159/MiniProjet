package ca.ulaval.ima.miniprojet.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import ca.ulaval.ima.miniprojet.model.PositionModel;
import ca.ulaval.ima.miniprojet.model.RequestModel;
import ca.ulaval.ima.miniprojet.util.ASyncURLRequest;
import ca.ulaval.ima.miniprojet.util.HttpCustomRequest;
import ca.ulaval.ima.miniprojet.util.Util;
import ca.ulaval.ima.miniprojet.R;
import ca.ulaval.ima.miniprojet.activity.MainActivity.PlaceholderFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMapActivity extends FragmentActivity{
	
	private static String url = "http://relaybit.com:2222/";
	private GoogleMap mMap;
	Location lastKnownLocation = new Location("dummy");
	JSONArray jSONrequests;

	
	private ArrayList<RequestModel> mRequestModelArray = new ArrayList<RequestModel>();
	private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();;
	private HashMap<String, RequestModel> mMarkerExtraInfo = new HashMap<String, RequestModel>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewmap);

		if (savedInstanceState != null) {
			getFragmentManager().beginTransaction()
					.add(R.id.map, new PlaceholderFragment()).commit();
		}
	
		//Vérifie si goodleplay est available
		isGooglePlayAvailable();
		
		//initialise la map si elle n'existe pas déjà
		SetUpMapifNeeded();

		//initialise les marqueurs sur la carte
		loadMapMarkers();
		
        //Obtient les coordonnées les plus récentes.
		Log.d("ViewMap", "Getting last known location");
		try {
	        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        LocationListener mlocListener = new MyLocationListener();
	        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
			
			lastKnownLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			Log.d("ViewMap", "Centering camera");
	        //information pour centré et zoomer sur la position de l'utilisateur
			CameraUpdate center=
		            CameraUpdateFactory.newLatLng(new LatLng(lastKnownLocation.getLatitude(),
		            		lastKnownLocation.getLongitude()));
		        CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);
		        mMap.moveCamera(center);
		        mMap.animateCamera(zoom);
		} catch  (NullPointerException e) {
			Log.d("ViewMap", "Last known GPS location is null - Setting up fake location");
			lastKnownLocation.setAltitude(71);
			lastKnownLocation.setLongitude(-41);
			CameraUpdate center=
		            CameraUpdateFactory.newLatLng(new LatLng(lastKnownLocation.getLatitude(),
		            		lastKnownLocation.getLongitude()));
		        CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);
		        mMap.moveCamera(center);
		        mMap.animateCamera(zoom);
		}

	        //Setting a custom info window adapter for the google map
	        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {

	            // Use default InfoWindow frame
	            @Override
	            public View getInfoWindow(Marker arg0) {
	                return null;
	            }

	            // Defines the contents of the InfoWindow
	            @Override
	            public View getInfoContents(Marker arg0) {

	            	Log.d("ViewMap - getInfoContent", "Setting values to InfoWindow");
	                // Getting view from the layout file info_window_layout
	                View v = getLayoutInflater().inflate(R.layout.custom_map_marker, null);
	        		
	                // Getting reference to the TextViews
	                TextView lbUsername = (TextView) v.findViewById(R.id.txtUsername);
	                TextView lbDestination = (TextView) v.findViewById(R.id.txtDestination);
	                TextView lbPassengers = (TextView) v.findViewById(R.id.txtPassengers);
	                //TextView lbMessage = (TextView) v.findViewById(R.id.txtMessage);
	                
	                lbUsername.setText(mMarkerExtraInfo.get(arg0.getId()).getmUsername());
	                lbDestination.setText(mMarkerExtraInfo.get(arg0.getId()).getmDestination());
	                lbPassengers.setText("" + mMarkerExtraInfo.get(arg0.getId()).getmPersonsCount());
	                //lbMessage.setText(mMarkerExtraInfo.get(arg0.getId()).);

	                // Returning the view containing InfoWindow contents
	                return v;
	            }

	        });
	        
	        //Comportement d'un click sur une info window.
            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() { 
            	    @Override
	  			    public void onInfoWindowClick(Marker marker){
            	    	Log.d("ViewMap - OnInfoWindowClick", "InfoWindow has been clicked");

	  			        //Passer les infos du marqueur à la fenêtre suivante.
            			Intent intent = new Intent(ViewMapActivity.this, AcceptRequest.class);
            			RequestModel reqModel = mMarkerExtraInfo.get(marker.getId());
            			intent.putExtra("MarkerInfo", reqModel);
            			Log.d("ViewMap - OnInfoWindowClick", "Starting the activity");
            			startActivity(intent);
	  			    }
	  			  });
	        
	}//fin de onCreate
	        
		private void loadMapMarkers(){
		Log.d("ViewMap", "Setting up for HttpCustomRequest");
		HttpCustomRequest connection = new HttpCustomRequest(this, url + "requests/");
		ASyncURLRequest loadRequest = new ASyncURLRequest(){
			@Override
			protected void onPostExecute(String s){
				if(s==null){
					Log.d("ViewMap - RequestsJSON", "The requests list is null");
					return;
				}
				
				try {
					JSONObject inData = new JSONObject(s);
					Log.d("ViewMap - RequestsJSON", "OBJECT" + inData);
					
					JSONArray lJsonArrayPromo = inData.getJSONArray("requests");
					Log.d("ViewMap - RequestsJSON", "LE JSONOBJECT CONVERTI EN JSONARRAY" + lJsonArrayPromo.toString());
					for (int i=0;i< lJsonArrayPromo.length();i++){

						JSONObject obj = lJsonArrayPromo.getJSONObject(i);
					    Log.d("ViewList", "OBJECT "+obj);
						RequestModel reqModel = new RequestModel(obj);

					    Log.d("ViewList", "OBJECT Adding to Array");
						mRequestModelArray.add(reqModel);
					    Log.d("ViewList", "OBJECT Added to Array");
						
					}
					Log.d("ViewMap", "Made it to iterator");
					Iterator<RequestModel> it = mRequestModelArray.iterator();
					while (it.hasNext()) {
						RequestModel obj = it.next();
						Log.d("ViewMap", "The value of latitude:" + obj.getmPosition().getmLatitude());
						Log.d("ViewMap", "The value of longitude:" + obj.getmPosition().getmLongitude());
						Marker m = mMap.addMarker(new MarkerOptions()
						.title(obj.getmUsername())
						.position(new LatLng(obj.getmPosition().getmLatitude(), obj.getmPosition().getmLongitude())));
						
						mMarkerExtraInfo.put(m.getId(), obj);
						mMarkerArray.add(m);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		loadRequest.execute(connection);
	}


	private void SetUpMapifNeeded() {
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			if (mMap != null) {
				//La map existe
				Log.d("ViewMap - setupmapifneeded", "Map already in memory");
			}
		}
	}
	
	private void isGooglePlayAvailable(){
		int av = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (av != ConnectionResult.SUCCESS) {
			Toast.makeText( getApplicationContext(),"" +"Google Play Services Unavailable\n "+"" +
					"This application requires Google Play Services to work",Toast.LENGTH_SHORT).show();
			this.finish();
		} else {
			//Google Play Services are available
			Log.d("ViewMap - isGooglePlayAvailable", "Googleplay is avaiable");
		}
	}
	
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc){
                loc.getLatitude();
                loc.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {
//              Toast.makeText( getApplicationContext(),"“Gps Disabled",Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onProviderEnabled(String provider){
//              Toast.makeText( getApplicationContext(),"Gps Enabled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){
        }
    }
	
}
