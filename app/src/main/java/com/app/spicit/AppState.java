package com.app.spicit;

import android.app.Application;
import android.widget.Toast;

public class AppState extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                //handleUncaughtException (thread, e);
                Toast.makeText(getApplicationContext(), "Ooops! Sorry, something went wrong !", Toast.LENGTH_LONG);
            }
        });
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
