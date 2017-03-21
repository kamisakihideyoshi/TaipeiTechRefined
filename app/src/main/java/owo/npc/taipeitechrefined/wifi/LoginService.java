package owo.npc.taipeitechrefined.wifi;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import owo.npc.taipeitechrefined.R;
import owo.npc.taipeitechrefined.runnable.BaseRunnable;
import owo.npc.taipeitechrefined.runnable.LoginNtutccRunnable;
import owo.npc.taipeitechrefined.runnable.LoginNtutccWay1Runnable;
import owo.npc.taipeitechrefined.utility.WifiUtility;
import owo.npc.taipeitechrefined.utility.Utility;

public class LoginService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private WifiFragment mFragment;
    private BroadcastReceiver stateReceiver;
    private boolean isLogin = false;
    private String account;
    private String password;

    @Override
    public IBinder onBind(Intent intent) {
        account = intent.getStringExtra("account");
        password = intent.getStringExtra("password");
        stateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (WifiUtility.isWifiOpen(context)) {
                        if (WifiUtility.isConnected(context)) {
                            String currentSSID = WifiUtility
                                    .getCurrentSSID(context);
                            if (currentSSID.contains("ntutcc")) {
                                if (!isLogin) {
                                    LoginNtutcc();
                                    Toast.makeText(context, R.string.wifi_message, Toast.LENGTH_SHORT).show();
                                    Utility.showNotification(context,
                                            getString(R.string.wifi_login), getString(R.string.wifi_message), true);
                                }
                            } else {
                                Utility.showNotification(
                                        getApplicationContext(), getString(R.string.wifi_login),
                                        currentSSID + getString(R.string.wifi_already_connected), true);
                                isLogin = false;
                            }
                        } else {
                            isLogin = false;
                        }
                    } else {
                        isLogin = false;
                    }
                } catch (Exception e) {
                    isLogin = false;
                }
            }
        };

        WifiUtility.regStateReceiver(this, stateReceiver);
        return mBinder;
    }

    public void LoginNtutcc() {
        Thread t = new Thread(new LoginNtutccRunnable(loginHandler));
        t.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(stateReceiver);
        return super.onUnbind(intent);
    }

    private Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof String) {
                        String result = (String) msg.obj;
                        Thread t = new Thread(new LoginNtutccWay1Runnable(
                                login1Handler, account, password));
                        t.start();
//                        } else if (result.contains("externalGuestRedirect.html")) {
//                            Thread t = new Thread(new LoginNtutccWay2Runnable(
//                                    login1Handler, result, account, password));
//                            t.start();
//                        }
                    }
                    break;
                case BaseRunnable.ERROR:
                    isLogin = false;
                    if (mFragment != null) {
                        mFragment.scanWifi();
                    }
                    break;
            }
        }
    };

    private Handler login1Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    Toast.makeText(getBaseContext(), R.string.wifi_message, Toast.LENGTH_SHORT).show();
                    Utility.showNotification(getApplicationContext(),
                            getString(R.string.wifi_login), getString(R.string.wifi_message), true);
                    break;
                case BaseRunnable.ERROR:
                    isLogin = false;
                    if (mFragment != null) {
                        mFragment.scanWifi();
                    }
                    break;
            }
        }
    };

//    private Handler checkHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BaseRunnable.REFRESH:
//                    if (msg.obj instanceof String) {
//                        String result = (String) msg.obj;
//                        if (result.contains("www.google.com")) {
//                            Utility.showNotification(getApplicationContext(),
//                                    getString(R.string.ntutcc_login), getString(R.string.ntut_message), true);
//                            isLogin = false;
//                        } else {
//                            Thread t = new Thread(new LoginNtutccRunnable(
//                                    loginHandler));
//                            t.start();
//                        }
//                    }
//                    break;
//                case BaseRunnable.ERROR:
//                    isLogin = false;
//                    if (mFragment != null) {
//                        mFragment.scanWifi();
//                    }
//                    break;
//            }
//        }
//    };

    public void setWifiFragment(WifiFragment wifiFragment) {
        this.mFragment = wifiFragment;
    }

    public class LocalBinder extends Binder {
        LoginService getService() {
            return LoginService.this;
        }
    }
}
