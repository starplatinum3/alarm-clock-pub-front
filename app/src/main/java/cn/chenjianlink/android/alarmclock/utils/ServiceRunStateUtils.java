package cn.chenjianlink.android.alarmclock.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

//https://blog.csdn.net/daojian5173/article/details/62432175
public class ServiceRunStateUtils {
    /**
     * 判断一个服务是否处于运行状态
     *
     * @param context 上下文
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> infos = am.getRunningServices(200);
        for (RunningServiceInfo info : infos) {
            String serviceClassName = info.service.getClassName();
            if (className.equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }
}
//————————————————
//版权声明：本文为CSDN博主「daojian5173」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//原文链接：https://blog.csdn.net/daojian5173/article/details/62432175