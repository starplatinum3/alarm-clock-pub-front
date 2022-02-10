package com.starp.roomUtil;//package com.example.whatrubbish.db;

import android.app.Application;
import android.content.Context;
import android.util.Log;

//import com.facebook.stetho.Stetho;

/**
 * @作者: njb
 * @时间: 2019/7/22 11:07
 * @描述:
 */
public class App extends Application {
    //全局Context
    private static Context sContext;
    private static final String  TAG = "App";

    //static com.example.whatrubbish.db.Repository repository;
    //static com.example.whatrubbish.db.Repository repository;
    static Repository repository;
    static void initDatabase() {
//        这里是null
        Log.i(TAG, "sContext  "+String.valueOf(sContext));
        //repository=new com.example.whatrubbish.db.Repository(sContext);
        repository=new Repository(sContext);
    }

//    public Repository getRepository() {
//        if(repository==null){
//            initDatabase();
//        }
//        return repository;
//    }
//
//    public void setRepository(Repository repository) {
//        this.repository = repository;
//    }


    //public static com.example.whatrubbish.db.Repository getRepository() {
    public static   Repository getRepository() {
        if(repository==null){
            initDatabase();
        }
        return repository;
    }

    //public static void setRepository(com.example.whatrubbish.db.Repository repository) {
    public static void setRepository(Repository repository) {
        App.repository = repository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        initDatabase();
        //Stetho.initializeWithDefaults(this);

    }


    public static Context getContext() {
//        if(sContext==null){
//            sContext= getApplicationContext();
//        }
        return sContext;
    }
}
