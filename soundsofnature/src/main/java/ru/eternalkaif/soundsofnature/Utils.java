package ru.eternalkaif.soundsofnature;

import android.app.ActivityManager;
import android.content.Context;

import ru.eternalkaif.soundsofnature.service.MusicService;

public class Utils {

    public static boolean isMusicServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
