package com.example.administrator.workwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import  android.content.pm.Signature;

/**
 * Created by Administrator on 7/7/2015.
 */
public class SignIn extends Activity {
    EditText Email,Password;
    Button SignInClick;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    StorageSharedPref sharedStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.e("Error","");
//        try {
//            PackageInfo info = null;
//            try {
//                info = getPackageManager().getPackageInfo(
//                        "com.example.administrator.socialbot", PackageManager.GET_SIGNATURES);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:",
//                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        callbackManager = CallbackManager.Factory.create();
        sharedStorage = new StorageSharedPref(SignIn.this);
        if(sharedStorage.GetPrefs("user_id",null)!=null){
            showHomeListActivity();
        }

        setContentView(R.layout.ui_parse_login_fragment);

        Email= (EditText) findViewById(R.id.login_username_input);

        Password= (EditText) findViewById(R.id.login_password_input);

        SignInClick= (Button) findViewById(R.id.parse_login_button);
        SignInClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if (Password.getText().toString().length() > 6) {

                        if (!Email.getText().toString().equals("")) {

                            new SignUpTask(SignIn.this).execute(new String[]{Email.getText().toString(), Password.getText().toString()});

                        } else {
                            Toast.makeText(getApplicationContext(), "Email should'nt be empty", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), "Password must be of greater than 6 characters", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
                }
            }
        });
        ((Button) findViewById(R.id.parse_signup_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, Signup.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.parse_login_help)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
        fbLoginButton = (LoginButton)findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile, email"));

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.v("LoginActivity", response.toString());
                                try {
                                    sharedStorage.StorePrefs("fb_account", loginResult.getAccessToken().getUserId().toString());
                                    if (isNetworkAvailable()) {
                                        new SignInFb(SignIn.this).execute(new String[]{loginResult.getAccessToken().getUserId().toString(),
                                                object.getString("email"), object.getString("name")
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                request.setParameters(parameters);
                request.executeAsync();


                Toast.makeText(SignIn.this, "Login Successful!", Toast.LENGTH_LONG).show();
               // sharedStorage.StorePrefs("login_cred", "1");

            }

            @Override
            public void onCancel() {
                Toast.makeText(SignIn.this, "Login cancelled by user!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(SignIn.this, "Login unsuccessful!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");
            }
        });

    }

    class SignUpTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;
        Context context;
        public SignUpTask(Context c) {
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
        protected Integer doInBackground(String... urls) {

                try {
                    //------------------>>
                    HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/SignIn.php?proj_email=" +
                            encodeHTML(urls[0]) +
                            "&proj_password=" +
                            encodeHTML(urls[1])).replaceAll(" ", "%20") );
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httppost);

                    // StatusLine stat = response.getStatusLine();
                    int status = response.getStatusLine().getStatusCode();

                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        String data = EntityUtils.toString(entity);
                        if(data.equals("404:")){
                            return 404;
                        }else{
                            sharedStorage.StorePrefs("user_id",data.split(":")[1]);
                            return 200;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }

            return 0;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(success==200){
              //  sharedStorage.StorePrefs("login_cred","1");
                showHomeListActivity();
            }else if(success==404){
                Toast.makeText(context,"Wrong Password or email",Toast.LENGTH_LONG).show();
            }else if(success==0){
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_LONG).show();
            }
        }
    }

    class SignInFb extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;
        Context context;
        public SignInFb(Context c) {
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
        protected Integer doInBackground(String... urls) {
            String url = ("http://xeamphiil.co.nf/JMS/FbLogin.php?proj_username=" +
                    encodeHTML(urls[0])+"" +
                    "&proj_email=" +
                    encodeHTML(urls[1]) +
                    "&proj_display_name=" +
                    encodeHTML(urls[2])
            ).replaceAll(" ", "%20");
            if(isFbUserExist(urls[0])) {
                     return 300;
            }else{
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);

                try {
                    HttpResponse response = client.execute(request);
                    HttpEntity entity = response.getEntity();

                    //
                    // Read the contents of an entity and return it as a String.
                    //
                    String content = EntityUtils.toString(entity);
                    System.out.println(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    //------------------>>

                    HttpGet httppost = new HttpGet(url);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httppost);

                    // StatusLine stat = response.getStatusLine();
                    int status = response.getStatusLine().getStatusCode();

                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        String resp = EntityUtils.toString(entity);
                        String[] data = resp.split(":");
                        if (data[0].equals("200")) {
                            sharedStorage.StorePrefs("user_id",data[1]);
                            return 200;
                        } else {
                            return 404;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(success==200){
                //sharedStorage.StorePrefs("login_cred","1");

                showHomeListActivity();
            }else if(success==300){
                //sharedStorage.StorePrefs("login_cred","1");

                showHomeListActivity();
            }else if(success==0){
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_LONG).show();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent i) {
        callbackManager.onActivityResult(reqCode, resCode, i);
    }
    Boolean isFbUserExist(String userName){
        try {

            //------------------>>
            HttpGet httppost = new HttpGet(("http://xeamphiil.co.nf/JMS/user_name_validation.php?proj_username=" +
                    encodeHTML(userName)).replaceAll(" ", "%20"));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String[] data = EntityUtils.toString(entity).split(":");
                if(data[0].equals("404")){
                    sharedStorage.StorePrefs("user_id",data[1]);
                    return true;
                }
                else{
                    return false;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void showHomeListActivity() {
        Intent intent = new Intent(this, Base.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // This closes the login screen so it's not on the back stack
    }

}