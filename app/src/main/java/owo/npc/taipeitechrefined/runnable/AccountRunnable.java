package owo.npc.taipeitechrefined.runnable;

import android.content.Context;
import android.os.Handler;

import owo.npc.taipeitechrefined.MainActivity;

/**
 * Created by Andy on 2017/2/20.
 */

public class AccountRunnable extends BaseRunnable {
    private final MainActivity context;

    public AccountRunnable(Handler handler, Context context) {
        super(handler);
        this.context = (MainActivity) context;
    }

    @Override
    public void run() {
        context.switchFragment(2);
    }
}
