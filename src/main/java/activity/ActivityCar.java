package activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import net.londatiga.android.bluetooth.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by project_x on 2015/4/22.
 */
public class ActivityCar  extends Activity {

    private static final String TAG = ActivityCar.class.getSimpleName();
    Timer send_timer = new Timer();
    ThrottleView leftThrottle = null;
    ThrottleView rightThrottle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_car);
        leftThrottle = (ThrottleView) findViewById(R.id.left_throttle_view);
        rightThrottle = (ThrottleView) findViewById(R.id.right_throttle_view);
        send_timer.schedule(send_task, 1000, 50);
    }

    public void onPause() {
        super.onPause();
    }
    public static int protocalGroup = 11;
    TimerTask send_task = new TimerTask() {
        byte[] bytes = new byte[protocalGroup * 3];

        public void run() {

            int leftThrottleValue = leftThrottle.getValue();
            int rightThrottleValue = rightThrottle.getValue();

            //Mode 2 = car mode
            util.communication.Package.BuildPackage(bytes,(byte)0,(byte)2,0);
            //leftThrottleValue
            util.communication.Package.BuildPackage(bytes, (byte)5, (byte)leftThrottleValue,11);
            //rightThrottleValue
            util.communication.Package.BuildPackage(bytes, (byte)6, (byte)rightThrottleValue,22);


            if( null != MainActivity.mRfcommClient)
            {
                MainActivity.mRfcommClient.write(bytes);
            }
        }
    };


    protected void onDestroy() {
        if (send_timer != null) {
            send_timer.cancel();
            send_timer = null;
        }
        super.onDestroy();
    }
}