package owo.npc.taipeitechrefined.runnable;

import android.content.Context;
import android.os.Handler;

import owo.npc.taipeitechrefined.BaseFragment;
import owo.npc.taipeitechrefined.MainActivity;

/**
 * Created by Andy on 2017/3/9.
 */

public class PortalRunnable extends BaseRunnable {
    private final MainActivity context;

    public PortalRunnable(Handler handler, Context context) {
        super(handler);
        this.context = (MainActivity) context;
    }

    @Override
    public void run() {
        context.switchFragment(5);
    }
}
