//package cn.chenjianlink.android.alarmclock.utils;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.Random;
//
//public class RedPackageUtil {
//    public static BigDecimal getRandomMoney(RedPackageDTO redPackage) {
//        int remainSize = redPackage.getRemainSize();
//        BigDecimal remainMoney = redPackage.getRemainMoney();
//        if (remainSize == 1) {
//            remainSize--;
//            redPackage.setRemainSize(remainSize);
//            redPackage.setRemainMoney(BigDecimal.ZERO);
//            return remainMoney;
//        }
//        Random r = new Random();
//        BigDecimal min = new BigDecimal(DEFAULT_MIN_VALUE);
//        BigDecimal max = remainMoney.divide(new BigDecimal(remainSize),
//                2, RoundingMode.HALF_UP).multiply(new BigDecimal(2));
//        BigDecimal money = max.multiply(BigDecimal.valueOf(r.nextDouble()));
//        if (money.compareTo(min) < 0)
//            remainSize--;
//        remainMoney = remainMoney.subtract(money);
//        redPackage.setRemainSize(remainSize);
//        redPackage.setRemainMoney(remainMoney);
//        return money;
//    }
//
//}
