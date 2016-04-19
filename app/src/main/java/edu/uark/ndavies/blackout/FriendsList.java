package edu.uark.ndavies.blackout;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsList extends AppCompatActivity {

    private RequestQueue requestQueue;
    private EditText email;
    private StringRequest request;
    private JsonObjectRequest listRequest;
    /*Intent intent = getIntent();
    private String USER = intent.getStringExtra("EXTRA_SESSION_ID");*/
    private String USER;
    JSONArray people = null;
    List friends;
    String[] friendArray;
    TextView textView;

    private static final String ADD_URL = "http://www.kanosthefallen.com/php_scripts/addfriends.php";//URL for connection to database
    private static final String GET_URL = "http://www.kanosthefallen.com/php_scripts/getfriendsacc.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        USER = ((MyApplication) this.getApplication()).getSomeVariable();


        requestQueue = Volley.newRequestQueue(this);
        email = (EditText) findViewById(R.id.email);
        textView = (TextView) findViewById(R.id.textView2);

        final Button load = (Button) findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getList();
            }
        });

        final Button enter = (Button) findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Creates a string to send to the URL as a POST method using the hash map to pull text from text fields and format it into a proper POST Call
                request = new StringRequest(Request.Method.POST, ADD_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
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
                   protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("email", email.getText().toString());
                    hashMap.put("Owner", USER);

                    return hashMap;

                }};

                requestQueue.add(request);
            }
        });
    }//End onCreate

    private void getList(){
        //Creates a string to send to the URL as a POST method using the hash map to pull text from text fields and format it into a proper POST Call
        /*request = new StringRequest(Request.Method.POST, GET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    people = jsonObject.getJSONArray("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){@Override        //Pulls text from fields for sending to URL as POST
           protected Map<String, String> getParams() throws AuthFailureError {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("email", USER);

            return hashMap;

        }};*/

         listRequest = new JsonObjectRequest(Request.Method.POST, GET_URL,
                 new Response.Listener<JSONObject>(){

                     @Override
                     public void onResponse(JSONObject response) {Toast.makeText(getApplicationContext(), "Loop " , Toast.LENGTH_SHORT).show();

                         try {
                             JSONArray jsonArray = response.getJSONArray("friendslist");
                             for(int i = 0; i < jsonArray.length(); i++){

                                 JSONObject friend = jsonArray.getJSONObject(i);
                                 friends.add(friend.getString("Friend"));
                                 String one = friend.getString("Friend");
                                 textView.append(one + "/n");
                             }
                             String[] friendArray = new String[friends.size()];
                             friends.toArray(friendArray);
                             ListAdapter mylist = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, friendArray);
                             ListView listView = (ListView) findViewById(R.id.listView);
                             listView.setAdapter(mylist);
                         }

                         catch (JSONException e) {
                             e.printStackTrace();
                         }

                     }
                 }, new Response.ErrorListener(){

             @Override
             public void onErrorResponse(VolleyError error) {

             }
         }){@Override        //Pulls text from fields for sending to URL as POST
             protected Map<String, String> getParams() throws AuthFailureError {
             HashMap<String, String> hashMap = new HashMap<String, String>();
             hashMap.put("email", USER);

             return hashMap;

         }};

        requestQueue.add(listRequest);
    }// End GetList()
}
