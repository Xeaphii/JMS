package com.example.administrator.workwork.adapter;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.workwork.ImageLoadPackge.ImageLoader;
import com.example.administrator.workwork.R;
import com.example.administrator.workwork.StorageSharedPref;
import com.example.administrator.workwork.model.NavDrawerItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	ImageLoader imageLoader;
	StorageSharedPref sharedStorage;
	ImageView temp;


	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		imageLoader=new ImageLoader(context);
		sharedStorage = new StorageSharedPref(context);
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.slidermenu_list_item, null);
        }
         
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.profile_picture_imageview);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
//        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
         if(position == 1){
			 if (isNetworkAvailable()) {
				 temp = imgIcon;
				new GetUserData().execute(new String[]{ sharedStorage.GetPrefs("user_id",null)});
			 }
			 else {
				 Toast.makeText(context, "No internet connection present", Toast.LENGTH_LONG).show();
			 }

			 //imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		 }else{
			 imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		 }

        txtTitle.setText(navDrawerItems.get(position).getTitle());
        
        // displaying count
        // check whether it set visible or not
//        if(navDrawerItems.get(position).getCounterVisibility()){
//        	txtCount.setText(navDrawerItems.get(position).getCount());
//        }else{
//        	// hide the counter view
//        	txtCount.setVisibility(View.GONE);
//        }
        
        return convertView;
	}
	class GetUserData extends AsyncTask<String, Void, String> {


		public GetUserData() {

		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... urls) {

			try {
				//------------------>>
				HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/profile_details.php?proj_user_id=" +
						encodeHTML(urls[0])).replaceAll(" ", "%20") );
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

			UpdateUserUI(Resp);
		}
	}
	void UpdateUserUI(String Resp){
		String[] RespArray = Resp.split(";");
		// Toast.makeText(getActivity(),RespArray[6].trim(),Toast.LENGTH_LONG).show();
		if((RespArray[4].equals(""))){
			imageLoader.DisplayImage("drawable://"+R.drawable.people, temp);
		}
		else {
			if(RespArray[5].trim().equals("0")){
				imageLoader.DisplayImage("http://xeamphiil.co.nf/JMS/images/" + RespArray[4]+".jpg", temp);
			}else{
				imageLoader.DisplayImage("https://graph.facebook.com/" + RespArray[4]  + "/picture?type=large", temp);
				//eventlist.setEventUserimage("https://graph.facebook.com/" + sharedStorage.GetPrefs("fb_account", null) + "/picture?type=large");
			}
			//new FbDownloadImage(ProfileActivity.this).execute(sharedStorage.GetPrefs("user_id", null));
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
