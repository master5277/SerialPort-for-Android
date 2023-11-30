package com.ex.serialport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ex.serialport.R;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Button button_4G = (Button) findViewById(R.id.btn_4G);
        button_4G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentone = new Intent(InitialActivity.this, FourGActivity.class);
                startActivity(intentone);
            }
        });
        Button button_Serialport = (Button) findViewById(R.id.btn_serialportinitial);
        button_Serialport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenttwo = new Intent(InitialActivity.this,MainActivity.class);
                startActivity(intenttwo);
            }
        });
    }
}