package kr.co.itforone.forestmk_android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class My_Firebase_Messaging_Service extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("remote","Message:"+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> pushMessage = remoteMessage.getData();

        if(remoteMessage.getData().size() > 0) {

            if (remoteMessage != null && remoteMessage.getData().size() > 0) {
                sendNotification(remoteMessage);
            }
        }
        //  sendNotification(remoteMessage);
    }
    public void sendNotification(RemoteMessage remoteMessage){
        String messageBody=remoteMessage.getData().get("message");
        String subject=remoteMessage.getData().get("subject");
        String goUrl=remoteMessage.getData().get("goUrl");
        String channelId = "forestmk_android";
        Log.d("remote","Message:"+remoteMessage.getFrom());


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("goUrl",goUrl);



        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Bitmap BigPictureStyle= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        long vibrate[]={500,0,500,0};
        /**/
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(subject)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setVibrate(vibrate)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));


        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
