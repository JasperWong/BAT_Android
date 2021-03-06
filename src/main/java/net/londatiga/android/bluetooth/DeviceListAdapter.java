package net.londatiga.android.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


public class DeviceListAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mData;
    private OnConnectButtonClickListener mListener;

    public DeviceListAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<BluetoothDevice> data)
    {
        mData = data;
    }

    public int getCount()
    {
        return (mData == null) ? 0 : mData.size();
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_item_device, null);

            holder = new ViewHolder();
            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.addressTv = (TextView) convertView.findViewById(R.id.tv_address);
            holder.connectBtn = (Button) convertView.findViewById(R.id.btn_connect);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        BluetoothDevice device = mData.get(position);
        holder.nameTv.setText(device.getName());
        holder.addressTv.setText(device.getAddress());
        // util.BLE 4.0 is not necessary to pair ,so we comment it - 2015.10 - MJ
        // holder.connectBtn.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Connect" : "Pair");
        holder.connectBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    BluetoothDevice device = mData.get(position);
                    mListener.onConnectButtonClick(device);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder
    {
        TextView nameTv;
        TextView addressTv;
        TextView connectBtn;
    }

    public void setListener(OnConnectButtonClickListener listener)
    {
        mListener = listener;
    }

    public interface OnConnectButtonClickListener
    {
        public abstract void onConnectButtonClick(BluetoothDevice device);
    }

    public void add(BluetoothDevice device)
    {
        if( null != mData)
        {
            if(mData.contains(device)==false)
            {
                mData.add(device);
            }
            notifyDataSetChanged();
        }
    }
}