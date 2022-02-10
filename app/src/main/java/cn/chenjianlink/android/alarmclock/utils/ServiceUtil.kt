package cn.chenjianlink.android.alarmclock.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build

class ServiceUtil {
    companion object{
        //const val FOREGROUND_SERVICE_NOTIFICATION_ID=1
        const val FOREGROUND_SERVICE_NOTIFICATION_ID=110
        //https://blog.csdn.net/hongye_main/article/details/97761860
        fun  createChannelAndStartService(service:Service){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "default"
                val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
                val nm = service.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                nm?.let {
                    if (it.getNotificationChannel(channelId) == null) {//没有创建
                        it.createNotificationChannel(channel)//则先创建
                    }
                }
                val notification: Notification
                val builder = Notification.Builder(service, channelId)
                    .setContentTitle("")
                    .setContentText("")
                notification = builder.build()
                service.startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
            }

        }


        //private void createNotificationChannel(NotificationManager manager) {
        //    if (manager.getNotificationChannel("default") == null) {
        //        NotificationChannel notificationChannel =
        //        new NotificationChannel("default",
        //        "notification_channel",
        //        NotificationManager.IMPORTANCE_LOW);
        //
        //        notificationChannel.setDescription(
        //            "notification_channel_description");
        //
        //        manager.createNotificationChannel(notificationChannel);
        //    }
        //}
    }
}