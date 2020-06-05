package com.swufe.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Week4Activity extends AppCompatActivity {

    private EditText Input;
    private Button btn;
    private TextView Result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week4);

        Input = (EditText) findViewById(R.id.tv_input);
        btn = (Button)findViewById(R.id.btn1);
        Result = (TextView)findViewById(R.id.tv_result);

        Button btn = (Button)findViewById(R.id.btn1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float C = Float.parseFloat(Input.getText().toString());
                Result.setText(String.valueOf(trans(C)));
            }
        });

    }
    private float trans(float c){
        return (c*1.8f)+32.0f;
    }

}
