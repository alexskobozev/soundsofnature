package ru.eternalkaif.soundsofnature;

import android.content.Intent;
import android.os.Bundle;

public interface ServiceCallbackListener {

    void onServiceCallBack(int requestId, Intent requestIntent, int resultCode, Bundle data);
}
