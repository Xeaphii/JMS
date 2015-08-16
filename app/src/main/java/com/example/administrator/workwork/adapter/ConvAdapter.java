package com.example.administrator.workwork.adapter;

/**
 * Created by Sunny on 8/16/2015.
 */
import android.content.Context;
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
import com.example.administrator.workwork.fragment.StorageSharedPref;
import com.example.administrator.workwork.model.Guest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConvAdapter extends BaseAdapter {
    boolean flag = true;
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private List<Guest> worldpopulationlist = null;
    private ArrayList<Guest> arraylist;
    com.example.administrator.workwork.fragment.StorageSharedPref sharedStorage;

    /**
     * Constructor from a list of items
     */
    public ConvAdapter(Context context, List<Guest> worldpopulationlist) {
        sharedStorage = new StorageSharedPref(context);
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
            view = inflater.inflate(R.layout.conv_list_item, null);

            holder.UserName = (TextView) view.findViewById(R.id.username_textView);
            holder.MessContent=(TextView)view.findViewById(R.id.messenger_);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(worldpopulationlist.get(position).getUserid().equals(sharedStorage.GetPrefs("user_id", ""))){
            holder.UserName.setText("me   "+":");
        }else{
            if(worldpopulationlist.get(position).getUsername().length()>4){
                holder.UserName.setText(worldpopulationlist.get(position).getUsername().substring(0,4)+".:");
            }else{
                holder.UserName.setText(worldpopulationlist.get(position).getUsername()+":");
            }
        }

        holder.MessContent.setText(worldpopulationlist.get(position).getImageurl());
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

        public TextView MessContent;
        public TextView  UserName;
    }

}