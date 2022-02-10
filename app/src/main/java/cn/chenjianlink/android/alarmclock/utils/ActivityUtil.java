package cn.chenjianlink.android.alarmclock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityUtil {
  public  static   void startActivity(Activity activity, Class<?> cls){
        Intent intent = new Intent(activity,cls);
        activity.startActivity(intent);
    }

    public  static   void startActivity(Context context, Class<?> cls){
        Intent intent = new Intent(context,cls);
        context.startActivity(intent);
    }
    public  static   void startService(Context context, Class<?> cls){
        Intent intent = new Intent(context,cls);
        context.startService(intent);
    }
}
