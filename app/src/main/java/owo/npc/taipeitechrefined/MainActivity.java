package owo.npc.taipeitechrefined;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import owo.npc.taipeitechrefined.calendar.CalendarFragment;
import owo.npc.taipeitechrefined.course.CourseFragment;
import owo.npc.taipeitechrefined.credit.CreditFragment;
import owo.npc.taipeitechrefined.etc.EtcFragment;
import owo.npc.taipeitechrefined.feedback.FeedbackFragment;
import owo.npc.taipeitechrefined.portal.PortalFragment;
import owo.npc.taipeitechrefined.setting.AccountSettingFragment;
import owo.npc.taipeitechrefined.activity.ActivityFragment;
import owo.npc.taipeitechrefined.utility.PermissionRequestListener;
import owo.npc.taipeitechrefined.wifi.WifiFragment;

import static owo.npc.taipeitechrefined.MainApplication.lang;

/**
 * Created by Alan on 2015/9/12.
 */
public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private NavigationView mSideBar;
    private DrawerLayout mDrawerLayout;
    private CourseFragment courseFragment = new CourseFragment();
    private CreditFragment creditFragment = new CreditFragment();
    private WifiFragment wifiFragment = new WifiFragment();
    private CalendarFragment calendarFragment = new CalendarFragment();
    private AccountSettingFragment accountSettingFragment = new AccountSettingFragment();
    private ActivityFragment activityFragment = new ActivityFragment();
    private FeedbackFragment feedbackFragment = new FeedbackFragment();
    private EtcFragment etcFragment = new EtcFragment();
    private PortalFragment portalFragment = new PortalFragment();
    private BaseFragment currentFragment;
    private Boolean lockFinish = true;
    private SharedPreferences firstOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainApplication.readSetting("uiLang").isEmpty() || MainApplication.readSetting("courseLang").isEmpty()) {
            MainApplication.writeSetting("uiLang", Locale.getDefault().getLanguage());
            MainApplication.writeSetting("courseLang", Locale.getDefault().getLanguage());
        }
        switchLanguage(MainApplication.readSetting("uiLang"));
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        setupSidePanel();
        setupDrawer();
        setupVersionText();

        String first_func = MainApplication.readSetting("first_func");
        lang = MainApplication.readSetting("courseLang");
        firstOpen = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(first_func)) {
            MainApplication.writeSetting("first_func", "5");
            first_func = MainApplication.readSetting("first_func");
            switchFragment(Integer.parseInt(first_func));
            showRight();
        } else {
            switchFragment(2);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        if (msg!=null)
            Log.d("FCM", "msg:"+msg);
    }

    private void setupSidePanel() {
        mSideBar = (NavigationView) findViewById(R.id.sidebar);
        mSideBar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sidebar_item_activity:
                        switchFragment(0);
                        break;
                    case R.id.sidebar_item_calendar:
                        switchFragment(1);
                        break;
                    case R.id.sidebar_item_course:
                        switchFragment(2);
                        break;
                    case R.id.sidebar_item_credit:
                        switchFragment(3);
                        break;
//                    case R.id.sidebar_item_wifi:
//                        switchFragment(4);
//                        break;
                    case R.id.sidebar_item_account_setting:
                        switchFragment(5);
                        break;
                    case R.id.sidebar_item_portal:
                        switchFragment(6);
                        break;
                    case R.id.sidebar_item_feedback:
                        switchFragment(7);
                        break;
                    case R.id.sidebar_item_etc:
                        switchFragment(8);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

    }

    private void showRight(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.right_text);
        builder.setMessage(R.string.right);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                closeSoftKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }


    private void setupVersionText() {
        try {
            String versionName = getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionName;
            TextView version_text_view = (TextView) findViewById(R.id.main_version_text_view);
            version_text_view.setText(getString(R.string.version_text, versionName)+"\n"+
                    getString(R.string.original_developer));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void switchFragment(int index) {
        switch (index) {
            case 0:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_activity_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_activity_selector));
                changeFragment(activityFragment);
                break;
            case 1:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_calendar_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_calendar_selector));
                changeFragment(calendarFragment);
                break;
            case 2:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_course_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_course_selector));
                changeFragment(courseFragment);
                break;
            case 3:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_credit_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_credit_selector));
                changeFragment(creditFragment);
                break;
            case 4:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_wifi_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_wifi_selector));
                changeFragment(wifiFragment);
                break;
            case 5:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_account_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_account_selector));
                changeFragment(accountSettingFragment);
                break;
            case 6:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_feedback_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_feedback_selector));
                changeFragment(portalFragment);
                break;
            case 7:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_feedback_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_feedback_selector));
                changeFragment(feedbackFragment);
                break;
            case 8:
                mSideBar.setItemIconTintList(getResources().getColorStateList(R.color.sidebar_etc_selector));
                mSideBar.setItemTextColor(getResources().getColorStateList(R.color.sidebar_etc_selector));
                changeFragment(etcFragment);
                break;
        }
    }

    private void changeFragment(BaseFragment to) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        to.setAnimationListener(fragmentAnimationListener);
        if (currentFragment != null) {
            if (currentFragment.equals(to)) {
                return;
            }
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(currentFragment)
                        .add(R.id.fragment_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(currentFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        } else {
            transaction.add(R.id.fragment_container, to).commit();
        }
        currentFragment = to;
        setActionBar();
    }

    public void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(currentFragment.getTitleStringId());
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(currentFragment.getTitleColorId())));
        }
        setStatusBarColor(getResources().getColor(currentFragment.getTitleColorId()));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (color == Color.BLACK
                    && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(color);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (lockFinish) {
                Toast.makeText(MainActivity.this, R.string.press_again_to_exit,
                        Toast.LENGTH_SHORT).show();
                lockFinish = false;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(2000);
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        lockFinish = true;
                    }
                };
                thread.start();
            } else {
                finish();
            }
        }
    }

    /*public void showAppDialog(View view) {
        ImageView qrImageView = new ImageView(this);
        qrImageView.setImageResource(R.drawable.qrcode);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        builder.setView(qrImageView);
        builder.setNegativeButton(R.string.play_store_text,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=owo.npc.taipeitechrefined"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this,
                                    R.string.play_store_not_support,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        builder.create().show();
    }*/

    private Animation.AnimationListener fragmentAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public void closeSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 1;
    private PermissionRequestListener mPermissionRequestListener = null;

    public void requestPermission(String permission, PermissionRequestListener listener) {
        mPermissionRequestListener = listener;
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                int requestResult = PackageManager.PERMISSION_DENIED;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    requestResult = grantResults[0];
                }
                if (mPermissionRequestListener != null) {
                    mPermissionRequestListener.onRequestPermissionsResult(permissions[0], requestResult);
                    mPermissionRequestListener = null;
                }
            }
        }
    }

//  與 EtcFragment 的方法相似，因 getResources() 問題在此複製一份使用
    protected void switchLanguage(String lang) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (lang) {
            case "zh":
                configuration.locale = Locale.TAIWAN;
                break;
            case "ja":
                configuration.locale = Locale.JAPAN;
                break;
            default:
                configuration.locale = Locale.ENGLISH;
                break;
        }

        resources.updateConfiguration(configuration, displayMetrics);
        /*  避免重複寫入
        MainApplication.writeSetting("uiLang", lang);
        //*/
    }
}
