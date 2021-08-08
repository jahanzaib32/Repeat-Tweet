package com.hfad.repeattweet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.FaceDetector;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;

import java.util.Arrays;

public class FacebookLogin extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessToken newAccessToken;
    private String userPageID;
    Button validator;
    private SQLiteDatabase writeableDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        validator = (Button) findViewById(R.id.validator_btn);
        if (AccessToken.getCurrentAccessToken() !=  null){
            //after log in
            validator.setText("Log out!");
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            validator.setVisibility(View.GONE);
        }else{
            //after log out
            findViewById(R.id.login_button).setVisibility(View.GONE);
            validator.setVisibility(View.VISIBLE);
            validator.setText("Give Permissions and save info!");
        }

        //set database (writable)
        SQLiteDB dbHelper = new SQLiteDB(this);
        writeableDatabase = dbHelper.getWritableDatabase();



        //set facebook login permissions and callback
        loginButton = findViewById(R.id.login_button);
        loginButton.setPublishPermissions(Arrays.asList("manage_pages", "publish_pages"));

        //loginButton.getAuthType()

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {}

            @Override
            public void onCancel() {
                Toast.makeText(FacebookLogin.this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookLogin.this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void saveToDB (String userID, String pageID, String applicationID, String pageToken){

        MainActivity.sharedPrefEditor.putString("appID", applicationID);
        MainActivity.sharedPrefEditor.putString("pageID", pageID);
        MainActivity.sharedPrefEditor.putString("pageToken", pageToken);
        MainActivity.sharedPrefEditor.putString("userID", userID);
        MainActivity.sharedPrefEditor.commit();

        /*
        ContentValues values = new ContentValues();
        values.put("appID", applicationID);
        values.put("pageID", pageID);
        values.put("pageToken", pageToken);
        values.put("userID", userID);

        try{
            long a = writeableDatabase.insert("facebook",
                    null,
                    values
            );
            int b = 909;
        }catch(Exception ex){

            Toast.makeText(this, "Database unknown error", Toast.LENGTH_SHORT).show();
            int b = 53;
        }*/

    }
    public void getPageToken(View view){
        EditText pageID = (EditText) findViewById(R.id.page_id);
        userPageID = pageID.getText().toString();

        /*
        //Get list of pages
        Bundle bundle = new Bundle();
        bundle.putString("fields", "access_token");

        GraphRequest graphRequest = new GraphRequest(
                new AccessToken(
                        newAccessToken.getToken(),
                        newAccessToken.getApplicationId(), //AccessToken.getCurrentAccessToken().getApplicationId(),
                        newAccessToken.getUserId(), //AccessToken.getCurrentAccessToken().getUserId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                "/me/accounts?global_brand_page_name",
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        int res = 34;
                        //response.
                    }
                }
        );
        graphRequest.executeAsync();
*/

        //Get page access token
        Bundle bundle = new Bundle();
        bundle.putString("fields", "access_token");

        GraphRequest graphRequest = new GraphRequest(
                new AccessToken(
                        newAccessToken.getToken(),
                        newAccessToken.getApplicationId(), //AccessToken.getCurrentAccessToken().getApplicationId(),
                        newAccessToken.getUserId(), //AccessToken.getCurrentAccessToken().getUserId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                "/" + userPageID,
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() == null){
                            try{
                            saveToDB(newAccessToken.getUserId(),
                                    userPageID,
                                    newAccessToken.getApplicationId(),
                                    response.getJSONObject().getString("access_token")
                                );
                            }catch (Exception e){

                            }
                        }else{
                            Toast.makeText(FacebookLogin.this,
                                    "Error getting page token. Please rechet page ID",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );
        graphRequest.executeAsync();
/*
        Bundle params = new Bundle();
        params.putString("message", "Welcome you are first time logged into the app!");
        new GraphRequest(
                newAccessToken,
                "/me/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        GraphResponse temp = response;
                    }
                }
        ).executeAsync();*/
    }
    public void validate(View view){
        EditText pageID = (EditText) findViewById(R.id.page_id);

        if (pageID.getText().toString() == ""){
            Toast.makeText(this, "Please enter the page ID", Toast.LENGTH_SHORT).show();
        }else{
            userPageID = pageID.getText().toString();
            loginButton.performClick();
        }
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken){
            if(currentAccessToken==null)
            {
                Toast.makeText(FacebookLogin.this,"User Logged out",Toast.LENGTH_LONG).show();

                //after log out
                findViewById(R.id.login_button).setVisibility(View.GONE);
                validator.setVisibility(View.VISIBLE);
                validator.setText("Give Permissions and save info!");

            }else{
                //after log in
                validator.setText("Log out!");
                validator.setVisibility(View.GONE);
                findViewById(R.id.login_button).setVisibility(View.VISIBLE);

                Toast.makeText(FacebookLogin.this, "You are logged in! Saving changes..." ,Toast.LENGTH_LONG).show();
                newAccessToken = currentAccessToken;
                getPageToken(null);

            }
        }
    };
}
