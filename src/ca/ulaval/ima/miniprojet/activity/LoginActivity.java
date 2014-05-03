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

import ca.ulaval.ima.miniprojet.model.UserModel;
import ca.ulaval.ima.miniprojet.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	// private AsyncCustomURLRequest mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	//SharedPreferences
	public static final String USER_PREFERENCES = "UserPrefs";
	SharedPreferences settings; 
	SharedPreferences.Editor prefEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		setupActionBar();

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		settings = getSharedPreferences(USER_PREFERENCES,MODE_PRIVATE);
	    mEmailView.setText(settings.getString("username", "Default"));
		

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptRegister();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);

			// -----------------------------------
			mAuthTask = new UserLoginTask() {
				@Override
				protected void onPostExecute(String response) {
					mAuthTask = null;
					showProgress(false);

					if (response != null) {
						Log.d("UserLoginTask", "login succeed");
						
						//return currentUser
						UserModel user = new UserModel(mEmail, mPassword);
						Intent intent = new Intent();
						intent.putExtra(MainActivity.CURRENT_USER, user);
						setResult(RESULT_OK, intent);
						
						  settings = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
						  prefEditor = settings.edit();

						  prefEditor.putString("username", mEmail);
						  prefEditor.commit();
						
						finish();
						
					} else {
						mPasswordView
								.setError(getString(R.string.error_incorrect_password));
						mPasswordView.requestFocus();
					}

				}
			};
			String url = "http://relaybit.com:2222/login";
			mAuthTask.execute(url);
			// ------------------------------

		}
	}
	

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);

			// ------------------------------
			// En utilisant HttpCustomRequest <----fonctionne pas

			/*
			 * String urlToLoad =
			 * Util.getFormatedAPIURL(getApplicationContext(), "users");
			 * HttpCustomRequest request = new
			 * HttpCustomRequest(this,urlToLoad);
			 * 
			 * //set http method request.setMethod("POST");
			 * 
			 * //set headers List<NameValuePair> headers = new
			 * ArrayList<NameValuePair>(); headers.add(new
			 * BasicNameValuePair("Content-Type", "application/json"));
			 * request.setHeaders(headers);
			 * 
			 * //set body JSONObject json = new JSONObject(); try {
			 * json.put("email", "test"); json.put("password", "test"); } catch
			 * (JSONException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 * 
			 * String reqString = json.toString(); request.setBody(reqString);
			 * 
			 * mAuthTask = new AsyncCustomURLRequest(){
			 * 
			 * @Override protected void onPostExecute(String response) {
			 * mAuthTask = null; showProgress(false); finish(); //<-------test
			 * 
			 * int responseCode = this.getResponseCode(); if (responseCode ==
			 * 200) { // success //.. Log.d("UserLoginTask","login succeed");
			 * finish(); } else { mPasswordView
			 * .setError(getString(R.string.error_incorrect_password));
			 * mPasswordView.requestFocus(); }
			 * 
			 * }
			 * 
			 * @Override protected void onCancelled() { mAuthTask = null;
			 * showProgress(false); } };
			 * 
			 * mAuthTask.execute(request);
			 */

			// -----------------------------------
			mAuthTask = new UserLoginTask(){
				@Override
				protected void onPostExecute(String response) {
					mAuthTask = null;
					showProgress(false);

					if (response != null) {
						Log.d("UserLoginTask", "register succeed");
						
						//return currentUser
						UserModel user = new UserModel(mEmail, mPassword);
						Intent intent = new Intent();
						intent.putExtra(MainActivity.CURRENT_USER, user);
						setResult(RESULT_OK, intent);
						
						  settings = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
						  prefEditor = settings.edit();

						  prefEditor.putString("username", mEmail);
						  prefEditor.commit();
						
						finish();
						
					} else {
						mPasswordView
								.setError(getString(R.string.error_incorrect_password));
						mPasswordView.requestFocus();
					}
				}
			};
			String url = "http://relaybit.com:2222/users";
			mAuthTask.execute(url);
			// ------------------------------

		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				Log.d("Exception: ", "Unable to retrieve web page. URL may be invalid.");
				return null;
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}

		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private String downloadUrl(String myurl) throws IOException {
			InputStream is = null;
			// Only display the first 500 characters of the retrieved
			// web page content.
			// int len = 500;

			try {
				URL url = new URL(myurl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);

				// pour un GET
				/*
				 * conn.setRequestMethod("GET"); conn.setDoInput(true);
				 */

				// pour un POST
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/json");
				//conn.setRequestProperty("Accept", "application/json");
				conn.setRequestMethod("POST");
				conn.setChunkedStreamingMode(0);

				// Starts the query
				conn.connect();

				OutputStream out = new BufferedOutputStream(
						conn.getOutputStream());
				writeStream(out);

				Log.d("debug", "Asking for responseCode");
				int response = conn.getResponseCode();
				Log.d("debug", "The responseCode is: " + response);
				//is = conn.getInputStream();

				// Convert the InputStream into a string
				/*
				 * String contentAsString = readIt(is, len); Log.d("debug",
				 * "The responseStream is: " + contentAsString); return
				 * contentAsString;
				 */

				StringBuilder sb = new StringBuilder();
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(
							conn.getInputStream()));
				} catch (Exception e1) {
					reader = new BufferedReader(new InputStreamReader(
							conn.getErrorStream()));
					Log.d("asyncHttp",
							"bufferedreader :" + e1 + " " + reader.readLine());
					e1.printStackTrace();
				}

				if (reader != null) {

					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
						}
					} catch (Exception e) {
						Log.d("asyncHttp", "line :" + e);
						e.printStackTrace();
					} finally {
						try {
							conn.getInputStream().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Log.d("asyncHttp", "answer" + sb.toString());
				}

				return sb.toString();

				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				/*if (is != null) {
					is.close();
				}*/
			}
		}

		private void writeStream(OutputStream out) throws IOException {
			// TODO Auto-generated method stub

			JSONObject json = new JSONObject();
			try {
				json.put("username", mEmail);
				json.put("password", mPassword);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			String body = json.toString();

			Log.d("LoginActivity", "Trying to send string" + body);

			byte[] encodedValue = null;
			try {
				encodedValue = body.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (encodedValue != null) {
				try {
					Log.d("LoginActivity", "encoded value isnt null!"
							+ encodedValue.toString());
					out.write(encodedValue);
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("LoginActivity", "encoded value is null!");
					e.printStackTrace();
				} finally {
					/*
					 * if (out != null) { out.close(); }
					 */
				}
			}

		}
	}
}
