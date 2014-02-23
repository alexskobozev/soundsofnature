package ru.eternalkaif.soundsofnature;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public abstract class BaseActivity extends ActionBarActivity implements ServiceCallbackListener {

    private ServiceHelper serviceHelper;

    protected BaseApplication getApp() {
        return (BaseApplication) getApplication();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHelper = getApp().getServiceHelper();
    }

    protected void onResume() {
        super.onResume();
        serviceHelper.addListener(this);
    }

    protected void onPause() {
        super.onPause();
        serviceHelper.removeListener(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }

    /**
     * Called when service finished executing
     *
     * @param requestId     original request id
     * @param requestIntent request data
     * @param resultCode    result of execution code
     */
    @Override
    public void onServiceCallBack(int requestId, Intent requestIntent, int resultCode, Bundle data) {

    }
}
