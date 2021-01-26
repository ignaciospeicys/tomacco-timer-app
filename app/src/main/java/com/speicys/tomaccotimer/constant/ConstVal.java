package com.speicys.tomaccotimer.constant;

/**
 * Strings that are viewable in the app by the user SHOULD NOT APPEAR IN THIS CLASS, those belong in the strings.xml file
 */
public class ConstVal {

    public static class Clock {

        public static final long OG_CLOCK_DURATION_MILLIS = 1500000L;
        public static final long TICK_INTERVAL_SECOND = 1000L;

        public static final long OG_REST_DURATION_MILLIS = 300000L;

        public static final int ORIGINAL_CLOCK_MINUTES = (int) (OG_CLOCK_DURATION_MILLIS / 1000) / 60;
        public static final int ORIGINAL_CLOCK_SECONDS = (int) (OG_CLOCK_DURATION_MILLIS / 1000) % 60;

        public static final int ORIGINAL_REST_MINUTES = (int) (OG_REST_DURATION_MILLIS / 1000) / 60;
        public static final int ORIGINAL_REST_SECONDS = (int) (OG_REST_DURATION_MILLIS / 1000) % 60;

    }

    public static class View {

        public static final String FORMATTED_CLOCK = "%s:%s";
    }

    public static class Sound {

        public static final int ALARM_DURATION_MILLIS = 5000;
    }

    public static class Animation {

        public static final long GROUP_ANIMATION_OFFSET = 150;
    }

    public static class Notification {

        public static final String TOMACCO_END_NOTIFICATION_CHANNEL_ID = "notification_clock_end";
        public static final int TOMACCO_END_NOTIFICATION_ID = 10;
    }

}
