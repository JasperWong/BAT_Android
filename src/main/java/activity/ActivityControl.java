package activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.londatiga.android.bluetooth.R;

import util.BLE.BLEService;
import util.BLE.GATTUtils;
import util.communication.*;
import util.communication.Package;

public class ActivityControl extends Activity {

    private static final String TAG = ActivityControl.class.getSimpleName();
    private BLEService mBluetoothLeService = null;

    BluetoothGattCharacteristic mCharacteristic;

    ArrayList<InputStream> inputStreamArrayList = new ArrayList<InputStream>();


    JoystickView joystick = null;
    ThrottleView throttle  = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_craft);

        //
        joystick = (JoystickView) findViewById(R.id.joystick_view);
        throttle = (ThrottleView)findViewById(R.id.throttle_view);

        //
        Intent gattServiceIntent = new Intent(ActivityControl.this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }




    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service)
        {
            Log.d(TAG, "start service Connection");


            //
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();


            //从搜索出来的services里面找出合适的service
            List<BluetoothGattService> gattServiceList = mBluetoothLeService.getSupportedGattServices();
            mCharacteristic = GATTUtils.lookupGattServices(gattServiceList, GATTUtils.BLE_TX);

            //
            if( null != mCharacteristic )
            {
                mBluetoothLeService.setCharacteristicNotification(mCharacteristic, true);
                InputStream inputStream = buildSendData();
                inputStreamArrayList.add(inputStream);
                byte[] writeBytes = new byte[11];
                int byteCount = 0;
                try
                {
                    byteCount = inputStream.read(writeBytes,0,11);
                    if( byteCount > 0)
                    {
                        mCharacteristic.setValue(writeBytes);
                        mBluetoothLeService.writeCharacteristic(mCharacteristic);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }



        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            Log.d(TAG, "end Service Connection");
            mBluetoothLeService = null;
        }
    };


    @Override
    protected void onResume()
    {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, buildGattUpdateIntentFilter());
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        unregisterReceiver(mGattUpdateReceiver);

    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unbindService(mServiceConnection);
    }


    InputStream buildSendData()
    {

        byte[] data = new byte[44];

        int roll = (int) joystick.getRoll();
        int pitch = (int)joystick.getPitch();
        int throttleValue = throttle.getValue();

        //Mode
        util.communication.Package.BuildPackage(data, Package.REG_MODE,(byte)1,0);
        //Roll
        util.communication.Package.BuildPackage(data, Package.REG_ROLL, (byte)roll,11);
        //Pitch
        util.communication.Package.BuildPackage(data, Package.REG_PITCH, (byte)pitch,22);
        //Throttle
        util.communication.Package.BuildPackage(data, Package.REG_THROTTLE, (byte) throttleValue, 33);

        return new ByteArrayInputStream(data);

    }


    private static IntentFilter buildGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BLEService.ACTION_DATA_WRITE);

        return intentFilter;
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "enter BroadcastReceiver");
            final String action = intent.getAction();
            Log.d(TAG, "action = " + action);
            if (BLEService.ACTION_DATA_WRITE.equals(action))
            {
                Log.d(TAG, "receive data");
                if( inputStreamArrayList.size() != 0)
                {
                    InputStream writeStream = inputStreamArrayList.get(0);
                    byte[] readBytes = new byte[11];
                    try
                    {
                        int byteCount = writeStream.read(readBytes, 0, 11);
                        if (byteCount > 0)
                        {
                            mCharacteristic.setValue(readBytes);
                            mBluetoothLeService.writeCharacteristic(mCharacteristic);
                            return;
                        }
                        else
                        {
                            inputStreamArrayList.remove(0);
                            Log.d(TAG, "finish send stream!");
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                if (inputStreamArrayList.size() == 0)
                {
                    InputStream inputStream = buildSendData();
                    inputStreamArrayList.add(inputStream);
                    byte[] writeBytes = new byte[11];
                    int byteCount = 0;
                    try
                    {
                        byteCount = inputStream.read(writeBytes,0,11);
                        if( byteCount > 0)
                        {
                            mCharacteristic.setValue(writeBytes);
                            mBluetoothLeService.writeCharacteristic(mCharacteristic);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


}
