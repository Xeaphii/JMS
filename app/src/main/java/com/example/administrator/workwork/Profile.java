package com.example.administrator.workwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.administrator.workwork.ImageLoadPackge.ImageLoader;

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

public class Profile extends Fragment {
    public Profile(){}
    StorageSharedPref sharedStorage;
    Button upgrade;
    EditText dispalsyname_textview;
    EditText name_textview;
    EditText age_textview;
    EditText about_textview;
    String displayname;
    String name;
    String age;
    String about;
    ImageLoader imageLoader;
    ImageView profile_picuture;
    String fileuri;
    Bitmap bitmap;
    byte[] saveData;
    RatingBar Ratingb;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        Intent intent=getActivity().getIntent();
        fileuri=intent.getStringExtra("serchresult");
        imageLoader=new ImageLoader(getActivity());
        sharedStorage = new StorageSharedPref(Profile.this.getActivity());
        upgrade=(Button)rootView.findViewById(R.id.join_button);
        Ratingb=(RatingBar)rootView.findViewById(R.id.ratingBar);
        dispalsyname_textview=(EditText)rootView.findViewById(R.id.username_editText);
        name_textview=(EditText)rootView.findViewById(R.id.first_editText);
        age_textview=(EditText)rootView.findViewById(R.id.age_editText);
        about_textview=(EditText)rootView.findViewById(R.id.about_editText);
        profile_picuture=(ImageView)rootView.findViewById(R.id.profile_picture_imageview);
        //display image

        Ratingb.setFocusable(true);
        Ratingb.setIsIndicator(true);
        Ratingb.setClickable(true);


        if(!(fileuri ==null)){

            setProgressDialog( ProgressDialog.show(getActivity(), "",
                    "uploading  data ...", true, true) );


            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(fileuri,
                    options);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            saveData = bos.toByteArray();
            if(!(saveData ==null)){
                if (isNetworkAvailable()) {
                    new uploadToServer().execute(new String[]{Base64.encodeToString(saveData, Base64.DEFAULT),  sharedStorage.GetPrefs("user_id",null)});
                }
                else {
                    Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
                }

            }



            profile_picuture.setImageBitmap(bitmap);

        }
        else {
            if (isNetworkAvailable()) {
                new GetUserData(Profile.this.getActivity()).execute(new String[]{ sharedStorage.GetPrefs("user_id",null)});

            }
            else {
                Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
            }
        }

        //

        profile_picuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),CameraActivity.class);
                startActivity(intent);
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    new UpdateUserData(Profile.this.getActivity()).execute(new String[]{ sharedStorage.GetPrefs("user_id",null),dispalsyname_textview.getText().toString(), name_textview.getText().toString(),age_textview.getText().toString(),
                            about_textview.getText().toString()});
                }
                else {
                    Toast.makeText(getActivity(), "No internet connection present", Toast.LENGTH_LONG).show();
                }

            }
        });
//        logout=(Button)rootView.findViewById(R.id.logout_button);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onLogoutButtonClicked();
//            }
//        });
//
        return rootView;
    }

    private void onLogoutButtonClicked() {
        // close this user's session
        sharedStorage.StorePrefs("user_id",null);
		sharedStorage.StorePrefs("fb_account",null);
        LoginManager.getInstance().logOut();

        startLoginActivity();
        // Log the user out

        // Go to the login view

    }

    private void startLoginActivity() {
        Intent intent = new Intent(getActivity(), SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    public void setProgressDialog(ProgressDialog pd){
        progressDialog = pd;
    }
    public ProgressDialog getProgressDialog(){
        return progressDialog;
    }
    public class uploadToServer extends AsyncTask<String, Void, String> {

        private ProgressDialog pd = new ProgressDialog(Profile.this.getActivity());
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
       // Toast.makeText(getActivity(),RespArray[6].trim(),Toast.LENGTH_LONG).show();
        Ratingb.setRating(Float.valueOf(RespArray[6].trim()));
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
                HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/updateProfile.php?proj_user_id=" +
                        encodeHTML(urls[0]).replaceAll(" ", "%20") +
                        "&proj_display_name=" +
                        encodeHTML(urls[1]).replaceAll(" ", "%20") +
                        "&proj_name=" +
                        encodeHTML(urls[2]).replaceAll(" ", "%20") +
                        "&proj_age=" +
                        encodeHTML(urls[3]) +
                        "&proj_about=" +
                        encodeHTML(urls[4]).replaceAll(" ", "%20")
                ));
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

        }
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
                = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
