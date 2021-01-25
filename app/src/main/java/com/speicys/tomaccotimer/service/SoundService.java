package com.speicys.tomaccotimer.service;

import android.media.RingtoneManager;
import android.net.Uri;

public class SoundService {

    public static Uri getAlarmSoundUri() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup ringtone
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if(alert == null) {
                // alert backup is also null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}
