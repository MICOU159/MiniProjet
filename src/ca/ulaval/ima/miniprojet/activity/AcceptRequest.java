package ca.ulaval.ima.miniprojet.activity;

import ca.ulaval.ima.miniprojet.R;
import android.app.Activity;
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
		
        View v = getLayoutInflater().inflate(R.layout.fragment_accept_request, null);
		
		TextView tvUsername = (TextView) v.findViewById(R.id.tvReqUsername);
		TextView tvPassengers = (TextView) v.findViewById(R.id.tvReqPassengers);
        TextView tvDestination = (TextView) v.findViewById(R.id.tvReqDestination);
        TextView tvAddress = (TextView) v.findViewById(R.id.tvReqAddress);
        
        //-------=============À complété=============------------------------------
        
        
        // Setting the values
       // lbUsername.setText(markerExtraInfo.get(arg0.getId()).get(TAG_USERID));
       // lbDestination.setText(markerExtraInfo.get(arg0.getId()).get(TAG_DESTINATION));
       // lbPassengers.setText(markerExtraInfo.get(arg0.getId()).get(TAG_PERSONS_COUNT));
       // lbMessage.setText(markerExtraInfo.get(arg0.getId()).get(TAG_MESSAGES));;
		
		
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

