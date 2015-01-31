package net.ichigotake.pyazing;

import android.app.Application;

import com.deploygate.sdk.DeployGate;

final class PazingApplication extends Application {

    @Override
    void onCreate() {
        super.onCreate();
        DeployGate.install(this, null, true);
    }
}
