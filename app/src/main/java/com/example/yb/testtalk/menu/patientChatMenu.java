package com.example.yb.testtalk.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.yb.testtalk.Menu_Chat.MessageActivity;
import com.example.yb.testtalk.R;

public class patientChatMenu extends AppCompatActivity {

    Button chatting_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_menu);
        chatting_menu = (Button)findViewById(R.id.chatting_menu);

        chatting_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patientChatMenu.this, MessageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
