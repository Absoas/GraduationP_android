package com.example.yb.testtalk.Menu_Search;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.yb.testtalk.R;
import com.example.yb.testtalk.model.NotificationModel;
import com.example.yb.testtalk.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Infusion_detail extends AppCompatActivity {

    TextView text_speed, text_remain_amount, text_remain_time, text_total_amount, text_name, text_disease;
    Button start;
    String num;
    private static String TAG = "Get_Infusion_detail";
    private NotificationManager mNM;
    private  Notification mNoti;


    private Timer mTimer;
    String infusion_name, infusion_total, infusion_disease , infusion_speed,infusion_remain_time;
    int infusion_remain_amount;
    private static final String TAG_ID = "ID";
    private static final String TAG_I_NAME = "INFUSION_NAME";
    private static final String TAG_T_AMOUNT = "INFUSION_TOTAL_AMOUNT";
    private static final String TAG_DISEASE = "DISEASE";
    private static final String TAG_REMAIN_TIME = "INFUSION_REMAIN_TIME";
    private static final String TAG_REMAIN_AMOUNT = "INFUSION_REMAIN_AMOUNT";
    private static final String TAG_SPEED = "INFUSION_SPEED";;
    // ProgressHandler handler;

    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;
    MainTimerTask timerTask = new MainTimerTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infusion_detail);
        mArrayList = new ArrayList<>();

        text_speed = (TextView) findViewById(R.id.motor_speed);
        text_remain_amount = (TextView) findViewById(R.id.Infusion_remain_amount);
        text_remain_time = (TextView) findViewById(R.id.Infusion_remain_time);
        text_total_amount = (TextView) findViewById(R.id.Infusion_total_amount);
        text_name = (TextView) findViewById(R.id.Infusion_name);
        text_disease = (TextView) findViewById(R.id.infusion_disease);


        Intent intent = getIntent(); // 보내온 Intent를 얻는다

        String name = intent.getStringExtra("id");

        System.out.println(name);

        setStartTime();

    }

    private void setStartTime(){
        mTimer = new Timer();
        mTimer.schedule(timerTask, 500, 1000);

    }


    private Handler handler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            Infusion_detail.GetData task = new Infusion_detail.GetData();
            task.execute(getResources().getString(R.string.infusionSelect));


            text_name.setText(infusion_name);
            text_disease.setText(infusion_disease);
            text_total_amount.setText(infusion_total + "mL");
            text_remain_amount.setText(infusion_remain_amount + "mL");
            text_speed.setText(infusion_speed + "mL/s");
            text_remain_time.setText(infusion_remain_time + "초");
        }
    };

    class  MainTimerTask extends TimerTask{
        public void run(){
            handler.post(mUpdateTimeTask);
        }
    }


    private class GetData extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {

            } else {

                mJsonString = result;
                mArrayList.clear();
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
                infusion_name = item.getString(TAG_I_NAME);
                infusion_total = item.getString(TAG_T_AMOUNT);
                infusion_disease = item.getString(TAG_DISEASE);
                infusion_remain_time = item.getString(TAG_REMAIN_TIME);
                infusion_speed= item.getString(TAG_SPEED);
                infusion_remain_amount = item.getInt(TAG_REMAIN_AMOUNT);

                String id = item.getString(TAG_ID);

                text_name.setText(infusion_name);
                text_disease.setText(infusion_disease);
                text_total_amount.setText(infusion_total + "mL");
                text_remain_amount.setText(infusion_remain_amount + "mL");
                text_speed.setText(infusion_speed + "mL/s");
                text_remain_time.setText(infusion_remain_time + "초");


                if(infusion_remain_amount <= 1){
                    sendGcm();
                    text_remain_amount.setTextSize(10);
                    text_remain_amount.setText("수액 투여가 모두 완료되었습니다.");
                    timerTask.cancel();
                }

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    void sendGcm() {

        PendingIntent intent = PendingIntent.getActivity(
                Infusion_detail.this, 0,
                new Intent(Infusion_detail.this, Infusion_detail.class), 0);


        Uri soundUri = RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_NOTIFICATION);
        mNoti = new Notification.Builder(getApplicationContext())
                .setContentTitle("간호사님")
                .setContentText("환자분 수액투여가 완료되었습니다.")
                .setSmallIcon(R.drawable.loading_icon)
                .setTicker("알림!!!")
                .setSound(soundUri) // 사운드
                .setVibrate(new long[]{0,3000}) //진동 3초
                .setAutoCancel(true) //누르면 자동 알림 지우기
                .setContentIntent(intent) // 누르면 액티비티 이동
                .build();

        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.notify(1234, mNoti);

    }

    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        Log.i(TAG, "onDestroy");
    }
}