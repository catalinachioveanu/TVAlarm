package com.tv.alarm;

import android.app.Activity;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tv.alarm.R;

public class AlarmScreen extends Activity
{

    //private static final int[] SAMSUNG_CHANNEL1_TOGGLE_COUNT = {168, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 21, 21, 63, 21, 63, 21, 63, 21, 63, 21, 63, 21, 1794, 169, 168, 21, 21, 21, 3694};
    //private static final int[] SAMSUNG_VOL_DOWN_TOGGLE_COUNT = {169, 168, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 21, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 21, 21, 63, 21, 63, 21, 63, 21, 63, 21, 1794, 169, 168, 21, 21, 21, 3694};
    private static final int[] SAMSUNG_VOL_UP_TOGGLE_COUNT = {169, 168, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 63, 21, 63, 21, 63, 21, 1794, 169, 168, 21, 21, 21, 3694};
    private static final int[] SAMSUNG_POWER_TOGGLE_COUNT = {169, 168, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 63, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 63, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 64, 21, 21, 21, 63, 21, 63, 21, 63, 21, 63, 21, 63, 21, 63, 21, 1794, 169, 168, 21, 21, 21, 3694};

    public final String TAG = this.getClass().getSimpleName();

    private WakeLock wakeLock;

    private static final int WAKE_LOCK_TIMEOUT = 60 * 1000;
    private ConsumerIrManager infraredManager;
    private static final int SAMSUNG_FREQ = 38028;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Setup layout
        this.setContentView(R.layout.activity_alarm_screen);

        int timeHour = getIntent().getIntExtra(AlarmManagerHelper.TIME_HOUR, 0);
        int timeMinute = getIntent().getIntExtra(AlarmManagerHelper.TIME_MINUTE, 0);

        TextView timeTextView = (TextView) findViewById(R.id.alarmTextView);
        timeTextView.setText(String.format("%02d : %02d", timeHour, timeMinute));

        Button alarmDismissButton = (Button)findViewById(R.id.alarmDismissButton);
        alarmDismissButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismissAlarm();
            }
        });

        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable()
        {

            @Override
            public void run()
            {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (wakeLock != null && wakeLock.isHeld())
                {
                    wakeLock.release();
                }
            }
        };


        new Handler().postDelayed(releaseWakelock, WAKE_LOCK_TIMEOUT);
    }

    @Override
    protected void onDestroy()
    {
        stopRepeatingTask();
        super.onDestroy();
    }

    private void dismissAlarm()
    {
        stopRepeatingTask();
        finish();
    }

    private void blastIr()
    {
        irInit();
        turnOn();
        //turnVolumeToMin();
        //turnToChannel1();
        turnVolumeUpIncrementally();
    }

//    private void turnToChannel1()
//    {
//        Log.v("IR state", "Switching channel");
//
//        if (Build.VERSION.SDK_INT == 19)
//        {
//            infraredManager.transmit(SAMSUNG_FREQ, SAMSUNG_CHANNEL1_TOGGLE_COUNT);
//        }
//    }

    int volume = 0;

    private void turnVolumeUpIncrementally()
    {
        Log.v("IR state", "Starting task" + (volume + 1));

        startRepeatingTask();
    }

    private final static int INTERVAL = 1000*10; //1 sec
    Handler handler = new Handler();

    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (volume < 15)
            {
                turnVolumeUp();
                handler.postDelayed(runnable, INTERVAL);
            }
            else
            {
                stopRepeatingTask();
            }
        }
    };

    private void turnVolumeUp()
    {
        Log.v("IR state", "Vol up" + (volume + 1));

        volume++;
        if (Build.VERSION.SDK_INT == 19)
        {
            infraredManager.transmit(SAMSUNG_FREQ, SAMSUNG_VOL_UP_TOGGLE_COUNT);
        }

    }

    void startRepeatingTask()
    {
        runnable.run();
    }

    void stopRepeatingTask()
    {
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(runnable, INTERVAL);
    }

//    private void turnVolumeToMin()
//    {
//        if (Build.VERSION.SDK_INT == 19)
//        {
//            for (int i = 0; i <= 20; i++)
//            {
//                Log.v("IR state", "Vol down");
//
//                infraredManager.transmit(SAMSUNG_FREQ, SAMSUNG_VOL_DOWN_TOGGLE_COUNT);
//            }
//        }
//    }

    public void irInit()
    {
        Log.v("IR state", "Initializing IR");
        infraredManager = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
        if (!infraredManager.hasIrEmitter())
        {
            Log.e("Error", "No IR Emitter found\n");
        }
    }

    private void turnOn()
    {
        Log.v("IR state", "Turning on");

        if (Build.VERSION.SDK_INT == 19)
        {
            // Before version of Android 4.4.2
            infraredManager.transmit(SAMSUNG_FREQ, SAMSUNG_POWER_TOGGLE_COUNT);
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wake lock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (wakeLock == null)
        {
            wakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!wakeLock.isHeld())
        {
            wakeLock.acquire();



            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    blastIr();
                }
            }).start();



        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (wakeLock != null && wakeLock.isHeld())
        {
            wakeLock.release();
        }
    }
}
