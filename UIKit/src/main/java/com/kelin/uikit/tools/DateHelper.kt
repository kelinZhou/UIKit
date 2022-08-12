package com.kelin.uikit.tools

import java.text.SimpleDateFormat
import java.util.*

/**
 * **描述:** 日期格式化工具。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-19 17:37
 *
 * **版本:** v 1.0.0
 */
object DateHelper {

    const val ONE_DAY = 24 * 60 * 60 * 1000

    /**
     * yyyy-MM-dd
     */
    const val YYYY_MM_DD = "yyyy-MM-dd"

    /**
     * yyyy.MM.dd 星期X
     */
    const val YYYY_MM_DD_EEE = "yyyy-MM-dd EEEE"

    /**
     * yyyy-MM-dd HH:mm
     */
    const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"

    /**
     * yyyy年MM月dd HH:mm
     */
    const val YYYY_MM_DD_HH_MM1 = "yyyy年MM月dd HH:mm"

    /**
     * yyyy.MM.dd HH:mm
     */
    const val YYYY_MM_DD_HH_MM2 = "yyyy.MM.dd HH:mm"

    /**
     * yyyy/MM/dd HH:mm
     */
    const val YYYY_MM_DD_HH_MM3 = "yyyy/MM/dd HH:mm"

    /**
     * yyyy-MM-dd HH时
     */
    const val YYYY_MM_DD_HH = "yyyy-MM-dd HH时"

    /**
     * yyyy-MM-dd HH:00:00
     */
    const val YYYY_MM_DD_HH_00_00 = "yyyy-MM-dd HH:00:00"

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

    /**
     * yyyy.MM.dd HH:mm:ss
     */
    const val YYYY_MM_DD_HH_MM_SS2 = "yyyy.MM.dd HH:mm:ss"

    /**
     * yyyy-MM-dd HH:mm:00
     */
    const val YYYY_MM_DD_HH_MM_00 = "yyyy-MM-dd HH:mm:00"

    /**
     * MM-dd HH:mm
     */
    const val MM_DD_HH_MM = "MM-dd HH:mm"

    /**
     * MM-dd HH:mm:ss
     */
    const val MM_DD_HH_MM_SS = "MM-dd HH:mm:ss"

    /**
     * HH:mm
     */
    const val HH_MM = "HH:mm"

    /**
     * MM-dd HH
     */
    const val MM_DD_HH = "MM-dd HH"

    fun formatData(format: String, date: Long): String? {
        return if (date < 1) {
            null
        } else {
            formatData(format, Date(date))
        }
    }

    @SuppressWarnings("SimpleDateFormat")
    fun formatData(format: String, date: Date?): String? {
        return if (date != null) {
            val f = SimpleDateFormat(format)
            f.timeZone = TimeZone.getTimeZone("GMT+8")
            f.format(date)
        } else {
            null
        }
    }

    fun formatDayOffsetCommon(time: Long, defaultValue: String? = "刚刚"): String? {
        if (time == 0L) {
            return ""
        }
        val now = Calendar.getInstance(Locale.CHINA)
        val cur = Calendar.getInstance(Locale.CHINA)
        cur.time = Date(time)
        val nowDayOfYear = now[Calendar.DAY_OF_YEAR]
        val curDayOfYear = cur[Calendar.DAY_OF_YEAR]
        return if (now[Calendar.YEAR] == cur[Calendar.YEAR]) {
            if (nowDayOfYear == curDayOfYear) {
                if (now.timeInMillis > cur.timeInMillis) { //今天的过去时间
                    //计算出现在距参数时间的分钟差。
                    val interMinute = (now[Calendar.HOUR_OF_DAY] * 60 + now[Calendar.MINUTE] /*现在时间的 MinuteOfDay*/
                            - (cur[Calendar.HOUR_OF_DAY] * 60 + cur[Calendar.MINUTE])).toLong() /*参数时间的 MinuteOfDay*/
                    when {
                        interMinute == 0L -> {
                            defaultValue
                        }
                        interMinute < 60 -> {
                            "${interMinute}分钟前"
                        }
                        else -> {
                            "${interMinute / 60}小时前"
                        }
                    }
                } else { // 今天的未来时间
                    "今天 " + formatData(HH_MM, cur.time)
                }
            } else if (nowDayOfYear > curDayOfYear) { //过去时间
                val offsetDay = nowDayOfYear - curDayOfYear
                if (offsetDay == 1) "昨天" else if (offsetDay <= 6) offsetDay.toString() + "天前" else formatData(MM_DD_HH_MM, cur.time)
            } else {  //未来时间
                val offsetDay = curDayOfYear - nowDayOfYear
                if (offsetDay == 1) "明天 " + formatData(HH_MM, cur.time) else formatData(MM_DD_HH_MM, cur.time)
            }
        } else {
            formatData(YYYY_MM_DD_HH_MM_SS, cur.time)
        }
    }

    /**
     * 获取后今天距离一个日期相差几天，并不是计算绝对的自然日，例如，2022年7月8 00:00:01 距离 2022年7月7 23:59:59 相差1天。
     * @return 返回今天距离一个日期相差的天数，如今天早于这个日期，这返回负的天数。
     */
    fun getDayOffset(target: Long): Int {
        val targetDay = Calendar.getInstance(Locale.getDefault()).apply { timeInMillis = target }
        val today = Calendar.getInstance(Locale.getDefault())
        val lastDayOfYear = targetDay.get(Calendar.DAY_OF_YEAR)
        val dayOfYear = today.get(Calendar.DAY_OF_YEAR)
        val lastYear = targetDay.get(Calendar.YEAR)
        val year = today.get(Calendar.YEAR)
        return when {
            lastYear == year -> {
                dayOfYear - lastDayOfYear
            }
            year > lastYear -> {
                if (lastYear % 4 == 0) {
                    366
                } else {
                    365
                } - lastDayOfYear + dayOfYear
            }
            else -> {
                dayOfYear - if (year % 4 == 0) {
                    366
                } else {
                    365
                } - lastDayOfYear
            }
        }
    }

}