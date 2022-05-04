package com.automl.automl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStateReceiver extends BroadcastReceiver {

    private boolean isOff; // Whether the screen is on/off.

    public ScreenStateReceiver() {
        super();
        this.isOff = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SCREEN_ON))
            this.isOff = false;
        else if (action.equals(Intent.ACTION_SCREEN_OFF))
            this.isOff = true;
    }

    public boolean isOn() {
        return this.isOff;
    }
}