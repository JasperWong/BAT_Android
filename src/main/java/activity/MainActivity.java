package activity;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;
import net.londatiga.android.bluetooth.DeviceListAdapter;
import net.londatiga.android.bluetooth.R;
import java.util.ArrayList;

import util.BLE.BLEScanHelper;
import util.communication.BluetoothRfcommClient;

/**
 * Main activity.
 *
 * @author MJ
 */
public class MainActivity extends Activity
{

    public static final String EXTRA_DEVICE_OBJ = "EXTRA_DEVICE_OBJ";

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    public static BluetoothRfcommClient mRfcommClient = null;

    private BLEScanHelper mScanHelper;

    private ListView mListView;

    private DeviceListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.action_bar_title);
        actionBar.setDisplayShowCustomEnabled(true);


        //
        mListView = (ListView) findViewById(R.id.lv_paired);
        mAdapter = new DeviceListAdapter(this);
        mAdapter.setData(mDeviceList);
        mListView.setAdapter(mAdapter);
        mAdapter.setListener(onConnectListener);


        //
        mScanHelper = new BLEScanHelper(this);
        mScanHelper.initialize();
        mScanHelper.setOnScanDeviceListener(mOnScanDeviceListener);

    }


    //
    DeviceListAdapter.OnConnectButtonClickListener onConnectListener = new DeviceListAdapter.OnConnectButtonClickListener()
    {
        @Override
        public void onConnectButtonClick(BluetoothDevice device)
        {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            intent.putExtra(EXTRA_DEVICE_OBJ,device);
            startActivity( intent );
        }
    };


    BLEScanHelper.onScanDeviceListener mOnScanDeviceListener = new BLEScanHelper.onScanDeviceListener()
    {
        @Override
        public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            mAdapter.add(device);
        }
    };


    @Override
    protected void onResume()
    {
        mDeviceList.clear();

        mScanHelper.startScan();

        super.onResume();
    }


    @Override
    public void onPause()
    {
        mScanHelper.stopScan();

        super.onPause();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}