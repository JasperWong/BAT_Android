package activity;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ListView;

import net.londatiga.android.bluetooth.R;

import java.util.ArrayList;
import java.util.List;

import util.BLE.BLEService;
import util.BLE.GATTUtils;

/**
 * Created by project_x on 2015/4/20.
 */
public class MenuActivity extends Activity
{

    private final static String TAG = MenuActivity.class.getSimpleName();

    private ArrayList<ClassMenuItem> mMenuList = new ArrayList<ClassMenuItem>();

    private ListView mListView;

    private MenuListAdapter mAdapter;

    private BLEService mBluetoothLeService = null;

    private BluetoothDevice mBluetoothDevice = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_mode);


        //
        Intent intent = getIntent();
        mBluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(MainActivity.EXTRA_DEVICE_OBJ);

        //
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.action_bar_title);
        actionBar.setDisplayShowCustomEnabled(true);


        //
        ClassMenuItem menuItemBAT = new ClassMenuItem();
        menuItemBAT.name = "BAT";
        menuItemBAT.description = "飞行器模式";
        menuItemBAT.iconResourceId = R.drawable.bat_mode;
        menuItemBAT.intent = new Intent(MenuActivity.this, ActivityControl.class);
        mMenuList.add(menuItemBAT);


        //
        ClassMenuItem menuItemCar = new ClassMenuItem();
        menuItemCar.name = "Car";
        menuItemCar.description = "二轮平衡车模式";
        menuItemCar.iconResourceId = R.drawable.car_mode;
        menuItemCar.intent = new Intent(MenuActivity.this, ActivityCar.class);
        mMenuList.add(menuItemCar);


        //
        ClassMenuItem menuItemFan = new ClassMenuItem();
        menuItemFan.name = "Fan";
        menuItemFan.description = "风扇模式";
        menuItemFan.iconResourceId = R.drawable.fan_mode;
        menuItemFan.intent = new Intent(MenuActivity.this, ActivityFan.class);
        mMenuList.add(menuItemFan);


        //
        ClassMenuItem menuItemDebug = new ClassMenuItem();
        menuItemDebug.name = "Debug";
        menuItemDebug.description = "调试模式";
        menuItemDebug.iconResourceId = R.drawable.debug_mode;
        menuItemDebug.intent = new Intent(MenuActivity.this, ActivityDebug.class);
        mMenuList.add(menuItemDebug);


        //
        mListView = (ListView) findViewById(R.id.lv_menu);
        mAdapter = new MenuListAdapter(this);
        mAdapter.setData(mMenuList);
        mListView.setAdapter(mAdapter);
        // start service
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service)
        {
            Log.d(TAG, "start service Connection");

            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();


            //
            if (!mBluetoothLeService.initialize())
            {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }


            // Automatically connects to the device upon successful start-up
            // initialization.
            Log.d(TAG, "mDeviceAddress = " + mBluetoothDevice.getAddress());
            boolean status = mBluetoothLeService.connect(mBluetoothDevice.getAddress());
            if (false == status)
            {
                Log.d(TAG, "Connection failed");
//                finish();
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
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mServiceConnection);
        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "enter BroadcastReceiver");
            final String action = intent.getAction();
            Log.d(TAG, "action = " + action);
            if (BLEService.ACTION_GATT_CONNECTED.equals(action))
            {
                Log.d(TAG, "BroadcastReceiver :" + "device connected");

            }
            else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action))
            {

            }
            else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                Log.d(TAG, "services discovered!!!");

                //从搜索出来的services里面找出合适的service
                List<BluetoothGattService> gattServiceList = mBluetoothLeService.getSupportedGattServices();
                BluetoothGattCharacteristic characteristic = GATTUtils.lookupGattServices(gattServiceList, GATTUtils.BLE_TX);

                // Error
                characteristic.setValue("this is a test write for characteristic");
                //characteristic.setValue("test");

                mBluetoothLeService.writeCharacteristic(characteristic);

            }
            else if (BLEService.ACTION_DATA_AVAILABLE.equals(action))
            {
                Log.d(TAG, "receive data");

            }
            else if (BLEService.ACTION_GATT_RSSI.equals(action))
            {
                Log.d(TAG, "BroadCast + RSSI");

            }
        }
    };


    private static IntentFilter buildGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BLEService.ACTION_GATT_RSSI);
        return intentFilter;
    }

}