package ca.ulaval.ima.miniprojet.activity;

import ca.ulaval.ima.miniprojet.R;
import ca.ulaval.ima.miniprojet.model.UserModel;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	
	static final int REQUEST_LOGIN = 1;
	private boolean loggedIn = false;
	static final String CURRENT_USER = "CURRENT_USER";
	private UserModel currentUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void openLoginForm(View view){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		//startActivityForResult(intent, REQUEST_LOGIN);
	}
	
	public void openRequestForm(View view){
		Intent intent = new Intent(this, MakeRequestActivity.class);
		intent.putExtra(CURRENT_USER, this.currentUser);
		startActivity(intent);
	}
	
	public void openMap(View view){
		Intent intent = new Intent(this, ViewMapActivity.class);
		startActivity(intent);
	}
	
	public void openList(View view){
		//...
	}

}
