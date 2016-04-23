package activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import net.londatiga.android.bluetooth.R;

import java.util.Timer;
import java.util.TimerTask;



public class ActivityFan extends Activity {

    private final boolean DEBUG = true;
    private final String TAG = "MainActivity";
    private Timer send_timer = new Timer();
    private int Fan_Power = 10;
    private boolean Fan_Switch = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//??????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//????????
        //RelativeLayout mainLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_fan, null);
        setContentView(R.layout.activity_fan);

        activity.CircleSeekBar circleSeekBar = (activity.CircleSeekBar) findViewById(R.id.circle_seekbar);
        circleSeekBar.setProgress(Fan_Power);
        circleSeekBar.setProgressFrontColor(Color.BLUE);
        circleSeekBar.setOnSeekBarChangeListener(new CircleSeekBarOnChangeListener());

        activity.StartButton mStartButton = (StartButton)findViewById(R.id.StartButton);
        mStartButton.setOnButtonChangeListener(new StartButtonOnChangeListener());

        send_timer.schedule(send_task,1000,50);

    }

    public static int protocalGroup = 11;
    TimerTask send_task = new TimerTask() {
        byte[] bytes = new byte[protocalGroup * 2];

        @Override
        public void run() {

            //Mode
            util.communication.Package.BuildPackage(bytes,(byte)0,(byte)3,0);


            int targetPower = 0;
            //Throttle
            if( true == Fan_Switch)
            {
                targetPower = Fan_Power ;
            }

            util.communication.Package.BuildPackage(bytes, (byte)7, (byte)targetPower,11);




//            bytes[0] = 'P';
//            bytes[1] = 'X';
//            bytes[2] = 0;
//            bytes[3] = 0;
//            bytes[4] = 0;
//            bytes[5] = 0;
//            bytes[6] = 0;
//            bytes[7] = (byte) Fan_Power;
//            //checksum
//            bytes[8] = 0;
//            bytes[9] = 'E';
//            bytes[10] = '\0';
//            bytes[11] = 'P';
//            bytes[12] = 'X';
//            bytes[13] = 0;
//            bytes[14] = 1;
//            bytes[15] = 0;
//            bytes[16] = 0;
//            bytes[17] = 0;
//            bytes[18] = (byte) Fan_Switch;
//            //checksum
//            bytes[19] = 0;
//            bytes[20] = 'E';
//            bytes[21] = '\0';
            if (null != MainActivity.mRfcommClient) {
                MainActivity.mRfcommClient.write(bytes);
            }
        }
    };

    private class CircleSeekBarOnChangeListener implements CircleSeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(int progress) {
            if(DEBUG) Log.d(TAG, "onProgressChanged progress = " + progress);
            Fan_Power = progress;
        }

        @Override
        public void onStartTrackingTouch() {
            if(DEBUG) Log.d(TAG, "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch() {
            if(DEBUG) Log.d(TAG, "onStopTrackingTouch");
        }

    }

    private class StartButtonOnChangeListener implements StartButton.OnButtonChangeListener {

        @Override
        public void onStartTrackingTouch() {
            if(DEBUG) Log.d(TAG, "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(int ButtonState) {
            if(DEBUG) Log.d(TAG, "onStopTrackingTouch"+ButtonState);
            if(ButtonState < 0)
            {
                Fan_Switch = true;
            }else if (ButtonState > 0){
                Fan_Switch = false;
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
