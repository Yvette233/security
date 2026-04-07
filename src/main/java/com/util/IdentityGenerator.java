package com.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

public class IdentityGenerator {

    public final static Log log = LogFactory.getLog(IdentityGenerator.class);

    /**
     * 顺序数，为了保证尽可能的不重复，取100000内的数字。顺序数和时间精度之间差3个0。 不能过大，否则会超过15位的数据库存储上限
     */
    private static final int numberMax = 10000;

    /**
     * 时间精度，单位是毫秒
     */
    private static final int precision = 10000;
    /**
     * 应用服务器编号，最多支持10台，可通过mm.properties文件配置 这个暂时还未处理好
     */
    private static final int applicatonNumber = 0;
    /**
     * 记录当前日期数值
     */
    private static long current = 0;

    /**
     * 当前的序号
     */
    private static long currentIndex = 0;

    /**
     * @return 返回long型的主键
     */
    public synchronized static long GetIdentityLong() {
        // if()
        // 精确到10毫秒，相对于2000-1-1的时间
        // 2000-1-1这个时间相对的微秒数为 949363200000=Date.UTC(100, 1, 1, 0, 0, 0)
        long id = (System.currentTimeMillis() - 949363200000L) / (precision);
        if (current < id) {
            current = id;
            currentIndex = 0;
        }

        long res = current * numberMax + currentIndex;
        currentIndex++;
        if (currentIndex > numberMax) {
            current++;
            currentIndex = 0;
        }
        return res;
    }

    /**
     * @return 返回字符型的主键
     */
    public synchronized static String GetIdentityString() {
        // return String.valueOf(GetIdentityLong());
        return UUID.randomUUID().toString();
    }

    /**
     * @return 返回字符型的UUID
     */
    public synchronized static String GetIdentityUUID() {
        return UUID.randomUUID().toString();
    }

}
