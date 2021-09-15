package com.dqc.puppymoney.util;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * (1)先计算出从格林威治时间公元2000年1月1日到计算日天数days;
 * (2)计算从格林威治时间公元2000年1月1日到计算日的世纪数t, 则t＝(days+UTo／360)／36525;
 * (3)计算太阳的平黄径 ： L=280.460+36000.770×t;
 * (4)计算太阳的平近点角 ：G＝357.528+35999.050×t
 * (5)计算太阳的黄道经度 ：λ＝L+1.915×sinG+0.020xsin(2G);
 * (6)计算地球的倾角     ε＝23.4393-0.0130×t;
 * (7)计算太阳的偏差     δ＝arcsin(sinε×sinλ);
 * (8)计算格林威治时间的太阳时间角GHA： GHA=UTo-180-1.915×sinG-0.020×sin(2G)   +2.466×sin(2λ)-0.053×sin(4λ)
 * (9)计算修正值e： e=arcos{[   sinh-sin(Glat)sin(δ)]/cos(Glat)cos(δ)}
 * (10)计算新的日出日落时间 ：UT＝UTo-(GHA+Long±e); 其中“+”表示计算日出时间,“-”表示计算日落时间;
 * (11)比较UTo和UT之差的绝对值,如果大于0.1°即0.007小时,把UT作为新的日出日落时间值,重新从第(2)步开始进行迭代计算,如果UTo和UT之差的绝对值小于0.007小时,则UT即为所求的格林威治日出日落时间;
 * (12)上面的计算以度为单位,即180°=12小时,因此需要转化为以小时表示的时间,再加上所在的时区数Zone,即要计算地的日出日落时间为 ：T=UT/15+Zone
 * 上面的计算日出日落时间方法适用于小于北纬60°和南纬60°之间的区域,如果计算位置为西半球时,经度Long为负数。
 */
public class SunRiseSetJ {

    private static int[] days_of_month_1 = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static int[] days_of_month_2 = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private final static double h = -0.833;//日出日落时太阳的位置

    private final static double UTo = 180.0;//上次计算的日落日出时间，初始迭代值180.0



    // 1、判断是否为闰年：若为闰年，返回1；若不是闰年,返回0
    public static boolean leap_year(int year) {
        if (((year % 400 == 0) || (year % 100 != 0) && (year % 4 == 0))) return true;
        else return false;
    }

    // 1、求从格林威治时间公元2000年1月1日到计算日天数days
    public static int days(int year, int month, int date) {

        int i, a = 0;

        for (i = 2000; i < year; i++) {

            if (leap_year(i)) a = a + 366;

            else a = a + 365;

        }

        if (leap_year(year)) {

            for (i = 0; i < month - 1; i++) {

                a = a + days_of_month_2[i];

            }

        } else {

            for (i = 0; i < month - 1; i++) {

                a = a + days_of_month_1[i];

            }

        }

        a = a + date;

        return a;

    }

    //求格林威治时间公元2000年1月1日到计算日的世纪数t
    public static double t_century(int days, double UTo) {

        return ((double) days + UTo / 360) / 36525;

    }

    //求太阳的平黄径
    public static double L_sun(double t_century) {

        return (280.460 + 36000.770 * t_century);

    }

    //求太阳的平近点角
    public static double G_sun(double t_century) {

        return (357.528 + 35999.050 * t_century);

    }

    //求黄道经度
    public static double ecliptic_longitude(double L_sun, double G_sun) {

        return (L_sun + 1.915 * Math.sin(G_sun * Math.PI / 180) + 0.02 * Math.sin(2 * G_sun * Math.PI / 180));

    }

    //求地球倾角
    public static double earth_tilt(double t_century) {

        return (23.4393 - 0.0130 * t_century);

    }

    //求太阳偏差
    public static double sun_deviation(double earth_tilt, double ecliptic_longitude) {

        return (180 / Math.PI * Math.asin(Math.sin(Math.PI / 180 * earth_tilt) * Math.sin(Math.PI / 180 * ecliptic_longitude)));

    }

    //求格林威治时间的太阳时间角GHA
    public static double GHA(double UTo, double G_sun, double ecliptic_longitude) {

        return (UTo - 180 - 1.915 * Math.sin(G_sun * Math.PI / 180) - 0.02 * Math.sin(2 * G_sun * Math.PI / 180) + 2.466 * Math.sin(2 * ecliptic_longitude * Math.PI / 180) - 0.053 * Math.sin(4 * ecliptic_longitude * Math.PI / 180));

    }

    //求修正值e
    public static double e(double h, double glat, double sun_deviation) {

        return 180 / Math.PI * Math.acos((Math.sin(h * Math.PI / 180) - Math.sin(glat * Math.PI / 180) * Math.sin(sun_deviation * Math.PI / 180)) / (Math.cos(glat * Math.PI / 180) * Math.cos(sun_deviation * Math.PI / 180)));

    }

    //求日出时间
    public static double UT_rise(double UTo, double GHA, double glong, double e) {

        return (UTo - (GHA + glong + e));

    }

    //求日落时间
    public static double UT_set(double UTo, double GHA, double glong, double e) {

        return (UTo - (GHA + glong - e));

    }

    //判断并返回结果（日出）
    public static double result_rise(double UT, double UTo, double glong, double glat, int year, int month, int date) {
        double d;

        if (UT >= UTo) d = UT - UTo;

        else d = UTo - UT;

        if (d >= 0.1) {

            UTo = UT;

            UT = UT_rise(UTo,

                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo)))),

                    glong,

                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo))))));

            result_rise(UT, UTo, glong, glat, year, month, date);


        }

        return UT;

    }

    //判断并返回结果（日落）
    public static double result_set(double UT, double UTo, double glong, double glat, int year, int month, int date) {

        double d;

        if (UT >= UTo) d = UT - UTo;

        else d = UTo - UT;

        if (d >= 0.1) {

            UTo = UT;

            UT = UT_set(UTo,

                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo)))),

                    glong,

                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo))))));

            result_set(UT, UTo, glong, glat, year, month, date);

        }

        return UT;

    }

    // 求时区
    public static int Zone(double glong) {

        if (glong >= 0) return (int) ((int) (glong / 15.0) + 1);

        else return (int) ((int) (glong / 15.0) - 1);

    }

    // 日出
    public static String getSunrise(BigDecimal longitude, BigDecimal latitude, Date sunTime) {
        if (sunTime != null && longitude != null && latitude != null) {
            double sunrise, glong, glat;
            int year, month, date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateTime = sdf.format(sunTime);
            String[] rq = dateTime.split("-");
            String y = rq[0];
            String m = rq[1];
            String d = rq[2];
            year = Integer.parseInt(y);
            if (m != null && m != "" && m.indexOf("0") == -1) {
                m = m.replaceAll("0", "");
            }
            month = Integer.parseInt(m);

            date = Integer.parseInt(d);

            glong = longitude.doubleValue();

            glat = latitude.doubleValue();

            sunrise = result_rise(UT_rise(UTo,

                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo)))),

                    glong,

                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo)))))), UTo, glong, glat, year, month, date);

            return (int) (sunrise / 15 + 8) + ":" + (int) (60 * (sunrise / 15 + 8 - (int) (sunrise / 15 + 8)));
        }
        return null;
    }

    // 日落
    public static String getSunset(BigDecimal longitude, BigDecimal latitude, Date sunTime) {
        if (sunTime != null && latitude != null && longitude != null) {
            double sunset, glong, glat;
            int year, month, date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateTime = sdf.format(sunTime);
            String[] rq = dateTime.split("-");
            String y = rq[0];
            String m = rq[1];
            String d = rq[2];
            year = Integer.parseInt(y);
            if (m != null && m != "" && m.indexOf("0") == -1) {
                m = m.replaceAll("0", "");
            }
            month = Integer.parseInt(m);

            date = Integer.parseInt(d);

            glong = longitude.doubleValue();

            glat = latitude.doubleValue();

            sunset = result_set(UT_set(UTo,

                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo)))),

                    glong,

                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),

                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),

                                    G_sun(t_century(days(year, month, date), UTo)))))), UTo, glong, glat, year, month, date);


            return (int) (sunset / 15 + 8) + ":" + (int) (60 * (sunset / 15 + 8 - (int) (sunset / 15 + 8)));
        }
        return null;
    }


    /**
     * 将当前时间转换为16进制
     *
     * @return
     */
    public static String getTimeTo16(String time) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Date格式
        Long t = date.getTime() / 1000;
        String hexString = Long.toHexString(t);
        System.out.println("十六进制：" + hexString);

        return hexString;
    }


    //将指定时间转换成 date 格式
    public static Date getTime(String time) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");           //日期
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void main(String[] args) {
//        System.out.println(SunRiseSetJ.getSunrise(new BigDecimal(118.72),new BigDecimal(32.00500),new Date()));
//        System.out.println(SunRiseSetJ.getSunset(new BigDecimal(118.72),new BigDecimal(32.00500),new Date()));
//
//        System.out.println(SunRiseSetJ.getSunrise(new BigDecimal(116.39),new BigDecimal(39.9),new Date()));
//        System.out.println(SunRiseSetJ.getSunset(new BigDecimal(116.39),new BigDecimal(39.9),new Date()));

        Gson gson = new Gson();
        String str = "{\"iGOPChannel\":\"121212\",\"iGOPChannelType\":\"iGOPChannel\"}";
        A a = gson.fromJson(str, A.class);
        System.out.println(" iGOPChannel:" + a.iGOPChannel + " iGOPChannelType:" + a.iGOPChannelType);
    }
}