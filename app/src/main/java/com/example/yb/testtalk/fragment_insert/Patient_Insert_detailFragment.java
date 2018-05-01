package com.example.yb.testtalk.fragment_insert;

        import android.app.Fragment;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;

        import com.example.yb.testtalk.R;

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

public class Patient_Insert_detailFragment extends Fragment {

    EditText respiration,pin,temp,bpm,blood_pressure,thrOther,Patient_Room;
    Button Send;
    TextView resultRx;
    Spinner room;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insert_detail,container,false);
        room = (Spinner) view.findViewById(R.id.spinnerRoom);
        pin = (EditText) view.findViewById(R.id.Edit_patient_pin);
        temp = (EditText) view.findViewById(R.id.Edit_patient_temp);
        bpm = (EditText) view.findViewById(R.id.Edit_patient_bpm);
        blood_pressure = (EditText) view.findViewById(R.id.Edit_patient_blood_pressure);
        respiration = (EditText) view.findViewById(R.id.Edit_patient_respiration);
        thrOther = (EditText) view.findViewById(R.id.Edit_patient_the_others);
        Patient_Room = (EditText) view.findViewById(R.id.Edit_patient_Patient_Room);

        resultRx = (TextView) view.findViewById(R.id.textView_detail);
        room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Patient_Room.setText(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        Send = (Button) view.findViewById(R.id.Button_append_detail);

        Send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Patient_Insert_detailFragment.JSONTask task = new Patient_Insert_detailFragment.JSONTask();
                task.execute(getResources().getString(R.string.infoDetail));
            }
        });

        return view;
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id",pin.getText().toString());
                jsonObject.accumulate("temp",temp.getText().toString());
                jsonObject.accumulate("bpm", bpm.getText().toString());
                jsonObject.accumulate("blood_pressure", blood_pressure.getText().toString());
                jsonObject.accumulate("respiration", respiration.getText().toString());
                jsonObject.accumulate("theOther", thrOther.getText().toString());
                jsonObject.accumulate("Patient_Room", Patient_Room.getText().toString());


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
            resultRx.setText(result);//서버로 부터 받은 값을 출력해주는 부
        }
    }
}
