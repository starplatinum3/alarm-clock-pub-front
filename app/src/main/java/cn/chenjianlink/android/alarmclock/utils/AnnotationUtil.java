package cn.chenjianlink.android.alarmclock.utils;

import android.util.Log;

import androidx.room.Database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import cn.chenjianlink.android.alarmclock.db.AppDatabase;
import cn.chenjianlink.android.alarmclock.db.BaseAlarmClockDatabase;

public class AnnotationUtil {

     public  static  void test() throws NoSuchFieldException, IllegalAccessException {
        //Class<?>cls = AppDatabase.class;
        Class<?>cls = BaseAlarmClockDatabase.class;
         //AppDatabase.getDatabase()
        Database annotation = cls.getAnnotation(Database.class);
        Map<String, Object> memberValues = getMemberValues(annotation);
        if(memberValues==null){
            Log.i("空的", "test: 空的");
            return;
        }
        Class<?>[] entities =(Class<?>[]) memberValues.get("entities");
        Log.i("entities", "main: "+entities);
    }
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
      Class<?>cls = AppDatabase.class;
        Database annotation = cls.getAnnotation(Database.class);
        Map<String, Object> memberValues = getMemberValues(annotation);
        Class<?>[] entities =(Class<?>[]) memberValues.get("entities");
        Log.i("entities", "main: "+entities);
    }

    //https://www.cnblogs.com/wangnanhui/p/10334027.html
  public  static    Map<String, Object> getMemberValues(Annotation annotation)
          throws NoSuchFieldException, IllegalAccessException {
        //Annotation
      if(annotation==null){
          //没有数据
          Log.i("注解是空的", "getMemberValues: 注解是空的");
          return null;
      }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
        return memberValues;
    }
    //修改注解内部值 @Cacheable(keyMode = KeyMode.ALL)
    //public Object cached(final ProceedingJoinPoint pjp) throws Throwable {
    //    Class<?> cls = CacheProxyBiz.class;
    //    //参数 ProceedingJoinPoint.class 是 "cached" 这个函数的第一个参数
    //    Method method = cls.getMethod("cached", ProceedingJoinPoint.class);
    //    Cacheable cacheable = method.getAnnotation(Cacheable.class);//获取注解
    //    System.out.println("修改前.....");
    //    System.out.println("模式" + cacheable.keyMode() + "\t 时长" + cacheable.expire());
    //    InvocationHandler invocationHandler = Proxy.getInvocationHandler(cacheable);
    //    Field value = invocationHandler.getClass().getDeclaredField("memberValues");
    //    value.setAccessible(true);
    //    Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
    //    memberValues.put("expire", 50);
    //    System.out.println("修改后.....");
    //    System.out.println("模式" + cacheable.keyMode() + "\t 时长" + cacheable.expire());
    //    return "";
    //
    //}
}
