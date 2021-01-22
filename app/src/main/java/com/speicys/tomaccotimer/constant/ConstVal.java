package com.speicys.tomaccotimer.constant;

import com.speicys.tomaccotimer.R;

public class ConstVal {

    public static class Clock {

        public static final Long OG_CLOCK_DURATION_MILIS = 25000L;
        public static final Long TICK_INTERVAL_SECOND = 1000L;

        public static final Integer ORIGINAL_CLOCK_MINUTES = (int) (OG_CLOCK_DURATION_MILIS / 60000);
        public static final Integer ORIGINAL_CLOCK_SECONDS = (int) (OG_CLOCK_DURATION_MILIS / 1000);

    }

}
