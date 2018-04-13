package com.example.yb.testtalk.menu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yb.testtalk.Menu_Search.After_Search;
import com.example.yb.testtalk.Menu_Search.Auto;
import com.example.yb.testtalk.R;

public class patientSearchMenu extends AppCompatActivity {

    Button Search_Auto,Search_NotAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search);
        Search_Auto = (Button)findViewById(R.id.Search_auto);
        Search_NotAuto = (Button)findViewById(R.id.Search_Notauto);

        Search_Auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patientSearchMenu.this, Auto.class);
                startActivity(intent);
            }
        });

        Search_NotAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patientSearchMenu.this, After_Search.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
