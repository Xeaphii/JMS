package com.example.administrator.workwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.workwork.model.Event;
import com.example.administrator.workwork.utill.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Home extends Fragment {
	//this class is class that is include google map.
	public Home(){}
    MapView mapView;
    StorageSharedPref sharedStorage;
    GoogleMap map;
    Hashtable<String, Integer> table;
     // Might be null if Google Play services APK is not available.
    GPSTracker gps;
    ProgressDialog mProgressDialog;
    public List<Event> data = null;
    View rootView;
    Button Offer,Job,search;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView  = inflater.inflate(R.layout.fragment_home, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView)  rootView.findViewById(R.id.map);

        Offer = (Button)  rootView.findViewById(R.id.create_offer_list_button);
        Job = (Button)  rootView.findViewById(R.id.create_event_event_list_button);
        search= (Button)  rootView.findViewById(R.id.search_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zoom = new Intent(getActivity(), AdressSearchActivity.class);
                startActivity(zoom);
            }
        });

        Job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(data == null)){
                    map.clear();
                    for (int i=0;i<data.size();i++){
                        if(data.get(i).getEventPosition().trim().equals("0")) {
                            String content = data.get(i).getEventPosition();
                            Marker mark = map.addMarker(new MarkerOptions().position(new LatLng(data.get(i).getPosition_latitude(), data.get(i).getPosition_longitude())).title(data.get(i).getEventNmae()).snippet(content).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.man)).draggable(true));
                            table.put(mark.getId(), Integer.valueOf(i));
                        }
                    }

                }
            }
        });

        Offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(data == null)){
                    map.clear();
                    for (int i=0;i<data.size();i++){
                        if(data.get(i).getEventPosition().trim().equals("1")) {
                            String content = data.get(i).getEventPosition();
                            Marker mark = map.addMarker(new MarkerOptions().position(new LatLng(data.get(i).getPosition_latitude(), data.get(i).getPosition_longitude())).title(data.get(i).getEventNmae()).snippet(content).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.man)).draggable(true));
                            table.put(mark.getId(), Integer.valueOf(i));
                        }
                    }

                }
            }
        });

        mapView.onCreate(savedInstanceState);
        gps = new GPSTracker(getActivity());
        table = new Hashtable<String, Integer>();
        sharedStorage = new StorageSharedPref(Home.this.getActivity());
        // check if GPS enabled
//        if(gps.canGetLocation()){
//
//            position_latitude = gps.getLatitude();
//            position_longitude = gps.getLongitude();
//
//            // \n is for new line
//            // Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//        }else{
//            // can't get location
//            // GPS or Network is not enabled
//            // Ask user to enable GPS/network in settings
//            gps.showSettingsAlert();
//        }
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        // map.getUiSettings().setMyLocationButtonEnabled(false);
      //  map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls

        MapsInitializer.initialize(this.getActivity());


        // Updates the location and zoom of the MapView
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
//        map.animateCamera(cameraUpdate);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 15));
        //map.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude())).title("This is me!").draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.man)));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int index = table.get(marker.getId());
                String id = data.get(index).getEventID();
////                DetailsEventActivity.parseObject = ob.get(index);
//                Intent in = new Intent(getActivity(), DetailsEventActivity.class);
//                in.putExtra("eventID",id);
//                startActivity(in);






                if (sharedStorage.GetPrefs("user_id", null).equals(data.get(index).getUserid())){

                    Intent zoom = new Intent(getActivity(), DetailsEventActivity.class);
                    zoom.putExtra("eventID", id);
                    startActivity(zoom);
                }

                else {
                    Intent zoom = new Intent(getActivity(), DetailsEventpublicActivity.class);
                    zoom.putExtra("eventID", id);
                    startActivity(zoom);
                }




               // Toast.makeText(getActivity(), "" + index , Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        if (isNetworkAvailable()) {
            new RemoteDataTask().execute();
        }
        else {
            Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
        }




        return  rootView;
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        if (mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Message");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            data = new ArrayList<Event>();
            //http://xeamphiil.co.nf/JMS/EventsList.php
            try {

                //------------------>>
                HttpGet httppost = new HttpGet("http://xeamphiil.co.nf/JMS/EventsList.php");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String resp = EntityUtils.toString(entity);
                    String[] RespData =resp.split(";");

                    Log.e("Error",resp);
                    for (int i = 0 ; i < RespData.length;i++) {
                        // Locate images in flag column
                        final Event eventlist = new Event();
                        String[] EvenetsEntry= RespData[i].split(":::");
                        eventlist.setUserid(EvenetsEntry[10]);
                        eventlist.setEventID(EvenetsEntry[0]);
                        eventlist.setEventNmae(EvenetsEntry[1]);
                        eventlist.setEventContente(EvenetsEntry[6]);
                        //eventlist.setEventLocation((String) event.get("location"));
                        eventlist.setEventPosition(EvenetsEntry[10]);
                        eventlist.setEventTimestart(EvenetsEntry[2]);
                        eventlist.setEventTimeend(EvenetsEntry[3]);

                        eventlist.setPosition_latitude( Double.parseDouble(EvenetsEntry[8]));
                        eventlist.setPosition_longitude( Double.parseDouble(EvenetsEntry[9]));

                        if (EvenetsEntry[7].equals("")) {

                            eventlist.setEventUserimage("");

                        } else {

                            if (EvenetsEntry[12].trim().equals("0")){
                                eventlist.setEventUserimage("http://xeamphiil.co.nf/JMS/images/" + EvenetsEntry[7] + ".jpg");
                            }else{
                                eventlist.setEventUserimage("https://graph.facebook.com/" +EvenetsEntry[7] + "/picture?type=large");
                                //eventlist.setEventUserimage("https://graph.facebook.com/" + sharedStorage.GetPrefs("fb_account", null) + "/picture?type=large");
                            }
                        }
                        data.add(eventlist);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml


            // Close the progressdialog
            mProgressDialog.dismiss();
            setDispalyEvent(data);

        }
    }

    public  void setDispalyEvent(List<Event> worldpopulationlist){


        List<Event> mapposition=worldpopulationlist;
        if(!(mapposition ==null)){

            for (int i=0;i<mapposition.size();i++){
                String content=mapposition.get(i).getEventPosition();
               Marker mark = map.addMarker(new MarkerOptions().position(new LatLng(mapposition.get(i).getPosition_latitude(), mapposition.get(i).getPosition_longitude())).title(mapposition.get(i).getEventNmae()).snippet(content).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.man)).draggable(true));
                table.put(mark.getId(), Integer.valueOf(i));
            }

        }

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}