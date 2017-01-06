package owo.npc.taipeitechrefined.runnable;

import android.os.Handler;

import owo.npc.taipeitechrefined.utility.CalendarConnector;
import owo.npc.taipeitechrefined.model.YearCalendar;

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
