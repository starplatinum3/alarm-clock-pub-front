package cn.chenjianlink.android.alarmclock.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.NotificationMonitorBinding;
import cn.chenjianlink.android.alarmclock.observer.PhoneCode;

//import com.example.myapplication.databinding.NotificationMonitorBinding;
//import com.example.myapplication.observer.PhoneCode;

public class NotificationMonitorActivity extends AppCompatActivity {


    private NotificationMonitorBinding binding;

    EditText mEdtPhone;
    PhoneCode mPhoneCode;
    /**
     * 固定手机号码
     */
    private void fixedPhone(String phone) {
//        requestPermissions();
        mPhoneCode = new PhoneCode(this, new Handler(), phone,
                new PhoneCode.SmsListener() {
                    @Override
                    public void onResult(String result) {
//                        ToastUtils.showMessage(PhoneActivity.this, result);
//                        ToastUtils.showMessage(NotificationMonitorActivity.this, result);
                        Toast.makeText(NotificationMonitorActivity.this, result,
                                Toast.LENGTH_LONG).show();
                        mEdtPhone.setText(result);
                    }
                });
        // 注册短信变化监听
        this.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, mPhoneCode);
    }

    /**
     * 没有手机号码
     */
    private void fixedPhone() {
        mPhoneCode = new PhoneCode(this, new Handler(),
                new PhoneCode.SmsListener() {
                    @Override
                    public void onResult(String result) {
//                        ToastUtils.showMessage(PhoneActivity.this, result);
//                        ToastUtils.showMessage(NotificationMonitorActivity.this, result);
                        Toast.makeText(NotificationMonitorActivity.this, result,
                                Toast.LENGTH_LONG).show();
                        mEdtPhone.setText(result);
                    }
                });
        // 注册短信变化监听
        this.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, mPhoneCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NotificationMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setSupportActionBar(binding.toolbar);

        setNotificationMonitor();

        mEdtPhone=findViewById(R.id.edtPhone);
//        fixedPhone();
//        binding.btn

    }


    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    boolean notificationMonitorOn=true;

//    文档：钉钉的消息都能收到吗.note
//    链接：http://note.youdao.com/noteshare?id=17be5b5f9851d45b2e842334d12e12be&sub=FD0626A63CD74402A684E95CF593E607
//    这个获得了验证码诶
    void setNotificationMonitor(){
        //        https://www.jianshu.com/p/82713b43b59e?appinstall=0
        // 通知栏监控器开关
//        https://www.jianshu.com/p/82713b43b59e?appinstall=0
        Button notificationMonitorOnBtn = (Button)findViewById(R.id.notification_monitor_on_btn);
        notificationMonitorOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //https://blog.csdn.net/cankingapp/article/details/50858229
//                不能用的话 就打开
//                他是检查这个 服务是不是 可以用 ，那就是没有重复开启 确实
                if (!isEnabled()) {
                    //打开一个 activity  是获取权限的 service 是自动启动的吗
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//                    Toast.makeText(getApplicationContext(),
//                            "监控器开关已打开 无需重复点击", Toast.LENGTH_SHORT).show();
//                    toast.show();
//                    notificationMonitorOn=true;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已打开", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        Button notificationMonitorOffBtn = (Button)findViewById(R.id.notification_monitor_off_btn);
        notificationMonitorOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                //android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                if (isEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//                    notificationMonitorOn=false;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已关闭", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

}
