package activity;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import net.londatiga.android.bluetooth.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityDebug extends Activity {
    private SurfaceHolder holder = null;
    private Paint paint;
    final int HEIGHT = 320;
    final int WIDTH = 320;
    final int X_OFFSET = 0;
    private int cx = X_OFFSET;
    //实际的Y轴的位置
    int centerY = HEIGHT / 2;
    Timer timer = new Timer();
    TimerTask task = null;
    Handler handler = null;
    WaverView waverView = null;
    byte[] pidBytes = new byte[33];
    ThrottleView throttle  = null;
    private List<Integer> data = new ArrayList<Integer>();
    TextView tvKp;
    TextView tvKi;
    TextView tvKd;
    SeekBar sbKp;
    SeekBar sbKi;
    SeekBar sbKd;
    RadioButton rbRoll;
    RadioButton rbPitch;
    RadioButton rbYaw;



    boolean bIsSendPID = false;
    JoystickView joystick = null;

    Timer send_timer = new Timer( );
    TimerTask send_task = new TimerTask( ) {
        byte[] send_bytes = new byte[22];

        public void run ( )
        {
//            waverView.addData(new Random().nextInt(180) - 90);
            if (rbRoll.isChecked()) {
                waverView.setTarget((int) (45.0f / 20.0f * joystick.getRoll()));
            }
            else if (rbPitch.isChecked()) {
                waverView.setTarget((int) (45.0f / 20.0f * joystick.getPitch()));
            }
            else if (rbYaw.isChecked()) {
                waverView.setTarget((int) (45.0f / 20.0f * joystick.getRoll()));
            }


            waverView.postInvalidate();
            if( bIsSendPID) {
                bIsSendPID = false;

                int kp = sbKp.getProgress();
                int ki = sbKi.getProgress();
                int kd = sbKd.getProgress();
                if (rbRoll.isChecked()) {
                    //kp
                    util.communication.Package.BuildPackage(pidBytes, (byte) 10, (byte) kp, 0);
                    //ki
                    util.communication.Package.BuildPackage(pidBytes, (byte) 11, (byte) ki, 11);
                    //kd
                    util.communication.Package.BuildPackage(pidBytes, (byte) 12, (byte) kd, 22);
                }
                else if (rbPitch.isChecked())
                {
                    //kp
                    util.communication.Package.BuildPackage(pidBytes, (byte) 13, (byte) kp, 0);
                    //ki
                    util.communication.Package.BuildPackage(pidBytes, (byte) 14, (byte) ki, 11);
                    //kd
                    util.communication.Package.BuildPackage(pidBytes, (byte) 15, (byte) kd, 22);
                }
                else if( rbYaw.isChecked())
                {
                    //kp
                    util.communication.Package.BuildPackage(pidBytes, (byte) 16, (byte) kp, 0);
                    //ki
                    util.communication.Package.BuildPackage(pidBytes, (byte) 17, (byte) ki, 11);
                    //kd
                    util.communication.Package.BuildPackage(pidBytes, (byte) 18, (byte) kd, 22);
                }

                if( null != MainActivity.mRfcommClient)
                {
                    MainActivity.mRfcommClient.write(pidBytes);
                }
            }
            else {
                int roll = (int) (45.0f/20.0f*joystick.getRoll());
                int pitch = (int)(45.0f/20.0f*joystick.getPitch());
                int yaw = (int) (90.0f/20.0f*joystick.getRoll());
                int throttleValue = throttle.getValue();

                if (rbRoll.isChecked())
                {
                    //Roll
                    util.communication.Package.BuildPackage(send_bytes, (byte)1, (byte) (roll), 0);
                    //Throttle
                    util.communication.Package.BuildPackage(send_bytes, (byte)4, (byte)throttleValue,11);
                }

                else if (rbPitch.isChecked())
                {
                    //pitch
                    util.communication.Package.BuildPackage(send_bytes, (byte) 2, (byte) (pitch), 0);
                    //Throttle
                    util.communication.Package.BuildPackage(send_bytes, (byte)4, (byte)throttleValue,11);
                }
                else if((rbYaw.isChecked()) )
                {
                    //yaw
                    util.communication.Package.BuildPackage(send_bytes, (byte) 3, (byte) (yaw), 0);
                    //Throttle
                    util.communication.Package.BuildPackage(send_bytes, (byte)4, (byte)throttleValue,11);
                }


                if (null != MainActivity.mRfcommClient) {
                    MainActivity.mRfcommClient.write(send_bytes);
                }
            }
        }
    };

    private int MaxDataSize = 320;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_debug);

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {

                    case 1:
                        int length = msg.arg1;
                        byte[] buffer = (byte[])(msg.obj);

                        for(int i=0;i< length;i++)
                        {
                            waverView.addData(buffer[i]);
                        }
                        break;
                    case 2:
                        //toastUtil.showToast((String)(msg.obj));
                        break;
                }

                super.handleMessage(msg);
            }
        };

        if( null != MainActivity.mRfcommClient) {
            MainActivity.mRfcommClient.setHandler(handler);
        }

        tvKp = (TextView) findViewById(R.id.tv_kp);
        tvKi = (TextView) findViewById(R.id.tv_ki);
        tvKd = (TextView) findViewById(R.id.tv_kd);
        sbKp = (SeekBar)  findViewById(R.id.sb_kp);
        sbKi = (SeekBar)  findViewById(R.id.sb_ki);
        sbKd = (SeekBar)  findViewById(R.id.sb_kd);
        joystick = (JoystickView) findViewById(R.id.debug_joystick);
        throttle = (ThrottleView) findViewById(R.id.debug_throttle);
        sbKp.setMax(200);
        sbKi.setMax(200);
        sbKd.setMax(200);
        rbRoll = (RadioButton) findViewById(R.id.rb_roll);
        rbPitch = (RadioButton) findViewById(R.id.rb_pitch);
        rbYaw = (RadioButton) findViewById(R.id.rb_yaw);

        sbKp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( fromUser )
                {
                    float value = progress / 10.0f;
                    String str = String.valueOf(value);
                    tvKp.setText("Kp"+str);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbKi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( fromUser )
                {
                    float value = progress / 10.0f;
                    String str = String.valueOf(value);
                    tvKi.setText("Ki"+str);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbKd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( fromUser )
                {
                    float value = progress / 10.0f;
                    String str = String.valueOf(value);
                    tvKd.setText("Kd"+str);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Button btnUpdateParam = (Button)findViewById(R.id.btn_update_param);



        btnUpdateParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bIsSendPID = true;
            }
        });

        waverView = (WaverView) findViewById(R.id.waver);
        send_timer.schedule(send_task,1000,50);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy ( ) {
        if (send_timer != null)
        {
            send_timer.cancel();
            send_timer = null;
        }
        super.onDestroy( );
    }
}