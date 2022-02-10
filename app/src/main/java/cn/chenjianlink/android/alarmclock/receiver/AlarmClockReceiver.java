package cn.chenjianlink.android.alarmclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.service.AlarmClockService;
import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;

/**
 * @author chenjian
 * 闹钟广播接收器，用于接收应用程序启用闹钟的广播
 */
public class AlarmClockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("AlarmClockReceiver", "--------------闹钟广播接收器开始执行-------------");
        String clockJson = intent.getStringExtra(CommonValue.ALARM_CLOCK_INTENT);
//        是不是这里收到了之后要重新传递呢，不然那边就拿不到ALARM_CLOCK_INTENT
        LogUtil.i("AlarmClockMessage", clockJson);

        //构建Intent
        Intent ringIntent = new Intent(context, AlarmClockService.class);
        ringIntent.putExtra(CommonValue.ALARM_CLOCK_INTENT, clockJson);
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(ringIntent);

//        https://blog.csdn.net/gusgao/article/details/52080351
//        if (intent.getAction().equals("DELIVERY_MESS_ACTION")) {
//            if (getResultCode() == -1) {
//                // Toast.makeText(context, "发送成功", 1).show();
//                MessSendActivity.getInstance().runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        // Toast.makeText(MessSendActivity.getInstance(),
//                        // "from messSend", 1).show();
//                        ProgressBar pb = (ProgressBar) MessSendActivity
//                                .getInstance().findViewById(R.id.pb);
//                        pb.setVisibility(View.INVISIBLE);
//                    }
//                });
//            } else if (getResultCode() == 0) {
//                Toast.makeText(context, "发送失败", 1).show();
//            }
//        }

    }

}
