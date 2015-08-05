package com.example.administrator.workwork.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.workwork.*;
import com.example.administrator.workwork.ImageLoadPackge.ImageLoader;
import com.example.administrator.workwork.model.Event;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EventLIstFragment extends Fragment {
    ProgressDialog mProgressDialog;
    private EventAdapter adapter;
    public List<Event> data = null;
    ListView eventlistView;
    StorageSharedPref sharedStorage;
    Button createevent;
    ArrayList<String> users;
    String userString;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_list, container, false);
        users=new ArrayList<String>();
        createevent=(Button)v.findViewById(R.id.create_event_event_list_button);
        createevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),CreateEventActivity.class);
                startActivity(intent);
            }
        });
        ((Button)v.findViewById(R.id.create_offer_list_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),CreateOfferActivity.class);
                startActivity(intent);
            }
        });
        eventlistView=(ListView)v.findViewById(R.id.event_listView);
        // Gets the MapView from the XML layout and creates it
        setRetainInstance(true);
        data = new ArrayList<Event>();
        sharedStorage = new StorageSharedPref(EventLIstFragment.this.getActivity());
       // Toast.makeText(EventLIstFragment.this.getActivity(),sharedStorage.GetPrefs("fb_account", null),Toast.LENGTH_LONG).show();
        if (isNetworkAvailable()) {
            new UsersEvents(EventLIstFragment.this.getActivity()).execute(new String[]{sharedStorage.GetPrefs("user_id", null)});

        }
        else {
            Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
        }

        return v;
    }






    public class EventAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        ImageLoader imageLoader;
        private List<Event> worldpopulationlist = null;
        private ArrayList<Event> arraylist;

        /**
         * Constructor from a list of items
         */
        public EventAdapter(Context context, List<Event> worldpopulationlist) {

            this.context = context;
            this.worldpopulationlist = worldpopulationlist;
            inflater = LayoutInflater.from(context);
            this.arraylist = new ArrayList<Event>();
            this.arraylist.addAll(worldpopulationlist);
            imageLoader = new ImageLoader(context);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.evnt_list_item_layout, null);

                holder.userimageview = (ImageView) view.findViewById(R.id.event_imageView);
                holder.eventnametextview=(TextView)view.findViewById(R.id.event_name_textView);
                holder.eventtimetextview=(TextView)view.findViewById(R.id.event_time_event_list_textView);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if(worldpopulationlist.get(position).getEventUserimage().equals("")){
                holder.userimageview.setImageResource(R.drawable.people);
            }
            else {

                imageLoader.DisplayImage(worldpopulationlist.get(position).getEventUserimage(), holder.userimageview);
            }
            holder.eventnametextview.setText(worldpopulationlist.get(position).getEventNmae());
            holder.eventtimetextview.setText("Payment : "+worldpopulationlist.get(position).getEventPosition());

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
            public TextView  eventtimetextview;

        }
    }
    class UsersEvents extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public UsersEvents(Context c) {
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
               // Log.e("Errpr","http://droidcube.move.pk/JMS/UsersEvent.php?proj_event_id="+urls[0]);
                HttpGet httppost = new HttpGet(("http://droidcube.move.pk/JMS/UsersEvent.php?proj_event_id="+urls[0]).replaceAll(" ", "%20")

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
            }
            if(!Resp.equals("404")){
            String[] EventsResp = Resp.split(";");
            for (int i = 0 ; i <EventsResp.length;i++ ) {
                // Locate images in flag column
                String[] EventResp = EventsResp[i].split(":::");
                final Event eventlist = new Event();

                eventlist.setUserid(EventResp[10]);
                eventlist.setEventID(EventResp[0]);
                eventlist.setEventNmae(EventResp[1]);
                eventlist.setEventContente(EventResp[6]);
                //eventlist.setEventLocation((String) event.get("location"));
                eventlist.setEventPosition(EventResp[5]);
                eventlist.setEventTimestart(EventResp[2]);
                eventlist.setEventTimeend(EventResp[3]);
                if (EventResp[7].equals("")) {

                    eventlist.setEventUserimage("");

                } else {

                    if(EventResp[12].trim().equals("0")){
                        eventlist.setEventUserimage("http://droidcube.move.pk/JMS/images/" + EventResp[7] + ".jpg");
                    }else{
                        eventlist.setEventUserimage("https://graph.facebook.com/" +EventResp[7] + "/picture?type=large");
                        //eventlist.setEventUserimage("https://graph.facebook.com/" + sharedStorage.GetPrefs("fb_account", null) + "/picture?type=large");
                    }
                }
                data.add(eventlist);

            }

            }
            adapter = new EventAdapter(getActivity(),
                    data);
            // Binds the Adapter to the ListView
            eventlistView.setAdapter(adapter);
            eventlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (sharedStorage.GetPrefs("user_id", null).equals(data.get(position).getUserid())){
                        //data.get(position).getEventID();
                        Intent zoom = new Intent(getActivity(), DetailsEventActivity.class);
                        zoom.putExtra("eventID", data.get(position).getEventID());
                        startActivity(zoom);
                    }

                    else {

                       // data.get(position).getEventID();
                        Intent zoom = new Intent(getActivity(), DetailsEventpublicActivity.class);
                        zoom.putExtra("eventID", data.get(position).getEventID());
                        startActivity(zoom);

                    }

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
	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
