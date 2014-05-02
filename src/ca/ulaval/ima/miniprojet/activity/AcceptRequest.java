package ca.ulaval.ima.miniprojet.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ca.ulaval.ima.miniprojet.R;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Fragment;

public class AcceptRequest extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_accept_request);

		if (savedInstanceState != null) {
			getFragmentManager().beginTransaction()
					.add(R.id.map, new PlaceholderFragment()).commit();
		}
        
        String username = getIntent().getExtras().getString("username");
        String destination = getIntent().getExtras().getString("destination");
        String passengers = getIntent().getExtras().getString("persons_count");
        
        String latitude = getIntent().getExtras().getString("latitude");
        String longitude = getIntent().getExtras().getString("longitude");
        
		TextView tvUsername = (TextView) findViewById(R.id.tvReqUsername);
		TextView tvPassengers = (TextView) findViewById(R.id.tvReqPassengers);
        TextView tvDestination = (TextView) findViewById(R.id.tvReqDestination);
        TextView tvAddress = (TextView) findViewById(R.id.tvReqAddress);
       
        tvUsername.setText(username);
        tvPassengers.setText(passengers);
        tvDestination.setText(destination);
        
        Geocoder geocoder = new Geocoder(this, Locale.CANADA);
        
        try{
        	List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude),Double.parseDouble(longitude), 1);
        	
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

