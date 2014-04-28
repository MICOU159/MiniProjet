package ca.ulaval.ima.miniprojet.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.ulaval.ima.miniprojet.R;
import ca.ulaval.ima.miniprojet.activity.MainActivity.PlaceholderFragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class ViewMapActivity extends FragmentActivity{
	
	private GoogleMap mMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewmap);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.map, new PlaceholderFragment()).commit();
		}
		
		isGooglePlayAvailable();
		//initialise la map si elle n'existe pas déjà
		SetUpMapifNeeded();
		
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        //Obtient les coordonnées les plus récentes.
        Location lastKnownLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
        
		//checking if google play services is available on the device.
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		//GoogleMap map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
	     //          .getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
		mMarker.setTitle("Quebec");
	    CameraUpdate center=
	            CameraUpdateFactory.newLatLng(new LatLng(lastKnownLocation.getLatitude(),
	            		lastKnownLocation.getLongitude()));
	        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

	        mMap.moveCamera(center);
	        mMap.animateCamera(zoom);
	}
	
	private void SetUpMapifNeeded() {
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				//La map existe
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
