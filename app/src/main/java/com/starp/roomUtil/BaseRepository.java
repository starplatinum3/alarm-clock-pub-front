package com.starp.roomUtil;//package com.example.whatrubbish.db;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

//import en.edu.zucc.pb.loginapplication.AppDatabase;
//import en.edu.zucc.pb.loginapplication.dao.UserDao;
//import en.edu.zucc.pb.loginapplication.entity.User;

//import androidx.lifecycle.LiveData;
//import cn.chenjianlink.android.alarmclock.db.AlarmClockDao;
//import cn.chenjianlink.android.alarmclock.db.BaseAlarmClockDatabase;
//import cn.chenjianlink.android.alarmclock.model.AlarmClock;
//import cn.chenjianlink.android.alarmclock.utils.LogUtil;

/**
 * @author chenjian
 * 对数据库操作进行封装，向ViewModel提供封装方法
 */
public class BaseRepository<T> {

//    private final ContentDao contentDao;
    private final BaseDao<T> baseDao;

//    private final LiveData<List<User>> userList;

    /**
     * 线程池，在子线程中处理增删改
     */
    private final ExecutorService executor;

    public ExecutorService getExecutor() {
        return executor;
    }

    public BaseDao<T> getBaseDao() {
        return baseDao;
    }



    public BaseRepository(BaseDao<T> baseDao) {
//        构造一定要构造的
        this.baseDao = baseDao;
         executor = Executors.newCachedThreadPool();
//        executor = new ThreadPoolExecutor(2, 4, 1,
//                TimeUnit.MINUTES, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

//    public BaseRepository(Context context) {
////        AppDatabase database = AppDatabase.getDatabase(context);
////        baseDao=database.
////        contentDao = AppDatabase.getDatabase(context).contentDao();
////        userList = contentDao.getAllLiveData();
//        executor = new ThreadPoolExecutor(2, 4, 1,
//                TimeUnit.MINUTES, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());
//    }

//    public void voidMethod() {
//        executor.execute(() -> baseDao.update(t));
//    }
//这里全部要 重新写 麻烦
    public void update(final T t) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                baseDao.update(t);
            }
        });
    }

    public void updateAll(final T ... ts) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                baseDao.updateAll(ts);
            }
        });
    }


    public void insert(final T t) {
        Log.i("Repository", "insert data");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                baseDao.insert(t);
            }
        });
    }

    public void insertAll(final T ... ts) {
        Log.i("Repository", "insertAll data");
//        executor.
//        executor.  getActiveCount()
//        executor.
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                至少他们还要生成 dao
//                RoomUtil.select(baseDao,ts[0])
//                jdbcutil 是从conn里面搜索 conn 是 pool里面来的
//                所以那个参数可以省略 但是这里针对每个不同的对象，都有不同的
//                所以不能 省略
//                话说运行中反射 其实效率应该比较差吧，更别说是 用在安卓 这种边缘设备了
//                不过我感觉可以减少写代码 就有价值 我喜欢
                baseDao.insertAll(ts);

            }
        });
//        executor.shutdown();
    }



//    public void clearAll() {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
////                @Query("DELETE FROM "+ Comment.TABLE_NAME)
////                void clearAll();
//                baseDao.clearAll();
////                baseDao.delete(t);
//            }
//        });
//    }


    public void delete(final T t) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                baseDao.delete(t);
            }
        });
    }

    /**
     * 查询出所有闹钟，若没有闹钟，则查找系统服务中的闹钟
     *
     * @return 返回LiveData包装的AlarmClock列表
     */
//    public LiveData<List<AlarmClock>> findAll() {
//        return alarmClockList;
//    }


//    public AlarmClock findById(int alarmClockId) {
//        return userDao.s(alarmClockId);
//    }

//    List<Content> all = contentDao.getAll();
//    @SuppressLint("StaticFieldLeak")
//    public  List<T> getAll() throws ExecutionException, InterruptedException {
////        List<T> all = baseDao.getAll();
//        return new AsyncTask<Void , Void, List<T>>() {
////            @SuppressLint("StaticFieldLeak")
//            @Override
//            protected List<T> doInBackground(Void ... voids) {
//              return   baseDao.getAll();
//            }
//
//        }.execute( ).get();
//    }

    @SuppressLint("StaticFieldLeak")
    public  List<T> selectBy(T obj) throws ExecutionException, InterruptedException {
//        List<T> all = baseDao.getAll();
        return new AsyncTask<Void , Void, List<T>>() {
            //            @SuppressLint("StaticFieldLeak")
            @SneakyThrows
            @Override
            protected List<T> doInBackground(Void ... voids) {
                //List<T> select = com.example.whatrubbish.db.RoomUtil.select(baseDao, obj);
                List<T> select =RoomUtil.select(baseDao, obj);
                return select;
//                return   baseDao.getViaQuery();
            }

        }.execute( ).get();
    }


//    @SuppressLint("StaticFieldLeak")
//    public  List<T> loadAllByIds(Long[] ids) throws ExecutionException, InterruptedException {
////        List<T> all = baseDao.getAll();
//        return new AsyncTask<Long , Void, List<T> >() {
////            @SuppressLint("StaticFieldLeak")
//            @Override
//            protected List<T> doInBackground(Long ... ids) {
//                return   baseDao.loadAllByIds(ids);
//            }
//
//        }.execute(ids ).get();
////        List<T> loadAllByIds(Long[] ids);
//    }


//    @SuppressLint("StaticFieldLeak")
//    public  T getById(Long id) throws ExecutionException, InterruptedException {
////        List<T> all = baseDao.getAll();
////        ContentDao
//        return new AsyncTask<Long , Void, T>() {
////            @SuppressLint("StaticFieldLeak")
//            @Override
//            protected T doInBackground(Long ... longs) {
//                Long id=longs[0];
//                return baseDao.getById(id);
////                return   baseDao.getAll();
//            }
//
//        }.execute( new Long[]{id}).get();
//
//    }

    /**
     * 查询出所有闹钟，若没有闹钟，则查找系统服务中的闹钟
     *
     * @return 返回LiveData包装的AlarmClock列表
     */
//    public LiveData<List<User>> getAll() {
//        return userList;
//    }

    /**
     * 查询单个闹钟
     *
     * @param userId 要查询的闹钟id
     * @return 闹钟对象
     */
//    public User findById(int userId) {
//        return userDao.selectById(userId);
//    }

    /**
     * 返回未封装在LiveData中的闹钟列表
     *
     * @return 闹钟列表
     */
//    public List<AlarmClock> findAllWithoutLiveData() {
//        return alarmClockDao.selectAllWithoutLiveData();
//    }

}
