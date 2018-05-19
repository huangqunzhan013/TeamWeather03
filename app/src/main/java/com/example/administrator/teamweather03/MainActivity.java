package com.example.administrator.teamweather03;

import com.example.administrator.teamweather03.adapter.DictionaryAdapter;
import com.example.administrator.teamweather03.db.DBHelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Lenovo on 2017/12/31.
 */

public class MainActivity extends AppCompatActivity implements Runnable,TextWatcher {
    HttpURLConnection httpConn=null;
    InputStream din=null;
    Vector<String> cityname=new Vector<String>();
    Vector<String> low=new Vector<String>();
    Vector<String> high=new Vector<String>();
    Vector<String> icon=new Vector<String>();
    Vector<Bitmap> bitmap=new Vector<>();
    Vector<String> summary=new Vector<String>();
    int weatherIndex[]=new int[20];
    //String Hcity="广州";
    String Pcity;
    //boolean bPress=false;
    //boolean bHasData=false;
    LinearLayout body;
    Button find;
    AutoCompleteTextView value;
    DBHelper dbHelper;
    SQLiteDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("省或市当天天气查询");
        body=(LinearLayout)findViewById(R.id.my_body);
        find=(Button)findViewById(R.id.find);
        value=(AutoCompleteTextView) findViewById(R.id.value);
        dbHelper=new DBHelper(getBaseContext());
        database = dbHelper.openDatabase();
        value.addTextChangedListener(this);

        value.setText("广东");

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body.removeAllViews();
                //city=value.getText().toString();
                PinYinHelperActivity pinyin=new PinYinHelperActivity();
                Pcity=pinyin.ToPinyin(value.getText().toString());
                Toast.makeText(MainActivity.this,"正在查询天气信息...",Toast.LENGTH_LONG).show();
                Thread thread=new Thread(MainActivity.this);
                thread.start();
            }
        });

    }

    @Override
    public void run() {
        cityname.removeAllElements();
        low.removeAllElements();
        high.removeAllElements();
        icon.removeAllElements();
        bitmap.removeAllElements();
        summary.removeAllElements();
        parseData();
        downImage();
        Message message=new Message();
        message.what=1;
        handler.sendMessage(message);
    }
    public void parseData(){
        int i=0;
        String sValue;
        String weatherUrl="http://flash.weather.com.cn/wmaps/xml/"+Pcity+".xml";
        String weatherIcon="http://m.weather.com.cn/img/c";
        try{
            URL url=new URL(weatherUrl);
            httpConn=(HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("GET");
            din=httpConn.getInputStream();
            XmlPullParser xmlParser= Xml.newPullParser();
            xmlParser.setInput(din,"UTF-8");
            int evtType=xmlParser.getEventType();
            while (evtType!=XmlPullParser.END_DOCUMENT){
                switch (evtType){
                    case XmlPullParser.START_TAG:
                        String tag=xmlParser.getName();
                        if(tag.equalsIgnoreCase("city")){
                            cityname.addElement(xmlParser.getAttributeValue(null,"cityname")+"天气:");
                            summary.addElement(xmlParser.getAttributeValue(null,"stateDetailed"));
                            low.addElement("最低温:"+xmlParser.getAttributeValue(null,"tem2")+"℃");
                            high.addElement("最高温:"+xmlParser.getAttributeValue(null,"tem1")+"℃");
                            icon.addElement(weatherIcon+xmlParser.getAttributeValue(null,"state1")+".gif");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                    default:break;
                }
                evtType=xmlParser.next();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            try {
                din.close();
                httpConn.disconnect();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    private void downImage(){
        int i=0;
        for(i=0;i<icon.size();i++){
            try{
                URL url=new URL(icon.elementAt(i));
                System.out.println(icon.elementAt(i));
                httpConn=(HttpURLConnection)url.openConnection();
                httpConn.setRequestMethod("GET");
                din=httpConn.getInputStream();
                bitmap.addElement(BitmapFactory.decodeStream(httpConn.getInputStream()));
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                try{
                    din.close();
                    httpConn.disconnect();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    private final Handler handler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    showData();
                    break;
            }
            super.handleMessage(msg);

        }
    };
    public void showData(){
        body.removeAllViews();
        body.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight=80;
        params.height=50;
        for(int i=0;i<cityname.size();i++){
            LinearLayout linearLayout=new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView dayView=new TextView(this);
            dayView.setLayoutParams(params);
            dayView.setText(cityname.elementAt(i));
            linearLayout.addView(dayView);

            TextView summaryView=new TextView(this);
            summaryView.setLayoutParams(params);
            summaryView.setText(summary.elementAt(i));
            linearLayout.addView(summaryView);

            ImageView icon=new ImageView(this);
            icon.setLayoutParams(params);
            icon.setImageBitmap(bitmap.elementAt(i));
            linearLayout.addView(icon);

            TextView lowView=new TextView(this);
            lowView.setLayoutParams(params);
            lowView.setText(low.elementAt(i));
            linearLayout.addView(lowView);

            TextView highView=new TextView(this);
            highView.setLayoutParams(params);
            highView.setText(high.elementAt(i));
            linearLayout.addView(highView);
            body.addView(linearLayout);
        }


    }

    public void afterTextChanged(Editable s) {
        Cursor cursor = database.rawQuery(
                "select  distinct(province_name) as _id from weathers where province_name like ?",
                new String[] { s.toString() + "%" });



        // 新建新的Adapter
        DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this,cursor, true);

        // 绑定适配器
        value.setAdapter(dictionaryAdapter);
        value.setThreshold(1);
    }


    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {

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
            /*ntent intent=new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
            MainActivity.this.finish();*/
        }else if(id == R.id.logo) {
            Intent intent=new Intent(MainActivity.this,LogoActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        } else {
            //Toast.makeText(this,"市未来五天天气信息",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,CityActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        /*switch (id){
            case R.id.privince:
                break;
            case R.id.city:
                Intent intent=new Intent(MainActivity.this,CityActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                break;
            case R.id.logo:
                Intent intent1=new Intent(MainActivity.this,LogoActivity.class);
                startActivity(intent1);
                MainActivity.this.finish();
                break;
            default:
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }



}
