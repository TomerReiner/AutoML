package com.automl.automl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStateReceiver extends BroadcastReceiver {

    private boolean isOn; // Whether the screen is on/off.

    public ScreenStateReceiver() {
        super();
        this.isOn = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SCREEN_ON))
            this.isOn = true;
        else if (action.equals(Intent.ACTION_SCREEN_OFF))
            this.isOn = false;
    }

    public boolean isOn() {
        return this.isOn;
    }
}