package com.example.administrator.workwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.workwork.adapter.ConvAdapter;
import com.example.administrator.workwork.fragment.*;
import com.example.administrator.workwork.fragment.StorageSharedPref;
import com.example.administrator.workwork.model.Guest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 8/16/2015.
 */
public class Conversate extends Activity {
    private ConvAdapter convadapter;
    public List<Guest> data = null;
    ListView convlistView;
    Button SendButton;
    EditText messageContent;
    com.example.administrator.workwork.fragment.StorageSharedPref sharedStorage;
    String User="me  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_layout);
        Intent intent = getIntent();
        final String Owner = intent.getExtras().getString("owner");
        final String Candidate = intent.getExtras().getString("worker");
        convlistView = (ListView) findViewById(R.id.interested_conv);
        data = new ArrayList<Guest>();
        sharedStorage = new StorageSharedPref(Conversate.this);
        SendButton = (Button) findViewById(R.id.bt_send);
        messageContent = (EditText) findViewById(R.id.message_content);
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Guest g = new Guest();
//
//                g.setImageurl();
//                g.setUsername();

                new SendMessage(Conversate.this).execute(new String[]{Owner
                        ,Candidate
                        ,messageContent.getText().toString()
                        ,sharedStorage.GetPrefs("user_id", null)
                });
            }
        });
        new ConvLists(Conversate.this).execute(new String[]{Owner,Candidate});
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
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    class ConvLists extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public ConvLists(Context c) {
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
                HttpGet httppost = new HttpGet("http://xeamphiil.co.nf/JMS/MessageList.php?proj_owner_id=" +
                        "" +encodeHTML(urls[0]).replaceAll(" ", "%20")+
                        "&proj_worker_id=" +
                        "" +encodeHTML(urls[1]).replaceAll(" ", "%20")
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
                String[] GuestsResp = Resp.trim().split(";");
                for (int i = 0; i < GuestsResp.length; i++) {
                    // Locate images in flag column
                    String[] GuestResp = GuestsResp[i].split(":::");
                    final Guest guestlist = new Guest();
                    guestlist.setImageurl(GuestResp[0]);
                    guestlist.setUsername(GuestResp[1]);
                    guestlist.setUserid(GuestResp[2]);
                    data.add(guestlist);

                }
            }else{
                Toast.makeText(Conversate.this, "No Guest joined yet", Toast.LENGTH_LONG).show();
            }
            convadapter = new ConvAdapter(Conversate.this,
                    data);
            // Binds the Adapter to the ListView
            convlistView.setAdapter(convadapter);
            convlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    data.get(position).getUserid();
                    Intent zoom = new Intent(Conversate.this, ProfileActivity.class);
                    zoom.putExtra("userid", data.get(position).getUserid());
                    //  Toast.makeText(getApplicationContext(), data.get(position).getUserid(),Toast.LENGTH_LONG).show();
                    startActivity(zoom);


//                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });

        }
    }
    class SendMessage extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        Context context;
        public SendMessage(Context c) {
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
                HttpGet httppost = new HttpGet("http://xeamphiil.co.nf/JMS/insertMessage.php?proj_owner_id=" +
                        encodeHTML(urls[0]).replaceAll(" ", "%20") +
                        "&proj_worker_id=" +
                        encodeHTML(urls[1]).replaceAll(" ", "%20") +
                        "&message=" +
                        encodeHTML(urls[2]).replaceAll(" ", "%20") +
                        "&from_id=" +
                        encodeHTML(urls[3]).replaceAll(" ", "%20")

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
                Guest g = new Guest();
                g.setImageurl(messageContent.getText().toString());
                g.setUsername(User);
                g.setUserid(sharedStorage.GetPrefs("user_id", null));
                data.add(g);
                convadapter.notifyDataSetInvalidated();
                convadapter.notifyDataSetChanged();
                messageContent.setText("");
                Toast.makeText(Conversate.this, "Message Sent", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(Conversate.this, "Some error occured", Toast.LENGTH_LONG).show();
            }
        }
    }
}
