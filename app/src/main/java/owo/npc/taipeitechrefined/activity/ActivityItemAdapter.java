package owo.npc.taipeitechrefined.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import owo.npc.taipeitechrefined.model.ActivityInfo;
import owo.npc.taipeitechrefined.model.ActivityList;
import owo.npc.taipeitechrefined.R;

public class ActivityItemAdapter extends ArrayAdapter<ActivityInfo> {

    public ActivityItemAdapter(Context context, ActivityList datas) {
        super(context, R.layout.activity_item, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = new ActivityItemView(getContext());
        }
        ((ActivityItemView) convertView).setActivityInfo(getItem(position));
        return convertView;
    }
}
