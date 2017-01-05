package owo.npc.ttr_s.runnable;

import android.os.Handler;

import owo.npc.ttr_s.model.YearCalendar;
import owo.npc.ttr_s.utility.CalendarConnector;

public class CalendarRunnable extends BaseRunnable {
    public CalendarRunnable(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            YearCalendar result = CalendarConnector.getEventList();
            sendRefreshMessage(result);
        } catch (Exception e) {
            sendErrorMessage(e.getMessage());
        }
    }
}
