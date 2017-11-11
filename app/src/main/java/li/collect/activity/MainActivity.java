package li.collect.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import li.collect.R;
import li.collect.dao.SensorModule;
import li.collect.service.EventService;
import li.collect.service.SensorService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btStartServer;
    private Button btStopServer;
    private TextView tvServiceState;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private Intent serviceIntent;
    private List<SensorModule> sensorModules;
    private Gson gson;
    private String res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initEvent();
        test();
        getFile();
    }


    private void getFile() {

    }

    private void test() {
//        EventModule eventModule = new EventModule();
//        eventModule.setEventType("sssss");
//        eventModule.setTime(1);
//        eventModule.save();
        SensorModule sensorModule = new SensorModule();
        sensorModule.setSensorType("test");
        sensorModule.setTime(2);

    }

    private void initEvent() {
        btStartServer.setOnClickListener(this);
        btStopServer.setOnClickListener(this);

    }

    private void initData() {
        serviceIntent = new Intent(this, SensorService.class);
        btStartServer = (Button) findViewById(R.id.btStartServer);
        btStopServer = (Button) findViewById(R.id.btStopServer);
        tvServiceState = (TextView) findViewById(R.id.tvServiceState);
        sensorModules = new ArrayList<>();
        sensorModules.clear();
        gson = new Gson();
        setServiceState();
    }

    private void setServiceState() {
        if (isServiceRunning(this, "li.collect.service.SensorService")) {
            tvServiceState.setText("服务后台开启中");
        } else {
            tvServiceState.setText("服务已经关闭");
        }
    }


    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(serviceIntent);
                    setServiceState();

                } else {
                    Toast.makeText(this, "无权限开启服务", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("zw", "activity dextory");
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(getPackageName());
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btStartServer:
                getPermission();
                break;
            case R.id.btStopServer:
                stopService(serviceIntent);
                setServiceState();

                break;
        }
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            startService(serviceIntent);
            setServiceState();
        }
    }


}