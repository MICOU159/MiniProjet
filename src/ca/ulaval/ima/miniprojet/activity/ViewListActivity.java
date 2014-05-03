package ca.ulaval.ima.miniprojet.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.miniprojet.R;
import ca.ulaval.ima.miniprojet.R.id;
import ca.ulaval.ima.miniprojet.R.layout;
import ca.ulaval.ima.miniprojet.R.menu;
import ca.ulaval.ima.miniprojet.model.RequestModel;
import ca.ulaval.ima.miniprojet.util.ASyncURLRequest;
import ca.ulaval.ima.miniprojet.util.HttpCustomRequest;
import ca.ulaval.ima.miniprojet.util.Util;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class ViewListActivity extends ListActivity {
	private RequestAdapter mAdapter;
	private final static int VIEW_LIST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_list);

		if (savedInstanceState == null) {
			//created for the first time...
		}
		
		mAdapter = new RequestAdapter(this, new ArrayList<RequestModel>());
		getListView().setAdapter(mAdapter);
		
		loadList();
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RequestModel selectedRequest = (RequestModel)arg0.getItemAtPosition(arg2);
				Intent i = new Intent(ViewListActivity.this, AcceptRequest.class);
				i.putExtra("request_info", selectedRequest);
    	    	startActivityForResult(i, VIEW_LIST);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == VIEW_LIST && resultCode== RESULT_OK)
		{
			Toast.makeText( getApplicationContext(),"User Notified",Toast.LENGTH_SHORT).show();
			this.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_list, menu);
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
	
	private void loadList(){
		String urlToLoad = Util.getFormatedAPIURL(getApplicationContext(), "requests/");
		HttpCustomRequest request = new HttpCustomRequest(this,urlToLoad);
		request.setMethod("GET");
		ASyncURLRequest loadRequest = new ASyncURLRequest(){
			@Override
			protected void onPostExecute(String s){

				if(s==null){
					Log.d("ViewList", "the value returned is null");
					return;
				}
				
				try {
					JSONObject inData = new JSONObject(s);
					Log.d("ViewList", "data " + inData);
					
					JSONArray lJsonArrayPromo = inData.getJSONArray("requests");//<-------
					for (int i=0;i<lJsonArrayPromo.length();i++){
						JSONObject obj = lJsonArrayPromo.getJSONObject(i);
						Log.d("ViewList", "OBJECT "+obj);
						mAdapter.addRequest(obj);
					}
					mAdapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("carretail", "onPostExecute json error : " + e);
				}			
			}
		};
		loadRequest.execute(request);
	}
	
	//----------------------------------Adapter--------------------------------
	static public class RequestAdapter extends ArrayAdapter<RequestModel>{

		private ArrayList<RequestModel> mRequestsList;
		private Activity mActivity;
		
		public RequestAdapter(Activity inActivity, ArrayList<RequestModel> inList){
			super(inActivity, R.layout.list_cell_request, inList);
			this.mActivity = inActivity;
			mRequestsList = inList;
			
		}
		
		public void addRequest(JSONObject inJson){
			RequestModel outItem = new RequestModel(inJson);
			mRequestsList.add(outItem);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mRequestsList.size();
		}

		@Override
		public RequestModel getItem(int arg0) {
			// TODO Auto-generated method stub
			return mRequestsList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		static class ViewHolder {
			public TextView username;
			public TextView destination;
			public ImageView status;
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			View childView = arg1;
			if(childView == null || childView.getTag() == null){

				childView = mActivity.getLayoutInflater().inflate(R.layout.list_cell_request, null);
				
				ViewHolder viewHolder = new ViewHolder();
				
				viewHolder.username = (TextView) childView.findViewById(R.id.username);
				viewHolder.destination = (TextView) childView.findViewById(R.id.destination);
				viewHolder.status = (ImageView) childView.findViewById(R.id.status_icon);
				
				childView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) childView.getTag();
			holder.username.setText(mRequestsList.get(arg0).getmUsername());
			holder.destination.setText(mRequestsList.get(arg0).getmDestination());
			
			int statusCode = mRequestsList.get(arg0).getmStatus();
			if(statusCode == 0){
				holder.status.setImageResource(R.drawable.circle_red);
			}
			else{
				holder.status.setImageResource(R.drawable.circle_green);
			}
			
			return childView;
		}
		
	}

}
