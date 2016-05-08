package com.kieran.winnipegbus;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kieran.winnipegbus.Activities.BaseActivity;
import com.kieran.winnipegbus.Activities.StopTimesActivity;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.ScheduledStopKey;
import com.kieran.winnipegbusbackend.StopSchedule;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.enums.CoverageTypes;

import org.w3c.dom.Document;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
    public static final String DEPARTURE_TEXT = "Departure: ";
    public static final String PAST_DUE_TEXT = "Past due";
    public static final int HALF_MINUTE = 30000;
    public static final int ONE_MINUTE = 60000;
    public static final int TWO_MINUTES = 120000;
    public static final int FIVE_MINUTES = 300000;
    public static final int FIFTEEN_MINUTES = 900000;
    public static final int ONE_HOUR = 3600000;
    public static final int TEN_SECONDS = 10000;
    public static final int HALF_HOUR = 1800000;
    public static final int TEN_MINUTES = 600000;
    public static final String CANCEL = "CANCEL";
    private static NotificationCompat.Builder builder;
    private static android.app.NotificationManager notifyMgr;
    private static ScheduledExecutorService scheduler;
    private Service service;

    public static NotificationData data;
    static Intent intent;
    static int id;
    static int nextId = 0;
    UpdateNotification updateNotification;
    static Notification notification;
    static PowerManager.WakeLock wakeLock;
    static PowerManager powerManager;

    Runnable runnable = new Runnable() {
        public void run() {
            try {
                updateNotification = new UpdateNotification();
                updateNotification.execute(BusUtilities.generateStopNumberURL(data.getNumber(), data.getRouteNumber(), data.getStartTime(), null));
            } catch (Exception e) {
                Log.e("error", e.getMessage());
                notifyMgr.notify(id, notification);
            }
        }
    };

    public static void createNotification(int stopNumber, int routeNumber, ScheduledStopKey key, String variantName, String stopName, StopTime startTime, Context context, CoverageTypes coverageType) {
        createNotification(new NotificationData(stopNumber, routeNumber, key, variantName, stopName, context, startTime, coverageType));
    }

    public static void createNotification(NotificationData newData) {
        data = newData;

        if (intent != null) {
            data.getContext().stopService(intent);
        }

        intent = new Intent(data.getContext(), NotificationService.class);

        id = nextId++;

        data.getContext().startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && !intent.getBooleanExtra(CANCEL, false)) {
            service = this;
            id = nextId++;
            Log.e("SERVICE", "Starting service");
            createNotification();

            if (powerManager == null)
                powerManager = (PowerManager) data.getContext().getSystemService(Context.POWER_SERVICE);
            if (wakeLock == null)
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
            wakeLock.acquire();

            return START_STICKY;
        }else {
            stopService(NotificationService.intent);
            stopSelf();

            return START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        Log.e("SERVICE", "Ending service");
        scheduler.shutdownNow();
        notifyMgr.cancelAll();
        wakeLock.release();
        super.onDestroy();
    }

    private void createNotification() {
        Intent clickIntent = new Intent(data.getContext(), StopTimesActivity.class);
        clickIntent.putExtra(StopTimesActivity.STOP, data);
        PendingIntent intent = PendingIntent.getActivity(data.getContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(data.getContext(), NotificationService.class);
        cancelIntent.putExtra(CANCEL, true);
        PendingIntent cancelPendingIntent = PendingIntent.getService(data.getContext(), 0, cancelIntent, 0);

        builder = new NotificationCompat.Builder(data.getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent)
                .setContentText(Integer.toString(data.getRouteNumber()) + " - " + data.getVariantName() + " at " + data.getName())
                .setDeleteIntent(cancelPendingIntent)
                .setContentTitle(DEPARTURE_TEXT)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        if (notifyMgr == null) {
            notifyMgr = (android.app.NotificationManager) data.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notification = builder.build();
        notifyMgr.notify(id, notification);

        updateNotificationText(DEPARTURE_TEXT + data.getStartTime().toFormattedString(null, BaseActivity.getTimeSetting(data.getContext())));
        callFirstRefresh();
    }

    public void callFirstRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(runnable, 0, TimeUnit.SECONDS);
    }

    private void scheduleNextRefresh(long time) {
        long delay = FIFTEEN_MINUTES;
        long difference = time - BusUtilities.lastQueryTime.getMilliseconds();

        if (difference < TWO_MINUTES)
            delay = TEN_SECONDS;
        else if (difference < TEN_MINUTES)
            delay = HALF_MINUTE;
        else if (difference < FIFTEEN_MINUTES)
            delay = ONE_MINUTE;
        else if (difference < HALF_HOUR)
            delay = TWO_MINUTES;
        else if(difference < ONE_HOUR)
            delay = FIVE_MINUTES;

        Log.e("DELAY", Long.toString(delay));
        if (!scheduler.isShutdown())
            scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    private void updateNotificationText(String text) {
        builder.setContentTitle(text);
        builder.setWhen(System.currentTimeMillis());
        notification = builder.build();
        notifyMgr.notify(id, notification);
    }

    private class UpdateNotification extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                StopSchedule stopSchedule = new StopSchedule((Document) result.getResult());
                ScheduledStop scheduledStop1 = stopSchedule.getScheduledStopByKey(data.getKey());

                if (scheduledStop1 != null) {
                    Log.e("STOP", "not null");

                    data.setStartTime(scheduledStop1.getEstimatedDepartureTime());
                    if (BusUtilities.lastQueryTime.getMilliseconds() - HALF_MINUTE > data.getStartTime().getMilliseconds()) {
                        updateNotificationText(DEPARTURE_TEXT + PAST_DUE_TEXT);
                    } else {
                        updateNotificationText(DEPARTURE_TEXT + scheduledStop1.getEstimatedDepartureTime().toFormattedString(BusUtilities.lastQueryTime, BaseActivity.getTimeSetting(data.getContext())));
                    }
                }
            } else {
                if (BusUtilities.lastQueryTime.getMilliseconds() - HALF_MINUTE > data.getStartTime().getMilliseconds()) {
                    updateNotificationText(DEPARTURE_TEXT + PAST_DUE_TEXT);
                } else {
                    updateNotificationText(DEPARTURE_TEXT + data.getStartTime().toFormattedString(null, BaseActivity.getTimeSetting(data.getContext())));
                }
            }

            if (BusUtilities.lastQueryTime.getMilliseconds() - TWO_MINUTES > data.getStartTime().getMilliseconds()) {
                scheduler.shutdown();
                notifyMgr.cancelAll();

                service.stopSelf();
            } else if (BusUtilities.lastQueryTime.getMilliseconds() - HALF_MINUTE > data.getStartTime().getMilliseconds()) {
                updateNotificationText(DEPARTURE_TEXT + PAST_DUE_TEXT);
            }
            scheduleNextRefresh(data.getStartTime().getMilliseconds());
        }
    }
}
