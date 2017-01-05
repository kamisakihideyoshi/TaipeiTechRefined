package owo.npc.ttr_s.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import owo.npc.ttr_s.BaseFragment;
import owo.npc.ttr_s.R;
import owo.npc.ttr_s.model.ActivityList;
import owo.npc.ttr_s.model.Model;
import owo.npc.ttr_s.runnable.ActivityRunnable;
import owo.npc.ttr_s.runnable.BaseRunnable;
import owo.npc.ttr_s.utility.Utility;
import owo.npc.ttr_s.utility.WifiUtility;

import java.lang.ref.WeakReference;

public class ActivityFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener {
    private static View fragmentView = null;
    private static SwipeRefreshLayout swipeLayout;
    private ListView listView;
    private static boolean isRefresh = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_activity, container,
                false);
        swipeLayout = (SwipeRefreshLayout) fragmentView
                .findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.yellow, R.color.orange,
                R.color.red, R.color.purple, R.color.blue, R.color.green);
        swipeLayout.setOnRefreshListener(this);
        listView = (ListView) fragmentView.findViewById(R.id.activity_listview);
        View header = inflater.inflate(R.layout.activity_divider, listView,
                false);
        listView.addHeaderView(header);
        View footer = inflater.inflate(R.layout.activity_divider, listView,
                false);
        listView.addFooterView(footer);
        View start_button = fragmentView.findViewById(R.id.start_button);
        start_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                updateActivity();
            }
        });
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setData();
    }

    private void setData() {
        View start_button = fragmentView.findViewById(R.id.start_button);
        ActivityList activity_list = Model.getInstance().getActivityArray();
        if (activity_list != null) {
            start_button.setVisibility(View.GONE);
            listView.setAdapter(new ActivityItemAdapter(getActivity(),
                    activity_list));
        } else {
            start_button.setVisibility(View.VISIBLE);
        }
    }

    private void updateActivity() {
        if (!isRefresh) {
            if (WifiUtility.isNetworkAvailable(getActivity())) {
                listView.setAdapter(null);
                isRefresh = true;
                swipeLayout.setRefreshing(true);
                Thread t = new Thread(new ActivityRunnable(new ActivityHandler(
                        this), getActivity()));
                t.start();
            } else {
                isRefresh = false;
                swipeLayout.setRefreshing(false);
                Toast.makeText(getActivity(), R.string.check_network_available,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void obtainActivityResult(ActivityList result) {
        if (result != null) {
            ActivityItemView.clearImageCache();
            Model.getInstance().setActivityArray(result);
            Model.getInstance().saveActivityArray();
            setData();
            if (result.size() == 0) {
                Toast.makeText(getActivity(), "活動更新完成，目前無任何活動資訊！",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "活動更新完成，可以開始瀏覽活動！",
                        Toast.LENGTH_LONG).show();
            }

        } else {
            showAlertMessage("此服務發生錯誤，請稍後再試！");
        }
    }

    @Override
    public int getTitleColorId() {
        return R.color.dark_yellow;
    }

    @Override
    public int getTitleStringId() {
        return R.string.activity_text;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_update:
                updateActivity();
                break;
        }
        return true;
    }

    static class ActivityHandler extends Handler {
        private WeakReference<ActivityFragment> fragmentRef;

        public ActivityHandler(ActivityFragment fragment) {
            fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            swipeLayout.setRefreshing(false);
            isRefresh = false;
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof ActivityList) {
                        ActivityList result = (ActivityList) msg.obj;
                        ActivityFragment fragment = fragmentRef.get();
                        if (fragment != null) {
                            fragment.obtainActivityResult(result);
                        }
                    }
                    break;
                case BaseRunnable.ERROR:
                    ActivityFragment fragment = fragmentRef.get();
                    if (fragment != null) {
                        fragment.setData();
                        Utility.showDialog("提示", (String) msg.obj,
                                fragment.getActivity());
                    }
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        updateActivity();
    }

}
