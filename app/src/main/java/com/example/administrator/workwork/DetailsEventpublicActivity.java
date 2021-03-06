package com.example.administrator.workwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.Rating;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.workwork.adapter.NavDrawerListAdapter;
import com.example.administrator.workwork.model.NavDrawerItem;
import com.facebook.login.LoginManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class DetailsEventpublicActivity extends ActionBarActivity {
    //This class is class that is include slider menu.
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;
    ActionBar actionBar;
    // used to store app title
    private CharSequence mTitle;
    Button eventbut;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private static final int LOGIN_REQUEST = 0;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    public static FragmentManager fragmentManager;
    TextView event_time_textview;
    TextView event_location_textview,tv_title,tv_price,tv_descp;
    TextView event_description_textview;
    TextView Titleview;
    Button cancle_button;
    Boolean isJoined = false;
    Button MessageClient;
    RatingBar RB;
    String OwnerId="";

    String eventID;


    ArrayList<String> users;

    StorageSharedPref sharedStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_other);
        eventbut=(Button)findViewById(R.id.event_imageButton);
        eventbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsEventpublicActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });
        users=new ArrayList<String>();
        Bundle b =getIntent().getExtras();
        eventID = b.getString("eventID");
//        deleteobject = DetailsEventActivity.parseObject;
//        Toast.makeText(getActivity(),eventID,Toast.LENGTH_LONG).show();
        Titleview = (TextView) findViewById(R.id.textView_title);
        event_time_textview=(TextView)findViewById(R.id.event_time_textView);
        event_location_textview=(TextView)findViewById(R.id.event_location_textView);
        event_description_textview=(TextView)findViewById(R.id.event_content_textView);
        RB = (RatingBar) findViewById(R.id.ratingBar);


        tv_title=(TextView)findViewById(R.id.textView4);
        tv_price=(TextView)findViewById(R.id.textView8);
        tv_descp=(TextView)findViewById(R.id.textView10);
        MessageClient = (Button) findViewById(R.id.message_button);
        MessageClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Conversate.class);
                i.putExtra("owner",OwnerId );
                i.putExtra("worker", sharedStorage.GetPrefs("user_id", null));
                startActivity(i);
            }
        });
        new GetOwnerId().execute(new String[]{eventID});
        sharedStorage = new StorageSharedPref(DetailsEventpublicActivity.this);
        cancle_button=(Button)findViewById(R.id.cancle_button);

        new CheckJoining(DetailsEventpublicActivity.this).execute(new String[]{eventID, sharedStorage.GetPrefs("user_id", null)});

        cancle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                deleteobject.deleteInBackground();
                if(isJoined){
                    new CancelJoining(DetailsEventpublicActivity.this).execute(new String[]{eventID,sharedStorage.GetPrefs("user_id",null)});
                }else{
                    new JoinEvent(DetailsEventpublicActivity.this).execute(new String[]{eventID,sharedStorage.GetPrefs("user_id",null)});
                }
            }
        });
        if (isNetworkAvailable()) {
            new GetEventDetails(DetailsEventpublicActivity.this).execute(new String[]{eventID});
        }
        else {
            Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        fragmentManager = getSupportFragmentManager();
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here



        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        actionBar = getSupportActionBar();
        // enabling action bar app icon and behaving it as toggle button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                //actionBar.setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // actionBar.setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        if (savedInstanceState == null) {
//            // on first time display view for first nav item
//            Fragment fragment=new DetailsFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.content_details, fragment).commit();
//        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    class RateUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/RateUpdateJob.php?proj_user_id=" +
                        encodeHTML(urls[0]).replaceAll(" ", "%20") +
                        "&proj_rating=" +
                        encodeHTML(urls[1]).replaceAll(" ", "%20")
                ));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity);

                }

            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {

            Toast.makeText(getApplicationContext(),"Profile rated",Toast.LENGTH_LONG).show();
            RB.setFocusable(true);
            RB.setIsIndicator(true);
            RB.setClickable(true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Home();
                break;
            case 1:
                Intent intent=new Intent(DetailsEventpublicActivity.this,ProfileActivity.class);
                intent.putExtra("myprofile","yes");
                startActivity(intent);
                break;
            case 2:
                Intent intentE=new Intent(DetailsEventpublicActivity.this,EventActivity.class);
                intentE.putExtra("contactus","1");
                startActivity(intentE);
                break;
//            case 3:
//                fragment = new Setting();
//                break;
            case 4:
                onLogoutButtonClicked();
                break;


            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_details, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
    private void onLogoutButtonClicked() {
        // close this user's session
        sharedStorage.StorePrefs("user_id",null);
		sharedStorage.StorePrefs("fb_account",null);
       LoginManager.getInstance().logOut();
        Intent intent=new Intent(DetailsEventpublicActivity.this,SignIn.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        // Log the user out

        // Go to the login view

    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        actionBar.setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
//    private ActionBar getActionBar() {
//        return ((ActionBarActivity) getActivity()).getSupportActionBar();
//    }

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
    class GetEventDetails extends AsyncTask<String, Void,String  > {

        private ProgressDialog dialog;
        Context context;
        public GetEventDetails(Context c) {
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
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/EventDetails.php?proj_event_id=" +
                        encodeHTML(urls[0])).replaceAll(" ", "%20"));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return  EntityUtils.toString(entity);

                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(Resp!=null){
                String[] RespData = Resp.split(":::");
                event_time_textview.setText(RespData[5]);
                event_location_textview.setText(RespData[13]);
                event_description_textview.setText(RespData[6]);
                Titleview.setText(RespData[1]);
                RB.setRating(Float.valueOf(RespData[12].trim()));
                RB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (!RB.isClickable())
                            new RateUpdate().execute(new String[]{eventID, String.valueOf(RB.getRating())});
                    }

                });
                if(RespData[10].trim().equals("0")){
                    tv_descp.setText("Job Description");
                    tv_price.setText("Job Rate");
                    tv_title.setText("Job Location");
                }else{
                    tv_descp.setText("Offer Description");
                    tv_price.setText("Offer Rate");
                    tv_title.setText("Offer Location");
                }
            }
        }
    }

    class JoinEvent extends AsyncTask<String, Void,String  > {

        private ProgressDialog dialog;
        Context context;
        public JoinEvent(Context c) {
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
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/JoinEvent.php?proj_event_id=" +
                        encodeHTML(urls[0]) +
                        "&proj_event_guest=" +
                        encodeHTML(urls[1])).replaceAll(" ", "%20") );
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return  EntityUtils.toString(entity);

                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
                if(Resp.equals("200")){
                Toast.makeText(DetailsEventpublicActivity.this, "Job entry saved!", Toast.LENGTH_LONG).show();
                cancle_button.setText("Cancel Job");
                isJoined = true;
                    showHomeListActivity();
                }else if(Resp.equals("404")){
                Toast.makeText(DetailsEventpublicActivity.this, "Already enlisted in job", Toast.LENGTH_LONG).show();
                cancle_button.setText("Cancel Job");
                isJoined = false;
                    showHomeListActivity();
                }
        }
    }
    private void showHomeListActivity() {
        Intent intent = new Intent(getApplicationContext(), Base.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // This closes the login screen so it's not on the back stack
    }
    class CheckJoining extends AsyncTask<String, Void,String  > {

        private ProgressDialog dialog;
        Context context;
        public CheckJoining(Context c) {
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
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/CheckJoining.php?proj_event_id=" +
                        encodeHTML(urls[0]) +
                        "&proj_event_guest=" +
                        encodeHTML(urls[1])).replaceAll(" ", "%20") );
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return  EntityUtils.toString(entity);

                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(Resp.equals("200")){
                cancle_button.setText("Cancel Job");
                isJoined = true;
            }else if(Resp.equals("404")){
                cancle_button.setText("Interested");
                isJoined = false;
            }
        }
    }
    class CancelJoining extends AsyncTask<String, Void,String  > {

        private ProgressDialog dialog;
        Context context;
        public CancelJoining(Context c) {
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
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/CancelJoining.php?proj_event_id=" +
                        encodeHTML(urls[0]) +
                        "&proj_event_guest=" +
                        encodeHTML(urls[1])).replaceAll(" ", "%20") );
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return  EntityUtils.toString(entity);

                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(Resp.equals("200")){
                Toast.makeText(getApplicationContext(),"Job left successfull",Toast.LENGTH_LONG).show();
                cancle_button.setText("Interested");
                isJoined = false;
            }else if(Resp.equals("404")){
                Toast.makeText(getApplicationContext(),"Job leftunsuccessful",Toast.LENGTH_LONG).show();
                cancle_button.setText("Cancel Job");
                isJoined = true;
            }
        }
    }
	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class GetOwnerId extends AsyncTask<String, Void,String  > {


        public GetOwnerId () {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                //------------------>>
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/GetOwnerId.php?proj_job_iid=" +
                        encodeHTML(urls[0])));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return  EntityUtils.toString(entity);

                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


            return null;
        }

        @Override
        protected void onPostExecute(final String Resp) {

            OwnerId = Resp;
        }
    }
}
