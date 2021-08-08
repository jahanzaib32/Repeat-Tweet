package com.hfad.repeattweet;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static public boolean posted = false;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private EditText editText;
    private MessageAdapter messageAdapter;
    private SQLiteDatabase writeableDatabase;
    private SQLiteDatabase readableDatabase;
    private ListView messagesView;
    public int REQUEST_CODE = 0;

    public static String SHARED_PREF_NAME;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor sharedPrefEditor;

    public static String fbAppID;
    public static String fbPageID;
    public static String fbUserID;
    public static String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Twitter.initialize(this);
        SQLiteDB dbHelper = new SQLiteDB(this);
        writeableDatabase = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle=new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editText = (EditText) findViewById(R.id.message_text);
        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        //Ini resources
        readableDatabase = dbHelper.getReadableDatabase();
        sharedPref = this.getSharedPreferences(this.SHARED_PREF_NAME, 0);
        sharedPrefEditor = sharedPref.edit();

        //setup the basic environment
        readMessages(readableDatabase); //show all messages to conversation
        readFbCredentials();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //writeableDatabase.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SQLiteDB dbHelper = new SQLiteDB(this);
        writeableDatabase = dbHelper.getWritableDatabase();
        readFbCredentials();
    }

    //Read facebook credentials from database from the user
    public void readFbCredentials(){

        //sharedPref.getString("fbAppID", null);

        this.fbAppID = sharedPref.getString("appID", null);
        this.fbPageID = sharedPref.getString("pageID", null);
        this.fbUserID = sharedPref.getString("userID", null);
        this.userToken = sharedPref.getString("pageToken", null);

    }


    //Writes a message to sqlite database
    public boolean writeMessage(SQLiteDatabase databse, String message, boolean isFacebook, boolean isTwitter, String date){

        ContentValues values = new ContentValues();
        values.put("message", message);
        values.put("isFacebook", isFacebook);
        values.put("isTwitter", isTwitter);
        values.put("poastedDate", date);

        try{

            long a = databse.insert("messages",
                        null,
                        values
                    );
            int b = 989;
            return true;
        }catch(Exception ex){
            Toast.makeText(this, "Database unknown error", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //read all the messsage from database
    // it reads all the messages to show up in conversation
    public void readMessages(SQLiteDatabase database){
        try {

            Cursor cursor = database.query("messages",
                    new String[]{"message", "poastedDate", "isFacebook", "isTwitter"},
                    null,null, null, null, null
                    );

            cursor.moveToFirst();
            String message;
            String date;
            do {
                message = cursor.getString(0);
                date = cursor.getString(1);
                sendCustomeMessage(message, date);
            }while (cursor.moveToNext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Whenever an item is selected from navigation drawer
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.twitter_drawer){
            Intent intent = new Intent(MainActivity.this, TwitterLogin.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.fb_drawer){
            Intent intent = new Intent(MainActivity.this, FacebookLogin.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.message_drawer){
            Intent intent = new Intent(MainActivity.this, ContactChoser.class);
            startActivity(intent);
            //contact_pic
        }else if (item.getItemId() == R.id.contact_drawer){
            Intent intent = new Intent(MainActivity.this, Contact.class);
            startActivity(intent);
            //contact_pic
        }else if (item.getItemId() == R.id.about_drawer){
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
            //contact_pic
        }

        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // when a menu-item is selected on toolbar
    // there is only one option in our case for navigation drawer
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //It just shows up the bubble on the conversation
    public void sendCustomeMessage(String message, String date){
        final Message messageObj = new Message(message, new MemberData("breeze", "green"), true, date);
        messageAdapter.add(messageObj);
        messagesView.setSelection(messagesView.getCount() - 1);

    }

    // Send SMS
    public void sendSMS(String message, String number){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String []{Manifest.permission.SEND_SMS},
                    REQUEST_CODE);
        }else{
            SmsManager.getDefault()
                    .sendTextMessage(number,
                            null,
                            message,
                            null,
                            null
                    );
        }

    }

    //publish post on facebook
    private void publishPost(String message){
        Bundle params = new Bundle();
        params.putString("message", message);
        GraphRequestAsyncTask postPublish = new GraphRequest(
                new AccessToken(
                        userToken,
                        fbAppID, //AccessToken.getCurrentAccessToken().getApplicationId(),
                        fbUserID, //AccessToken.getCurrentAccessToken().getUserId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                "/" + fbPageID + "/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        int respnce = 45;
                        FacebookRequestError error = response.getError();
                        if (error == null){
                            posted = true;
                        }else{
                            posted = true;
                        }
                    }

                }
        ).executeAsync();
    }

    //publish tweet on twitter
    public boolean postTweet( String message ){
        boolean isTweeted = false;

        try{
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            TwitterAuthToken authToken = session.getAuthToken();

            String token = authToken.token;
            String secret = authToken.secret;

            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = twitterApiClient.getStatusesService();
            Call<Tweet> call = statusesService.update(message, null, null, null, null, null, null, null, null);
            call.enqueue(new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    posted = true;
                }

                public void failure(TwitterException exception) {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    posted = false;
                }
            });
            return posted;
        }catch(Exception ex){
            Toast.makeText(this, "Error posting on Twitter. Please login to Twitter", Toast.LENGTH_SHORT).show();
            posted = false;
            return posted;
        }
    }


    //when send button is clicked it gets invoked
    // it validates required info and publish message
    public void sendMessage( View view ){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, YYYY hh:mm");
        String date = dateFormat.format(calendar.getTime());

        String message = editText.getText().toString(); // message edit text

        //send message on all (three) platforms

        if (MainActivity.sharedPref.contains("contact_name") && MainActivity.sharedPref.getString("contact_name", null) != ""){
            sendSMS(message, MainActivity.sharedPref.getString("contact_number", null));
        }

        // publish on facebook if user is logged in
        if (AccessToken.getCurrentAccessToken() !=  null){
            publishPost(message);
        }else{
            Toast.makeText(this, "Can't publish on Facebook. Please login to Facebook!", Toast.LENGTH_SHORT).show();

        }

        boolean isTweeted = postTweet(message); // Twitter publish/tweet

        //write message to database
        writeMessage(writeableDatabase, message, true, isTweeted, date);

        //Show up the bubble
        sendCustomeMessage(message, date);
        editText.getText().clear();

    }
}
class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
