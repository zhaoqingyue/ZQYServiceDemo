package com.zqy.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private CommunicableService.MyBinder myBinder;

    private boolean isBinded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.startService, R.id.stopService, R.id.bindService, R.id.unbindService,
            R.id.foregroundService, R.id.intentService})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startService: {
                //构建启动服务的Intent对象
                Intent startIntent = new Intent(this, CommonService.class);
                //调用startService()方法-传入Intent对象,以此启动服务
                startService(startIntent);
                break;
            }
            case R.id.stopService: {
                //构建停止服务的Intent对象
                Intent stopIntent = new Intent(this, CommonService.class);
                //调用stopService()方法-传入Intent对象,以此停止服务
                stopService(stopIntent);
                break;
            }
            case R.id.bindService: {
                //构建绑定服务的Intent对象
                Intent bindIntent = new Intent(this, CommunicableService.class);
                //调用bindService()方法,以此停止服务

                isBinded = bindService(bindIntent, connection,BIND_AUTO_CREATE);
                //参数说明
                //第一个参数:Intent对象
                //第二个参数:上面创建的Serviceconnection实例
                //第三个参数:标志位
                //这里传入BIND_AUTO_CREATE表示在Activity和Service建立关联后自动创建Service
                //这会使得MyService中的onCreate()方法得到执行，但onStartCommand()方法不会执行
                break;
            }
            case R.id.unbindService: {
                //调用unbindService()解绑服务
                //参数是上面创建的Serviceconnection实例
                unbindService(connection);
                isBinded = false;
                break;
            }
            case R.id.foregroundService: {
                Intent startIntent = new Intent(this, ForegroundService.class);
                startService(startIntent);
                break;
            }
            case R.id.intentService: {
                // 同一服务只会开启1个工作线程
                // 在onHandleIntent（）函数里，依次处理传入的Intent请求
                // 将请求通过Bundle对象传入到Intent，再传入到服务里

                // 请求1
                Intent i = new Intent("com.zqy.service.intentService");
                Bundle bundle = new Bundle();
                bundle.putString("taskName", "task1");
                i.putExtras(bundle);
                startService(i);

                // 请求2
                Intent i2 = new Intent("com.zqy.service.intentService");
                Bundle bundle2 = new Bundle();
                bundle2.putString("taskName", "task2");
                i2.putExtras(bundle2);
                startService(i2);

                startService(i);  //多次启动
                break;
            }
        }
    }

    //创建ServiceConnection的匿名类
    private ServiceConnection connection = new ServiceConnection() {

        //重写onServiceConnected()方法和onServiceDisconnected()方法
        //在Activity与Service建立关联和解除关联的时候调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        //在Activity与Service解除关联的时候调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //实例化Service的内部类myBinder
            //通过向下转型得到了MyBinder的实例
            myBinder = (CommunicableService.MyBinder) service;
            //在Activity调用Service类的方法
            myBinder.service_connect_Activity();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBinded && connection != null)
            unbindService(connection);
    }
}
