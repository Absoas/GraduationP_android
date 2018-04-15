package com.example.yb.testtalk.HttpJson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.yb.testtalk.R;

public class PatientInfo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_patient);

        TextView tvID = (TextView)findViewById(R.id.textView1);
        TextView tvNAME = (TextView)findViewById(R.id.textView2);
        TextView tvBPM = (TextView)findViewById(R.id.textView3);
        TextView tvTEMP = (TextView)findViewById(R.id.textView4);
        TextView tvresi = (TextView)findViewById(R.id.textView5);
        TextView tvroom = (TextView)findViewById(R.id.textView6);
        TextView tvpress = (TextView)findViewById(R.id.textView7);
        TextView tvage = (TextView)findViewById(R.id.textView8);
        TextView tvothers = (TextView)findViewById(R.id.textView9);

        Intent intent = getIntent(); // 보내온 Intent를 얻는다

        tvID.setText(intent.getStringExtra("id"));
        tvNAME.setText(intent.getStringExtra("name"));
        tvage.setText(intent.getStringExtra("age"));
        tvpress.setText(intent.getStringExtra("press"));
        tvBPM.setText(intent.getStringExtra("bpm"));
        tvTEMP.setText(intent.getStringExtra("temp"));
        tvroom.setText(intent.getStringExtra("room"));
        tvothers.setText(intent.getStringExtra("others"));
        tvresi.setText(intent.getStringExtra("respiration"));

    } // end of onCreate
} // end of class
