package kr.ac.kaist.kse.ic.msbanddatacollector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by kimauk on 2017. 3. 10..
 */

public class MSBandDataLogger extends LoggerApplication{
     void closeApplicationWithWarning(Throwable throwable, String msg){
        e(throwable, DOMAIN_APPLICATION, msg);
         info("Exit application");
        System.exit(0);
    }

    void warn(String msg){
        Toast m = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        m.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 300);
        m.show();
        info("Warn user for %s", msg);
    }

    void notify(String msg){
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, SensingService.class), 0);
        Notification noti = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MSBandDataCollector")
                .setContentText(msg)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(intent)
                .setOngoing(true)
                .build();
        getNotificationManager().notify(ID_NOTIFICATION, noti);
        info("Notify user for %s", msg);
    }

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private final int ID_NOTIFICATION = 0x1;

    private NotificationManager getNotificationManager(){
        return notificationManager;
    }


    private void info(String format, String... args){
        i(System.currentTimeMillis(), DOMAIN_APPLICATION, format, args);
    }

    @Override
    public void onTerminate() {
        getNotificationManager().cancel(ID_NOTIFICATION);
        super.onTerminate();
    }
}
