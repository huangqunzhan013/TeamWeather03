package com.example.administrator.teamweather03;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Lenovo on 2018/1/3.
 */

public class LogoActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        setTitle("Design by Team 03");
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
            Intent intent=new Intent(LogoActivity.this,MainActivity.class);
            startActivity(intent);
            LogoActivity.this.finish();
        }else if(id == R.id.logo) {
            /*Intent intent=new Intent(LogoActivity.this,LogoActivity.class);
            startActivity(intent);
            LogoActivity.this.finish();*/
        } else {
            //Toast.makeText(this,"市未来五天天气信息",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(LogoActivity.this,CityActivity.class);
            startActivity(intent);
            LogoActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
