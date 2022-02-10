package cn.chenjianlink.android.alarmclock.logInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.whatrubbish.Bus;
//import com.example.whatrubbish.R;
//import com.example.whatrubbish.util.ThreadPoolManager;

import com.starp.roomUtil.RoomUtil;
import com.starp.roomUtil.RoomUtilAppDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.chenjianlink.android.alarmclock.R;
import cn.chenjianlink.android.alarmclock.databinding.FragmentLogInfoBinding;
import cn.chenjianlink.android.alarmclock.databinding.ItemLogBinding;
import cn.chenjianlink.android.alarmclock.db.AppDatabase;
import cn.chenjianlink.android.alarmclock.model.LogInfo;
import cn.chenjianlink.android.alarmclock.utils.DateUtil;

//public class LogInfoAdapter extends Fragment {
//    private MypaimingAdapter myAdapter;
//    View view;
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        //  initGridIcons();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        view = inflater.inflate(R.layout.paiming, container, false);
//        //getActivity().setContentView(R.layout.paiming);
//        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view1);
//        //设置布局管理器
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        //设置adapter
//        myAdapter=new MypaimingAdapter(getActivity());
//        mRecyclerView.setAdapter(myAdapter);
//        //设置默认属性
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//
////       view = inflater.inflate(R.layout.paiming, container, false);
//
//        return view;
//    }
//
//
//
//
//
//}

public class LogInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //com.example.whatrubbish.databinding.PaimingitemBinding paimingitemBinding;
    ItemLogBinding binding;

    //List<String> namelist = new ArrayList<>();
    //List<String> jifenlist = new ArrayList<>();
    //List<Integer> touxianglist = new ArrayList<>();
    private Context context;
    private AppDatabase database;
    private List<LogInfo> logInfos;

    public List<LogInfo> getLogInfos() {
        return logInfos;
    }

    public void setLogInfos(List<LogInfo> logInfos) {
        this.logInfos = logInfos;
    }

    public LogInfoAdapter(Context context) {
        this.context = context;
        //init();
        //initDb();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.paimingitem, parent, false);
        View view = LayoutInflater.from(context).inflate(R.layout.item_log, parent, false);
        //binding= ItemLogBinding.bind(binding);//最重要
        ViewHolder holder = new ViewHolder(view);
        //holder.
        //binding = ItemLogBinding.inflate(inflater, container, false);
        //holder.tvTitle = binding.tvTitle;
        //holder.tvContent = binding.tvContent;
        //holder.tvDate = binding.tvDate;

        holder.tvTitle = view.findViewById(R.id.tv_title);
        holder.tvContent = view.findViewById(R.id.tv_content);
        holder.tvDate =view.findViewById(R.id.tv_date);

        //holder.paim=(TextView)view.findViewById(R.id.paimnum);
        //holder.touxiang=(ImageView) view.findViewById(R.id.imageView5);
        //holder.jifen=(TextView) view.findViewById(R.id.textView8);
        // holder.jifen=paimingitemBinding.textView8;
        // holder.name=(TextView) view.findViewById(R.id.textView6);
        return holder;


    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Do whatever you want on clicking the normal items
                }
            });
        }

        private TextView name;
        private TextView jifen;
        private ImageView touxiang;
        // private com.example.whatrubbish.databinding.PaimingitemBinding paimingitemBinding;


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        LogInfo logInfo = logInfos.get(position);

        holder.tvTitle.setText(logInfo.getTitle());
        holder.tvContent.setText(logInfo.getContent());
        holder.tvDate.setText(DateUtil.dateToString(logInfo.getDate(), DateUtil.FORMAT_ONE));

        //if (position == 0) {
        //    holder.paim.setText(String.valueOf(position + 1));
        //    holder.name.setText(namelist.get(position));
        //    holder.touxiang.setImageResource(touxianglist.get(position));
        //    Thread thread = new Thread(() -> {
        //        try {
        //            setPointCnt();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        //
        //    });
        //    ThreadPoolManager.run(thread);
        //
        //} else if (position < getItemCount() - 1) {
        //    holder.paim.setText(String.valueOf(position + 1));
        //    holder.name.setText(namelist.get(position));
        //    holder.jifen.setText(jifenlist.get(position));
        //    holder.touxiang.setImageResource(touxianglist.get(position));
        //} else {
        //    holder.name.setText("");
        //    holder.paim.setText("");
        //    holder.touxiang.setVisibility(View.GONE);
        //}
    }


    //void initDb() {
    //    database = AppDatabase.getDatabase(context);
    //    //database.logInfoDao().
    //    try {
    //        logInfos = RoomUtil.select(database.logInfoDao(), LogInfo.builder().build());
    //
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}


    @Override
    public int getItemCount() {
        return logInfos.size();
        //return 9;
    }
}