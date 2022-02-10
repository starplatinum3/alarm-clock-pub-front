package com.starp.roomUtil;
//package cn.chenjianlink.android.alarmclock.db;
//package com.example.whatrubbish.db;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

//@Dao
//extends Entity
public interface BaseDao<T> {
//    public interface BaseDao<T> {
//    @RawQuery
//    List<T> getViaQuery(SupportSQLiteQuery query);


    //    @Query()
//    @Query("SELECT * FROM " + T.TABLE_NAME)
//    这里好像不会用 具体的，而是用的 Entity
//    List<T> getAll();
////    如果这里不写 sele 是不是 不会出错啊
//
//    T getById(Long id);
//
//    //    是不是没有编译他就是红色的 id
//    List<T> loadAllByIds(Long[] ids);



//    @Query(String.format("SELECT * FROM %s where id = :id" ,T.TABLE_NAME ))
//    @Query("SELECT * FROM "+T.TABLE_NAME +" where 'id' = :id limit 1" )
//    T getById(long id);

//    @Query("SELECT * FROM "+ User.TABLE_NAME)
//    LiveData<List<User>> getAll();

//    @Query("SELECT * FROM "+T.TABLE_NAME+" WHERE 'id' IN (:ids)")
//    List<T> loadAllByIds(int[] ids);

    //    comment 有个 爸爸是 content 他会有两个爸爸吗， 直接这样子拼串 会怎么样
//    @Query("SELECT * FROM " + Comment.TABLE_NAME+ " where :fatherIdColName = :fatherId " )
//    List<T> getAllByFatherId(Long fatherId, String fatherIdColName);

    @RawQuery
    List<T> getViaQuery(SupportSQLiteQuery query);

    //搞不了
    //@RawQuery(observedEntities = T.class)
    //Flow<List<T>> getViaQueryToFlow(SupportSQLiteQuery query);


    @Insert
    void insertAll(T... ts);

    @Insert
    long insert(T t);

    @Update
    void updateAll(T... ts);

    @Update
    void update(T t);

    @Delete
    void delete(T t);

//    @Query("DELETE FROM author")
//    void clearAll();
}
