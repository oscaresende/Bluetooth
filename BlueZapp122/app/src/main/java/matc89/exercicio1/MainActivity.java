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

public class MainActivity extends AppCompatActivity {

    Switch switch1;
    ListView listView;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapter;
    TextView textView;
    ProgressBar progbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = (Switch) findViewById(R.id.switch1);
        listView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        textView = (TextView)findViewById(R.id.textView);
        progbar = (ProgressBar) findViewById(R.id.progressBar);

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
                textView.setText("Dispositivos Disponíveis");
                progbar.setVisibility(View.VISIBLE);

                listarDispositivos(this);
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
                    textView.setText("Dispositivos Disponíveis");
                    progbar.setVisibility(View.VISIBLE);

                    while (bluetoothAdapter.isDiscovering() == false) {
                        listarDispositivos(MainActivity.this);
                    }
                }
                else
                    {
                        switch1.setText("Desativado");
                        textView.setText("");
                        progbar.setVisibility(View.INVISIBLE);
                        arrayAdapter.clear();
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
