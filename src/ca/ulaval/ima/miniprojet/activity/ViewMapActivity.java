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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMapActivity extends FragmentActivity{
	
    // JSON Node names
	private static final String TAG_ID = "id";
    private static final String TAG_USERID = "userID";
    private static final String TAG_DESTINATION = "destination"; 
    private static final String TAG_POSITION = "position"; 
    private static final String TAG_LATITUDE = "lalitude"; //  <----------LOL lalitude
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_PERSONS_COUNT = "persons_count";
    private static final String TAG_MESSAGES = "messages"; 
    private static final String TAG_STATUS = "status"; 
	
	private static String url = "http://relaybit.com:2222/";
	private GoogleMap mMap;

	//Tableau de HashMap. Chaque HashMap est l'information d'une request (username,destination,etc.)
	private ArrayList<HashMap<String, String>> hashmapArray = new ArrayList<HashMap<String,String>>();
	//Tableau de marker. Chaque marker créé est dans ce tableau.
	private ArrayList<Marker> markerArray = new ArrayList<Marker>();
	//HashMap de d'information sur chaque marker. La clé est l'id du marker et le HashMap contient l'information
	//associé au marker identifié.
	private HashMap<String, HashMap<String,String>> markerExtraInfo = new HashMap<String, HashMap<String,String>>();
	JSONArray jSONrequests;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewmap);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.map, new PlaceholderFragment()).commit();
		}
		
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	
		//Vérifie si goodleplay est available
		Log.d("ViewMap", "Is google play available?");
		isGooglePlayAvailable();
		Log.d("ViewMap", "Google play is available");
		
		//initialise la map si elle n'existe pas déjà
		Log.d("ViewMap", "Setting up the map");
		SetUpMapifNeeded();
		Log.d("ViewMap", "The map is set up");

		//initialise le tableau d'information json
		Log.d("ViewMap", "Loading extra marker info");
		loadExtraMarkerInfo();
		Log.d("ViewMap", "Extra marker info is loaded");
		//Crée le tableau de marker
		Log.d("ViewMap", "Generating marker array?");
		generateMarkerArray();
		Log.d("ViewMap", "Marker array is generated");
		
		//Une fois les deux étapes précédantes faites, les marker sont sur la map.
		
		
		//Devrait peutêtre faire ce qui suit dans setupmap?
		
        //Obtient les coordonnées les plus récentes.
		Log.d("ViewMap", "Getting last known location");
		Location lastKnownLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //information pour centré et zoomer sur la position de l'utilisateur
		Log.d("ViewMap", "Centering camera");
		CameraUpdate center=
	            CameraUpdateFactory.newLatLng(new LatLng(lastKnownLocation.getLatitude(),
	            		lastKnownLocation.getLongitude()));
	        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
	        mMap.moveCamera(center);
	        mMap.animateCamera(zoom);

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
	                TextView lbMessage = (TextView) v.findViewById(R.id.txtMessage);
	                
	                // Setting the values
	                lbUsername.setText(markerExtraInfo.get(arg0.getId()).get(TAG_USERID));
	                lbDestination.setText(markerExtraInfo.get(arg0.getId()).get(TAG_DESTINATION));
	                lbPassengers.setText(markerExtraInfo.get(arg0.getId()).get(TAG_PERSONS_COUNT));
	                lbMessage.setText(markerExtraInfo.get(arg0.getId()).get(TAG_MESSAGES));; //problème d'affichage si message trop long

	                // Returning the view containing InfoWindow contents
	                return v;
	            }

	        });
	        
	        //Comportement d'un click sur une info window.
	        //Devra passer l'info du marquer à une nouvelle activité
            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() { 
            	    @Override
	  			    public void onInfoWindowClick(Marker marker){
            	    	Log.d("ViewMap - OnInfoWindowClick", "InfoWindow has been clicked");
	  			      //Intent nextScreen = new Intent(MapsActivity.this,EventActivity.class);
	  			        //mettre les ExtraMarkerInfo dans les extras pour les passer à la fenêtre suivante.
	  			      	//nextScreen.putExtra("userId", "" + userId);
	  			        //nextScreen.putExtra("eventId", "" + eventId);

	  			        //startActivityForResult(nextScreen, 0);
	  			    }
	  			  });
	        
	} //fin de onCreate
	        
	private void loadExtraMarkerInfo(){
		//pas certain que comment aller chercher l'url avec le code du TP.
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
					Log.d("JSON STRING ANVEC REQUEST", "{\"requests\":" + s +"}"); // peut-être touver façon moins broche à foin
					JSONObject inData = new JSONObject("{\"requests\":" + s +"}");
					Log.d("ViewMap - RequestsJSON", "Data of the list" + inData);
					
					JSONArray requests = inData.getJSONArray("requests");
					for (int i=0;i<requests.length();i++){
						JSONObject obj = requests.getJSONObject(i);
						String id = obj.getString(TAG_ID);
						String userId = obj.getString(TAG_USERID);
						String destination = obj.getString(TAG_DESTINATION);
						String latitude = obj.getJSONObject(TAG_POSITION).getString(TAG_LATITUDE);
						String longitude = obj.getJSONObject(TAG_POSITION).getString(TAG_LONGITUDE);
						String persons_count = obj.getString(TAG_PERSONS_COUNT);
						String messages = obj.getString(TAG_MESSAGES);
						String status = obj.getString(TAG_STATUS);

						Log.d("ViewMap - LoadMarkerExtraInfo", "JSONObj "+obj);
						
			            // temp hashmap
			            HashMap<String, String> tempHMap = new HashMap<String, String>();
			            
			            tempHMap.put(TAG_ID, id);
			            tempHMap.put(TAG_USERID, userId);
			            tempHMap.put(TAG_DESTINATION, destination);
			            tempHMap.put(TAG_LATITUDE, latitude);
			            tempHMap.put(TAG_LONGITUDE, longitude);
			            tempHMap.put(TAG_PERSONS_COUNT, persons_count);
			            tempHMap.put(TAG_MESSAGES, messages);
			            tempHMap.put(TAG_STATUS, status);
						
			            //Array qui contient tous les objets JSON sous forme de HashMap
			            
						hashmapArray.add(tempHMap);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("ViewMap - JSON parser", "json parsing error : " + e.toString());
				}
				Log.d("ViewMap - LoadMarkerExtraInfo", "hashmapArray" +hashmapArray.toString());
			}
		};
		loadRequest.execute(connection);
	}

 
	
	//Fait l'array de marker et le hashmap de markerExtraInfo associé à chaque marker de l'array.
	//L'association est faite à partir de l'id du marker.
	private void generateMarkerArray() {
		Log.d("ViewMap - generateMarkerArray", "Beginning the marker array generation");
		if (hashmapArray.isEmpty()) {
			Log.d("ViewMap - generateMarkerArray", "hashmapArray is empty!");
		}
		Iterator<HashMap<String,String>> it = hashmapArray.iterator();
		while (it.hasNext()) {
			Log.d("ViewMap - generateMarkerArray", "Adding a marker to the array");
			HashMap<String,String> obj = it.next();
			Marker m = mMap.addMarker(new MarkerOptions()
					   .title(obj.get(TAG_USERID))
					   .position(new LatLng(Double.parseDouble(obj.get(TAG_LATITUDE)),
							   				Double.parseDouble(obj.get(TAG_LONGITUDE)))));
			
							   				
				   //L'info de UN marker (contenu dans un HashMap<String,String>, représentée ici par obj) 
				   //est associée à la clé du marker  			   				
				   markerExtraInfo.put(m.getId(), obj);
				   markerArray.add(m);					
		}
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
