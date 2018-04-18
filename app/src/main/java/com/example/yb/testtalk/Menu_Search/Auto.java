package com.example.yb.testtalk.Menu_Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yb.testtalk.HttpJson.PatientInfoDetail;
import com.example.yb.testtalk.Menu_Search.AutoClass.AudioWriterPCM;
import com.example.yb.testtalk.R;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Auto extends Activity {

    private static final String TAG_NAME = "NAME";
    private static final String TAG_AGE = "AGE";
    private static final String TAG_ID = "ID";
    private static final String TAG_PATIENT_ROOM = "PATIENT_ROOM";
    private static final String TAG_BPM = "BPM";
    private static final String TAG_BODY_TEMP = "BODY_TEMP";
    private static final String TAG_RESPIRATION = "RESPIRATION";
    private static final String TAG_BLOOD_PRESSURE = "BLOOD_PRESSURE";
    private static final String TAG_THE_OTHERS = "THE_OTHERS";

    HashMap<String, String> hashMap = new HashMap<>();
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;

    private static final String TAG = Auto.class.getSimpleName();
    private static final String CLIENT_ID = "6os8bnsBSk62Kz0bJNEN";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    TextView[] txt = new TextView[10];
    String[] array = new String[50];
    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    String[] mResult2 = new String[10];
    private LinearLayout linearLayout1;

    private AudioWriterPCM writer;

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                for (int i = 0; i < results.size(); i++) {
                    txt[i] = new TextView(this);
                    array[i] = results.get(i);
                    mResult2[i] = array[i];
                    txt[i].setText("검색 결과 : '" + mResult2[i] + "' 입니다.");
                    txt[i].setHeight(100);
                    txt[i].setWidth(100);
                    txt[i].setTag(i);
                    linearLayout1.addView(txt[i]);
                }

                System.out.println(txt.length);
                System.out.println();
                String ch = "김용빈";
                txtResult.setText(mResult);

                for (int i = 0; i < results.size(); i++) {
                    if (array[i].equals(ch)) {
                        showResult();
                    } else {
                        Toast.makeText(getApplicationContext(), "환자 이름을 검색하지 못하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);


        mlistView = (ListView) findViewById(R.id.AutolistView);
        mArrayList = new ArrayList<>();

        Auto.GetData task = new Auto.GetData();
        task.execute(getResources().getString(R.string.JoinSelect));

        linearLayout1 = (LinearLayout) findViewById(R.id.AutoLinearLayout);
        txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart = (Button) findViewById(R.id.btn_start);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    btnStart.setText(R.string.str_stop);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);


    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();


    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<Auto> mActivity;

        RecognitionHandler(Auto activity) {
            mActivity = new WeakReference<Auto>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Auto activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Auto.this,
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
                    Auto.this, mArrayList, R.layout.list_patient_item,
                    new String[]{TAG_ID , TAG_NAME, TAG_AGE, TAG_PATIENT_ROOM},
                    new int[]{R.id.textView_list_id,R.id.textView_list_name, R.id.textView_list_age,R.id.textView_list_room}
            );
            mlistView.setAdapter(adapter);

            mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재화면의 제어권자
                            PatientInfoDetail.class); // 다음넘어갈 화면

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
