package edu.uark.ndavies.blackout;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;
import android.widget.EditText;
import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //Initializing variables
    private EditText email, password, fname, lname, emailc, passwordc, confirm;
    private RequestQueue requestQueue;
    private static final String URL = "http://www.kanosthefallen.com/php_scripts/user_control.php";//URL for connection to database
    private static final String friendURL = "http://www.kanosthefallen.com/php_scripts/create_friendslist.php";
    private StringRequest request;
    private StringRequest friendRequest;
    ViewFlipper viewFlipper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Set text field variables to corresponding text fields from layout
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        fname = (EditText) findViewById(R.id.First_Name);
        lname = (EditText) findViewById(R.id.Last_Name);
        emailc = (EditText) findViewById(R.id.emailc);
        passwordc = (EditText) findViewById(R.id.passc);
        confirm = (EditText) findViewById(R.id.PassConfirm);
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);

        requestQueue = Volley.newRequestQueue(this);

        //Button to get to MapActivity for testing
        //HIDE OR DELETE IN FINAL BUILD
        final Button Map = (Button) findViewById(R.id.map);
        Map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveUser("test@gmail.com");
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
        //HIDE OR DELETE IN FINAL BUILD

        //Creating a ViewFlipper to show multiple views in same activity
        final ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        //Login Button with it's on click listener
        final Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                //Creates a string to send to the URL as a POST method using the hash map to pull text from text fields and format it into a proper POST Call
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                Toast.makeText(getApplicationContext(), "success " +jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                //Successful login, Begin MapsActivity
                               // Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                               // intent.putExtra("EXTRA_SESSION_ID", email.getText().toString());
                                //((MyApplication) this.getApplication()).setSomeVariable(email.getText().toString());
                                saveUser(email.getText().toString());
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "error" +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){@Override        //Pulls text from fields for sending to URL as POST
                    protected Map<String, String> getParams() throws AuthFailureError{
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("email", email.getText().toString());
                    hashMap.put("password", password.getText().toString());

                    return hashMap;

                }};

                requestQueue.add(request);
            }
        });

        //Sign Up Button on first screen shown, switches the ViewFlipper to the next view showing the sign up screen
        final Button sign_up_button = (Button) findViewById(R.id.sign_up_button);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                viewFlipper.showNext();
            }
        });

        //Submit Button on sign up screen
        //Same action as login button with more fields sent as POST and checks that password and confirm password are equal before sending request.
        final Button Sign_submit = (Button) findViewById(R.id.Sign_Submit);
        Sign_submit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Perform action on click
                if (passwordc.getText().toString().equals(confirm.getText().toString())) {

                    request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.names().get(0).equals("success")){
                                    Toast.makeText(getApplicationContext(), "success " +jsonObject.getString("success"), Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "error" +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){@Override
                       protected Map<String, String> getParams() throws AuthFailureError{
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("emailc", emailc.getText().toString());
                        hashMap.put("passwordc", passwordc.getText().toString());
                        hashMap.put("firstname", fname.getText().toString());
                        hashMap.put("lastname", lname.getText().toString());

                        return hashMap;

                    }};

                    requestQueue.add(request);
                    friendRequest = new StringRequest(Request.Method.POST, friendURL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.names().get(0).equals("success")){
                                    //SharedPreferences.Editor editor = sharedpreferences.edit();
                                    //editor.putString(owner, email.getText().toString());
                                    //startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                    viewFlipper.setDisplayedChild(0);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "error" +jsonObject.getString("error") + emailc.getText().toString(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){@Override
                       protected Map<String, String> getParams() throws AuthFailureError{
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("email", emailc.getText().toString());

                        return hashMap;

                    }};


                    requestQueue.add(friendRequest);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Passwords do NOT match", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }//End OnCreate

    private void saveUser(String name){
        ((MyApplication) this.getApplication()).setSomeVariable(name);
    }

    //Returns to default view on back pressed. If already on default view exits activity.
    @Override
    public void onBackPressed()
    {
        if(viewFlipper.getDisplayedChild() == 0){
            super.onBackPressed();
        }
        else {
            viewFlipper.setDisplayedChild(0);
            return;
        }
    }

}
