package com.swufe.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //定义类变量
    TextView out;
    EditText inp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //查找id：findViewById(R.id....)
        out = (TextView) findViewById(R.id.showText);
        inp = (EditText)findViewById(R.id.tv_input);
        //String str = inp.getText().toString(); //Editable->String

        //android打印不用System.out.println(),用log
        //Log.i("main","input=" + str);

        Button btn = (Button)findViewById(R.id.btn1);

        //btn.setOnClickListener(this);
        //事件绑定需要监听器。若使用当前类做监听，则当前类需要实现接口

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("mail","onClick called....");
                String str = inp.getText().toString();

                out.setText("Hello " + str);
            }
        });
    }

    public void onClick(View v) {
        Log.i("click","onClick .....");

        //TextView tv = (TextView) findViewById(R.id.showText);
        //EditText inp = (EditText)findViewById(R.id.inputText);
        String str = inp.getText().toString();

        out.setText("Hello " + str);
    }

    public void btn(View v) {
        Log.i("click", "onClick .....");
    }
}
