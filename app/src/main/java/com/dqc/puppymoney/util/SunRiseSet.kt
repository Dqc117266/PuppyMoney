package com.dqc.puppymoney.util

import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SunRiseSet {
    private val days_of_month_1 = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    private val days_of_month_2 = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    private val h = -0.833 //日出日落时太阳的位置


    private val UTo = 180.0 //上次计算的日落日出时间，初始迭代值180.0


    // 1、判断是否为闰年：若为闰年，返回1；若不是闰年,返回0
    fun leap_year(year: Int): Boolean {
        return if (year % 400 == 0 || year % 100 != 0 && year % 4 == 0) true else false
    }

    // 1、求从格林威治时间公元2000年1月1日到计算日天数days
    fun days(year: Int, month: Int, date: Int): Int {
        var i: Int
        var a = 0
        i = 2000
        while (i < year) {
            a = if (leap_year(i)) a + 366 else a + 365
            i++
        }
        if (leap_year(year)) {
            i = 0
            while (i < month - 1) {
                a = a + days_of_month_2[i]
                i++
            }
        } else {
            i = 0
            while (i < month - 1) {
                a = a + days_of_month_1[i]
                i++
            }
        }
        a = a + date
        return a
    }

    //求格林威治时间公元2000年1月1日到计算日的世纪数t
    fun t_century(days: Int, UTo: Double): Double {
        return (days.toDouble() + UTo / 360) / 36525
    }

    //求太阳的平黄径
    fun L_sun(t_century: Double): Double {
        return 280.460 + 36000.770 * t_century
    }

    //求太阳的平近点角
    fun G_sun(t_century: Double): Double {
        return 357.528 + 35999.050 * t_century
    }

    //求黄道经度
    fun ecliptic_longitude(L_sun: Double, G_sun: Double): Double {
        return L_sun + 1.915 * Math.sin(G_sun * Math.PI / 180) + 0.02 * Math.sin(2 * G_sun * Math.PI / 180)
    }

    //求地球倾角
    fun earth_tilt(t_century: Double): Double {
        return 23.4393 - 0.0130 * t_century
    }

    //求太阳偏差
    fun sun_deviation(earth_tilt: Double, ecliptic_longitude: Double): Double {
        return 180 / Math.PI * Math.asin(Math.sin(Math.PI / 180 * earth_tilt) * Math.sin(Math.PI / 180 * ecliptic_longitude))
    }

    //求格林威治时间的太阳时间角GHA
    fun GHA(UTo: Double, G_sun: Double, ecliptic_longitude: Double): Double {
        return UTo - 180 - 1.915 * Math.sin(G_sun * Math.PI / 180) - 0.02 * Math.sin(2 * G_sun * Math.PI / 180) + 2.466 * Math.sin(2 * ecliptic_longitude * Math.PI / 180) - 0.053 * Math.sin(4 * ecliptic_longitude * Math.PI / 180)
    }

    //求修正值e
    fun e(h: Double, glat: Double, sun_deviation: Double): Double {
        return 180 / Math.PI * Math.acos((Math.sin(h * Math.PI / 180) - Math.sin(glat * Math.PI / 180) * Math.sin(sun_deviation * Math.PI / 180)) / (Math.cos(glat * Math.PI / 180) * Math.cos(sun_deviation * Math.PI / 180)))
    }

    //求日出时间
    fun UT_rise(UTo: Double, GHA: Double, glong: Double, e: Double): Double {
        return UTo - (GHA + glong + e)
    }

    //求日落时间
    fun UT_set(UTo: Double, GHA: Double, glong: Double, e: Double): Double {
        return UTo - (GHA + glong - e)
    }

    //判断并返回结果（日出）
    fun result_rise(UT: Double, UTo: Double, glong: Double, glat: Double, year: Int, month: Int, date: Int): Double {
        var UT = UT
        var UTo = UTo
        val d: Double
        d = if (UT >= UTo) UT - UTo else UTo - UT
        if (d >= 0.1) {
            UTo = UT
            UT = UT_rise(UTo,
                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))),
                    glong,
                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo))))))
            result_rise(UT, UTo, glong, glat, year, month, date)
        }
        return UT
    }

    //判断并返回结果（日落）
    fun result_set(UT: Double, UTo: Double, glong: Double, glat: Double, year: Int, month: Int, date: Int): Double {
        var UT = UT
        var UTo = UTo
        val d: Double
        d = if (UT >= UTo) UT - UTo else UTo - UT
        if (d >= 0.1) {
            UTo = UT
            UT = UT_set(UTo,
                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))),
                    glong,
                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo))))))
            result_set(UT, UTo, glong, glat, year, month, date)
        }
        return UT
    }

    // 求时区
    fun Zone(glong: Double): Int {
        return if (glong >= 0) ((glong / 15.0).toInt() + 1) else ((glong / 15.0).toInt() - 1)
    }

    // 日出
    fun getSunrise(longitude: BigDecimal?, latitude: BigDecimal?, sunTime: Date?): String? {
        if (sunTime != null && longitude != null && latitude != null) {
            val sunrise: Double
            val glong: Double
            val glat: Double
            val year: Int
            val month: Int
            val date: Int
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val dateTime = sdf.format(sunTime)
            val rq = dateTime.split("-".toRegex()).toTypedArray()
            val y = rq[0]
            var m = rq[1]
            val d = rq[2]
            year = y.toInt()
            if (m != null && m !== "" && m.indexOf("0") == -1) {
                m = m.replace("0".toRegex(), "")
            }
            month = m.toInt()
            date = d.toInt()
            glong = longitude.toDouble()
            glat = latitude.toDouble()
            sunrise = result_rise(UT_rise(UTo,
                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))),
                    glong,
                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))))), UTo, glong, glat, year, month, date)
            return   ((sunrise / 15 + 8).toInt()).toString() + ":" + (60 * (sunrise / 15 + 8 - (sunrise / 15 + 8).toInt())).toInt()
        }
        return null
    }

    // 日落
    fun getSunset(longitude: BigDecimal?, latitude: BigDecimal?, sunTime: Date?): String? {
        if (sunTime != null && latitude != null && longitude != null) {
            val sunset: Double
            val glong: Double
            val glat: Double
            val year: Int
            val month: Int
            val date: Int
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val dateTime = sdf.format(sunTime)
            val rq = dateTime.split("-".toRegex()).toTypedArray()
            val y = rq[0]
            var m = rq[1]
            val d = rq[2]
            year = y.toInt()
            if (m != null && m !== "" && m.indexOf("0") == -1) {
                m = m.replace("0".toRegex(), "")
            }
            month = m.toInt()
            date = d.toInt()
            glong = longitude.toDouble()
            glat = latitude.toDouble()
            sunset = result_set(UT_set(UTo,
                    GHA(UTo, G_sun(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))),
                    glong,
                    e(h, glat, sun_deviation(earth_tilt(t_century(days(year, month, date), UTo)),
                            ecliptic_longitude(L_sun(t_century(days(year, month, date), UTo)),
                                    G_sun(t_century(days(year, month, date), UTo)))))), UTo, glong, glat, year, month, date)
            return ((sunset / 15 + 8).toInt()).toString() + ":" + (60 * (sunset / 15 + 8 - (sunset / 15 + 8).toInt())).toInt()
        }
        return null
    }


    /**
     * 将当前时间转换为16进制
     *
     * @return
     */
    fun getTimeTo16(time: String?): String? {
        var date: Date? = null
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            date = formatter.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        //Date格式
        val t = date!!.time / 1000
        val hexString = java.lang.Long.toHexString(t)
        println("十六进制：$hexString")
        return hexString
    }


    //将指定时间转换成 date 格式
    fun getTime(time: String?): Date? {
        var date: Date? = null
        val formatter = SimpleDateFormat("yyyy-MM-dd") //日期
        try {
            date = formatter.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }
}

fun main() {
    println(SunRiseSet().getSunrise(BigDecimal(118.78333), BigDecimal(32.05000), Date()))
    println(SunRiseSet().getSunset(BigDecimal(118.78333), BigDecimal(32.05000), Date()))
}