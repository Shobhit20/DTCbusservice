package com.example.shobhit.dtcbusservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class livetracking extends AppCompatActivity {
    ArrayList<String> fare_places = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        ListView lst = (ListView) findViewById(R.id.listvw);
        fare_places = (ArrayList<String>) getIntent().getSerializableExtra("map");
        int start_index = (Integer) getIntent().getSerializableExtra("start");
        int end_index = (Integer) getIntent().getSerializableExtra("end");
        fare_places =  new ArrayList<String>(fare_places.subList(start_index, end_index+1));

        ArrayAdapter<String> arrayadapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fare_places);
        lst.setAdapter(arrayadapter);

    }

}
