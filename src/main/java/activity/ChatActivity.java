package activity;
import net.londatiga.android.bluetooth.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import util.communication.BluetoothRfcommClient;


/*
import com.chat.bluetooth.R;
import com.chat.bluetooth.business.ChatBusinessLogic;
import com.chat.bluetooth.util.ToastUtil;
*/
/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * 05/10/2012 14:41:34
 */
public class ChatActivity extends Activity {
	
	public static int MSG_TOAST = 1;
	public static int MSG_BLUETOOTH = 2;
	public static int BT_TIMER_VISIBLE = 30; 
	
	private final int BT_ACTIVATE = 0;
	private final int BT_VISIBLE = 1;

	private Button buttonService;
	private Button buttonClient;
	private ImageButton buttonSend;
	private EditText editTextMessage;
	private ListView listVewHistoric;
	private ArrayAdapter<String> historic;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);

		settingsView();

        MainActivity.mRfcommClient.setHandler(handler);

		/*
		inicializaBluetooth();
		registerFilters();
		*/
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }


	public void settingsView() {
		editTextMessage = (EditText)findViewById(R.id.editTextMessage);
		
		historic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		listVewHistoric = (ListView)findViewById(R.id.listVewHistoric);
		listVewHistoric.setAdapter(historic);
		
		buttonSend = (ImageButton)findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = editTextMessage.getText().toString();

				if(message.trim().length() > 0)
                {
                    if(MainActivity.mRfcommClient.getState() == BluetoothRfcommClient.STATE_CONNECTED)
                    {
                        MainActivity.mRfcommClient.write(message.getBytes());

                        editTextMessage.setText("");

                        historic.add("I: " + message);
                        historic.notifyDataSetChanged();
                    }
				}else{
					//toastUtil.showToast(getString(R.string.enter_message));
				}

			}
		});
		
		buttonService = (Button)findViewById(R.id.buttonService);
		buttonService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BT_TIMER_VISIBLE);
				startActivityForResult(discoverableIntent, BT_VISIBLE);
			}
		});
		
		buttonClient = (Button)findViewById(R.id.buttonClient);
		buttonClient.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//chatBusinessLogic.startFoundDevices();
			}
		});
	}
	
	public void inicializaBluetooth() {
        /*
		if (chatBusinessLogic.getBluetoothManager().verifySuportedBluetooth()) {
			if (!chatBusinessLogic.getBluetoothManager().isEnabledBluetooth()) { 
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, BT_ACTIVATE);
			}
		} else {
			//toastUtil.showToast(getString(R.string.no_support_bluetooth));
			finish();
		}
		*/
	}
	
	public void registerFilters(){
		//chatBusinessLogic.registerFilter();
	}

	private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            synchronized (msg) {
                switch (msg.what) {

                	case 1:
                        int length = msg.arg1;
                        String s = new String((byte[])(msg.obj),0,length);
                        historic.add(s);
       				 	historic.notifyDataSetChanged();
       				 	listVewHistoric.requestFocus();
       				 	break;
                    case 2:
                        //toastUtil.showToast((String)(msg.obj));
                        break;
                }
            }
        };
    };
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case BT_ACTIVATE:
				if (RESULT_OK != resultCode) {
					//toastUtil.showToast(getString(R.string.activate_bluetooth_to_continue));
					finish(); 
				}
				break;

			case BT_VISIBLE:
				if (resultCode == BT_TIMER_VISIBLE) {
					
					//chatBusinessLogic.stopCommucanition();
					//chatBusinessLogic.startServer();
				} else {
					//toastUtil.showToast(getString(R.string.device_must_visible));
				}
				break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//chatBusinessLogic.unregisterFilter();
		//chatBusinessLogic.stopCommucanition();
	}

}