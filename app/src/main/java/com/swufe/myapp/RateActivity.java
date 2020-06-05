package com.swufe.myapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable{

    private final String TAG = "Rate";

    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    private String updateDate = "";

    EditText rmb;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.show);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);//推荐方法，版本>24
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);

        String updateDate = sharedPreferences.getString("update_date","");
        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today); //转型

        //defValue设置默认值
        Log.i(TAG, "onCreate: sp dollarRate=" + dollarRate);
        Log.i(TAG, "onCreate: sp euroRate=" + euroRate);
        Log.i(TAG, "onCreate: sp wonRate=" + wonRate);
        Log.i(TAG, "onCreate: sp updateDate=" + updateDate);
        Log.i(TAG, "onCreate: todayStr=" + todayStr);

        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i(TAG, "onCreate: 需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG, "onCreate: 不需要更新");
        }

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    Bundle bdl = (Bundle) msg.obj;//强制转型
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");
                    /*Log.i(TAG, "handleMessage: getMessage msg = " + str);
                    show.setText(str);*/
                    Log.i(TAG, "handleMessage: dollarRate = " + dollarRate);
                    Log.i(TAG, "handleMessage: euroRate = " + euroRate);
                    Log.i(TAG, "handleMessage: wonRate = " + wonRate);

                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);

                //保存更新的日期
                SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putFloat("dollar_rate",dollarRate);
                editor.putFloat("euro_rate",euroRate);
                editor.putFloat("won_rate",wonRate);
                String todayStr = new String();
                editor.putString("update_date",todayStr);
                editor.apply();
            }
        };//类和类方法，重写
    }

    @SuppressLint("DefaultLocale")
    public void onClick(View btn) {
        //获取用户输入内容
        Log.i(TAG, "onClick: ");
        String str = rmb.getText().toString();
        Log.i(TAG, "onClick: get str=" + str);

        float r = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            //用户没有输入内容
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "onClick: r=" + r);


        //计算1：常量计算
//        float val=0;
//        if(btn.getId()==R.id.btn_dollar){
//            val = r*(1/6.7f);
//            show.setText(String.valueOf(val));
//        }else if(btn.getId()==R.id.btn_euro){
//            val = r*(1/11f);
//            show.setText(val+"");
//        }else{
//            val = r*(1/500f);
//        }
//        show.setText(String.valueOf(val));
//    }

        //计算2：数值直接计算
        if (btn.getId() == R.id.btn_dollar) {
            show.setText(String.format("%.2f",r * dollarRate));
        } else if (btn.getId() == R.id.btn_euro) {
            show.setText(String.format("%.2f",r * euroRate));
        } else {
            show.setText(String.format("%.2f",r * wonRate));
        }
    }
//    public void openOne(View btn){
//        //打开一个页面activity
//        Log.i("open","openOne");
//        Intent hello = new Intent(this,Week4Activity.class);
//        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jd.com"));
//        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:87092"));
//        //startActivity(hello);
//        //startActivity(web);
//        //startActivity(dial);
//
//        finish();
//    }

    public void openConfig(){
        openConfig();
    }

    public void openConfig(View btn){
        Intent config = new Intent(this,ConfigActivity.class);
        //传值
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);

        //输出
        Log.i(TAG, "openOne: dollarRate=" + dollarRate);
        Log.i(TAG, "openOne: euroRate=" + euroRate);
        Log.i(TAG, "openOne: wonRate=" + wonRate);

        //startActivity(config);
        startActivityForResult(config,1);//带回数据(打开对象，整数)
    }

    @Override
    //添加菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.open_list){
            openConfig();
        }else if(item.getItemId()==R.id.menu_set){
            //打开列表窗口
            Intent list = new Intent(this,MyList2Activity.class);
            startActivity(list);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            /*
            bdl.putFloat("key_dollar", newDollar);
         bdl.putFloat("key_euro", newEuroi);
         bdl.putFloat("key_won", newWon);
             */
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);
            Log.i(TAG, "onActivityResult: dollarResult" + dollarRate);
            Log.i(TAG, "onActivityResult: euroResult" + euroRate);
            Log.i(TAG, "onActivityResult: wonResult" + wonRate);

            //将新设置的汇率写到SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");
        }

    }

    @Override
    public void run() {
        //用于保存获取的利率
        Bundle bundle = new Bundle();

        Log.i(TAG, "run: run()......");
        for(int i=1;i<6;i++){
            Log.i(TAG, "run: i=" + i);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }//当前停止两秒钟
        }

        //获取网络数据
        /*URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();//强制转型
            InputStream in = http.getInputStream();//获得输入流

            String html = inputStream2String(in);
            Log.i(TAG, "run: html=" + html);
            Document doc = Jsoup.parse(html);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        // bundle = getFromBOC();

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        msg.obj = bundle;
        handler.sendMessage(msg);

    }

    /*
    从bankofchina获取数据
     */
    private Bundle GetFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //相当于  doc = Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");//得到集合
                /*for(Element table : tables){
                    Log.i(TAG, "run: table=["+i+"]" + table);
                    i++;
                }*/

            Element table6 = tables.get(5);
            //Log.i(TAG, "run: table6=" + table6);
            //获取TD中的数据
            Elements tds = table6.getElementsByTag("td");
            for(int i = 0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG, "run: " + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();

                float v = 100f / Float.parseFloat(val);
                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate", v);
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate", v);
                }else if("韩国元".equals(str1)){
                    bundle.putFloat("won-rate", v);
                }
            }


               /* for(Element td : tds){
                    Log.i(TAG, "run: td=" + td);
                    Log.i(TAG, "run: text=" + td.text());
                    Log.i(TAG, "run: html=" + td.html());
                }*/

                /*Elements newsHeadinglines = doc.select("#mp-itn b a");
                for(Element headline : newsHeadinglines){
                    Log.i(TAG, "%s\n\t%s" + headline.attr("title") + headline.absUrl("href"));
                }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    /*
    从bankofchina获取数据
     */
    private Bundle GetFromUsdCny() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //相当于  doc = Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");//得到集合
                /*for(Element table : tables){
                    Log.i(TAG, "run: table=["+i+"]" + table);
                    i++;
                }*/

            Element table2 = tables.get(1);
            //Log.i(TAG, "run: table6=" + table6);
            //获取TD中的数据
            Elements tds = table2.getElementsByTag("td");
            for(int i = 0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);

                String str1 = td1.text();
                String val = td2.text();

                Log.i(TAG, "run: " + str1 + "==>" + val);

//                float v = 100f / Float.parseFloat(val);
                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate", 100f/Float.parseFloat(val));
                }else if("韩国元".equals(str1)){
                    bundle.putFloat("won-rate", 100f/Float.parseFloat(val));
                }
            }


               /* for(Element td : tds){
                    Log.i(TAG, "run: td=" + td);
                    Log.i(TAG, "run: text=" + td.text());
                    Log.i(TAG, "run: html=" + td.html());
                }*/

                /*Elements newsHeadinglines = doc.select("#mp-itn b a");
                for(Element headline : newsHeadinglines){
                    Log.i(TAG, "%s\n\t%s" + headline.attr("title") + headline.absUrl("href"));
                }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");//转中文字符串方式
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }

}
