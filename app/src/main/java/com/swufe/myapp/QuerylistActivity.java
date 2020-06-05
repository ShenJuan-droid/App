package com.swufe.myapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryListActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {

    private String TAG = "query_list";
    Handler handler;
    private ArrayList<HashMap<String, String>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter; // 适配器
    private int msgWhat = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_query_list);

        initListView();
        this.setListAdapter(listItemAdapter);

//        ListAdapter adapter = new ArrayAdapter<String>(QueryListActivity.this,android.R.layout.simple_list_item_1,list_data);
//        setListAdapter(adapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 7){
                    List<HashMap<String,String>> list = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(QueryListActivity.this, list, // listItems数据源
                            R.layout.list_item, // ListItem的XML布局实现
                            //两个参数确定布局和数据的对应关系
                            new String[] { "ItemTitle", "ItemURL" },
                            new int[] { R.id.itemTitle, R.id.itemURL }
                    );
                    setListAdapter(listItemAdapter);
                    Log.i("handler","reset list...");
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


    @Override
    public void run() {
        //获取网络数据，放入list带回到主线程中
        List<HashMap<String, String>> retList = new ArrayList<>();
        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG, "run = " + doc.title());
            Elements hrefs = doc.getElementsByTag("href");

//            int i = 1;
//            for(Element href : hrefs){
//                Log.i(TAG,"run: href["+i+"]=" + href);
//                i++;
//            }
            Element href77 = hrefs.get(76);

            //从网页中获取数据
            for(int i = 77; i <=98; i++){
                Element href = hrefs.get(i);

                String url = href.attr("href");
                String title = href.attr("title");

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle",""+title);
                map.put("ItemURL",""+url);
                retList.add(map);

                SharedPreferences sp = getSharedPreferences("title", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("通知公告"+i, "title");


            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: parent=" + parent);
        Log.i(TAG, "onItemClick: view=" + view);
        Log.i(TAG, "onItemClick: position=" + position);
        Log.i(TAG, "onItemClick: id=" + id);

        //从ListView中获取选中数据
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String urlStr = map.get("ItemURL");
        Log.i(TAG, "onItemClick: titleStr=" + titleStr);
        Log.i(TAG, "onItemClick: urlStr=" + urlStr);

    }
}
