package com.example.yb.testtalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yb.testtalk.Menu_Insert.Insert_to_PatientInfo;
import com.example.yb.testtalk.menu.patientSearchMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private Button menu_search , menu_insert, menu_chatting;
    private DatabaseReference mdatabase;
    String uid;
    ArrayList<String> mArrayList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu_search= (Button) findViewById(R.id.menuButton_1);
        menu_insert= (Button) findViewById(R.id.menuButton_2);
        menu_chatting= (Button) findViewById(R.id.menuButton_3);



        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        mdatabase = FirebaseDatabase.getInstance().getReference();


        // 데이터베이스 읽기 #2. Single ValueEventListener
        FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("TempTest", "Single ValueEventListener : " + snapshot.getValue());
                    mArrayList.add(snapshot.getValue().toString());
                }

                // ArrayList 값 확인
                for(int i = 0; i < mArrayList.size(); i++) {
                    System.out.println("one index " + i + " : value " + mArrayList.get(i));
                }

                final String permission = mArrayList.get(0).toString();
                final String userName = mArrayList.get(4).toString();


                menu_search.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (permission.equals("간호사")) {
                                Toast.makeText(MenuActivity.this, "안녕하세요. "+userName+" "+permission+"님", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MenuActivity.this, patientSearchMenu.class);
                                startActivity(intent);
                            }else if(permission.equals("환자")){
                                showDialog();
                            }
                        }
                    });

                    menu_insert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (permission.equals("간호사")) {
                                Toast.makeText(MenuActivity.this, "안녕하세요. "+userName+" "+permission+"님", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MenuActivity.this, Insert_to_PatientInfo.class);
                                startActivity(intent);
                            }else if(mArrayList.get(0).equals("환자")){
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

