package ca.ulaval.ima.miniprojet.activity;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import ca.ulaval.ima.miniprojet.R;
import ca.ulaval.ima.miniprojet.activity.MainActivity.PlaceholderFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ViewMapActivity extends FragmentActivity{
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewmap);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.map, new PlaceholderFragment()).commit();
		}
		
		//checking if google play services is available on the device.
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		GoogleMap map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
	               .getMap();
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	}
	
}
