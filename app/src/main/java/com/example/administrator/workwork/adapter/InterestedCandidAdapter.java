package com.example.administrator.workwork.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.workwork.Conversate;
import com.example.administrator.workwork.ImageLoadPackge.ImageLoader;
import com.example.administrator.workwork.R;
import com.example.administrator.workwork.fragment.CreateOfferFragment;
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

public class InterestedCandidAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private List<Guest> worldpopulationlist = null;
    private ArrayList<Guest> arraylist;
    StorageSharedPref sharedStorage;

    /**
     * Constructor from a list of items
     */
    public InterestedCandidAdapter(Context context, List<Guest> worldpopulationlist) {

        this.context = context;
        this.worldpopulationlist = worldpopulationlist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Guest>();
        this.arraylist.addAll(worldpopulationlist);
        imageLoader = new ImageLoader(context);
        sharedStorage = new StorageSharedPref(context);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.interested_candidates, null);

            holder.userimageview = (ImageView) view.findViewById(R.id.userphoto_imageView);
            holder.eventnametextview=(TextView)view.findViewById(R.id.username_textView);
            holder.MessageProj = (Button) view.findViewById(R.id.bt_message);
            holder.CommitProj=(Button)view.findViewById(R.id.bt_commit);

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
        holder.MessageProj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent i = new Intent(context, Conversate.class);
                    i.putExtra("owner", sharedStorage.GetPrefs("user_id", null));
                    i.putExtra("worker", worldpopulationlist.get(position).getUserid());
                    context.startActivity(i);
                }
                else {
                    Toast.makeText(context, "No internet connection present", Toast.LENGTH_LONG).show();
                }

            }
        });
        holder.CommitProj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    new CommitProjToUser().execute(new String[]{worldpopulationlist.get(position).getJobId(),
                            worldpopulationlist.get(position).getUserid()});
                }
                else {
                    Toast.makeText(context, "No internet connection present", Toast.LENGTH_LONG).show();
                }

            }
        });

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
        public Button MessageProj;
        public Button  CommitProj;

    }

    class CommitProjToUser extends AsyncTask<String, Void, String> {


        public CommitProjToUser() {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                //------------------>>
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/CommitProj.php?proj_job_id=" +
                        encodeHTML(urls[0])).replaceAll(" ", "%20")+"" +
                        "&proj_user_id=" + encodeHTML(urls[1]).replaceAll(" ", "%20"));
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

            Toast.makeText(context,"Job committed",Toast.LENGTH_LONG).show();
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
                = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}