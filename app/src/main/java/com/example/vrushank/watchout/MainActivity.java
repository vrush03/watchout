package com.example.vrushank.watchout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

     EditText editText;
     Button button;
     String str;
     TextToSpeech t1;
    RequestQueue queue;
    TextView mTextView;
    RequestQueue queue2;
    CountDownTimer timer;
    int wp=0,k;
    int visited[] = new int[100];
    Coordinates[] coordinates = new Coordinates[1000];
    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //HttpClient httpClient = new DefaultHttpClient();
        queue = Volley.newRequestQueue(this);


        mTextView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.edittext);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    t1.setLanguage(Locale.UK);
                    System.out.println("Here");
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = editText.getText().toString();
                Toast.makeText(getApplicationContext(), str,Toast.LENGTH_SHORT).show();
                t1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        sendRequest();
    }

    public static String replaceAllChar(String s, String f, String r){
        String temp = s.replace(f ,r);
        return temp;
    }
    public void sendRequest(){

        final String URL= "https://maps.googleapis.com/maps/api/directions/json?origin=13.3516,74.7932&destination=13.3532,74.7908&key=AIzaSyCLxTgtFo_WM1SQqkJHPGP8-pAOyTLvAKs";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        JSONObject jsonObject = null,jsonObject2=null;
                        try {
                            jsonObject = new JSONObject(response);
//                            mTextView.setText("Response is: "+ response);
                            //System.out.println(jsonObject);
                            //mTextView.setText(jsonObject.names());
                            Log.d("test",jsonObject.names().toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");

                            JSONArray legs = jsonArray.
                                    getJSONObject(0).getJSONArray("legs");

                            k=0;
                            for(int i=0;i<legs.length();i++)
                            {

                                JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");
                                for(int j=0;j<steps.length();j++){
                                    JSONObject lel = steps.getJSONObject(j);
                                    //Log.d("sfsffsa", ""+lel.get("end_location") +"\n" + lel.get("html_instructions")+"\n");
                                    String str= lel.get("html_instructions")+"\n";
                                    //String str1 = replaceAllChar(str,"<b>"," ");
                                    //String str2= replaceAllChar(str1,"</b>"," ");
                                    //Log.d("sdadasd",str2);
                                    JSONObject lel2 = lel.getJSONObject("end_location");
                                    String str2 = str.replaceAll("\\<.*?\\>","");
                                    double lat1 = (double)lel2.get("lat");
                                    double longi = (double)lel2.get("lng");
                                    coordinates[k] = new Coordinates(lat1,longi,str2,k);
                                    k++;


                                }
                            }
                            //Log.d("k:",k+"");
                            for(int i=0;i<k;i++){
                                Log.d("somedsdf","Lat: " +  coordinates[i].lat+" Longi: " + coordinates[i].longi + " HTML: " + coordinates[i].html_dir+"\n");
                            }


                            //mTextView.setText(jsonArray[0]);
                            //jsonObject2 = new JSONObject(jsonArray.getJSONArray(0));
                            //JSONArray jsanArray2 = jsonArray[0].getJSONArray()



                            //mTextView.setText("Response is: "+ jsonObject.get("geocoded_waypoints"));
                        } catch (JSONException e) {
                           e.printStackTrace();
                        }
                        }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        //sendLocalRequest();

        timer = new CountDownTimer(5000, 20) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    sendLocalRequest();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();
    }
    public void sendLocalRequest(){

        gps = new GPSTracker(MainActivity.this);
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            //wp=0;
            double res = coordinates[wp].distance(coordinates[wp].lat,latitude,coordinates[wp].longi,longitude);
            if(res <= 0.01){
                visited[wp]=1;
                wp++;
                mTextView.setText("Reache Waypoint:" + wp);
                t1.speak(coordinates[wp].html_dir,TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                mTextView.setText("Changing:"+latitude+"\n"+longitude);
            }

            // \n is for new line

            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
        timer.start();
    }

    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
