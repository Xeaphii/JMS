package com.example.administrator.workwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.administrator.workwork.ImageLoadPackge.ImageLoader;
import com.example.administrator.workwork.adapter.NavDrawerListAdapter;
import com.example.administrator.workwork.model.NavDrawerItem;

import com.facebook.login.LoginManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class ProfileActivity extends ActionBarActivity {
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
    StorageSharedPref sharedStorage;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private static final int LOGIN_REQUEST = 0;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    public static FragmentManager fragmentManager;
    String photo;


    ImageButton upgrade;
    Button joinbutton;
    EditText dispalsyname_textview;
    EditText name_textview;
    EditText age_textview;
    EditText about_textview;
    String displayname;
    String name;
    String age;
    String about;
    String userid;
    String myprofile;
    ImageLoader imageLoader;
    ImageView profile_picuture;
    String fileuri;
    Bitmap bitmap;
    RatingBar Ratingb;
    byte[] saveData;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfprofile);
        Intent intent=getIntent();
        photo=intent.getStringExtra("takephoto");
        userid=intent.getStringExtra("userid");
        myprofile=intent.getStringExtra("myprofile");
        sharedStorage = new StorageSharedPref(ProfileActivity.this);
        //

        fileuri=intent.getStringExtra("serchresult");
        imageLoader=new ImageLoader(this);
        joinbutton=(Button)findViewById(R.id.join_button);
        Ratingb=(RatingBar) findViewById(R.id.ratingBar);

        upgrade=(ImageButton)findViewById(R.id.edit_profile_imageButton);
        dispalsyname_textview=(EditText)findViewById(R.id.username_editText);
        name_textview=(EditText)findViewById(R.id.firstprotile_editText);
        age_textview=(EditText)findViewById(R.id.age_editText);
        about_textview=(EditText)findViewById(R.id.about_editText);
        profile_picuture=(ImageView)findViewById(R.id.profile_picture_imageview);
        //display image




        if(!(fileuri ==null)){




            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(fileuri,
                    options);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            saveData = bos.toByteArray();

            if(!(saveData ==null)){
                if (isNetworkAvailable()) {
                    new uploadToServer().execute(new String[]{Base64.encodeToString(saveData, Base64.DEFAULT),sharedStorage.GetPrefs("user_id", null)});
                }
                else {
                    Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
                }

            }

            profile_picuture.setImageBitmap(bitmap);
        }

        if (!(userid ==null) &&!(userid .equals(sharedStorage.GetPrefs("user_id",null)))){
            Ratingb.setFocusable(false);
            Ratingb.setIsIndicator(false);
            Ratingb.setClickable(false);

            upgrade.setVisibility(View.GONE);
            name_textview.setEnabled(false);
            age_textview.setEnabled(false);
            about_textview.setEnabled(false);
            dispalsyname_textview.setEnabled(false);
            if (isNetworkAvailable()) {
                new GetUserData(ProfileActivity.this).execute(new String[]{userid});
              //  Toast.makeText(getApplicationContext(),userid,Toast.LENGTH_LONG).show();

            }
            else {
                Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
            }
        }
        else {
            if (isNetworkAvailable()) {
                Ratingb.setFocusable(true);
                Ratingb.setIsIndicator(true);
                Ratingb.setClickable(true);
              //  Toast.makeText(getApplicationContext(),sharedStorage.GetPrefs("user_id",null),Toast.LENGTH_LONG).show();
                new GetUserData(ProfileActivity.this).execute(new String[]{sharedStorage.GetPrefs("user_id",null)});

            }
            else {
                Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
            }

        }



        //

        profile_picuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent =new Intent(ProfileActivity.this,CameraActivity.class);
                    startActivity(intent);

            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    new UpdateUserData(ProfileActivity.this).execute(new String[]{sharedStorage.GetPrefs("user_id",null),dispalsyname_textview.getText().toString(), name_textview.getText().toString(),age_textview.getText().toString(),
                            about_textview.getText().toString() });
                }
                else {
                    Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
                }

            }
        });



        //

//        eventbut=(ImageButton)findViewById(R.id.event_imageButton);
//        eventbut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(ProfileActivity.this,EventActivity.class);
//                startActivity(intent);
//            }
//        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


//        fragmentManager = getSupportFragmentManager();
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
//        actionBar = getSupportActionBar();
//        // enabling action bar app icon and behaving it as toggle button
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);

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
//            Fragment fragment=new Profile();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.content_myself, fragment).commit();
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
//        Fragment fragment = null;
        switch (position) {
            case 0:
                  Intent intent=new Intent(ProfileActivity.this,Base.class);
                  startActivity(intent);
                break;
            case 1:
//                fragment = new Profile();
                break;
            case 2:
                Intent intentE = new Intent(ProfileActivity.this, EventActivity.class);
                intentE.putExtra("contactus", "1");
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

//        if (fragment != null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.content_create, fragment).commit();
//
//            // update selected item and title, then close the drawer
//            mDrawerList.setItemChecked(position, true);
//            mDrawerList.setSelection(position);
//            setTitle(navMenuTitles[position]);
//            mDrawerLayout.closeDrawer(mDrawerList);
//        } else {
//            // error in creating fragment
//            Log.e("MainActivity", "Error in creating fragment");
//        }
    }

    private void onLogoutButtonClicked() {
        // close this user's session
        sharedStorage.StorePrefs("user_id",null);
		sharedStorage.StorePrefs("fb_account",null);
        LoginManager.getInstance().logOut();

        Intent intent=new Intent(ProfileActivity.this,SignIn.class);
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



    public void setProgressDialog(ProgressDialog pd){
        progressDialog = pd;
    }
    public ProgressDialog getProgressDialog(){
        return progressDialog;
    }
    public class uploadToServer extends AsyncTask<String, Void, String> {

        private ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Wait image uploading!");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", params[0]));
            nameValuePairs.add(new BasicNameValuePair("ImageName", params[1] + ".jpg"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://xeamphiil.co.nf/JMS/images/UploadImage.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
        }
    }
    class GetUserData extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public GetUserData(Context c) {
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
                    HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/profile_details.php?proj_user_id=" +
                            encodeHTML(urls[0]) ).replaceAll(" ", "%20"));
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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            UpdateUserUI(Resp);

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
    void UpdateUserUI(String Resp){
        String[] RespArray = Resp.split(";");
        Ratingb.setRating(Float.valueOf(RespArray[6].trim()));
        Ratingb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!Ratingb.isClickable())
                    new RateUpdate().execute(new String[]{userid, String.valueOf(Ratingb.getRating())});
            }
        });
        //Toast.makeText(getApplicationContext(),RespArray[6].trim(),Toast.LENGTH_LONG).show();
        joinbutton.setVisibility(View.GONE);
        if((RespArray[4].equals(""))){
            imageLoader.DisplayImage("drawable://"+R.drawable.people, profile_picuture);
        }
        else {
            if(RespArray[5].trim().equals("0")){
                imageLoader.DisplayImage("http://xeamphiil.co.nf/JMS/images/" + RespArray[4]+".jpg", profile_picuture);
            }else{
                imageLoader.DisplayImage("https://graph.facebook.com/" + RespArray[4]  + "/picture?type=large", profile_picuture);
                //eventlist.setEventUserimage("https://graph.facebook.com/" + sharedStorage.GetPrefs("fb_account", null) + "/picture?type=large");
            }
            //new FbDownloadImage(ProfileActivity.this).execute(sharedStorage.GetPrefs("user_id", null));
        }
        if (!(RespArray[0].equals(""))){

            displayname= CheckNull(RespArray[0]);
        }
        else {

            displayname="";
        }
        if (!(RespArray[1].equals(""))){

            name= CheckNull(RespArray[1]);
        }
        else {

            name="";
        }
        if (!(RespArray[2].equals(""))){

            age= CheckNull(RespArray[2]);
        }
        else {

            age="";
        }

        if (!(RespArray[3].equals(""))){

            about = CheckNull(RespArray[3]);
        }
        else {

            about="";
        }
        name_textview.setText(name);
        age_textview.setText(age);
        about_textview.setText(about);
        dispalsyname_textview.setText(displayname);
    }
    String CheckNull(String input){
        if(input == null)
            return "";
        else
            return input;
    }
    class UpdateUserData extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public UpdateUserData(Context c) {
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
//                Log.e("Error","http://xeamphiil.co.nf/JMS/updateProfile.php?proj_user_id=" +
//                        encodeHTML(urls[0]).replaceAll(" ", "%20") +
//                        "&proj_display_name=" +
//                        encodeHTML(urls[1]).replaceAll(" ", "%20") +
//                        "&proj_name=" +
//                        encodeHTML(urls[2]).replaceAll(" ", "%20") +
//                        "&proj_age=" +
//                        encodeHTML(urls[3]) +
//                        "&proj_about=" +
//                        encodeHTML(urls[4]).replaceAll(" ", "%20")+
//                        "&proj_rating=" +
//                        encodeHTML(urls[5]));
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/updateProfile.php?proj_user_id=" +
                        encodeHTML(urls[0]).replaceAll(" ", "%20") +
                        "&proj_display_name=" +
                        encodeHTML(urls[1]).replaceAll(" ", "%20") +
                        "&proj_name=" +
                        encodeHTML(urls[2]).replaceAll(" ", "%20") +
                        "&proj_age=" +
                        encodeHTML(urls[3]).replaceAll(" ", "%20") +
                        "&proj_about=" +
                        encodeHTML(urls[4]).replaceAll(" ", "%20")
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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getApplicationContext(),"Profile updation successful",Toast.LENGTH_LONG).show();
            showHomeListActivity();
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

                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/RateUpdate.php?proj_user_id=" +
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
            showHomeListActivity();
        }
    }
    private void showHomeListActivity() {
        Intent intent = new Intent(this, Base.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // This closes the login screen so it's not on the back stack
    }
    class FbDownloadImage extends AsyncTask<String, Void, Bitmap> {

        private ProgressDialog dialog;
        Context context;
        public FbDownloadImage(Context c) {
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
        protected Bitmap doInBackground(String... urls) {

            Bitmap bm = null;
            try {
                URL aURL = new URL("https://graph.facebook.com/" +urls[0] + "/picture?type=large");
                URLConnection conn = aURL.openConnection();
                conn.setUseCaches(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(final Bitmap Resp) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            profile_picuture.setImageBitmap(Resp);
        }
    }
	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
