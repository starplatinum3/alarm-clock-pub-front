package cn.chenjianlink.android.alarmclock.db;
//package com.example.whatrubbish.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

//import com.example.whatrubbish.dao.ArticleDao;
//import com.example.whatrubbish.dao.BasketDao;
//import com.example.whatrubbish.dao.CardDao;
//import com.example.whatrubbish.dao.CardGameDao;
//import com.example.whatrubbish.dao.CityDao;
//import com.example.whatrubbish.dao.ColeFragGameNowDao;
//import com.example.whatrubbish.dao.ColeFragGameStatDao;
//import com.example.whatrubbish.dao.FriendshipDao;
//import com.example.whatrubbish.dao.GameDao;
//import com.example.whatrubbish.dao.GameHonorDao;
//import com.example.whatrubbish.dao.GameRecordDao;
//import com.example.whatrubbish.dao.PlaceDao;
//import com.example.whatrubbish.dao.PresentDao;
//import com.example.whatrubbish.dao.PsnExchgRecDao;
//import com.example.whatrubbish.dao.RubbishDao;
//import com.example.whatrubbish.dao.RubbishTypeDao;
//import com.example.whatrubbish.dao.RubTyCorespDao;
//import com.example.whatrubbish.dao.ShootGameDao;
//import com.example.whatrubbish.dao.SignInHonorDao;
//import com.example.whatrubbish.dao.SignInStdDao;
//import com.example.whatrubbish.dao.UserDao;
//import com.example.whatrubbish.dao.WikiHistoryDao;
//
//import com.example.whatrubbish.entity.Article;
//import com.example.whatrubbish.entity.Basket;
//import com.example.whatrubbish.entity.Card;
//import com.example.whatrubbish.entity.CardGame;
//import com.example.whatrubbish.entity.City;
//import com.example.whatrubbish.entity.ColeFragGameNow;
//import com.example.whatrubbish.entity.ColeFragGameStat;
//import com.example.whatrubbish.entity.Friendship;
//import com.example.whatrubbish.entity.Game;
//import com.example.whatrubbish.entity.GameHonor;
//import com.example.whatrubbish.entity.GameRecord;
//import com.example.whatrubbish.entity.Place;
//import com.example.whatrubbish.entity.Present;
//import com.example.whatrubbish.entity.PsnExchgRec;
//import com.example.whatrubbish.entity.Rubbish;
//import com.example.whatrubbish.entity.RubbishType;
//import com.example.whatrubbish.entity.RubTyCoresp;
//import com.example.whatrubbish.entity.ShootGame;
//import com.example.whatrubbish.entity.SignInHonor;
//import com.example.whatrubbish.entity.SignInStd;
//import com.example.whatrubbish.entity.User;
//import com.example.whatrubbish.entity.WikiHistory;
import com.starp.roomUtil.RoomConverter;
import com.starp.roomUtil.RoomUtilAppDatabase;

import cn.chenjianlink.android.alarmclock.dao.LogInfoDao;
import cn.chenjianlink.android.alarmclock.model.LogInfo;


@Database(entities = {LogInfo.class}, version = 3, exportSchema = false)
@TypeConverters({RoomConverter.class})
//注解会继承吗 感觉可以
//https://www.cnblogs.com/chenkeyu/p/7895751.html
//public abstract class AppDatabase extends RoomUtilAppDatabase {
public abstract class AppDatabase extends RoomDatabase {

    public abstract LogInfoDao logInfoDao();
    //static String dbFileName = "whatRubbish.db";
    static String dbFileName = "clock_log.db";
    //
    //public abstract ArticleDao articleDao();
    //
    //public abstract BasketDao basketDao();
    //
    //public abstract CardDao cardDao();
    //
    //public abstract CardGameDao cardGameDao();
    //
    //public abstract CityDao cityDao();
    //
    //public abstract ColeFragGameNowDao coleFragGameNowDao();
    //
    //public abstract ColeFragGameStatDao coleFragGameStatDao();
    //
    //public abstract FriendshipDao friendshipDao();
    //
    //public abstract GameDao gameDao();
    //
    //public abstract GameHonorDao gameHonorDao();
    //
    //public abstract GameRecordDao gameRecordDao();
    //
    //public abstract PlaceDao placeDao();
    //
    //public abstract PresentDao presentDao();
    //
    //public abstract PsnExchgRecDao psnExchgRecDao();
    //
    //public abstract RubbishDao rubbishDao();
    //
    //public abstract RubbishTypeDao rubbishTypeDao();
    //
    //public abstract RubTyCorespDao rubTyCorespDao();
    //
    //public abstract ShootGameDao shootGameDao();
    //
    //public abstract SignInHonorDao signInHonorDao();
    //
    //public abstract SignInStdDao signInStdDao();
    //
    //public abstract UserDao userDao();
    //
    //public abstract WikiHistoryDao wikiHistoryDao();


    private static volatile AppDatabase INSTANCE;
    //
    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, dbFileName)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


