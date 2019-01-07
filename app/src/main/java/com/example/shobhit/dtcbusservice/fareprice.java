package com.example.shobhit.dtcbusservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//
public class fareprice extends AppCompatActivity {
    ArrayList<String> fare_places = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fareprice);
        ListView lst = (ListView) findViewById(R.id.listvw);
        fare_places = (ArrayList<String>) getIntent().getSerializableExtra("map");
        int start_index = (Integer) getIntent().getSerializableExtra("start");
        int end_index = (Integer) getIntent().getSerializableExtra("end");
        fare_places =  new ArrayList<String>(fare_places.subList(start_index, end_index+1));
        ArrayList<String> final_places = new ArrayList<String>();
        for (int i=1; i < fare_places.size(); i++){
            final_places.add("\n" + fare_places.get(0) +"\n\n"+fare_places.get(i)+"\n");
        }
        ArrayAdapter<String> arrayadapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, final_places);
        lst.setAdapter(arrayadapter);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv= (TextView) view;
                Toast.makeText(fareprice.this, "The fare price is X",Toast.LENGTH_LONG).show();

            }
        });


    }

}
