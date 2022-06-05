package com.example.mymemories;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ShowNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Time up! :)", Toast.LENGTH_LONG).show();
        System.out.println("Alarma estoy en show notification");

        String titulo = intent.getStringExtra("titulo");
        String categoria = intent.getStringExtra("categoria");
        String contentText;
        if(categoria.equals("Viajes"))
        {
            contentText = "estuviste en " + titulo + ", ¡que buen recuerdo!";
        }
        else if(categoria.equals("Cervezas"))
        {
            contentText = "te tomaste una cerveza " + titulo + ", ¡que buen recuerdo!";
        }
        else if(categoria.equals("Ramos"))
        {
            contentText = "disfrutaste este ramo de flores " + titulo + ", ¡que buen recuerdo!";
        }else
        {
            contentText = "estuviste en el concierto de " + titulo + ", ¡que buen recuerdo!";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyLemubit")
                .setSmallIcon(R.drawable.imagen_icon)
                .setContentTitle("Tal día como hoy...")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());
    }
}
