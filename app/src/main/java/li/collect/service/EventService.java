package li.collect.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import li.collect.base.MyApplication;
import li.collect.dao.EventModule;

/**
 * 创建时间: 2017/11/7
 * 创建人: Administrator
 * 功能描述:监听系统实体按键，蓝牙，耳机 并写入文件
 */

public class EventService extends AccessibilityService {
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private HomeWatcherReceiver mHomeKeyReceiver;
    private HeadsetPlugReceiver headsetPlugReceiver;
    private CallPhoneBroadCast callPhoneBroadCast;
    private MyApplication myApplication;
    private SimpleDateFormat dateFormat;
    private static FileWriter eventWriter;
    private File eventFile;
    private File eventDir;
    private File file;
    private boolean isRecord;
    private TelephonyManager tm;
    private MyPhoneStateListener myPhoneStateListener;
    private Gson gson;
    private static String date = "";
    private EventModule eventModule;
    private AudioManager audioManager;
    private boolean sw = true;
    private boolean isheadin = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("zw", "EventService onreate");
        initData();

        initEventListen();
    }

    private void initEventListen() {
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);
        callPhoneBroadCast = new CallPhoneBroadCast();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(callPhoneBroadCast, intentFilter);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver, filter);
        mHomeKeyReceiver = new HomeWatcherReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver, homeFilter);
        tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void initData() {
        dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myApplication = (MyApplication) getApplication();
        gson = new Gson();
        eventModule = new EventModule();

    }

    private void initFile() {
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Collect");
        eventDir = new File(file.getAbsolutePath() + "/Event");
        if (!file.exists()) {
            file.mkdir();
        }
        if (!eventDir.exists()) {
            eventDir.mkdir();
        }
    }

    @Override
    public void onDestroy() {
        System.exit(0);
        super.onDestroy();
        Log.d("zw", "EventService ondestory");

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        isRecord = myApplication.isRecord();

        //判断是否按键抬起
        if (isRecord && event.getAction() == KeyEvent.ACTION_UP) {
            initFile();
            initWriter();
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK://返回键
                    Log.d("zw", "KEYCODE_BACK " + event.getAction());
                    saveEventModuleToFile(Long.parseLong(dateFormat.format(new Date()).trim()), "KEYCODE_BACK");
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN://音量减
                    Log.d("zw", "KEYCODE_VOLUME_DOWN");
                    saveEventModuleToFile(Long.parseLong(dateFormat.format(new Date()).trim()), "KEYCODE_VOLUME_DOWN");
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP://音量加
                    Log.d("zw", "KEYCODE_VOLUME_UP");
                    saveEventModuleToFile(Long.parseLong(dateFormat.format(new Date()).trim()), "KEYCODE_VOLUME_UP");
                    break;
                case KeyEvent.KEYCODE_MENU://菜单键
                    Log.d("zw", "KEYCODE_MENU");
                    saveEventModuleToFile(Long.parseLong(dateFormat.format(new Date()).trim()), "KEYCODE_MENU");
                    break;
            }
        }
        return super.onKeyEvent(event);

    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int type = accessibilityEvent.getEventType();
        isRecord = myApplication.isRecord();
        if (isRecord) {
            initFile();
            initWriter();
            switch (type) {
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  //收到通知栏消息
                    Log.d("zw", "TYPE_NOTIFICATION_STATE_CHANGED");
                    saveEventModuleToFile(Long.parseLong(dateFormat.format(new Date()).trim()), "TYPE_NOTIFICATION_STATE_CHANGED");
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:    //界面状态改变
                    Log.d("zw", "TYPE_WINDOW_STATE_CHANGED");
                    saveEventModuleToFile(Long.parseLong(dateFormat.format(new Date()).trim()), "TYPE_WINDOW_STATE_CHANGED");
                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:   //点击事件
                    Log.d("zw", "TYPE_VIEW_CLICKED");
                    saveEventModuleToFile(getCurrentTime(), "TYPE_VIEW_CLICKED");
                    break;

            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 耳机插拔
     */
    public class HeadsetPlugReceiver extends BroadcastReceiver {

        private static final String TAG = "HeadsetPlugReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            isRecord = myApplication.isRecord();
            if (isRecord) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        Log.d("Zw", "erjiduankai");
                        saveEventModuleToFile(getCurrentTime(), "TYPE_HEADSETPLUG_OFF");//耳机断开
                        isheadin = false;
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        Log.d("Zw", "erjilianjie");

                        saveEventModuleToFile(getCurrentTime(), "TYPE_HEADSETPLUG_ON");//耳机连接
                        isheadin = true;
                    }
                }
            }
        }

    }

    /**
     * 电话状态监听
     */
    public class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            isRecord = myApplication.isRecord();
            if (isRecord) {
                initFile();
                initWriter();
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE://挂断
                        sw = false;
                        saveEventModuleToFile(getCurrentTime(), "CALL_STATE_IDLE");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://接听
                        Log.d("zw", "接听");
                        new Thread() {
                            public void run() {
                                super.run();
                                sw = true;
                                String res = "";
                                String te = "";
                                isheadin = audioManager.isWiredHeadsetOn();
                                while (sw) {
                                    if (audioManager.isBluetoothScoOn()) {
                                        res = "蓝牙";
                                    }
                                    if (audioManager.isSpeakerphoneOn()) {
                                        res = "扬声器";
                                    }
                                    if (!audioManager.isBluetoothScoOn() && !audioManager.isSpeakerphoneOn()) {
                                        if (isheadin) {
                                            res = "耳机";
                                        } else {
                                            res = "听筒";
                                        }
                                    }
                                    if (!res.trim().equals(te.trim())) {
                                        if (res.equals("耳机")) {
                                            Log.d("zw", "耳机");
                                            saveEventModuleToFile(getCurrentTime(), "TYPE_CALL_HEAD");
                                        }
                                        if (res.equals("听筒")) {
                                            Log.d("zw", "听筒");

                                            saveEventModuleToFile(getCurrentTime(), "TYPE_CALL_EAR");
                                        }
                                        if (res.equals("蓝牙")) {
                                            Log.d("zw", "蓝牙");

                                            saveEventModuleToFile(getCurrentTime(), "TYPE_CALL_BlUETOOCH");
                                        }
                                        if (res.equals("扬声器")) {
                                            Log.d("zw", "扬声器");

                                            saveEventModuleToFile(getCurrentTime(), "TYPE_CALL_SPEAKER");
                                        }
                                    }

                                    te = res;
                                }
                            }
                        }.start();
                        saveEventModuleToFile(getCurrentTime(), "CALL_STATE_OFFHOOK");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃
                        Log.d("zw", "响铃:来电号码 " + incomingNumber);
                        saveEventModuleToFile(getCurrentTime(), "CALL_STATE_RINGING");
                        break;
                }
            }
        }
    }

    /**
     * 去电
     */
    public class CallPhoneBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isRecord = myApplication.isRecord();
            if (isRecord) {
                initFile();
                initWriter();
                saveEventModuleToFile(getCurrentTime(), "TYPE_CALL_OTHER");
            }
        }

    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                isRecord = myApplication.isRecord();
                if (isRecord) {
                    initFile();
                    initWriter();
                    saveEventModuleToFile(getCurrentTime(), "ACTION_SCREEN_OFF");
                }
            }
        }
    };

    /**
     * 监听Home键
     */
    public class HomeWatcherReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            isRecord = myApplication.isRecord();
            if (isRecord) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                        initFile();
                        initWriter();
                        saveEventModuleToFile(getCurrentTime(), "TYPE_CLICK_HOME");
                    }
                }
            }
        }
    }


    private void initWriter() {
        if (date.trim().equals("")) {
            date = dateFormat.format(new Date());
        }
        eventFile = new File(eventDir.getAbsolutePath() + "/Event" + date + ".txt");
        try {
            eventWriter = new FileWriter(eventFile.getAbsolutePath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEventModuleToFile(long time, String type) {
        eventModule.setTime(time);
        eventModule.setEventType(type);
        try {
            eventWriter.append(gson.toJson(eventModule));
            eventWriter.append("\n");
            eventWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 获取当前时间长整形格式为:yyMMddHHmmssSSS
     */
    public long getCurrentTime() {
        return Long.parseLong(dateFormat.format(new Date()).trim());
    }


    /**
     * 关闭io流
     */
    public static void closeIo() {
        date = "";
        try {
            if (eventWriter != null)
            eventWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
