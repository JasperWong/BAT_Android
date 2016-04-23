package util.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by Jeff on 10/8/15.
 */
public class BLEScanHelper
{
    private final static String TAG = BLEService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;

    private BluetoothAdapter mBluetoothAdapter;

    private Context mContext;

    public BLEScanHelper(Context context)
    {
        mContext = context;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize()
    {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null)
            {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }


        //
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public boolean startScan()
    {
        if(null == mBluetoothAdapter)
        {
            return false;
        }

        return mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public boolean stopScan()
    {
        if(null == mBluetoothAdapter)
        {
            return false;
        }

        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        return true;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord)
        {
            //
            if(null!= mOnScanDeviceListener)
            {
                mOnScanDeviceListener.onScanDevice(device,rssi,scanRecord);
            }
        }
    };

    private onScanDeviceListener mOnScanDeviceListener;

    public void setOnScanDeviceListener(onScanDeviceListener l)
    {
        mOnScanDeviceListener = l;
    }

    public interface onScanDeviceListener
    {
        public void onScanDevice(final BluetoothDevice device, final int rssi, byte[] scanRecord);
    }
}
