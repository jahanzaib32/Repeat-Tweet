package com.hfad.repeattweet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        String [] titles = {"Setup Facebook", "Setup Twitter", "Select Contacts"};
        String [] descs = {"Please provide Facebook credentials along with pages info to publish posts",
                "Authorization to Twitter is needed to tweet on your Twitter account",
                "Select contact to send messages"};
        int [] images = {R.drawable.fb_image, R.drawable.fb_image, R.drawable.fb_image};

        /*
        ListView list = (ListView) findViewById(R.id.menu_list_container);
        MyMenuAdapter myAdapter = new MyMenuAdapter(this, titles, descs, images);
        list.setAdapter(myAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(menu.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}
