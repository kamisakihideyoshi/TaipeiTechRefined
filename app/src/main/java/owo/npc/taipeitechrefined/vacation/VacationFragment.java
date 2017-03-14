package owo.npc.taipeitechrefined.vacation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import owo.npc.taipeitechrefined.BaseFragment;

/**
 * Created by Andy on 2017/3/13.
 */

public class VacationFragment extends BaseFragment {

    public VacationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getTitleColorId() {
        return 0;
    }

    @Override
    public int getTitleStringId() {
        return 0;
    }
}
