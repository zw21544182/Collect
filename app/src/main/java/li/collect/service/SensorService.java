package li.collect.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import li.collect.base.MyApplication;
import li.collect.dao.EventModule;
import li.collect.dao.SensorModule;

import static li.collect.util.CommonUtil.TYPE_ACCELEROMETER;
import static li.collect.util.CommonUtil.TYPE_AMBIENT_TEMPERATURE;
import static li.collect.util.CommonUtil.TYPE_GRAVITY;
import static li.collect.util.CommonUtil.TYPE_GYROSCOPE;
import static li.collect.util.CommonUtil.TYPE_LIGHT;
import static li.collect.util.CommonUtil.TYPE_LINEAR_ACCELERATION;
import static li.collect.util.CommonUtil.TYPE_MAGNETIC_FIELD;
import static li.collect.util.CommonUtil.TYPE_ORIENTATION;
import static li.collect.util.CommonUtil.TYPE_PRESSURE;
import static li.collect.util.CommonUtil.TYPE_PROXIMITY;
import static li.collect.util.CommonUtil.TYPE_ROTATION_VECTOR;

/**
 * 创建时间: 2017/10/31
 * 创建人: Administrator
 * 功能描述:
 */

public class SensorService extends Service implements SensorEventListener {
    private MyApplication myApplication;
    private SensorManager sensorManager;
    private File file;
    private SimpleDateFormat simpleDateFormat;
    private SensorModule sensorModule;
    private EventModule eventModule;
    private List<Float> valueList;
    private String date;
    private long i = 0;
    private ExecutorService executorService;
    private Gson gson;
    private File sensorFile;
    private FileWriter sensorWriter;
    private FileWriter eventWriter;
    private File eventFile;
    private File eventDir;
    private File sensorDir;
    private TelephonyManager tm;
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private WindowManager mWindowManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = (MyApplication) getApplication();
        myApplication.setRecord(true);
        if (!isAccessibilitySettingsOn(this)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
        Toast.makeText(this, "监听服务开启", Toast.LENGTH_SHORT).show();
        initDate();
        initFile();
        initSensorListen();
    }


    private void initDate() {
        simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        sensorModule = new SensorModule();
        eventModule = new EventModule();
        valueList = new ArrayList<>();
        executorService = Executors.newCachedThreadPool();
        gson = new Gson();
    }

    private void initFile() {
        date = simpleDateFormat.format(new Date());
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Collect");
        sensorDir = new File(file.getAbsolutePath() + "/Sensor");
        sensorFile = new File(sensorDir.getAbsolutePath() + "/Sensor" + date + ".txt");
        try {
            if (!file.exists()) {
                file.mkdir();
            }
            if (!sensorDir.exists()) {
                sensorDir.mkdir();
            }
            if (!sensorFile.exists()) {
                sensorFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sensorWriter = new FileWriter(sensorFile.getAbsolutePath(), true);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "yichang", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        // 获取传感器类型
        int type = sensorEvent.sensor.getType();
        i++;
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_ACCELEROMETER);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_ORIENTATION:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_ORIENTATION);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_GYROSCOPE:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_GYROSCOPE);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_MAGNETIC_FIELD);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Sensor.TYPE_GRAVITY:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_GRAVITY);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_LINEAR_ACCELERATION);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_AMBIENT_TEMPERATURE);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Sensor.TYPE_LIGHT:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_LIGHT);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Sensor.TYPE_PRESSURE:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_PRESSURE);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_ROTATION_VECTOR);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_PROXIMITY:
                date = simpleDateFormat.format(new Date());
                sensorModule.setTime(Long.parseLong(date));
                valueList.clear();
                for (float value :
                        values) {
                    valueList.add(value);
                }
                sensorModule.setFloats(valueList);
                sensorModule.setSensorType(TYPE_PROXIMITY);
                try {
                    sensorWriter.append(gson.toJson(sensorModule) + "");
                    sensorWriter.append("\n");
                    sensorWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void initSensorListen() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        // 为方向传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        // 为陀螺仪传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
        // 为磁场传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        // 为重力传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
        // 为线性加速度传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
        // 为温度传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_GAME);
        // 为光传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_GAME);
        // 为压力传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_GAME);
        //为旋转向量传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_GAME);

    }


    @Override
    public void onDestroy() {
//        sensorManager.unregisterListener(this);
//        try {
//            sensorWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        unregisterBrocast();
        myApplication.setRecord(false);
        EventService.closeIo();
        Toast.makeText(this, "监听服务关闭 ", Toast.LENGTH_SHORT).show();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + EventService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("zw", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("zw", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("zw", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("zw", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("zw", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("zw", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }


}
