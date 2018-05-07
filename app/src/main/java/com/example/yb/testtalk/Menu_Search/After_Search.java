package com.example.yb.testtalk.Menu_Search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.yb.testtalk.R;

public class After_Search extends AppCompatActivity {

    Button Fsearch,Ssearch;
    //Spinner spinner;
    // ArrayList<String > list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_search);


        Fsearch = (Button) findViewById(R.id.after_search1);
        Ssearch = (Button) findViewById(R.id.after_search2);
        // spinner = (Spinner) findViewById(R.id.after_search_spinner);



//        Ssearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getFragmentManager().beginTransaction().replace(R.id.Search_fragment,new Patient_search_detailFragment()).commit();
//            }
//        });
//
//        Fsearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getFragmentManager().beginTransaction().replace(R.id.Search_fragment,new Patient_search_detailFragment()).commit();
//            }
//        });


    }

//
//
//    public void Setspinner(){
//        ArrayAdapter spinnerAdatper;
//
//        spinnerAdatper = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,list);
//        spinner.setPrompt("검색 할 층수를 선택하세요.");
//        spinner.setAdapter(spinnerAdatper);
//
//        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long d){
//                spinner.getItemAtPosition(position).toString()
//            }
//        });
//
//    }
}