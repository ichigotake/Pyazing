package net.ichigotake.pyazing;

import android.app.Application;

import com.deploygate.sdk.DeployGate;

public final class PazingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DeployGate.install(this, null, true);
    }
}
