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
import android.widget.Button;

public class MainActivity extends Activity {
	
	static final int REQUEST_LOGIN = 1;
	private boolean loggedIn = false;
	static final String CURRENT_USER = "CURRENT_USER";
	static final String REQUEST_SELECTED = "REQUEST_SELECTED";
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
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(CURRENT_USER, currentUser);
		super.onSaveInstanceState(outState);
	}
	

	@Override
	//The system calls onRestoreInstanceState() only if there is a saved state to restore, 
	//so no need to check whether the Bundle is null
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentUser = savedInstanceState.getParcelable(CURRENT_USER);
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
			
			//To disable buttons
			/*Button btn1 = (Button)rootView.findViewById(R.id.makeRequest_btn);
			Button btn2 = (Button)rootView.findViewById(R.id.viewMap_btn);
			Button btn3 = (Button)rootView.findViewById(R.id.viewList_btn);
			
			if (currentUser == null) {
				btn1.setEnabled(false);
				btn2.setEnabled(false);
				btn3.setEnabled(false);
			} else {
				btn1.setEnabled(true);
				btn2.setEnabled(true);
				btn3.setEnabled(true);
				
			}*/
			
			return rootView;
		}

		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_LOGIN && resultCode==RESULT_OK)
		{
			this.currentUser = data.getExtras().getParcelable(CURRENT_USER);
			this.loggedIn = true;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if (currentUser != null || loggedIn == true) {
			Button btn_request = (Button) findViewById(R.id.makeRequest_btn);
			Button btn_map = (Button) findViewById(R.id.viewMap_btn);
			Button btn_list = (Button) findViewById(R.id.viewList_btn);
			Button btn_login = (Button) findViewById(R.id.login_btn);
			btn_request.setEnabled(true);
			btn_map.setEnabled(true);
			btn_list.setEnabled(true);
			btn_login.setEnabled(false);
			loggedIn = true;
		}
		super.onResume();
	}

	public void openLoginForm(View view){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}
	
	public void openRequestForm(View view){
		Intent intent = new Intent(this, MakeRequestActivity.class);
		intent.putExtra(CURRENT_USER, this.currentUser);
		startActivity(intent);
	}
	
	public void openMap(View view){
		Intent intent = new Intent(MainActivity.this, ViewMapActivity.class);
		startActivity(intent);
	}
	
	public void openList(View view){
		Intent intent = new Intent(MainActivity.this, ViewListActivity.class);
		startActivity(intent);
	}

}
