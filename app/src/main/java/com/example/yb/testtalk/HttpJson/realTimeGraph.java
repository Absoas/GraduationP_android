package com.example.yb.testtalk.HttpJson;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class realTimeGraph extends Fragment {

    private static String TAG = "SetJSon";
    private static final String TAG_TEMP = "TEMP";
    private static final String TAG_BPM = "BPM";
    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;
    GraphView graph, graph1;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> series1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.realtime_test, container, false);

        graph = (GraphView) rootView.findViewById(R.id.graph);
        graph1 = (GraphView) rootView.findViewById(R.id.graph1);

        series = new LineGraphSeries<>(generateData());
        series1 = new LineGraphSeries<>(generateData1());

        graph.addSeries(series);
        graph.addSeries(series1);

        mArrayList = new ArrayList<>();
        GetData task = new GetData();
        task.execute(getResources().getString(R.string.users));

        return rootView;
    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {
            } else {

//                mArrayList.clear();
//                mJsonString = result;
//                showResult();
//                showGraph();
//                swipeContainer.setRefreshing(false);
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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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

    public DataPoint[] generateData() {
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);
            int list_cnt = jsonArray.length();

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