package com.rescuefarm;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;

public final class RescueFarmApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        FirebaseApp app = FirebaseApp.initializeApp(this);
        if (app != null) {
            AppCheckInstaller.install(FirebaseAppCheck.getInstance(app));
        }
    }
}
