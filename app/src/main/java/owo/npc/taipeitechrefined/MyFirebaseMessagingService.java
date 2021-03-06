package owo.npc.taipeitechrefined;

/**
 * Created by Andy on 2017/2/4.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        final int notifyID = 1; // 通知的識別號碼
        final boolean autoCancel = true; // 點擊通知後是否要自動移除掉通知
        final int requestCode = notifyID; // PendingIntent的Request Code
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        final int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        // ONE_SHOT：PendingIntent只使用一次；
        // CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；
        // NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；
        // UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final PendingIntent pendingIntent = PendingIntent
                .getActivity(getApplicationContext(), requestCode, intent, flags); // 取得PendingIntent

        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("消息")
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentIntent(pendingIntent)
                .setAutoCancel(autoCancel).build(); // 建立通知
        notificationManager.notify(notifyID, notification); // 發送通知
    }
}
