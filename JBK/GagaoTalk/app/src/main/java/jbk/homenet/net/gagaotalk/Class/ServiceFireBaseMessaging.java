package jbk.homenet.net.gagaotalk.Class;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import jbk.homenet.net.gagaotalk.Activity.MainActivity;
import jbk.homenet.net.gagaotalk.Activity.MainFrameActivity;
import jbk.homenet.net.gagaotalk.R;

public class ServiceFireBaseMessaging extends FirebaseMessagingService {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (CommonService.UserInfo != null) {
            if (CommonService.UserInfo.isPush){
                Map<String, String> pushDataMap = remoteMessage.getData();
                sendNotification(pushDataMap);
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        if (CommonService.UserInfo != null){
            DatabaseReference messageInfo= FirebaseDatabase.getInstance().getReference();
            messageInfo.child("users").child(CommonService.UserInfo.uid).child("tokenId").setValue(s);
        }
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    private void sendNotification(Map<String, String> dataMap) {
        Intent intent = new Intent(this, MainFrameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(dataMap.get("from_user)"))
                .setContentText(dataMap.get("contents"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setLights(Color.WHITE, 1500, 1500)
                .setContentIntent(contentIntent);


        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(0 /* ID of notification */, nBuilder.build());
    }
}