package matc89.exercicio1;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Switch switch1;
    ListView listView;
    ListView listView2;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter2;
    TextView textView;
    TextView textView2;
    TextView textView3;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = (Switch) findViewById(R.id.switch1);
        listView = (ListView)findViewById(R.id.listView);
        listView2 = (ListView)findViewById(R.id.listView2);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Que pena! Hardware Bluetooth não está funcionando :(", Toast.LENGTH_SHORT).show();
        } else {

            if (bluetoothAdapter.isEnabled()) {

                switch1.setChecked(true);
                switch1.setText("Ativado");
                textView.setText("Dispositivos Pareados -----------------------------------------");
                textView3.setText("Dispositivos Disponíveis ----------------------------");
                progressBar.setVisibility(View.VISIBLE);

                textView2.setText("");

                listarDispositivos(this);
            }
            else
            {
                textView.setText("");
                textView3.setText("");
                textView2.setText("Com o Bluetooth ativado, o dispositivo pode se comunicar.");
            }
        }

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch1.isChecked()) {

                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    }

                    switch1.setText("Ativado");
                    textView.setText("Dispositivos Pareados -----------------------------------------");
                    textView3.setText("Dispositivos Disponíveis ----------------------------");
                    textView2.setText("");
                    progressBar.setVisibility(View.VISIBLE);

                    while (bluetoothAdapter.isDiscovering() == false) {
                        listarDispositivos(MainActivity.this);
                    }
                }
                else
                    {
                        switch1.setText("Desativado");
                        textView.setText("");
                        textView3.setText("");
                        textView2.setText("Com o Bluetooth ativado, o dispositivo pode se comunicar.");
                        progressBar.setVisibility(View.INVISIBLE);
                        arrayAdapter.clear();
                        arrayAdapter2.clear();
                        if (bluetoothAdapter.isEnabled())
                            bluetoothAdapter.disable();
                    }
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    public void listarDispositivos(MainActivity view)
    {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView2.setAdapter(arrayAdapter2);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                arrayAdapter2.add(device.getName() + "\n" + device.getAddress());
            }
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(receiver, filter);

        bluetoothAdapter.cancelDiscovery();

        bluetoothAdapter.startDiscovery();

        listView.setAdapter(arrayAdapter);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
