package com.agenda.ter.smartgenda;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Giyomm on 12/05/2016.
 *La classe qui permet de programmer une notification a un moment précis
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * La methode qui déclenche une notfication pour chaque rappel d'une notification
     * @param context Le contexte actuel de l'appmication
     * @param intent les paramétres transmis a l'AlarmReceiver
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationEventName = intent.getStringExtra(EventActivity.EXTRA_NOTIFICATION_ALARM_NAME);
        String notificationEventDateAndTime = intent.getStringExtra(EventActivity.EXTRA_NOTIFICATION_ALARM_DATE_AND_TIME);
        Long notificationEventID = intent.getLongExtra(EventActivity.EXTRA_NOTIFICATION_ALARM_LAST_ID,-1);

        Intent notificationIntent = new Intent(context, CalendarActivity.class);
        notificationIntent.putExtra(CalendarActivity.EXTRA_ALARM_RECEIVER_EVENT_ID,""+notificationEventID);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 0,notificationIntent, 0);

        android.support.v7.app.NotificationCompat.Builder mBuilder =
                (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_app)
                        .setContentIntent(pi)
                        .setContentTitle("Rappel Smartgenda")
                        .setContentText("Vous avez l'évènement "+ notificationEventName +" prévu pour "+notificationEventDateAndTime)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true);

        int mNotificationId = (int) Calendar.getInstance().getTimeInMillis();
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
    public void sendEmail(Context context){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"rivas.guillaume@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
