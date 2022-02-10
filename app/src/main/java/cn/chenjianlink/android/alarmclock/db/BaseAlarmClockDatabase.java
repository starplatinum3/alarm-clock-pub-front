package cn.chenjianlink.android.alarmclock.db;

//import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;

import cn.chenjianlink.android.alarmclock.model.AlarmClock;
import cn.chenjianlink.android.alarmclock.utils.LogUtil;

/**
 * @author chenjian
 * room 数据库 连接
 * 闹钟应用数据库
 */
@Database(entities = {AlarmClock.class}, version = 2)
public abstract class BaseAlarmClockDatabase extends RoomDatabase {
// 是不是不用实现 而是根据 注解来获取的啊
    private static final String TAG  = "BaseAlarmClockDatabase";
    /**
     * 获取AlarmClockDao对象
     *
     * @return AlarmClockDao对象
     */
    public abstract AlarmClockDao getAlarmClockDao();
    // 这是哪里实现的

    private static volatile BaseAlarmClockDatabase INSTANCE;

//    https://blog.csdn.net/wjzj000/article/details/95863976
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            room   migrate   之后 程序闪退
            // 因为没有变化，所以是一个空实现
            // 创建临时表
//            BaseAlarmClockDatabase.class.getAnnotation(Database.class)
//            BaseAlarmClockDatabase.
//            Entity annotation = AlarmClock.class.getAnnotation(Entity.class);
//            annotation.tableName();
//            Annotation 'Entity.class' is not retained for reflective access

//            database.execSQL(
//                    "CREATE TABLE users_new (userid TEXT, username TEXT, last_update INTEGER, PRIMARY KEY(userid))");
//            // 拷贝数据
//            database.execSQL(
//                    "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users");
//            // 删除老的表
//            database.execSQL("DROP TABLE users");
//            // 改名
//            database.execSQL("ALTER TABLE users_new RENAME TO users");

//————————————————
//            版权声明：本文为CSDN博主「MDove」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//            原文链接：https://blog.csdn.net/wjzj000/article/details/95863976

//            文档：Errorjava Compilation failed interna...
//            链接：http://note.youdao.com/noteshare?id=f30e9e7b123807fc0d866c604ba6e307&sub=C585900A2A8A48598FB687FD5F69471E

//            Cursor query = database.query("select * from clock_table");
////            query.
//            query.moveToFirst();
//            while (query.moveToNext()){
//                String[] columnNames = query.getColumnNames();
////                query.ge
////                int nameColumnIndex = query.getColumnIndex(Contacts.People.NAME);
////                int nameColumnIndex = query.getColumnIndex(AlarmClock.tableName);
////                String name = cur.getString(nameColumnIndex);
//                LogUtil.i(TAG, "columnNames  "+ Arrays.toString(columnNames));
//            }


//            schemas 不知道是不是要手动创建文件夹的，也有可能是运行了之后才会出现，总之build gradle的时候还是报错的
//            但是运行之后 文件是有的

//            https://blog.csdn.net/u013762572/article/details/106315045
            database.execSQL("ALTER TABLE clock_table ADD COLUMN stopOnce INTEGER");

//            // Create the new table
////            database.execSQL("CREATE TABLE clock_table_new ( clock_id INTEGER , hour INTEGER , minute INTEGER , ringCycle INTEGER , isStarted TINYINT , remark VARCHAR(255) , ringMusicUri VARCHAR(255) , isVibrated TINYINT , stopOnce TINYINT ,  PRIMARY KEY( clock_id )  )");
////            database.execSQL("CREATE TABLE `clock_table_new` (`clock_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `ringCycle` INTEGER NOT NULL, `isStarted` INTEGER NOT NULL, `remark` TEXT, `ringMusicUri` TEXT, `isVibrated` INTEGER NOT NULL, `stopOnce` INTEGER NOT NULL )   ");
//            database.execSQL("CREATE TABLE IF NOT EXISTS `clock_table_new` (`clock_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `ringCycle` INTEGER NOT NULL, `isStarted` INTEGER NOT NULL, `remark` TEXT, `ringMusicUri` TEXT, `isVibrated` INTEGER NOT NULL, `stopOnce` INTEGER NOT NULL) ");
//////  Copy the data
////            database.execSQL("INSERT INTO clock_table_new ( clock_id, hour, minute, ringCycle, isStarted, remark, ringMusicUri, isVibrated, stopOnce ) SELECT  clockId, hour, minute, ringCycle, isStarted, remark, ringMusicUri, isVibrated, stopOnce  FROM   clock_table  ");
//            database.execSQL("INSERT INTO clock_table_new ( clock_id, hour, minute, ringCycle, isStarted, remark, ringMusicUri, isVibrated,stopOnce ) SELECT  clockId, hour, minute, ringCycle, isStarted, remark, ringMusicUri, isVibrated,'0'  FROM   clock_table  ");
////            database.execSQL("INSERT INTO clock_table_new  SELECT * FROM   clock_table  ");
//
////            Cursor  安卓
//
//// Remove the old table
//            database.execSQL("DROP TABLE clock_table ");
//// Change the table name to the correct one
//            database.execSQL("ALTER TABLE clock_table_new RENAME TO clock_table");

        }
    };
//————————————————
//    版权声明：本文为CSDN博主「MDove」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/wjzj000/article/details/95863976


    public static BaseAlarmClockDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (BaseAlarmClockDatabase.class) {
                if (INSTANCE == null) {
//                    "alarm_clock" 这个是储存的文件的名字吧 databases里面的
                    String  dbFileName="alarm_clock.db";
//                    String  dbFileName="alarm_clock";
                    INSTANCE = Room.databaseBuilder(context, BaseAlarmClockDatabase.class, dbFileName)
                            .allowMainThreadQueries()
//                            .addMigrations(MIGRATION_1_2)
//                    https://blog.csdn.net/u013762572/article/details/106315045
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
