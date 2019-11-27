package cn.poile.blog.common.util;

import java.time.chrono.IsoChronology;

/**
 * @author: yaohw
 * @create: 2019-11-27 17:07
 **/
public class DateUtil {

    /**
     * 获取一个月最大天数
     * @param year
     * @param month
     * @return
     */
    public static int getMaxDayOfMonth(int year, int month) {
        int dom;
        switch (month) {
            case 2:
                dom = (IsoChronology.INSTANCE.isLeapYear(year) ? 29 : 28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                dom = 30;
                break;
            default:
                dom = 31;
        }
        return dom;
    }
}
