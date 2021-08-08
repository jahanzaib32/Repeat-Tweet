package com.hfad.repeattweet;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ContactChoser extends AppCompatActivity {

    //declare globally, this can be any int
    public TextView name;
    public TextView contact;
    public final int PICK_CONTACT = 2015;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_choser);

        (findViewById(R.id.button)).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        //set values to the layout
        name = (TextView) findViewById(R.id.contact_name);
        contact = (TextView) findViewById(R.id.contact_number);

        setDetailsOnScreen();

    }

    public void setDetailsOnScreen(){
        if (MainActivity.sharedPref.contains("contact_name") && MainActivity.sharedPref.getString("contact_name", null) != ""){

            name.setText(MainActivity.sharedPref.getString("contact_name", null));
            contact.setText(MainActivity.sharedPref.getString("contact_number", null));

        }else{
            name.setText("Not set yet!");
            contact.setText("Not set yet!");

        }
    }

    public void deleteContact(View view){

        MainActivity.sharedPrefEditor.putString("contact_name", "");
        MainActivity.sharedPrefEditor.putString("contact_number", "");
        MainActivity.sharedPrefEditor.commit();
        setDetailsOnScreen();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            //(new normalizePhoneNumberTask()).execute(cursor.getString(column));
            MainActivity.sharedPrefEditor.putString("contact_name", cursor.getString(name));
            MainActivity.sharedPrefEditor.putString("contact_number", cursor.getString(column));
            MainActivity.sharedPrefEditor.commit();

            setDetailsOnScreen();

            //Log.d("Phone number", cursor.getString(column));
            //Log.d("Name", cursor.getString(name));
        }
    }
}
