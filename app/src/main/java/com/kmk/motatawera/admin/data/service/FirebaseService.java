package com.kmk.motatawera.admin.data.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kmk.motatawera.admin.R;
import com.kmk.motatawera.admin.ui.auth.LoginActivity;
import com.kmk.motatawera.admin.util.CheckInternetConn;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (new CheckInternetConn(this).isConnection())
            if (firebaseAuth.getCurrentUser() != null) {
                String uid = firebaseAuth.getUid();
                Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
                if (uid != null) {
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String token = task.getResult();
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("admin")
                                            .document(uid)
                                            .get()
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    DocumentReference db = FirebaseFirestore.getInstance()
                                                            .collection("admin")
                                                            .document(uid);
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("token", token);
                                                    db.update(map);
                                                    Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }

            }
    }



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Context context = getApplicationContext();
        if (firebaseUser != null && firebaseUser.getUid().equals(sented)) {

            pushNotification(context, remoteMessage);

        }
    }

    private void pushNotification(Context context, RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "News";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.default_notification_channel_id);
            String Description = "any news about Institute";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.WHITE);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder.setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else {
            builder.setSmallIcon(R.drawable.common_full_open_on_phone);
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(body));
        }
        builder.setContentTitle(title)
                .setContentText(body)
                .setLights(Color.YELLOW, 3000, 300)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 600})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent resultIntent = new Intent(context, LoginActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(m, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        notificationManager.notify(m, builder.build());
    }

}