package com.sreesharp.sreechat.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;

import com.sreesharp.sreechat.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by purayil on 10/17/2015.
 */
public class Utility {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

    public static String formatDateTime(Context context, String timeToFormat) {
        String finalDateTime = "";
        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }
            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

    public static String getRelativeTimeAgo(String timeToFormat) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }
        }
        if(date == null)
            return "";
        return getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS).toString();

    }

    /**
     * Only handles past time in twitter format.
     * 2s
     * 2m
     * 2h
     * 3d
     *
     * @param time
     * @param now
     * @param minResolution
     * @return
     */
    private static CharSequence getRelativeTimeSpanString(long time,
                                                         long now,
                                                         long minResolution) {
        StringBuilder result = new StringBuilder();

        Resources r = Resources.getSystem();
        boolean past = (now >= time);
        long duration = Math.abs(now - time);

        long count;
        if (duration < MINUTE_IN_MILLIS && minResolution < MINUTE_IN_MILLIS) {
            count = duration / SECOND_IN_MILLIS;
            if (count <= 10) {
                result.append("Just Now");
            } else if (past) {
                result.append(count);
                result.append("s");
            } else {
                result.append("Just Now");
            }
        } else if (duration < HOUR_IN_MILLIS && minResolution < HOUR_IN_MILLIS) {
            count = duration / MINUTE_IN_MILLIS;
            if (past) {
                result.append(count);
                result.append("m");
            } else {
                result.append("Just Now");
            }
        } else if (duration < DAY_IN_MILLIS && minResolution < DAY_IN_MILLIS) {
            count = duration / HOUR_IN_MILLIS;
            if (past) {
                result.append(count);
                result.append("h");
            } else {
                result.append("Just Now");
            }
        } else if (duration < WEEK_IN_MILLIS && minResolution < WEEK_IN_MILLIS) {
            result.append(DateUtils.getRelativeTimeSpanString(time, now, minResolution));
        } else {

            result.append(DateUtils.formatDateRange(null, time, time, 0));
        }

        return result.toString();
    }

}
