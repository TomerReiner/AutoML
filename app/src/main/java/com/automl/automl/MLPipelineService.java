package com.automl.automl;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.List;

/**
 * This service will create the ML Model pipeline
 */
public class MLPipelineService extends Service {

    public static final String CHANNEL_ID = "channel";
    public static final int NOTIFICATION_ID = 1;
    public static final String ML_TEST = "mlTest";

    private String[] columns; // The columns in the dataset.
    private Object[][] data; // The data in the dataset.
    private ArrayList<Block> blocks; // The list of blocks (i.e., the data analysis actions).
    private MLModel mlModel; // The ML Model that the user would like to build.
    private String yColumnName;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private SQLiteDatabaseHelper sqLiteDatabaseHelper;

    public MLPipelineService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Creating the ML Model. You can quit the app for now.", Toast.LENGTH_LONG).show();

        for (int i = 0; i < 1000000000; i++) { }

        ScreenStateReceiver screenStateReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(screenStateReceiver, filter);

        Bundle extras = intent.getExtras();

        FileManager fileManager = (FileManager) extras.getSerializable("fileManager");

        this.columns = fileManager.getColumns();
        this.data = fileManager.getData();

        this.blocks = (ArrayList<Block>) extras.getSerializable("blocks");
        this.mlModel = (MLModel) extras.getSerializable("mlModel");
        this.yColumnName = extras.getString("yColumn");
        this.firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
        this.sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        ArrayList<MLModelDisplay> models = (ArrayList<MLModelDisplay>) extras.getSerializable("models");
        this.firebaseDatabaseHelper.setModels(models);

        if (this.sqLiteDatabaseHelper.getUser() == null) { // If there is no user logged in to the app.
            Toast.makeText(this, "You must be logged in to create ML Models.", Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);
        }

        MLTest mlTest = null; // All the information needed to test the ML Model.

        Python py = Python.getInstance();
        PyObject pyFile = py.getModule("main");
        PyObject result = pyFile.callAttr("main", this.columns, this.data, this.blocks,
                this.mlModel.getType(),
                this.mlModel.getAttributes(), this.yColumnName); // Starting the ML pipeline.

        List<PyObject> lst = result.asList();

        if (lst.size() == 1) { // If the size is one then something went wrong and we should notify the user about that.
            String errorMessage = lst.get(0).toString();

            if (!screenStateReceiver.isOn() || isAppInBackground()) // If the screen is off or the app is not in foreground.
                createMLModelFailedNotification(errorMessage);
            else
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }

        else { // The task was successful.
            String score = lst.get(0).toString();
            String numColumns = lst.get(1).toString();
            String numRows = lst.get(2).toString();
            PyObject yColumnEncoding = lst.get(3);
            PyObject ml = lst.get(4);
            PyObject normalizationInfo = lst.get(5);
            List<PyObject> columns = lst.get(6).asList();

            MLModelDisplay display = new MLModelDisplay(this.mlModel, numColumns, numRows, score);

            this.firebaseDatabaseHelper.addMLModel(this.sqLiteDatabaseHelper.getUser().getUsername(), display); // Add the ML Model to the database.

            mlTest = new MLTest(this.yColumnName,score, yColumnEncoding, ml, normalizationInfo, columns);
        }

        if (!screenStateReceiver.isOn() || isAppInBackground()) // If the screen is off or the app is not in foreground.
            createMLReadyNotification(mlTest);
        else { // If the user is still in the app.
            Intent intentMoveToTestMLModelActivity = new Intent(this, TestMLModelActivity.class);
            intentMoveToTestMLModelActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentMoveToTestMLModelActivity.putExtra(ML_TEST, mlTest);
            startActivity(intentMoveToTestMLModelActivity);
        }

        stopSelf();
        unregisterReceiver(screenStateReceiver);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This function checks if the app is in the background.
     * If the app is not in foreground and/or the screen is off, the user should receive a notification that the ML Model is ready to be tested.
     * @return <code>true</code> if the app is in background, <code>false</code> otherwise.
     */
    private boolean isAppInBackground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE;
    }

    /**
     * This function creates a notification to notify the user that their ML Model is ready.
     * @param mlTest All the information needed to test the ML Model.
     */
    private void createMLReadyNotification(MLTest mlTest) {
        System.out.println("here");
        Intent intent = new Intent(this, TestMLModelActivity.class);
        intent.putExtra("mlTest", mlTest);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify_001")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("My ML")
                .setContentText("Your ML Model is Ready. \nClick Here to Test it!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My ML", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);
        builder.setChannelId(CHANNEL_ID);
        notificationManager.notify(0, builder.build());
        stopSelf();
    }

    /**
     * This function creates a message that notifies the user that the ML pipeline failed.
     * This message will only appear if the app is not in foreground or if the screen is off.
     * This notification is only meant to notify the user about the problem, not help them fix it.
     * @param message The error message explaining exactly what was the problem in the pipeline.
     */
    private void createMLModelFailedNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(ML_TEST)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}