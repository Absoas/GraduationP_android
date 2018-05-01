package com.example.yb.testtalk.Menu_Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yb.testtalk.MenuActivity;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class PatientInfoDetail extends Activity {
    private static String TAG = "SetJSon";
    private static final String TAG_TEMP = "TEMP";
    private static final String TAG_BPM = "BPM";
    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    Button delete;
    ListView mlistView;
    String mJsonString;
    GraphView graph,graph1;
    double[] Jsontemp = new double[100];
    int[] Jsonbpm = new int[100];
    Button Infusion;
    String id;
    TextView tvID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail);

        tvID = (TextView)findViewById(R.id.textView1);
        TextView tvNAME = (TextView)findViewById(R.id.textView2);
        TextView tvBPM = (TextView)findViewById(R.id.textView3);
        TextView tvTEMP = (TextView)findViewById(R.id.textView4);
        TextView tvresi = (TextView)findViewById(R.id.textView5);
        TextView tvroom = (TextView)findViewById(R.id.textView6);
        TextView tvpress = (TextView)findViewById(R.id.textView7);
        TextView tvage = (TextView)findViewById(R.id.textView8);
        TextView tvothers = (TextView)findViewById(R.id.textView9);

        Intent intent = getIntent(); // 보내온 Intent를 얻는다

        id = intent.getStringExtra("id");

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
        delete = (Button) findViewById(R.id.delete_button);

        mArrayList = new ArrayList<>();
        PatientInfoDetail.GetData task = new PatientInfoDetail.GetData();
        task.execute(getResources().getString(R.string.users));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog();
            }
        });

        Infusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientInfoDetail.this, Infusion_detail.class);

                intent.putExtra("name", tvID.getText().toString() );
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

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id",id);


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
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

    void alertdialog(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Wellfare")
                .setMessage("데이터를 정말 삭제하시겠습니까??")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PatientInfoDetail.JSONTask task1 = new PatientInfoDetail.JSONTask();
                        task1.execute(getResources().getString(R.string.delete));
                        Toast.makeText(PatientInfoDetail.this, "식별번호 "+id+" 번 환자분의 데이터가 사라졌습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PatientInfoDetail.this, MenuActivity.class);
                        startActivity(intent);}

                })
                .setNegativeButton("No", null)
                .show();
    }
} // end of onCreate
