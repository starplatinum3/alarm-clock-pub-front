package cn.chenjianlink.android.alarmclock.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Scanner;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.FragmentShowLogBinding;
import cn.chenjianlink.android.alarmclock.logInfo.ShowLogTableFragmentCp;
import cn.chenjianlink.android.alarmclock.logInfo.ShowLogTableFragmentCpKt;
import cn.chenjianlink.android.alarmclock.utils.ActivityUtil;
import cn.chenjianlink.android.alarmclock.utils.LogcatHelper;
import lombok.SneakyThrows;

//import com.example.whatrubbish.databinding.FragmentImageBinding;
//import com.example.whatrubbish.databinding.FragmentRubbishBinding;
//import com.example.whatrubbish.entity.RubbishInfo;

//@EqualsAndHashCode(callSuper = true)
//@Data
//@Builder
public class ShowLogFragment extends Fragment {

    //FragmentRubbishBinding binding;
    FragmentShowLogBinding binding;
    //RubbishInfo rubbishInfo;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String TAG = "ImageFragment";

    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //这个修改为对应子Fragment和父Fragment的布局文件
//        R.id
        binding = FragmentShowLogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //boolean ok = showLog();
        //if(ok==false){
        //    return root;
        //}
        //String pathLogcat = LogcatHelper.getPathLogcat(getContext());
        //String pathLogcat = LogcatHelper.getAbsFilename(getContext());
        //File file = new File(pathLogcat);
        ////file.
        //Log.i(TAG, "onCreateView: file "+file);
        //Log.i(TAG, "onCreateView: pathLogcat "+pathLogcat);
        //if(file.exists()==false){
        //    Log.i(TAG, "onCreateView: 不存在");
        //    return root;
        //}
        //binding.showLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        //try(Scanner scanner = new Scanner(new File(pathLogcat))){
        //    StringBuilder data= new StringBuilder();
        //    while (scanner.hasNextLine()) {
        //        data.append(scanner.nextLine());
        //    }
        //    binding.showLog.setText(data.toString());
        //}


        binding.btnToLogTable.setOnClickListener(view -> {
            //ActivityUtil.startActivity(getActivity(),);
           getActivity(). getSupportFragmentManager().beginTransaction().
                            //replace(R.id.holder,new ShowLogTableFragment()).commit();
                            replace(R.id.holder,new ShowLogTableFragmentCp()).commit();
        });

        return root;
    }

    boolean showLog(){
        //String pathLogcat = LogcatHelper.getPathLogcat(getContext());
        String pathLogcat = LogcatHelper.getAbsFilename(getContext());
        File file = new File(pathLogcat);
        //file.
        Log.i(TAG, "onCreateView: file "+file);
        Log.i(TAG, "onCreateView: pathLogcat "+pathLogcat);
        if(file.exists()==false){
            Log.i(TAG, "onCreateView: 不存在");
            return false;
        }
        binding.showLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        try(Scanner scanner = new Scanner(new File(pathLogcat))){
            StringBuilder data= new StringBuilder();
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            binding.showLog.setText(data.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
//————————————————
//    版权声明：本文为CSDN博主「不知 不知」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/qq_41858698/article/details/105452276
}
