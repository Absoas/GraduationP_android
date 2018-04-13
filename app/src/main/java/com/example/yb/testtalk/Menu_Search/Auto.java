package com.example.yb.testtalk.Menu_Search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yb.testtalk.Menu_Search.AutoClass.AudioWriterPCM;
import com.example.yb.testtalk.R;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import java.lang.ref.WeakReference;
import java.util.List;

public class Auto extends Activity {


    private static final String TAG = Auto.class.getSimpleName();
    private static final String CLIENT_ID = "6os8bnsBSk62Kz0bJNEN";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    TextView[] txt = new TextView[10];
    String[] array = new String [50];
    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    String[] mResult2 = new String [10];
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
                for(int i =0; i< results.size(); i++) {
                    txt[i] = new TextView(this);
                    array[i] = results.get(i);
                    mResult2[i] = array[i];
                    txt[i].setText("검색 결과 : '"+mResult2[i]+"' 입니다.");
                    txt[i].setHeight(100);
                    txt[i].setWidth(100);
                    txt[i].setTag(i);
                    linearLayout1.addView(txt[i]);
                }

                System.out.println(txt.length);
                String ch ="김용빈";
                txtResult.setText(mResult);

                for(int i=0; i<results.size(); i++){
                    if(array[i].equals(ch)){
                        txt[i].setText(null);
                        Toast.makeText(getApplicationContext(), "환자 이름이 검색되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, After_Search.class);
                        startActivity(intent);
                    }else {
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

        txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart = (Button) findViewById(R.id.btn_start);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
