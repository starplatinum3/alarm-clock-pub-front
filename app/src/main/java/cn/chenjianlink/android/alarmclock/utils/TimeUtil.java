package cn.chenjianlink.android.alarmclock.utils;

import android.icu.util.Calendar;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author chenjian
 * 时间处理工具类
 */
public class TimeUtil {

    public static Timestamp nowSqlTime() {
        return new Timestamp(System.currentTimeMillis());
    }

//    public static Timestamp strToSqlTime(String strTime,String formatStr) {
//        Date date = DateUtil.stringtoDate(strTime, formatStr);
//        return new Timestamp(date.getTime());
//    }


    /*
     * 将时间戳转换为时间
     * https://www.cnblogs.com/qiantao/p/10218921.html
     */
    public static String stampToDate(String timeStampStr) {

        long timeStamp = new Long(timeStampStr);
        return stampToDate(timeStamp);

    }

    void stoD() {

    }

    static void test() throws InterruptedException, ParseException {

//        long l = sqlStampToMillionSeconds(nowSqlTime());
//        System.out.println("l");
//        System.out.println(l);
//
//        String timeStr = toTimeStr(new Date(), "yyyy-MM-dd");
//        System.out.println("timeStr");
//        System.out.println(timeStr);

//        List<ComRedPageTime> list = new ArrayList<>();
//        ComRedPageTime comRedPageTime = new ComRedPageTime(nowSqlTime(), "11");
//
//        list.add(comRedPageTime);
//
//        Thread.sleep(1000);
//        list.add(new ComRedPageTime(nowSqlTime(), "22"));
//
////        list.sort();
//        list.sort(Comparator.comparing(ComRedPageTime::getUpdateTime, Comparator.reverseOrder()));
//        System.out.println(list);

        Timestamp sendDate = TimeUtil.nowSqlTime();
        System.out.println("sendDate");
        System.out.println(sendDate);

//        sqlStampToTimeStr()
//        toTimeStr()

//        String format = new SimpleDateFormat("yyyy-MM-dd").format(sendDate);
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sendDate);
        System.out.println("format");
        System.out.println(format);
        Date date = TimeUtil.sqlStampToDate(sendDate);
//        log.info("date");
//        log.info(String.valueOf(date));
        String timeStr = TimeUtil.toTimeStr(date, "yyyy-MM-dd");
        System.out.println("timeStr");
        System.out.println(timeStr);
    }

    public static void main(String[] args) throws ParseException, InterruptedException {
        Date date = sqlStampToDate(nowSqlTime());
        Date date2 = sqlStampToDate(nowSqlTime());
        System.out.println("date");
        System.out.println(date);
        System.out.println(date2.getDay()-date.getDay());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(date);
//        date getday 弃用

    }

    public static long sqlStampToMillionSeconds(Timestamp timestamp) throws ParseException {
        //获取当前时间
//        Timestamp t = new Timestamp(new Date().getTime());
//        System.out.println("当前时间："+t);
        //定义时间格式
        Date date = sqlStampToDate(nowSqlTime());

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
//        String str = dateFormat.format(timestamp);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
//        //此处转换为毫秒数
//        Date parse = sdf.parse(str);
//        System.out.println("parse");
//        System.out.println(parse);
        long millionSeconds = date.getTime();
//        long millionSeconds = sdf.parse(str).getTime();// 毫秒
//        System.out.println("毫秒数：" + millionSeconds);
        return millionSeconds;
    }

    /**
     * 不能用 .. date 会多几天
     * 改了 可以使用了
     * 来自 https://blog.csdn.net/gkx_csdn/article/details/88421994
     *
     * @param timestamp
     * @return
     * @throws
     */
    public static Date sqlStampToDate(Timestamp timestamp)  {
        //获取当前时间
//        Timestamp t = new Timestamp(new Date().getTime());
//        System.out.println("当前时间："+t);
        //定义时间格式
        return new Date(timestamp.getTime());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
//        String str = dateFormat.format(timestamp);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
//        //此处转换为毫秒数
//        Date parse = sdf.parse(str);
////        System.out.println("parse");
////        System.out.println(parse);
//
//        return parse;
//        long millionSeconds = sdf.parse(str).getTime();// 毫秒
//        System.out.println("毫秒数：" + millionSeconds);
//        return millionSeconds;
    }

    public static String sqlStampToTimeStr(Timestamp timestamp, String timePattern) throws ParseException {
        String formatTime = new SimpleDateFormat(timePattern).format(timestamp);
        return formatTime;

    }

//    public static Date longToDate(long timeStamp) {
//        String res;
////        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////        long lt = new Long(s);
//        Date date = new Date(timeStamp);
////        res = simpleDateFormat.format(date);
//        return date;
//    }



    public static String stampToDate(long timeStamp) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long lt = new Long(s);
        Date date = new Date(timeStamp);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    public static long getUnixTimestamp() {

        return System.currentTimeMillis() / 1000;
    }

    public static long getUnixTimestamp(String timeStr, String timePattern) throws ParseException {
//        "2020-03-25 15:01:17"
//        "yyyy-MM-dd HH:mm:ss"
        Date date = new SimpleDateFormat(timePattern).parse(timeStr);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattern);
//        simpleDateFormat.format()
        //        System.out.println(timestamp);
        return date.getTime() / 1000;
    }

    public static String toTimeStr(Date date, String timePattern) {
//        "2020-03-25 15:01:17"
//        "yyyy-MM-dd HH:mm:ss"
        //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattern);
//        simpleDateFormat.format()
        //        System.out.println(timestamp);

        return new SimpleDateFormat(timePattern).format(date);
    }


    /**
     * 将给定的时间按标准格式输出
     *
     * @param hour   小时
     * @param minute 分钟
     * @return 标准时间格式显示的字符串
     */
    public static String getShowTime(int hour, int minute) {
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }

    /**
     * 只响一次的 时间间隔
     *
     * @param hour   自定义的小时
     * @param minute 自定义的分钟
     * @return 返回"当前时间"距"给定时间"的时间差 	   如果"给定时间"<"当前时间"，则返回的是下一天的"给定时间"
     */
    public static int nextTime(int hour, int minute) {
        long currentTimeInMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long givenTimeInMillis = calendar.getTimeInMillis();
        int result;
        if (currentTimeInMillis < givenTimeInMillis) {
            result = (int) (givenTimeInMillis - currentTimeInMillis);
        } else {
            result = (int) (24 * 60 * 60 * 1000 - (currentTimeInMillis - givenTimeInMillis));
        }
        return result;
    }

    /**
     * 当前时间 与 自定义周期 的 时间间隔（只能指定一天）
     *
     * @param hour   自定义的 小时
     * @param minute 自定义的 分钟
     * @param day    自定义的 星期几
     * @return 返回"当前时间"距"给定时间"的时间差 	   如果"给定时间"<"当前时间"，则返回的是下一周的"给定时间"
     */
    public static int nextTime(int hour, int minute, int day) {
        long currentTimeInMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long givenTimeInMillis = calendar.getTimeInMillis();
        int result;
        if (currentTimeInMillis < givenTimeInMillis) {
            result = (int) (givenTimeInMillis - currentTimeInMillis);
        } else {
            result = (int) (7 * 24 * 60 * 60 * 1000 - (currentTimeInMillis - givenTimeInMillis));
        }
        return result;
    }

    /**
     * 当前时间 与 自定义周期 的 最近的时间间隔 （可指定多个天数）
     *
     * @param hour   自定义的 小时
     * @param minute 自定义的 分钟
     * @param days   自定义的 星期几（可变数组 比如 周一，周三，周日等）
     * @return 返回"当前时间"距"最近星期时间"的时间差
     * 比如：今天周四，自定义重复时间是 周三，周五，那么返回的是 此刻距离周五的时间间隔
     * 再比如：今天周四10:00AM，自定义重复时间是 周四 和 周五 的 9:00AM，那么返回的依然是 此刻距离周五的时间间隔
     */
    public static int nextTime(int hour, int minute, List<Integer> days) {
        List<Integer> nextTimes = new ArrayList<>();
        for (int day : days) {
            int nextTime;
            if (day == Calendar.MONDAY) {
                nextTime = nextTime(hour, minute, Calendar.MONDAY);
            } else if (day == Calendar.TUESDAY) {
                nextTime = nextTime(hour, minute, Calendar.TUESDAY);
            } else if (day == Calendar.WEDNESDAY) {
                nextTime = nextTime(hour, minute, Calendar.WEDNESDAY);
            } else if (day == Calendar.THURSDAY) {
                nextTime = nextTime(hour, minute, Calendar.THURSDAY);
            } else if (day == Calendar.FRIDAY) {
                nextTime = nextTime(hour, minute, Calendar.FRIDAY);
            } else if (day == Calendar.SATURDAY) {
                nextTime = nextTime(hour, minute, Calendar.SATURDAY);
            } else {
                nextTime = nextTime(hour, minute, Calendar.SUNDAY);
            }
            nextTimes.add(nextTime);
        }
        return Collections.min(nextTimes);
    }

    /**
     * 将毫秒转化成"X天X小时X分钟"的格式
     *
     * @param result 要转化的毫秒数
     * @return 对应的格式化时间
     */
    public static String dateFormat(int result) {
        StringBuilder builder = new StringBuilder();
        int days = result / (1000 * 60 * 60 * 24);
        LogUtil.i("days", "" + days);
        if (days > 0) {
            builder.append(days).append("天");
        }
        int hours = (result % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        int minutes = (result % (1000 * 60 * 60)) / (1000 * 60);
        if (hours == 0 && minutes == 0) {
            builder.append("响铃时间不足1分钟");
        } else {
            builder.append(hours).append("小时").append(minutes).append("分钟之后响铃");
        }
        return builder.toString();
    }

    /**
     * 根据重复周期列表返回重复周期字符串
     *
     * @param days 重复周期列表
     * @return 重复周期字符串
     */
    public static String repeatMessage(List<Integer> days) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            switch (days.get(i)) {
                case Calendar.SUNDAY:
                    builder.append("Sun");
                    break;
                case Calendar.MONDAY:
                    builder.append("Mon");
                    break;
                case Calendar.TUESDAY:
                    builder.append("Tue");
                    break;
                case Calendar.WEDNESDAY:
                    builder.append("Wed");
                    break;
                case Calendar.THURSDAY:
                    builder.append("Thu");
                    break;
                case Calendar.FRIDAY:
                    builder.append("Fri");
                    break;
                default:
                    builder.append("Sat");
            }
            if (i < days.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    /**
     * 将循环周期和小时与分钟转成下一次响铃时间的时间差
     *
     * @param ringCycle 响铃周期
     * @param hour      小时
     * @param minute    分钟
     * @return 响铃时间差
     */
    public static int nextRingTime(int ringCycle, int hour, int minute) {
        if (ringCycle == 0) {
            return nextTime(hour, minute);
        } else {
            List<Integer> ringCycleList = buildList(ringCycle);
            return nextTime(hour, minute, ringCycleList);
        }
    }

    /**
     * 通过循环周期构建循环列表
     *
     * @param ringCycle 循环周期
     * @return 循环列表
     */
    public static List<Integer> buildList(int ringCycle) {
        List<Integer> ringCycleList = new ArrayList<>();
        if ((ringCycle & CommonValue.SUNDAY) != 0) {
            ringCycleList.add(1);
        }
        if ((ringCycle & CommonValue.MONDAY) != 0) {
            ringCycleList.add(2);
        }
        if ((ringCycle & CommonValue.TUESDAY) != 0) {
            ringCycleList.add(3);
        }
        if ((ringCycle & CommonValue.WEDNESDAY) != 0) {
            ringCycleList.add(4);
        }
        if ((ringCycle & CommonValue.THURSDAY) != 0) {
            ringCycleList.add(5);
        }
        if ((ringCycle & CommonValue.FRIDAY) != 0) {
            ringCycleList.add(6);
        }
        if ((ringCycle & CommonValue.SATURDAY) != 0) {
            ringCycleList.add(7);
        }
        return ringCycleList;
    }

    public static String showRingCycle(int ringCycle, int hour, int minute) {
        List<Integer> ringCycleList = buildList(ringCycle);
        return repeatMessage(ringCycleList);
    }
}
