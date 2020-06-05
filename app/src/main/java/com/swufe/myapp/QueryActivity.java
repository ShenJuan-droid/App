package com.swufe.myapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class QueryActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {

    EditText keywords;
    ListView query_list;
    String announ;
    Handler handler;
    String updateDate;
    String url;
    String title;
    private ArrayList<HashMap<String, String>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter; // 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        keywords = (EditText) findViewById(R.id.query_keyword);
        query_list = (ListView) findViewById(R.id.query_list);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("announcement", Activity.MODE_PRIVATE);
        announ = sharedPreferences.getString("announcement", "");
        Log.i(TAG, "onCreate: sp announcement=" + announ);

        updateDate = sharedPreferences.getString("update_Date","");
        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
        Log.i(TAG, "onCreate: sp updateDate=" + updateDate);

        //判断时间
        Log.i(TAG, "onCreate: sp updateDate=" + updateDate);
        Log.i(TAG, "onCreate: todayStr=" + todayStr);

        if(!todayStr.equals(updateDate)){
            Log.i(TAG, "onCreate: 需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG, "onCreate: 不需要更新");
        }


        initListView();
        this.setListAdapter(listItemAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {
                    List<HashMap<String,String>> list = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(QueryActivity.this, list, // listItems数据源
                            R.layout.list_item, // ListItem的XML布局实现
                            //两个参数确定布局和数据的对应关系
                            new String[] { "ItemTitle", "ItemURL" },
                            new int[] { R.id.itemTitle, R.id.itemURL }
                    );
                    setListAdapter(listItemAdapter);
                    Log.i("handler","reset list...");

                    //保存更新的日期
                    SharedPreferences sp = getSharedPreferences("announcement", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("update_date",todayStr);
                    editor.apply();

                    Toast.makeText(QueryActivity.this,"内容已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnClickListener(new ImageButton.OnClickListener() {

            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.baidu.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public void onClick(View btn) {
        //获取用户输入的关键词
        String str = keywords.getText().toString();
        if (str.length() > 0) {
        } else {//提示用户输入关键词
            Toast.makeText(this, "关键词不能为空", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "onClick: ");
        Log.i(TAG, "onClick: get str=" + str);
        match();
    }

    private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "Rate： " + i); // 标题
            map.put("ItemURL", "detail" + i); // 链接
            listItems.add(map);
        }
        // 生成适配器SimpleAdapter的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
                R.layout.list_item, // ListItem的XML布局实现
                //两个参数确定布局和数据的对应关系
                new String[] { "ItemTitle", "ItemURL" },
                new int[] { R.id.itemTitle, R.id.itemURL }
        );
    }

    public void match() {
        String str = keywords.getText().toString();
        if (announ != null && announ.contains(str)) {
            int index = announ.indexOf(str);
            int len = keywords.length();
            Spanned temp = Html.fromHtml(announ.substring(0, index)
                    + "<font color=#FF0000>"
                    + announ.substring(index, index + len) + "</font>"
                    + announ.substring(index + len, announ.length()));
        } else {
            return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();
            url = bundle.getString("ItemURL","");
            title = bundle.getString("ItemTitle","");
            Log.i(TAG, "onActivityResult: ItemURL" + url);
            Log.i(TAG, "onActivityResult: ItemTitle" + title);

            //将新内容写到SP里
            SharedPreferences sharedPreferences = getSharedPreferences("announcement", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ItemURL",url);
            editor.putString("ItemTitle",title);
            editor.commit();
            Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");
        }

    }

    public void run() {
        Log.i("thread","run.....");
        Bundle bundle =new Bundle();
        //保存获取的数据
        List<HashMap<String, String>> retList = new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG, "run = " + doc.title());
//            int i = 1;
//            for(Element href : hrefs){
//                Log.i(TAG,"run: href["+i+"]=" + href);
//                i++;
//            }
            //从网页中获取数据
            Elements hrefs = doc.getElementsByTag("href");
            for(int i = 0; i <hrefs.size(); i++){
                Element href = hrefs.get(i);

                String url = href.attr("href");
                String title = href.attr("title");

                SharedPreferences sp = getSharedPreferences("title",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle",""+title);
                map.put("ItemURL",""+url);
                retList.add(map);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        msg.obj = retList;
        handler.sendMessage(msg);
    }


    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: parent=" + parent);
        Log.i(TAG, "onItemClick: view=" + view);
        Log.i(TAG, "onItemClick: position=" + position);
        Log.i(TAG, "onItemClick: id=" + id);

        //从ListView中获取选中数据
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String title = map.get("ItemTitle");
        String url = map.get("ItemURL");
        Log.i(TAG, "onItemClick: titleStr=" + title);
        Log.i(TAG, "onItemClick: urlStr=" + url);
    }
}
