package cn.chenjianlink.android.alarmclock.activity;

//import android.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.ActivityHolderBinding;
//import it.feio.android.omninotes.R;
//import it.feio.android.omninotes.databinding.ActivityHolderBinding;
//import it.feio.android.omninotes.fragment.BackupInfoFragment;
//import it.feio.android.omninotes.utils.FileChooseUtil;
import cn.chenjianlink.android.alarmclock.fragment.ShowLogFragment;
import lombok.SneakyThrows;

//implements CardPagerAdapter.MySendValue
public class HolderActivity extends AppCompatActivity {

    //    ImageView weiduback;
//    TextView weidunum;

//    public int getFlag(){
//        return flag;
//    }

    //private ActivityMainBinding binding;
    //private ActivityTestBinding binding;
    //private ActivityNoBottomNavBinding binding;
    private ActivityHolderBinding binding;
    //ActivityHolderBinding


    //    Repository repository;
//    void initDatabase() {
//        repository=new Repository(this);
//    }
//    NavHostFragment navHostFragment;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
//判断触摸UP事件才会进行返回事件处理
            if (event.getAction() == KeyEvent.ACTION_UP) {
                onBackPressed();
            }
//只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
// do something…
//        FragmentManager fm = getFragmentManager();
        FragmentManager fm = getSupportFragmentManager();
        Log.d("onBackPressed", "onBackPressed: fm.getBackStackEntryCount() "+fm.getBackStackEntryCount() );
        if (fm.getBackStackEntryCount() > 0) {
        //if (fm.getBackStackEntryCount() >=2) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
        Log.d("onBackPressed", "onBackPressed: ");
    }

    static final int REQUEST_CHOOSEFILE=1;

    public  static  final  String KEY_INFO="KEY_INFO";
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        WindowManager windowManager = getWindowManager();
//        Circlei
//        CircleImageView
//        binding = ActivityTestBinding.inflate(getLayoutInflater());
        binding = ActivityHolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //holder
        //R.id
        Intent intent = getIntent();
        //intent.geex
        //String info = intent.getStringExtra(KEY_INFO);
        //getSupportFragmentManager().beginTransaction().
        //        //replace(R.id.holder,new TestHolderFragment()).addToBackStack(null).commit();
        //        replace(R.id.holder,new ShowLogFragment()).addToBackStack(null).commit();
        try{
            getSupportFragmentManager().beginTransaction().
                    //replace(R.id.holder,new TestHolderFragment()).addToBackStack(null).commit();
                            replace(R.id.holder,new ShowLogFragment()).commit();
        }catch (Exception e){
            e.printStackTrace();
        }

        //这个不要放到 addToBackStack

    }

    //@Override
    //public void onCardViewClicked(String url) {
    //    getSupportFragmentManager().beginTransaction().
    //            replace(R.id.nav_host_fragment_activity_main,new WebFragment(url))
    //            .addToBackStack(null).commit();
    //}
}