package com.example.yb.testtalk.Menu_Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yb.testtalk.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class PatientInfoDetail extends Activity {
    private static String TAG = "SetJSon";
    private static final String TAG_TEMP = "TEMP";
    private static final String TAG_BPM = "BPM";
    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    GraphView graph,graph1;
    double[] Jsontemp = new double[100];
    int[] Jsonbpm = new int[100];
    Button Infusion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail);

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

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mlistView = (ListView) findViewById(R.id.listView_main_list);
        graph = (GraphView) findViewById(R.id.graph);
        graph1 = (GraphView) findViewById(R.id.graph1);
        Infusion = (Button) findViewById(R.id.Infusion_detail);

        mArrayList = new ArrayList<>();
        PatientInfoDetail.GetData task = new PatientInfoDetail.GetData();
        task.execute(getResources().getString(R.string.users));

        Infusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientInfoDetail.this, Infusion_detail.class);
                startActivity(intent);
            }
        });

    }
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PatientInfoDetail.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mArrayList.clear();
                mJsonString = result;
                showResult();
                showGraph();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }


    public void showResult(){
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);

            int list_cnt = jsonArray.length();

            Jsonbpm = new int[list_cnt];
            Jsontemp = new double[list_cnt];

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String temp = item.getString(TAG_TEMP);
                String bpm = item.getString(TAG_BPM);

                Jsontemp[i] = item.getDouble(TAG_TEMP);
                Jsonbpm[i] = item.getInt(TAG_BPM);

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(TAG_TEMP, temp);
                hashMap.put(TAG_BPM, bpm);
                mArrayList.add(hashMap);
            }

            DataPoint[] points = new DataPoint[Jsontemp.length];
            for(int i=0; i<Jsontemp.length; i++){
                points[i] = new DataPoint(i,Jsontemp[i]);
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

            DataPoint[] points1 = new DataPoint[Jsonbpm.length];
            for(int i=0; i<Jsonbpm.length; i++){
                points1[i] = new DataPoint(i,Jsonbpm[i]);
            }LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(points1);

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(PatientInfoDetail.this, "Series1: On Data Point clicked: "+dataPoint, Toast.LENGTH_SHORT).show();
                }
            });

            series1.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(PatientInfoDetail.this, "Series1: On Data Point clicked: "+dataPoint, Toast.LENGTH_SHORT).show();
                }
            });

            graph.addSeries(series);
            graph1.addSeries(series1);

            //mlistView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    public void showGraph(){

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(30);
        graph.getViewport().setMaxY(40);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        graph1.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setMinY(60);
        graph1.getViewport().setMaxY(120);

        graph1.getViewport().setXAxisBoundsManual(true);
        graph1.getViewport().setMinX(0);
        graph1.getViewport().setMaxX(10);

        // enable scaling and scrolling
        graph1.getViewport().setScalable(true);
        graph1.getViewport().setScalableY(true);

    }
} // end of onCreate
