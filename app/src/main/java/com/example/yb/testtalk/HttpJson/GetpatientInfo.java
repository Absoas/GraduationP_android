package com.example.yb.testtalk.HttpJson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.yb.testtalk.R;

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

public class GetpatientInfo extends AppCompatActivity {

    private static String TAG = "Get_PatientInfo";

    private static final String TAG_NAME = "NAME";
    private static final String TAG_AGE = "AGE";
    private static final String TAG_ID = "ID";
    private static final String TAG_PATIENT_ROOM = "PATIENT_ROOM";
    private static final String TAG_BPM = "BPM";
    private static final String TAG_BODY_TEMP = "BODY_TEMP";
    private static final String TAG_RESPIRATION = "RESPIRATION";
    private static final String TAG_BLOOD_PRESSURE = "BLOOD_PRESSURE";
    private static final String TAG_THE_OTHERS = "THE_OTHERS";

    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_patient_info);


        mlistView = (ListView) findViewById(R.id.listView1);

        mArrayList = new ArrayList<>();

        GetpatientInfo.GetData task = new GetpatientInfo.GetData();
        task.execute(getResources().getString(R.string.JoinSelect));
    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GetpatientInfo.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {

            } else {

                mJsonString = result;
                showResult();
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

    public void showResult() {
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                String name = item.getString(TAG_NAME);
                String age = item.getString(TAG_AGE);
                String id = item.getString(TAG_ID);
                String patient_room = item.getString(TAG_PATIENT_ROOM);
                String bpm = item.getString(TAG_BPM);
                String body_temp = item.getString(TAG_BODY_TEMP);
                String respiration = item.getString(TAG_RESPIRATION);
                String blood_pressure = item.getString(TAG_BLOOD_PRESSURE);
                String the_others = item.getString(TAG_THE_OTHERS);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_AGE, age);
                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_PATIENT_ROOM, patient_room);
                hashMap.put(TAG_BPM, bpm);
                hashMap.put(TAG_BODY_TEMP, body_temp);
                hashMap.put(TAG_RESPIRATION, respiration);
                hashMap.put(TAG_BLOOD_PRESSURE, blood_pressure);
                hashMap.put(TAG_THE_OTHERS, the_others);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    GetpatientInfo.this, mArrayList, R.layout.list_patient_item,
                    new String[]{TAG_ID , TAG_NAME, TAG_AGE, TAG_PATIENT_ROOM},
                    new int[]{R.id.textView_list_id,R.id.textView_list_name, R.id.textView_list_age,R.id.textView_list_room}
            );
            mlistView.setAdapter(adapter);

            mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재화면의 제어권자
                            PatientInfo.class); // 다음넘어갈 화면

                    intent.putExtra("name", mArrayList.get(position).get(TAG_NAME));
                    intent.putExtra("age", mArrayList.get(position).get(TAG_AGE));
                    intent.putExtra("id", mArrayList.get(position).get(TAG_ID));
                    intent.putExtra("room", mArrayList.get(position).get(TAG_PATIENT_ROOM));
                    intent.putExtra("bpm", mArrayList.get(position).get(TAG_BPM));
                    intent.putExtra("temp", mArrayList.get(position).get(TAG_BODY_TEMP));
                    intent.putExtra("respiration", mArrayList.get(position).get(TAG_RESPIRATION));
                    intent.putExtra("press", mArrayList.get(position).get(TAG_BLOOD_PRESSURE));
                    intent.putExtra("others", mArrayList.get(position).get(TAG_THE_OTHERS));

                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

}