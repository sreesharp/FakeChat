package com.sreesharp.sreechat.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;

import com.sreesharp.sreechat.models.MessageType;
import com.sreesharp.sreechat.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
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
    private static Random rnd = new Random();

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

    //Generate random texts to reply for chat messages
    public static String getRandomTexts(MessageType type){
        String[] textReply = {"I’ve heard so much about you","It’s good to have you here!","I’d like you to meet someone!",
                "I am indeed! And you must be","I’ll leave you two to get acquainted!","Please, call me…","I almost didn’t recognize you!",
                "Have we met before?","It’s good to see you again!","How are you getting on?","You doing OK?","Do you mind me asking…?",
                "OK, here’s the thing","Thanks, I’ve been keeping busy","Can’t complain","Can you say it again, please?","And how about you?",
                "To the best of my knowledge","As far as I know","Good for you!","Can’t argue with that","How do you know?","That’s a good one!",
                "Really? Tell me more about it!","Frankly speaking,","Well, to be honest with you","No problem","Never mind, it’s fine!",
                "Never mind, forget what I just said","You got me there","You’ve got to be kidding me!","That’s a good question",
                "Well, how to put it in the right words","That would be great!","… you know what I mean?","You see, the thing is that",
                "Nose to the grindstone!","Anything new going on?","The boss is in a mood","All work and no play!","Better keep the head down today",
                "You working the weekend?","Are you working hours in?","I’m tired – I got no sleep last night","Had a few drinks so I’m flying under the radar!",
                "Can you cover me?","It’s so boring!","Wish I had her job!","We’re not paid enough!","We’re not paid enough!","That’s one job I wouldn’t do!",
                "That’s a cushy number!","I don’t know how he got that job!","I’d better be going","I really gotta go","OK, I’m sorry but I have to leave now!",
                "It’s been good talking to you!"};
        String[] imgReply = {"Nice photo","Cool. You could have apply nice filters", "Looks cool. Did you upload to facebook?", "This is classic", "I would frame this photo",
                            "Did you capture it with phone?","Good one, but crop it properly", "Awesome, who captured it?", "This is called photography!"};
        if(type == MessageType.Image)
            return imgReply[rnd.nextInt(imgReply.length)];
        else
            return textReply[rnd.nextInt(textReply.length)];
    }

}
