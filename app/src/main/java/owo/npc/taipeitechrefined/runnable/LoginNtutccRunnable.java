package owo.npc.taipeitechrefined.runnable;

import android.os.Handler;

import owo.npc.taipeitechrefined.utility.NtutccLoginConnector;

public class LoginNtutccRunnable extends BaseRunnable {
    private static final String TEST_URL = "http://www.google.com/";

    public LoginNtutccRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            String result = NtutccLoginConnector.getRedirectUri(TEST_URL);
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
