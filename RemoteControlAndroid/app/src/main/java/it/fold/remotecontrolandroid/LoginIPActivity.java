package it.fold.remotecontrolandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via IP/password.
 */
public class LoginIPActivity extends ActionBarActivity implements LoaderCallbacks<Cursor>,LoginView {

    /**
     * A dummy authentication store containing known user names and passwords.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    final String TAG = this.getClass().getCanonicalName();
    // UI references.
    private AutoCompleteTextView mIPView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View focusView;
    boolean cancel;
    private LoginIPPresenter presenter;
    private String FRC_SHARED_PREFS = "FRCSharedPreferences";

    @Override
    /**
     * Initializes based off of bundle
     *
     * @param savedInstanceState parsable strings used for init
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ip);
        // Set up the login form.
        mIPView = (AutoCompleteTextView) findViewById(R.id.ip);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mIPSignInButton = (Button) findViewById(R.id.IP_sign_in_button);
        mIPSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        presenter = new LoginIPPresenter(this);

        /*
         * Adding an activity for tutorials on 'TUTORIAL' Button click
         */
        Button mTutorial = (Button) findViewById(R.id.tutorialButton);
        mTutorial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "creating Tutorial Activity");
                Intent intent = new Intent(LoginIPActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(FRC_SHARED_PREFS, MODE_PRIVATE);
        String ip = prefs.getString("ip", null);

        if (ip != null) {
            String password  = prefs.getString("key", "");//"No name defined" is the default value.
            mIPView.setText(ip);
            mPasswordView.setText(password);
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid IP, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        presenter.attemptLogin();
    }


    @Override
    public String getIPAddress() {
        return mIPView.getText().toString();
    }

    @Override
    public String getPassword() {
        return mPasswordView.getText().toString();
    }

    @Override
    public void showIPEmptyError(int resId) {
        mIPView.setError(getString(resId));
        focusView = mIPView;
        cancel = true;
    }


    @Override
    public void showIpInvalidError(int resId) {
        mIPView.setError(getString(resId));
        focusView = mIPView;
        cancel = true;
    }

    @Override
    public void showPasswordInvalidError(int resId) {
        mPasswordView.setError(getString(resId));
        focusView = mPasswordView;
        cancel = true;
    }

    @Override
    public void resetErrors() {
        // Reset errors.
        mIPView.setError(null);
        mPasswordView.setError(null);
        cancel = false;
        focusView = null;
    }

    /**
     * Checks to make sure the entered IP address is a valid location
     *
     * @param IP The value input to the IP input in the login menu
     * @return If the IP string input actually contains a valid IPV4 IP String
     */
    @Override
    public boolean isIPValid(String IP) {
        try {
            if ( IP == null || IP.isEmpty() ) {
                return false;
            }

            String[] parts = IP.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return !IP.endsWith(".");

        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Makes sure the entered password is within desired constraints
     *
     * @param password the password that the user inputs
     * @return whether the password is of valid length
     */
    @Override
    public boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void attemptLoginTask(String IP, String password) {
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuthTask = new LoginIPActivity.UserLoginTask(IP, password);
            mAuthTask.execute((Void) null);
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only IP addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary IP addresses first. Note that there won't be
                // a primary IP address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> IPs = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            IPs.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addIPsToAutoComplete(IPs);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addIPsToAutoComplete(List<String> IPAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginIPActivity.this,
                        android.R.layout.simple_dropdown_item_1line, IPAddressCollection);

        mIPView.setAdapter(adapter);
    }

    // Begin GameActivity

    public void sendMessage() {
        SharedPreferences.Editor editor = getSharedPreferences(FRC_SHARED_PREFS, MODE_PRIVATE).edit();
        editor.putString("ip", getIPAddress());
        editor.putString("key", getPassword());
        editor.commit();
        Intent intent = new Intent(this, GameActivity.class);
        this.startActivity(intent);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mIP;
        private final String mPassword;

        UserLoginTask(String IP, String password) {
            mIP = IP;
            mPassword = password;
        }


        @Override
        protected Boolean doInBackground(Void ... params) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mIP)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Constants.IP_ADDRESS = mIP;
                sendMessage();
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        public String getIP() {
            return mIP;
        }
    }


    /**
     * Shows users the tutorial once the tutorial button is pressed
     * @param view the current screem view, unused variable
     */
    public void runTutorial(View view)
    {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        String tutorialText = createTutorialText();
        dlgAlert.setMessage(tutorialText);
        dlgAlert.setTitle("Tutorial");
        dlgAlert.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    /**
     * A method that creates the text to be displayed in the tutorial box
     * @return An edited string to display in the tutorial box
     */
    public String createTutorialText()
    {
        return "Visit https://fold.it \n\n" +
                "Click the link 'Are you new to Foldit? Click here.' and follow " +
                "the instructions on the next page. \n\n" +
                "(Note: In order to use this Remote control, you must be playing " +
                "on 'Science Puzzles'. \n\n" +
                "On the Foldit game, select the 'Social' tab on the bottom left, " +
                "and then select 'Remote Control'. \n\n" +
                "Enter the displayed 'Local IP' into the 'IP Address' field on " +
                "the Remote Control device. \n\n" +
                "(If the 'Require Key' box was selected, enter the displayed " +
                "'Unique Key' into the 'Password' field on the Remote Control " +
                "device. \n\n" +
                "Tap the 'Connect' button, and enjoy your game!";
    }
}

