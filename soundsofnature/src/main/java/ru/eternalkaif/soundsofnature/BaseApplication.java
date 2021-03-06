package ru.eternalkaif.soundsofnature;

import android.app.Application;
import android.content.Context;

import org.jetbrains.annotations.Nullable;

public class BaseApplication extends Application {

    public static final String PACKAGE = "ru.eternalkaif.soundsofnature";

    private ServiceHelper serviceHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHelper = new ServiceHelper(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }

    @Nullable
    public static BaseApplication getApplication(Context context) {
        if (context instanceof BaseApplication) {
            return (BaseApplication) context;
        }
        return (BaseApplication) context.getApplicationContext();
    }
}
