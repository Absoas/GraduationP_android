package com.example.yb.testtalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yb.testtalk.Menu_Insert.Insert_to_PatientInfo;
import com.example.yb.testtalk.Menu_Search.GetpatientInfo;
import com.example.yb.testtalk.Menu_Search.patientSearchMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private Button menu_search , menu_chatting;
    private DatabaseReference mdatabase;
    String uid;
    ArrayList<String> mArrayList = new ArrayList<String>();
    String permission,userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu_search= (Button) findViewById(R.id.menuButton_1);

        menu_chatting= (Button) findViewById(R.id.menuButton_3);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        mdatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() { //DB의 users 하위 uid에 있는 데이터 중
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 모든 데이터를 돔
                    mArrayList.add(snapshot.getValue().toString()); // 배열에 데이터 넣음
                }

                permission = mArrayList.get(1).toString(); // permission 환자 인지 간호사인지 체크
                userName = mArrayList.get(0).toString();  // 이름 체크

                menu_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (permission.equals("간호사")) {
                            Toast.makeText(MenuActivity.this, "안녕하세요. "+userName+" "+permission+"님", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MenuActivity.this, GetpatientInfo.class);
                            startActivity(intent);
                        }else if(permission.equals("환자")){
                            showDialog();
                        }
                    }
                });


                menu_chatting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (permission.equals("간호사") || permission.equals("환자")) {
                            Toast.makeText(MenuActivity.this, "안녕하세요. "+userName+" "+permission+"님", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Wellfare")
                .setMessage("Are you sure you want to close?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("  접속 제한  ");
        builder.setMessage("접속 할 수 있는 권한이 아닙니다.");
        builder.show();

    }
}

