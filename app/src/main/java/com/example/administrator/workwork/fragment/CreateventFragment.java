package com.example.administrator.workwork.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.administrator.workwork.AdressSearchActivity;
import com.example.administrator.workwork.Base;
import com.example.administrator.workwork.R;
import com.example.administrator.workwork.utill.GPSTracker;
import com.example.administrator.workwork.utill.HttpResponseData;
import com.example.administrator.workwork.utill.HttpUtility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateventFragment extends Fragment {


    Spinner eventtypeitem;
    TextView event_start;
    TextView event_start_time;
    TextView event_end_date_textview;
    TextView event_end_time_textview;
    EditText event_content_edittext,PaymentJob;
    EditText event_name_edittext;
    TextView event_location_edittext;
    StorageSharedPref sharedStorage;
    Button submit_event;
    int mYear;
    int mMonth;
    int mDay;
    Date start_time;
    Date end_time;
    String start_time_str;
    String start_date_temp_str;
    String start_time_temp_str;
    String end_time_temp_str;
    String end_date_temp_str;
    String end_time_str;
    String adress_str;
    String name_str;
    Double position_latitude;
    Double position_longitude;
    String content_str;
    String event_type;
    GPSTracker gps;
    String eventID;
    String address;
    String city;
    String search_adress;
    public static final String GOOGLE_GEOLOCATION_API_GET_COORDINATES = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyD478Y5RvbosbO4s34uRaukMwiPkBxJi5A";
    public static final String PLACES_API_NO_RESULTS = "ZERO_RESULTS";

    private ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_createvent, container, false);


        Intent intent=getActivity().getIntent();
        sharedStorage = new StorageSharedPref(CreateventFragment.this.getActivity());
        eventID =intent.getStringExtra("eventID");

        eventtypeitem = (Spinner)v. findViewById(R.id.event_type_spinner);
        List<String> list =  new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.jobs_type)));

        submit_event=(Button)v.findViewById(R.id.event_submint_button);
        event_end_date_textview=(TextView)v.findViewById(R.id.event_end_date_textView);
        event_end_time_textview=(TextView)v.findViewById(R.id.event_end_time_textView);
        event_start=(TextView)v.findViewById(R.id.event_start_textView);

        event_start_time=(TextView)v.findViewById(R.id.event_start_time_textView);
        event_end_time_textview=(TextView)v.findViewById(R.id.event_end_time_textView);
        event_content_edittext=(EditText)v.findViewById(R.id.event_content_editText);
        PaymentJob=(EditText)v.findViewById(R.id.job_payment_editText);
        event_name_edittext=(EditText)v.findViewById(R.id.event_name_editText);
        event_location_edittext=(TextView)v.findViewById(R.id.event_location_editText);
        event_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AdressSearchActivity.class);
                startActivity(intent);
            }
        });

        //get current date and display.
        Calendar c = Calendar.getInstance();
        int datestr=c.get(Calendar.DAY_OF_MONTH);
        int month=c.get(Calendar.MONTH)+1;
        int yearstr=c.get(Calendar.YEAR);
        int current_hours=c.get(Calendar.HOUR);
        int current_minutes=c.get(Calendar.MINUTE);
        String current_time=String.format("%02d:%02d",current_hours,current_minutes);
        String current_date = String.format("%02d/%02d/%d",datestr,month,yearstr );

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_values, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventtypeitem.setAdapter(dataAdapter);
        event_start.setText(current_date);
        event_end_date_textview.setText(current_date);
        event_start_time.setText(current_time);
        event_end_time_textview.setText(current_time);
        event_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                event_start.setText(String.format("%02d/%02d/%d",dayOfMonth,monthOfYear+1,year ));


                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

       event_end_date_textview.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Calendar c = Calendar.getInstance();
               mYear = c.get(Calendar.YEAR);
               mMonth = c.get(Calendar.MONTH);
               mDay = c.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                       new DatePickerDialog.OnDateSetListener() {

                           @Override
                           public void onDateSet(DatePicker view, int year,
                                                 int monthOfYear, int dayOfMonth) {
                               event_end_date_textview.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));

                           }
                       }, mYear, mMonth, mDay);
               dpd.show();
           }
       });


        event_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        event_start_time.setText(String.format("%02d:%02d",selectedHour,selectedMinute));

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        event_end_time_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        event_end_time_textview.setText(String.format("%02d:%02d",selectedHour,selectedMinute));

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        setRetainInstance(true);
        //new RemoteDataTask().execute();
        Intent searchintent=getActivity().getIntent();

        search_adress=searchintent.getStringExtra("address");
        if (!(search_adress ==null)){

            getLatLongValueFromAddress(search_adress);
//            Toast.makeText(getActivity(),position_latitude.toString()+":"+position_longitude.toString(),Toast.LENGTH_LONG).show();
            event_location_edittext.setText(search_adress);

        }

        else{

            gps = new GPSTracker(getActivity());

            // check if GPS enabled
            if(gps.canGetLocation()){

                position_latitude = gps.getLatitude();
                position_longitude = gps.getLongitude();



                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(position_latitude, position_longitude, 1);
                    address = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getAddressLine(1);
                    String country = addresses.get(0).getAddressLine(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                event_location_edittext.setText(address+","+city);

                // \n is for new line
                // Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }

        }


        submit_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                start_date_temp_str=event_start.getText().toString();
                end_date_temp_str=event_end_date_textview.getText().toString();
                start_time_temp_str=event_start_time.getText().toString();
                end_time_temp_str=event_end_time_textview.getText().toString();
                start_time_str=start_date_temp_str+", "+start_time_temp_str;
                end_time_str=end_date_temp_str+", "+end_time_temp_str;
                name_str=event_name_edittext.getText().toString();
                event_type= (String) eventtypeitem.getSelectedItem();
                content_str=event_content_edittext.getText().toString();

                if (!(eventID ==null)){

                    if (isNetworkAvailable()) {new UpdateEventJob(CreateventFragment.this.getActivity()).execute(new String[]{
                            name_str,
                            start_time_str,
                            end_time_str,
                            event_type,
                            PaymentJob.getText().toString(),
                            event_content_edittext.getText().toString()
                            ,""

                            ,position_latitude.toString(),
                            position_longitude.toString()
                            ,sharedStorage.GetPrefs("user_id", null)
                            ,eventID});}
                    else {
                        Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    if (isNetworkAvailable()) {
                        new CreateEventJob(CreateventFragment.this.getActivity()).execute(new String[]{
                                name_str,
                                start_time_str,
                                end_time_str,
                                event_type,
                                PaymentJob.getText().toString(),
                                event_content_edittext.getText().toString()
                                ,""

                                ,position_latitude.toString(),
                                position_longitude.toString()
                                ,sharedStorage.GetPrefs("user_id", null)});
                    }
                    else {
                        Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });



        return v;
    }


        public void getLatLongValueFromAddress(String address) {
        final String searchKey = address.replace(" ", "+");
        final HttpUtility httpUtility=new HttpUtility();
        new Thread() {
            public void run() {
                String reqUrl = String.format(
                        GOOGLE_GEOLOCATION_API_GET_COORDINATES,
                        searchKey);
                HttpResponseData responseData = httpUtility.sendGet(reqUrl);
                if (responseData != null && responseData.getStatusCode() == 200) {
                    try {
                        JSONObject jsonData = new JSONObject(
                                responseData.getResponseContent());
                        if (!PLACES_API_NO_RESULTS.equals(jsonData
                                .getString("status"))) {
                            JSONObject result = jsonData
                                    .getJSONArray("results").getJSONObject(0);
                            position_latitude = result.getJSONObject("geometry")
                                    .getJSONObject("location").getDouble("lat");
                            position_longitude = result.getJSONObject("geometry")
                                    .getJSONObject("location").getDouble("lng");
                            Log.d("success adress", position_latitude.toString() + ":" + position_longitude.toString());
//                            Bundle bundle = new Bundle();
//                            bundle.putDouble("latitude", lat);
//                            bundle.putDouble("longitude", lng);

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    class CreateEventJob extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public CreateEventJob(Context c) {
            dialog = new ProgressDialog(c);
            context= c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                //------------------>>
                String Url = "http://droidcube.move.pk/JMS/CreateJob.php?eventNmae=" +
                        encodeHTML(urls[0]).replaceAll(" ", "%20") +
                        "&eventTimestart=" +
                        encodeHTML(urls[1]).replaceAll(" ", "%20") +
                        "&eventTimeend=" +
                        encodeHTML(urls[2]).replaceAll(" ", "%20")+
                        "&eventType=" +
                        encodeHTML(urls[3]).replaceAll(" ", "%20")+
                        "&payment=" +
                        encodeHTML(urls[4]).replaceAll(" ", "%20") +
                        "&eventContente=" +
                        encodeHTML(urls[5]).replaceAll(" ", "%20") +
                        "&eventUserimage=" +
                        encodeHTML(urls[6]).replaceAll(" ", "%20") +
                        "&position_latitude=" +
                        encodeHTML(urls[7]).replaceAll(" ", "%20") +
                        "&position_longitude=" +
                        encodeHTML(urls[8]).replaceAll(" ", "%20") +
                        "&userid=" +
                        encodeHTML(urls[9]).replaceAll(" ", "%20");
                Log.e("Error",Url);
                HttpGet httppost = new HttpGet(Url.replaceAll(" ","%20")
                        );
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String[] data = EntityUtils.toString(entity).split(":");

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getActivity(),"Event creation successful",Toast.LENGTH_LONG).show();
            showHomeListActivity();
        }
    }
    private void showHomeListActivity() {
        Intent intent = new Intent(getActivity(), Base.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish(); // This closes the login screen so it's not on the back stack
    }
    class UpdateEventJob extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public UpdateEventJob(Context c) {
            dialog = new ProgressDialog(c);
            context= c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                //------------------>>
                HttpGet httppost = new HttpGet(("http://droidcube.move.pk/JMS/UpdaetEvent.php?eventNmae=" +
                        encodeHTML(urls[0]).replaceAll(" ", "%20") +
                        "&eventTimestart=" +
                        encodeHTML(urls[1]).replaceAll(" ", "%20") +
                        "&eventTimeend=" +
                        encodeHTML(urls[2]).replaceAll(" ", "%20")+
                        "&eventType=" +
                        encodeHTML(urls[3]).replaceAll(" ", "%20")+
                        "&payment=" +
                        encodeHTML(urls[4]).replaceAll(" ", "%20") +
                        "&eventContente=" +
                        encodeHTML(urls[5]).replaceAll(" ", "%20") +
                        "&eventUserimage=" +
                        encodeHTML(urls[6]).replaceAll(" ", "%20") +
                        "&position_latitude=" +
                        encodeHTML(urls[7]).replaceAll(" ", "%20") +
                        "&position_longitude=" +
                        encodeHTML(urls[8]).replaceAll(" ", "%20") +
                        "&userid=" +
                        encodeHTML(urls[9]).replaceAll(" ", "%20")
                         +"&eventID=" +
                        encodeHTML(urls[10])).replaceAll(" ", "%20"));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String[] data = EntityUtils.toString(entity).split(":");

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getActivity(),"Event updation successful",Toast.LENGTH_LONG).show();
            showHomeListActivity();
        }
    }

    public static String encodeHTML(String s)
    {
        StringBuffer out = new StringBuffer();
        for(int i=0; i<s.length(); i++)
        {
            char c = s.charAt(i);
            if(c > 127 || c=='"' || c=='<' || c=='>')
            {
                out.append("&#"+(int)c+";");
            }
            else
            {
                out.append(c);
            }
        }
        return out.toString();
    }


    public void setProgressDialog(ProgressDialog pd){
        progressDialog = pd;
    }
    public ProgressDialog getProgressDialog(){
        return progressDialog;
    }
	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
