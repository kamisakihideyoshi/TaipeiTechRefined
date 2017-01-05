package owo.npc.ttr_s.runnable;

import android.os.Handler;

import owo.npc.ttr_s.utility.CourseConnector;

public class CourseLoginRunnable extends BaseRunnable {
    public CourseLoginRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            String result = CourseConnector.loginCourse();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }

}
