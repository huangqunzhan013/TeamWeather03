package com.example.administrator.teamweather03;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.teamweather03.adapter.DictionaryAdapter;
import com.example.administrator.teamweather03.db.DBHelper;

import com.example.administrator.teamweather03.db.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Lenovo on 2018/1/2.
 */

public class CityActivity extends AppCompatActivity implements TextWatcher{
    private DBHelper dbHelper; // 用户输入文本框
    private AutoCompleteTextView word; // 定义数据库的名字
    private SQLiteDatabase database;
    private Button searchWord; // 搜索按钮
    private TextView showResult; // 用户显示查询结果
    HttpURLConnection httpConn=null;
    InputStream din =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        setTitle("城市未来五天天气查询");
        dbHelper = new DBHelper(getBaseContext());// 打开数据库
        database = dbHelper.openDatabase();
        searchWord = (Button) findViewById(R.id.find);
        word = (AutoCompleteTextView) findViewById(R.id.value);
        showResult = (TextView) findViewById(R.id.my_body);
        word.addTextChangedListener(this); // 绑定文字改变监听器
        word.setText("广州");
        searchWord.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                showResult.setText("");
                Toast.makeText(CityActivity.this,"正在查询天气信息...",Toast.LENGTH_LONG).show();
                GetJson gj=new GetJson(word.getText().toString());
                gj.start();

            }
        });
    }

    class GetJson extends Thread{
        private String urlstr =  "http://wthrcdn.etouch.cn/weather_mini?city=";
        public GetJson(String cityname){
            try {
                urlstr=urlstr+ URLEncoder.encode(cityname,"UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            try {
                URL url=new URL(urlstr);
                httpConn= (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET");
                din=httpConn.getInputStream();
                InputStreamReader in=new InputStreamReader(din);
                BufferedReader buffer=new BufferedReader(in);
                StringBuffer sbf=new StringBuffer();
                String line=null;
                while ((line=buffer.readLine())!=null){
                    sbf.append(line);
                }
                Message msg=new Message();
                msg.obj=sbf.toString();
                msg.what=123;
                handler.sendMessage(msg);
                Looper.prepare();
                Toast.makeText(CityActivity.this,"获取数据成功",Toast.LENGTH_LONG).show();
                Looper.loop();
            }catch (Exception e){
                Looper.prepare();
                Toast.makeText(CityActivity.this,"获取数据失败",Toast.LENGTH_LONG).show();
                Looper.loop();
                e.printStackTrace();
            }finally {
                try{
                    httpConn.disconnect();
                    din.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 123:
                    showData((String)msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void showData(String jData){

        try {
            JSONObject jobj=new JSONObject(jData);
            JSONObject weather=jobj.getJSONObject("data");
            StringBuffer wbf=new StringBuffer();
            wbf.append("温度："+weather.getString("wendu")+"℃"+"\n");
            wbf.append("天气提示："+weather.getString("ganmao")+"\n"+"\n"+"\n");
            JSONArray jary = weather.getJSONArray("forecast");
            for(int i=0;i<jary.length();i++){
                JSONObject pobj = (JSONObject)jary.opt(i);
                wbf.append("日期："+pobj.getString("date")+"\n");
                wbf.append("最高温："+pobj.getString("high")+"\n");
                wbf.append("最底温："+pobj.getString("low")+"\n");
                wbf.append("风向："+pobj.getString("fengxiang")+"    ");
                String fengli=pobj.getString("fengli");
                String fengli1=fengli.substring(9);
                String removerStr="]]>";
                String fengli2=fengli1.replace(removerStr,"");
                wbf.append("风力："+fengli2+"\n");
                wbf.append("天气："+pobj.getString("type")+"\n"+"\n");

            }
            showResult.setText(wbf.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        //tv_body.setText(jData);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("dayang","打开Menu时执行该方法");
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("dayang","onCreateOptionsMenu");
        //创建Menu菜单
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("dayang","选择列表项时执行------------");
        //对菜单项点击内容进行设置
        int id = item.getItemId();
        if (id == R.id.privince) {
            //Toast.makeText(this,"省或市天气信息",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(CityActivity.this,MainActivity.class);
            startActivity(intent);
            CityActivity.this.finish();
        }else if(id == R.id.logo) {
            Intent intent=new Intent(CityActivity.this,LogoActivity.class);
            startActivity(intent);
            CityActivity.this.finish();
        } else {
            //Toast.makeText(this,"市未来五天天气信息",Toast.LENGTH_SHORT).show();
            /*Intent intent=new Intent(CityActivity.this,CityActivity.class);
            startActivity(intent);
            CityActivity.this.finish();*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Cursor cursor = database.rawQuery(
                "select  distinct(area_name) as _id from weathers where area_name like ?",
                new String[] { editable.toString() + "%" });

        // 新建新的Adapter
        DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this,cursor, true);

        // 绑定适配器
        word.setAdapter(dictionaryAdapter);
        word.setThreshold(1);
    }




}
