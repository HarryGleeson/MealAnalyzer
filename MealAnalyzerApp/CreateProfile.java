package com.example.harry.sqltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/** CreateProfile.java
 * Adds information given by user to the profile database
 */

public class CreateProfile extends AppCompatActivity {
    DatabaseHelper profilesDB;
    Button btnAddProfile;
    EditText etName, etLipase, etTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        profilesDB = new DatabaseHelper(this);

        btnAddProfile = (Button)findViewById(R.id.btnAddProfile);
        etName = (EditText) findViewById(R.id.etName);
        etLipase = (EditText) findViewById(R.id.etLipase);
        etTablet = (EditText) findViewById(R.id.etTablet);
        AddData();
    }

    public void AddData(){
        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                int lipase = Integer.parseInt(etLipase.getText().toString());
                int tablet = Integer.parseInt(etTablet.getText().toString());
                boolean insertData = profilesDB.addData(name, lipase, tablet);
                if(insertData==true){
                    Toast.makeText(getApplicationContext(), "Data sucessfully added to database", Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(getApplicationContext(), "Something went wrong adding to database", Toast.LENGTH_LONG).show();

                Intent main = new Intent(CreateProfile.this, MainActivity.class);
                startActivity(main);
            }
        });
    }
}
