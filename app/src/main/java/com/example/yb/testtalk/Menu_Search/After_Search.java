package com.example.yb.testtalk.Menu_Search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.yb.testtalk.R;
import com.example.yb.testtalk.fragment_info.first_PatientRoom;
import com.example.yb.testtalk.fragment_info.second_PatientRoom;

public class After_Search extends AppCompatActivity {

    Button Fsearch,Ssearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_search);

        Fsearch = (Button) findViewById(R.id.after_search1);
        Ssearch = (Button) findViewById(R.id.after_search2);

        Fsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.Search_fragment,new first_PatientRoom()).commit();
            }
        });

        Ssearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.Search_fragment,new second_PatientRoom()).commit();
            }
        });

    }
}