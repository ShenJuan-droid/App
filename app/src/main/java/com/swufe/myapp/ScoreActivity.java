package com.swufe.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private static final String TAG = "score";
    TextView score;
    TextView score2;

    public void btnAdd2(View view) {
    }

    public void btnAdd1(View view) {
    }

    public void btnAdd3(View view) {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Log.i(TAG, "onCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String scorea = ((TextView)findViewById(R.id.score)).getText().toString();
        String scoreb = ((TextView)findViewById(R.id.score2)).getText().toString();

        Log.i(TAG, "onSaveInstanceState: ");
        outState.putString("teama_score",scorea);
        outState.putString("teamb_score",scoreb);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String scorea = savedInstanceState.getString("teama_score");
        String scoreb = savedInstanceState.getString("teamb_score");

        Log.i(TAG, "onRestoreInstanceState: ");
        ((TextView)findViewById(R.id.score)).setText(scorea);
        ((TextView)findViewById(R.id.score2)).setText(scoreb);
    }
    public class SecondActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_score);

            score = (TextView) findViewById(R.id.score);
        }


        public void btnAdd1(View btn) {
            if(btn.getId()==R.id.btn1a) {
                showScore(1);
            }else{
                showScore2(1);
            }
        }

        public void btnAdd2(View btn) {
            if(btn.getId()==R.id.btn1a) {
                showScore(2);
            }else{
                showScore2(2);
            }
        }

        public void btnAdd3(View btn) {
            if(btn.getId()==R.id.btn1a) {
                showScore(3);
            }else{
                showScore2(3);
            }
        }

        public void btnReset(View btn) {
            score.setText("0");
            score2.setText("0");
        }

        private void showScore(int inc) {
            Log.i("show", "inc=" + inc);
            String oldScore = (String) score.getText();
            int newScore = Integer.parseInt(oldScore) + inc;
            score.setText("" + newScore);
        }

        private void showScore2(int inc) {
            Log.i("show", "inc=" + inc);
            String oldScore = (String) score2.getText();
            int newScore = Integer.parseInt(oldScore) + inc;
            score2.setText("" + newScore);
        }
    }

}
