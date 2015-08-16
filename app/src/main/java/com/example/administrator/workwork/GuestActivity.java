package com.example.administrator.workwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.workwork.ImageLoadPackge.ImageLoader;
import com.example.administrator.workwork.adapter.NavDrawerListAdapter;
import com.example.administrator.workwork.model.Guest;
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
import java.util.List;


public class GuestActivity extends ActionBarActivity {
    //This class is class that is include slider menu.
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;
    ActionBar actionBar;
    // used to store app title
    private CharSequence mTitle;
    StorageSharedPref sharedStorage;
    Button event;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private static final int LOGIN_REQUEST = 0;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    public static FragmentManager fragmentManager;
    private GuestAdapter guestadapter;
    public List<Guest> data = null;
    ListView guestlistView;
    String EvendID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        event=(Button)findViewById(R.id.event_guest_imageButton);
        guestlistView=(ListView)findViewById(R.id.guest_listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle b =getIntent().getExtras();
        EvendID = b.getString("EventId");

        fragmentManager = getSupportFragmentManager();
        mTitle = mDrawerTitle = getTitle();
        sharedStorage = new StorageSharedPref(GuestActivity.this);
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
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestActivity.this, EventActivity.class);
                startActivity(intent);
//                Fragment fragment=new EventLIstFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.content_event, fragment).commit();
            }
        });
        data = new ArrayList<Guest>();
        if (isNetworkAvailable()) {
            new GusetLists(GuestActivity.this).execute(new String[]{EvendID});

        }
        else {
            Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
        }


//        if (savedInstanceState == null) {
//            // on first time display view for first nav item
//            Fragment fragment=new EventLIstFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.content_event, fragment).commit();
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
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Home();
                break;
                case 1:
                    Intent intent=new Intent(GuestActivity.this,ProfileActivity.class);
                    intent.putExtra("myprofile","yes");
                    startActivity(intent);
                break;
            case 2:
                Intent intentE = new Intent(GuestActivity.this, EventActivity.class);
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

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_event, fragment).commit();

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

        Intent intent=new Intent(GuestActivity.this,SignIn.class);
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





    public class GuestAdapter extends BaseAdapter {
        boolean flag = true;
        Context context;
        LayoutInflater inflater;
        ImageLoader imageLoader;
        private List<Guest> worldpopulationlist = null;
        private ArrayList<Guest> arraylist;

        /**
         * Constructor from a list of items
         */
        public GuestAdapter(Context context, List<Guest> worldpopulationlist) {

            this.context = context;
            this.worldpopulationlist = worldpopulationlist;
            inflater = LayoutInflater.from(context);
            this.arraylist = new ArrayList<Guest>();
            this.arraylist.addAll(worldpopulationlist);
            imageLoader = new ImageLoader(context);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.guesglayout, null);

                holder.userimageview = (ImageView) view.findViewById(R.id.userphoto_imageView);
                holder.eventnametextview=(TextView)view.findViewById(R.id.username_textView);


                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if(worldpopulationlist.get(position).getImageurl().equals("")){
                holder.userimageview.setImageResource(R.drawable.people);
            }
            else {

                imageLoader.DisplayImage(worldpopulationlist.get(position).getImageurl(), holder.userimageview);
            }
            holder.eventnametextview.setText(worldpopulationlist.get(position).getUsername());



            // Restore the checked state properly
            final ListView lv = (ListView) parent;
//        holder.layout.setChecked(lv.isItemChecked(position));

            return view;
        }

        @Override
        public int getCount() {
            return worldpopulationlist.size();
        }

        @Override
        public Object getItem(int position) {
            return worldpopulationlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {

            public ImageView userimageview;
            public TextView  eventnametextview;


        }
    }
    class GusetLists extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public GusetLists(Context c) {
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
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/InterestedCandidates.php?proj_job_id=" +
                        encodeHTML(urls[0])).replaceAll(" ", "%20")
                );
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
            }if(!Resp.equals("404")) {
                String[] GuestsResp = Resp.split(";");
                for (int i = 0; i < GuestsResp.length; i++) {
                    // Locate images in flag column
                    String[] GuestResp = GuestsResp[i].split(":");
                    final Guest guestlist = new Guest();
                    guestlist.setUserid(GuestResp[0]);
                    guestlist.setUsername(GuestResp[1]);
                    if (GuestResp[2].equals("")) {
                        guestlist.setImageurl("");
                    } else {
                        //guestlist.setImageurl(GuestResp[2]);

                        if(GuestResp[3].trim().equals("0")){
                            guestlist.setImageurl("http://xeamphiil.co.nf/JMS/images/" + GuestResp[2] + ".jpg");
                        }else{
                            guestlist.setImageurl("https://graph.facebook.com/" + GuestResp[2] + "/picture?type=large");
                            //eventlist.setEventUserimage("https://graph.facebook.com/" + sharedStorage.GetPrefs("fb_account", null) + "/picture?type=large");
                        }
                    }
                    data.add(guestlist);

                }
            }else{
                Toast.makeText(GuestActivity.this,"No Guest joined yet",Toast.LENGTH_LONG).show();
            }
            guestadapter = new GuestAdapter(GuestActivity.this,
                    data);
            // Binds the Adapter to the ListView
            guestlistView.setAdapter(guestadapter);
            guestlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    data.get(position).getUserid();
                    Intent zoom = new Intent(GuestActivity.this, ProfileActivity.class);
                    zoom.putExtra("userid", data.get(position).getUserid());
                  //  Toast.makeText(getApplicationContext(), data.get(position).getUserid(),Toast.LENGTH_LONG).show();
                    startActivity(zoom);


//                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });

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

//http://xeamphiil.co.nf/JMS/GuestsList.php?proj_event_id=2

//    private ActionBar getActionBar() {
//        return ((ActionBarActivity) getActivity()).getSupportActionBar();
//    }
private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
