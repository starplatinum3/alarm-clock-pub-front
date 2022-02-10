package cn.chenjianlink.android.alarmclock.logInfo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.model.LogInfo;
import kale.adapter.item.AdapterItem;

public class LogInfoItem implements AdapterItem<LogInfo> {

    @Override
    public int getLayoutResId() {
        //CommonAdapter
        //return R.layout.demo_item_text;
        return R.layout.item_log;
    }

    @Override
    public void bindViews(@NonNull View root) {
        //textView = (TextView) root.findViewById(R.id.textView);
    }

    @Override
    public void setViews() {

    }

    @Override
    public void handleData(LogInfo logInfo, int position) {
        //textView.setText(model.content);
    }

    TextView textView;


}