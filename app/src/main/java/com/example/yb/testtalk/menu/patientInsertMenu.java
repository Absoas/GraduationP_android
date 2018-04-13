package com.example.yb.testtalk.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.yb.testtalk.Menu_Insert.Insert_to_PatientInfo;
import com.example.yb.testtalk.R;

public class patientInsertMenu extends AppCompatActivity {

    Button Patient_Insert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_insert_menu);
        Patient_Insert = (Button)findViewById(R.id.Menu_Patient_Insert);

        Patient_Insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patientInsertMenu.this, Insert_to_PatientInfo.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
