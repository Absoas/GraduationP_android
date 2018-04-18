package com.example.yb.testtalk.HttpJson;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yb.testtalk.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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


public class GetJson extends AppCompatActivity {
    private Runnable mTimer;
    private final Handler mHandler = new Handler();
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
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> series1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getjson);

        mTextViewResult = (TextView) findViewById(R.id.textView_main_result);
        mlistView = (ListView) findViewById(R.id.listView_main_list);

        graph = (GraphView) findViewById(R.id.graph);
        graph1 = (GraphView) findViewById(R.id.graph1);

        series = new LineGraphSeries<>(generateData());
        series1 = new LineGraphSeries<>(generateData1());

        graph.addSeries(series);
        graph.addSeries(series1);

        mArrayList = new ArrayList<>();
        GetData task = new GetData();
        task.execute(getResources().getString(R.string.users));

    }


    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GetJson.this,
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

                mJsonString = result;
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


    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Runnable() {
            @Override
            public void run() {
                series.resetData(generateData());
                mHandler.postDelayed(this, 300);
            }
        };
        mHandler.postDelayed(mTimer, 300);

    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    public DataPoint[] generateData() {
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);

            int list_cnt = jsonArray.length();
            System.out.println(list_cnt);

            DataPoint[] points = new DataPoint[list_cnt];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                double Jsontemp = item.getDouble(TAG_TEMP);
                DataPoint v = new DataPoint(i, Jsontemp);

                points[i] = v;
            }
            return points;
        }
        catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);

        }
        return new DataPoint[0];
    }

    public DataPoint[] generateData1() {
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);
            int list_cnt = jsonArray.length();

            DataPoint[] points = new DataPoint[list_cnt];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                int Jsonbpm = item.getInt(TAG_BPM);
                DataPoint v = new DataPoint(i, Jsonbpm);

                points[i] = v;
            }
            return points;
        }
        catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);

        }
        return new DataPoint[0];
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
}