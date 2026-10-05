package com.rescuefarm.service.sync;

import android.content.Context;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public final class OfflineSyncScheduler {
    private static final String PERIODIC_NAME = "rescuefarm-read-cache-periodic";
    private static final String STARTUP_NAME = "rescuefarm-read-cache-startup";
    private OfflineSyncScheduler() { }

    public static void schedule(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest periodic = new PeriodicWorkRequest.Builder(ReadCacheSyncWorker.class,
                6, TimeUnit.HOURS).setConstraints(constraints).build();
        OneTimeWorkRequest startup = new OneTimeWorkRequest.Builder(ReadCacheSyncWorker.class)
                .setConstraints(constraints).setInputData(new Data.Builder()
                        .putBoolean("force", false).build()).build();
        WorkManager manager = WorkManager.getInstance(context.getApplicationContext());
        manager.enqueueUniquePeriodicWork(PERIODIC_NAME, ExistingPeriodicWorkPolicy.KEEP, periodic);
        manager.enqueueUniqueWork(STARTUP_NAME, ExistingWorkPolicy.KEEP, startup);
    }

    public static void refreshNow(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ReadCacheSyncWorker.class)
                .setConstraints(constraints).setInputData(new Data.Builder()
                        .putBoolean("force", true).build()).build();
        WorkManager.getInstance(context.getApplicationContext()).enqueueUniqueWork(
                STARTUP_NAME, ExistingWorkPolicy.REPLACE, request);
    }
}
