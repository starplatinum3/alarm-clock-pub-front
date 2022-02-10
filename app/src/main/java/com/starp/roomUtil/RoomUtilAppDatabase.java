package com.starp.roomUtil;
//package com.example.whatrubbish.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

//import com.starp.roomUtil.RoomConverter;

//import com.starp.roomUtil.RoomConverter;


//@Database(entities = {Article.class, Basket.class, Card.class, CardGame.class, City.class, ColeFragGameNow.class, ColeFragGameStat.class, Friendship.class, Game.class, GameHonor.class, GameRecord.class, Place.class, Present.class, PsnExchgRec.class, Rubbish.class, RubbishType.class, RubTyCoresp.class, ShootGame.class, SignInHonor.class, SignInStd.class, User.class, WikiHistory.class}, version = 2, exportSchema = false)
@TypeConverters({RoomConverter.class})
public abstract class RoomUtilAppDatabase extends RoomDatabase {

//这个感觉没有用处
    static String dbFileName = "whatRubbish.db";

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


    private static volatile RoomUtilAppDatabase INSTANCE;

    public static RoomUtilAppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (RoomUtilAppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, RoomUtilAppDatabase.class, dbFileName)
                    //INSTANCE = Room.databaseBuilder(context, , dbFileName)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //public static RoomUtilAppDatabase getDatabase(Context context,Class<?> cls) {
    //    if (INSTANCE == null) {
    //        synchronized (cls) {
    //            if (INSTANCE == null) {
    //                INSTANCE = Room.databaseBuilder(context, cls, dbFileName)
    //                        //INSTANCE = Room.databaseBuilder(context, , dbFileName)
    //                        .fallbackToDestructiveMigration()
    //                        .build();
    //            }
    //        }
    //    }
    //    return INSTANCE;
    //}
}


