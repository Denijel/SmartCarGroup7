package com.example.denijel.smartcargroup7;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

/**
 * Created by denijel on 4/21/16.
 */
public class joystickService extends Service{
    private Looper mServiceLoop;
    //private ServiceHandler mServiceHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
