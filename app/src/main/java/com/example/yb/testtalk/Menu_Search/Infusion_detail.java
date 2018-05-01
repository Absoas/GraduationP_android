package com.example.yb.testtalk.Menu_Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    TextView speed, remain_amount, remain_time, total_amount, infusion_name, infusion_disease;
    Button start;
    String num;
    private static String TAG = "Get_Infusion_detail";
    private UserModel destinationUserModel;


    static double infusion_speed = 0.33;
    private Timer mTimer;
    double infusion_total;
    String name, total, disease;
    private static final String TAG_ID = "ID";
    private static final String TAG_I_NAME = "INFUSION_NAME";
    private static final String TAG_T_AMOUNT = "INFUSION_TOTAL_AMOUNT";
    private static final String TAG_DISEASE = "DISEASE";
    private static final String TAG_REMAIN_TIME = "REMAINTIME";
    private static final String TAG_R_AMOUNT = "TOTALAMOUNT";
    private static final String TAG_SPEED = "SPEED";
    // ProgressHandler handler;

    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;
    MainTimerTask timerTask = new MainTimerTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infusion_detail);
        mArrayList = new ArrayList<>();



        start = (Button) findViewById(R.id.btn_infusion_start);
        speed = (TextView) findViewById(R.id.motor_speed);
        remain_amount = (TextView) findViewById(R.id.Infusion_remain_amount);
        remain_time = (TextView) findViewById(R.id.Infusion_remain_time);
        total_amount = (TextView) findViewById(R.id.Infusion_total_amount);
        infusion_name = (TextView) findViewById(R.id.Infusion_name);
        infusion_disease = (TextView) findViewById(R.id.infusion_disease);

        Infusion_detail.GetData task = new Infusion_detail.GetData();
        task.execute(getResources().getString(R.string.infusionSelect));

        Intent intent = getIntent(); // 보내온 Intent를 얻는다

        String name = intent.getStringExtra("id");

        System.out.println(name);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartTime();
            }
        });

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String callValue =pref.getString("key", "");

        remain_amount.setText(callValue+"mL");

    }

    private void setStartTime(){
        mTimer = new Timer();
        mTimer.schedule(timerTask, 500, 1000);

    }


    private Handler handler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
                    infusion_total = infusion_total - infusion_speed;
                    num = String.format("%.2f" , infusion_total);
                    remain_amount.setText(String.valueOf(num) +"mL");

                    Log.d("TAG",num);
                    if(infusion_total<=0.00){
                        sendGcm();
                        timerTask.cancel();
                        remain_amount.setText("수액 투여가 모두 완료되었습니다.");
                    }

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("key", num);
                    editor.commit();

        }
    };

    class  MainTimerTask extends TimerTask{
        public void run(){
            handler.post(mUpdateTimeTask);
        }
    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Infusion_detail.this,
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
                name = item.getString(TAG_I_NAME);
                total = item.getString(TAG_T_AMOUNT);
                disease = item.getString(TAG_DISEASE);
                String id = item.getString(TAG_ID);

                infusion_total = item.getInt(TAG_T_AMOUNT);


                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(TAG_I_NAME, name);
                hashMap.put(TAG_T_AMOUNT, total);
                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_DISEASE, disease);

                infusion_name.setText(name);
                infusion_disease.setText(disease);
                total_amount.setText(total + "mL");
                speed.setText(infusion_speed + "ML/s");

                mArrayList.add(hashMap);
            }

                } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    void sendGcm() {

        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = "";
        notificationModel.data.title = userName;
        notificationModel.data.text = "";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyB3Y1luYBFpMVSaP-tZgeT0Gn6SFJPv1TE")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();
          OkHttpClient okHttpClient = new OkHttpClient();
          okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }
}
