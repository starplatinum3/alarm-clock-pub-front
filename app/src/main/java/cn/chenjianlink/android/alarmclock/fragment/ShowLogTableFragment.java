package cn.chenjianlink.android.alarmclock.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;

import com.starp.roomUtil.RoomUtil;
import com.starp.roomUtil.RoomUtilAppDatabase;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.FragmentLogInfoBinding;
import cn.chenjianlink.android.alarmclock.databinding.FragmentShowLogBinding;
import cn.chenjianlink.android.alarmclock.db.AppDatabase;
import cn.chenjianlink.android.alarmclock.logInfo.LogInfoAdapter;
import cn.chenjianlink.android.alarmclock.logInfo.LogInfoItem;
import cn.chenjianlink.android.alarmclock.model.LogInfo;
import cn.chenjianlink.android.alarmclock.utils.LogcatHelper;
import cn.chenjianlink.android.alarmclock.utils.ThreadPoolFactory;
import kale.adapter.CommonRcvAdapter;
import kale.adapter.item.AdapterItem;
import lombok.SneakyThrows;

//import com.example.whatrubbish.databinding.FragmentImageBinding;
//import com.example.whatrubbish.databinding.FragmentRubbishBinding;
//import com.example.whatrubbish.entity.RubbishInfo;

//@EqualsAndHashCode(callSuper = true)
//@Data
//@Builder
public class ShowLogTableFragment extends Fragment {

    //FragmentRubbishBinding binding;
    //FragmentShowLogBinding binding;
    FragmentLogInfoBinding binding;
    private AppDatabase database;
    //RubbishInfo rubbishInfo;

    String emptyStr="数据为空";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String TAG = "ImageFragment";

    RecyclerView.Adapter  mAdapter;
    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //这个修改为对应子Fragment和父Fragment的布局文件
//        R.id
        binding = FragmentLogInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database = AppDatabase.getDatabase(getContext());
        //database.logInfoDao().
        binding.emptyStr.setText(emptyStr);
        //compose 和 sqlite
        ThreadPoolFactory.getExecutorService().execute(()->{
            try{
                //LogInfo logInfo = new LogInfo(null,null,null,null,null);
                LogInfo logInfo = new LogInfo(null);
                //LogInfo.class
                //List<LogInfo> select = RoomUtil.select(database.logInfoDao(), LogInfo.builder().build());
                List<LogInfo> select = RoomUtil.select(database.logInfoDao(), logInfo);
                //mAdapter = new CommonRcvAdapter<LogInfo>(select) {
                //    @NonNull
                //    @Override
                //    public AdapterItem createItem(Object type) {
                //        return new LogInfoItem();
                //    }
                //};

                if(select.size()!=0){
                    emptyStr="";
                    getActivity().runOnUiThread(()->{
                        binding.emptyStr.setText(emptyStr);
                    });

                }
                Log.i(TAG, "onCreateView: select  "+select);
                getActivity().runOnUiThread(()->{
                    //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    //binding.recyclerView.setLayoutManager(layoutManager);
                    //binding.recyclerView.setAdapter(mAdapter);
                    ////设置默认属性
                    //binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //
                    //RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view1);
                    //设置布局管理器
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    //设置adapter
                    //myAdapter=new MypaimingAdapter(getActivity());
                    LogInfoAdapter   myAdapter=new LogInfoAdapter(getActivity());
                    myAdapter.setLogInfos(select);
                    binding.recyclerView.setAdapter(myAdapter);
                    //设置默认属性
                    binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                });

                //mAdapter.
            }catch (Exception e){
                e.printStackTrace();
            }


        });

        //binding.emptyStr.setText(emptyStr);
        return root;
    }

    void initDb() {
        database = AppDatabase.getDatabase(getActivity());
        //database.logInfoDao().
        //ThreadPoolFactory.getExecutorService().execute(()->{
        //    try {
        //        List<LogInfo>       logInfos = RoomUtil.select(database.logInfoDao(), LogInfo.builder().build());
        //        //logInfos
        //    } catch (Exception e) {
        //        e.printStackTrace();
        //    }
        //});

    }


    @Override
    public void onPause() {
        super.onPause();
    }
//————————————————
//    版权声明：本文为CSDN博主「不知 不知」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/qq_41858698/article/details/105452276
}
