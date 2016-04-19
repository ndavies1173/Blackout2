package edu.uark.ndavies.blackout;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsList extends AppCompatActivity {

    private RequestQueue requestQueue;
    private EditText email;
    private StringRequest request;
    private JsonArrayRequest listRequest;
    /*Intent intent = getIntent();
    private String USER = intent.getStringExtra("EXTRA_SESSION_ID");*/
    private String USER;
    String JSON_STRING, friendString;
    JSONArray people = null;
    List friends;
    String[] friendArray;
    TextView textView;
    JSONObject jsonObject;
    JSONArray jsonArray;
    FriendsAdapter friendsAdapter;
    ListView listView;

    private static final String ADD_URL = "http://www.kanosthefallen.com/php_scripts/addfriends.php";//URL for connection to database
    private static final String GET_URL = "http://www.kanosthefallen.com/php_scripts/getfriendsacc.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        USER = ((MyApplication) this.getApplication()).getUser();


        requestQueue = Volley.newRequestQueue(this);
        email = (EditText) findViewById(R.id.email);
        textView = (TextView) findViewById(R.id.textView2);
        friendsAdapter = new FriendsAdapter(this, R.layout.row_layout);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(friendsAdapter);

        new BackgroundTask().execute();

        /*final Button load = (Button) findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getList();
            }
        });*/

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


        /*Log.e("Test", "In getList");
         listRequest = new JsonArrayRequest(Request.Method.POST, GET_URL,
                 new Response.Listener<JSONArray>(){
                     @Override
                     public void onResponse(JSONArray response) {

                         Log.e("Test", "In response");
                         try {
                             JSONArray jsonArray = new JSONArray(response);
                             Toast.makeText(getApplicationContext(), "Loop " , Toast.LENGTH_SHORT).show();
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

        requestQueue.add(listRequest);*/



        try {
            jsonObject = new JSONObject(friendString);
            jsonArray = jsonObject.getJSONArray("friendslist");
            int count = 0;
            String name;
            while(count < jsonObject.length()){
                JSONObject JO = jsonArray.getJSONObject(count);
                name = JO.getString("Friend");
                Friends friends = new Friends(name);
                friendsAdapter.add(friends);

                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }// End GetList()

    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(GET_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("email", USER);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes("email=" + USER);
                dataOutputStream.flush();
                dataOutputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while((JSON_STRING = bufferedReader.readLine()) != null){
                    stringBuilder.append(JSON_STRING + "/n");
                    //Log.e("Test", JSON_STRING);
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }


        protected void onProgressUpdate(Void... voids){
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(String result){
           // textView.setText(result);
            friendString = result;
            getList();
        }
    }
}
