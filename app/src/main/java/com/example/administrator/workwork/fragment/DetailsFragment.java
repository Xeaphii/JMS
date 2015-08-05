package com.example.administrator.workwork.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.workwork.CreateEventActivity;
import com.example.administrator.workwork.EventActivity;
import com.example.administrator.workwork.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.IOException;


public class DetailsFragment extends Fragment {
    TextView event_time_textview;
    TextView event_location_textview;
    TextView event_description_textview;
    Button edit_button;
    Button cancle_button;
    String eventID;
    TextView Titleview,tv_title,tv_price,tv_descp;
    RatingBar RB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        Bundle b = getActivity().getIntent().getExtras();
        eventID = b.getString("eventID");
//        deleteobject = DetailsEventActivity.parseObject;
//        Toast.makeText(getActivity(),eventID,Toast.LENGTH_LONG).show();

        event_time_textview=(TextView)v.findViewById(R.id.event_time_textView);
        Titleview = (TextView) v.findViewById(R.id.textView_title);
        event_location_textview=(TextView)v.findViewById(R.id.event_location_textView);

        tv_title=(TextView)v.findViewById(R.id.textView4);
        tv_price=(TextView)v.findViewById(R.id.textView8);
        tv_descp=(TextView)v.findViewById(R.id.textView10);
        RB = (RatingBar) v.findViewById(R.id.ratingBar);
        RB.setFocusable(true);
        RB.setIsIndicator(true);
        RB.setClickable(true);
        event_description_textview=(TextView)v.findViewById(R.id.event_content_textView);
        edit_button=(Button)v.findViewById(R.id.edit_button);
        cancle_button=(Button)v.findViewById(R.id.cancle_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                intent.putExtra("eventID", eventID);
                startActivity(intent);
            }
        });
        cancle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {
                    new DeleteEvent(DetailsFragment.this.getActivity()).execute(new String[]{eventID});
                } else {
                    Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
                }


            }
        });
        if (isNetworkAvailable()) {new GetEventDetails(DetailsFragment.this.getActivity()).execute(new String[]{eventID});}
        else {
            Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
        }



        setRetainInstance(true);
        return v;
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
                    HttpGet httppost = new HttpGet(("http://droidcube.move.pk/JMS/EventDetails.php?proj_event_id=" +
                            encodeHTML(urls[0])).replaceAll(" ", "%20") );
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
                RB.setRating(Float.valueOf(RespData[12].trim()));
                event_location_textview.setText(RespData[8]+","+RespData[9]);
                event_description_textview.setText(RespData[6]);
                Titleview.setText(RespData[1]);
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
    class DeleteEvent extends AsyncTask<String, Void,Boolean  > {

        private ProgressDialog dialog;
        Context context;
        public DeleteEvent(Context c) {
            dialog = new ProgressDialog(c);
            context= c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Deleting Object");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                //------------------>>
                HttpGet httppost = new HttpGet(("http://droidcube.move.pk/JMS/DeleteEvent.php?proj_event_id=" +
                        encodeHTML(urls[0]) ).replaceAll(" ", "%20"));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                   return true;

                }

            } catch (IOException e) {
                e.printStackTrace();

            }


            return false;
        }

        @Override
        protected void onPostExecute(final Boolean Success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(Success) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                startActivity(intent);
            }
        }
    }
	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
