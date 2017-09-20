package com.example.user.emergencyhealthcare.actvities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.user.emergencyhealthcare.R;

import java.util.ArrayList;
import java.util.HashMap;

public class blood_group extends AppCompatActivity {


    private ListView lv;
    String districtName;
    ArrayAdapter<String> adapter;
    ArrayList<HashMap<String, String>> productList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_group);

        // Listview Data
        final String districts[] = {"EastGodavari",
                "WestGodavari",
                "Krishna",
                "Guntur",
                "Prakasam",
                "Nellore",
                "Srikakulam",
                "Vizianagaram",
                "Visakhapatnam","Kurnool","Chittoor","Kadapa","Anantapur"};

        lv = (ListView) findViewById(R.id.list_view);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, districts);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                districtName = districts[position];
                Bundle obj = new Bundle();
                obj.putString("name", districtName);
                Intent i = new Intent(blood_group.this, output_bloodgroup.class);
                i.putExtras(obj);
                startActivity(i);
            }
        });


    }
}
