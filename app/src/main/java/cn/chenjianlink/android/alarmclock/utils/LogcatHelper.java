package cn.chenjianlink.android.alarmclock.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.chenjianlink.android.alarmclock.activity.MainActivity;

/**
 * Created by elena on 2017/9/4.
 */

public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;

    public static String getPathLogcat() {
        //if(PATH_LOGCAT==null){
        //    initPath();
        //}
        return PATH_LOGCAT;
    }

    public static String getPathLogcat(Context context) {
        if (PATH_LOGCAT == null) {
            initPath(context);
        }
        return PATH_LOGCAT;
    }


    public static void setPathLogcat(String pathLogcat) {
        PATH_LOGCAT = pathLogcat;
    }

    public static void initPath(Context context) {
        //这里没有权限 自己的包下面是有权限的
        //if (Environment.getExternalStorageState().equals(
        //        Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
        //    //保存在这里貌似没有权限啊
        //    PATH_LOGCAT = Environment.getExternalStorageDirectory()
        //            .getAbsolutePath() + File.separator + "bsyqLog";
        //} else {// 如果SD卡不存在，就保存到本应用的目录下
        //    PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
        //            + File.separator + "llvisionLog";
        //}

        PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                + File.separator + "llvisionLog";

        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            Log.i("mkdirs", "initPath: " + mkdirs);
        }
        //if()
    }

    /**
     * 初始化目录
     */
    public void init(Context context) {
        initPath(context);
        //if (Environment.getExternalStorageState().equals(
        //        Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
        //    PATH_LOGCAT = Environment.getExternalStorageDirectory()
        //            .getAbsolutePath() + File.separator + "bsyqLog";
        //} else {// 如果SD卡不存在，就保存到本应用的目录下
        //    PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
        //            + File.separator + "llvisionLog";
        //}
        Log.i("PATH_LOGCAT", "init: " + PATH_LOGCAT);
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        Log.i("PATH_LOGCAT", "start: " + PATH_LOGCAT);
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
            mLogDumper.start();
        }
        //不是null 就是之前弄出来过了 就不要再来一个了

    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    public static String getAbsFilename(Context context) {
        String pathLogcat = getPathLogcat(context);
        return pathLogcat + "/log-"
                + getFileName() + ".log";
        //File file = new File(dir, "log-"
        //        + getFileName() + ".log");
    }

    public static String getAbsFilename() {
        String pathLogcat = getPathLogcat();
        return pathLogcat + "/log-"
                + getFileName() + ".log";
        //File file = new File(dir, "log-"
        //        + getFileName() + ".log");
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;
        Activity activity;

        public LogDumper(String pid, String dir)   {
            mPID = pid;
            try {
                File file = new File(dir, "log-"
                        + getFileName() + ".log");
                File fileDir = new File(dir);
                if (!fileDir.exists()) {
                    boolean mkdirs = file.mkdirs();
                    Log.i("mkdirs", "LogDumper: " + mkdirs);
                }
                if (!file.exists()) {
                    //ActivityCompat.requestPermissions(activity, new String[]{android
                    //        .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    try{
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    //try(FileWriter fileWriter=new FileWriter(file)){
                    //    fileWriter.
                    //}
                    //boolean mkdirs = file.mkdirs();
                    //file.mkdirs false
                    //创建不了啊
                    Log.i("file.exists()", "LogDumper: file " + file.exists());
                    //Log.i("mkdirs", "LogDumper: "+mkdirs);
                }
                //out = new FileOutputStream(new File(dir, "log-"
                //        + getFileName() + ".log"));
                if (file.isDirectory()) {
                    boolean delete = file.delete();
                    Log.i("delete", "LogDumper: "+delete);
                    try{
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    Log.i("file", "LogDumper: "+file);
                    Log.i("file.exists()", "LogDumper: "+file.exists());

                }
                //FileOutputStream append 模式
                //out = new FileOutputStream(file);
                out = new FileOutputStream(file,true);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }

    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;// 2012年10月03日 23:41:31
    }

//        public  String getDateEN() {
//            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String date1 = format1.format(new Date(System.currentTimeMillis()));
//            return date1;// 2012-10-03 23:41:31
//        }
}