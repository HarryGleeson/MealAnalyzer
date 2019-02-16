package com.example.harry.sqltest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/** MainActivity.java
 * This class is used to load and display the profiles stored in the SQLite database
 */
public class MainActivity extends AppCompatActivity {
    DatabaseHelper profilesDB;
    Button btnAddProfile;
    ListView lvProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profilesDB = new DatabaseHelper(this);

        btnAddProfile = (Button)findViewById(R.id.btnAddProfile);
        lvProfiles = (ListView) findViewById(R.id.lvProfiles);

        populateListView();

        AddProfile();

    }

    public void AddProfile(){
        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(MainActivity.this, CreateProfile.class));
            }
        });
    }

    public void populateListView(){
        Cursor data = profilesDB.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        lvProfiles.setAdapter(adapter);

        lvProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Cursor lipaseData = profilesDB.getItemLipase(name);
                Cursor tabletData = profilesDB.getItemTablets(name);
                int lipase = -1;
                int tablet = -1;
                while(lipaseData.moveToNext()&&tabletData.moveToNext()) {
                    lipase = lipaseData.getInt(0);
                    tablet = tabletData.getInt(0);
                    Intent takePhoto = new Intent(MainActivity.this, TakePhoto.class);
                    takePhoto.putExtra("lipase", lipase);
                    takePhoto.putExtra("tablet", tablet);
                    startActivity(takePhoto);
                }
            }
        });


    }
}
