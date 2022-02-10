package com.starp.roomUtil;
//package com.example.whatrubbish.db;
//package en.edu.zucc.pb.loginapplication.util;

import androidx.sqlite.db.SimpleSQLiteQuery;

//import com.example.moments.dao.BaseDao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import kotlinx.coroutines.flow.Flow;

//import en.edu.zucc.pb.loginapplication.AppDatabase;

//extends SQLiteOpenHelper
public class RoomUtil {

    public static String q(Object object) {
        return " '" + object + "' ";
    }

    enum ColumnType{
        UNDERSCORE,CAMEL,PASCAL,NO_CHANGE
//        underscore,camel
    }

    /**
     * 根据实体名字
     *
     * @param object
     * @param sql
     * @return
     * @throws IllegalAccessException
     */
    public static StringBuilder where(Object object, StringBuilder sql) throws
            IllegalAccessException {


        Field[] declaredFields = object.getClass().getDeclaredFields();

        ColumnType columnType= ColumnType.NO_CHANGE;
        int index = 1;
        boolean first = true;
        for (Field field : declaredFields) {
            field.setAccessible(true);

            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)) {

                continue;
            }


            Object val = field.get(object);
            if (val == null) {
                continue;
            }
//            ""的字符串 也不搜索嘛
            if (val instanceof String) {
                if (val.equals("")) {
                    continue;
                }
            }


            String name = field.getName();
            String columnName=name;
            if(columnType== ColumnType.UNDERSCORE){
                columnName= underscoreNameLower(name);
            }else if(columnType== ColumnType.NO_CHANGE){
                columnName=name;
            }

            if (first) {
                sql.append(" where ").append(columnName).append(" = ").append(q(val));
                first = false;
            } else {

                sql.append(" and ").append(columnName).append(" = ").append(q(val));
            }

            index++;

        }

        return sql;
    }

    /**
     * 数据库 是可以大写的
     * https://blog.csdn.net/xianrenmodel/article/details/110792291
     * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。
     * 例如：HelloWorld->HELLO_WORLD
     *
     * @param name 转换前的驼峰式命名的字符串
     * @return 转换后下划线大写方式命名的字符串
     */
    public static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toUpperCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成大写
                result.append(s.toUpperCase());
            }
        }
        return result.toString();
    }

    /**
     * 数据库 是可以大写的
     * https://blog.csdn.net/xianrenmodel/article/details/110792291
     * 将驼峰式命名的字符串转换为下划线小写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。
     * 例如：HelloWorld->hello_world
     *
     * @param name 转换前的驼峰式命名的字符串
     * @return 转换后下划线大写方式命名的字符串
     */
    public static String underscoreNameLower(String name) {
        String underscoreName = underscoreName(name);
        return underscoreName.toLowerCase();

    }


    public static StringBuilder whereLike(Object object, StringBuilder sql) throws
            IllegalAccessException {
//        TblOrder tblOrder = new TblOrder();
//        tblOrder.setCarId(1);
        Field[] declaredFields = object.getClass().getDeclaredFields();


        int index = 1;
        boolean first = true;
        for (Field field : declaredFields) {
            field.setAccessible(true);

//            System.out.println("获取到字段：" + field.getType().getCanonicalName()
//                    + ",值：" + field.get(object));
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)) {
//                if (!getStatic) {
//                    continue;
//                }
                continue;
            }

            Object val = field.get(object);
            if (val == null) {
                continue;
            }
//            ""的字符串 也不搜索嘛
            if (val instanceof String) {
                if (val.equals("")) {
                    continue;
                }
            }


//            String canonicalName = field.getType().getCanonicalName();
//            String packaging = BeanUtil.primaryToPackaging(canonicalName);

            String name = field.getName();
//            orderId== name
//            String underscoreName = StringUtils.underscoreName(name);
            String underscoreName = underscoreNameLower(name);
//            String underscoreName = StringUtils.underscoreNameLower(name);
//            underscoreName  order_id
            if (first) {
                sql.append(" where ").append(underscoreName).append(" like ").append(l(val));
//                sql += " where "+underscoreName+" = " + q(val);
                first = false;
            } else {
//                sql += " and "+underscoreName+" = " + q(val);
                sql.append(" and ").append(underscoreName).append(" like ").append(l(val));
//                sql += "and car_status = " + q(carStatus);
            }

            index++;

        }

        return sql;
    }

    static String l(Object o) {
        return " '%" + o + "%' ";
//        new FlowLayout(FlowLayout.CENTER)
    }




    public static <T> List<T> select(BaseDao<T> dao, T obj) throws IllegalAccessException {
        String sql = "select * from  " + getTableName(obj) + "   ";
        List<T> select = select(dao, sql, obj);
        return select;
    }




    //    https://www.cnblogs.com/Marydon20170307/p/14149970.html

    public static <T> List<T> select(BaseDao<T> dao, String sql, T obj) throws  IllegalAccessException {

//        如果 addsql 是null 那就不会去添加 addsql
        return select(dao, sql, obj, null,null);
    }




    public static <T> String getTableName(T obj) {
        String tableName = obj.getClass().getSimpleName();
//        tableName = underscoreNameLower(tableName);
//        这里貌似不用 下划线了 而且是 Entity
//        tableName = StringUtils.underscoreNameLower(tableName);
        return tableName;
    }


    public static <T> String getSelectSqlStr(String sql, T obj,
                                             T likeObj, String addSql) throws IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder(sql);
        StringBuilder sqlNow = where(obj, stringBuilder);
        if (likeObj != null) {
            whereLike(likeObj, stringBuilder);
        }

        if (addSql != null) {
            sqlNow.append("   ").append(addSql);

            int whereStart = sqlNow.indexOf(" where ");
            if (whereStart == -1) {

                int andStart = sqlNow.indexOf(" and ");
                if (andStart != -1) {
                    sqlNow.replace(andStart, andStart + 5, " where ");
                }
//                如果没有 and ，就不用替换了  ， 也就是 没有and 也没有where

            }
            //            有where  就不管

        }

        return String.valueOf(sqlNow);
    }

    /**
     * 比如数据库有 stu(name=starp,age=18)， stu(name=starp,age=19)如果我new stu(name=starp,age=18) 当作obj参数
     * 我就可以搜索 到 stu(name=starp,age=18)， 也就是我的sql是 sel * where name =starp and age =18
     * likeObj 模糊搜索的 obj ，一般搜索字符串字段，也就是 sel * where name like :name and age like :age
     * addSql 后面还要添加的sql语句 ，比如limit ， order之类的 ，比如写 limit 5 ，就可以搜索出前面5条记录
     * 拼出了 这样的sql字符串 sel * where name like :name and age like :age limit 5
     * 为什么会有 addSql 这样的参数 主要是我后面的东西还没有封装，order这些，为了让他有可拓展性，于是就加入这个参数
     * 虽然他可能会造成调bug困难，不过只要打印一下sql，其实应该还是可行的
     *
     * @param dao roomDao
     * @param obj  搜索的obj，数据库里的对应字段和他相等的话 搜索出来，比如 stu(name=starp,age=18)
     * @param likeObj 模糊搜索的 obj，一般搜索字符串字段，
     * @param addSql 后面还要添加的sql语句 ，比如limit ， order之类的
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> List<T> select(BaseDao<T> dao, T obj,
                                     T likeObj, String addSql) throws IllegalAccessException {
        String sql = "select * from  " + getTableName(obj) + "   ";
      return   select(dao,sql,obj,likeObj,addSql);

    }



    /**
     * 如果没有输入 likeObj 那么我就不会搜索 like ，
     * 没有传入的参数 就是不去使用他
     * @param dao
     * @param sql
     * @param obj  是用来相等的 参数
     * @param likeObj
     * @param addSql 后面加上的 后缀的sql 比如 limit 1，或者 and time > 2020
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> List<T> select(BaseDao<T> dao, String sql, T obj,
                                     T likeObj, String addSql) throws IllegalAccessException {

        String selectSqlStr = getSelectSqlStr(sql, obj, likeObj, addSql);
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(selectSqlStr);
        List<T> viaQuery2 = dao.getViaQuery(query);
        return viaQuery2;
    }


    //public static <T>  Flow<List<T>> selectFlow(BaseDao<T> dao, String sql, T obj,
    //                                 T likeObj, String addSql) throws IllegalAccessException {
    //
    //    String selectSqlStr = getSelectSqlStr(sql, obj, likeObj, addSql);
    //    SimpleSQLiteQuery query = new SimpleSQLiteQuery(selectSqlStr);
    //    //Flow<List<T>> viaQuery2 = dao.getViaQuery(query);
    //    Flow<List<T>> viaQuery2 = dao.getViaQueryToFlow(query);
    //    return viaQuery2;
    //}

    //public static <T> Flow<List<T>> selectFlow(BaseDao<T> dao, T obj,
    //                                           T likeObj, String addSql) throws IllegalAccessException {
    //    String sql = "select * from  " + getTableName(obj) + "   ";
    //    return   selectFlow(dao,sql,obj,likeObj,addSql);
    //
    //}

//    public static <T> Flow<List<T>> selectFlow(BaseDao<T> dao, String sql, T obj) throws  IllegalAccessException {
//
////        如果 addsql 是null 那就不会去添加 addsql
//        return selectFlow(dao, sql, obj, null,null);
//    }

    //public static <T> Flow<List<T>> selectFlow(BaseDao<T> dao, T obj) throws IllegalAccessException {
    //    String sql = "select * from  " + getTableName(obj) + "   ";
    //    //List<T> select = select(dao, sql, obj);
    //    return    selectFlow(dao, sql, obj);
    //    //return select;
    //}

}
