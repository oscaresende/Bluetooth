package com.example.luisoscar.bluetooth;

import android.Manifest;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    Switch switchteste;
    ListView lv;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchteste = (Switch) findViewById(R.id.switch1);
        lv = (ListView) findViewById(R.id.listView);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Que pena! Hardware Bluetooth não está funcionando :(", Toast.LENGTH_SHORT).show();
        } else {

            if (bluetoothAdapter.isEnabled()) {

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);

                switchteste.setChecked(true);
                ListarDispositivos();
            }
        }

        switchteste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchteste.isChecked()) {

                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            //startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                    }

                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter);

                    ListarDispositivos();

                        //Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
                        //startActivity(discoverableIntent);


                } else {
                    OcultarDispositivos(); }
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        /*  Este método é executado sempre que um novo dispositivo for descoberto.
         */
        public void onReceive(Context context, Intent intent) {

            /*  Obtem o Intent que gerou a ação.
                Verifica se a ação corresponde à descoberta de um novo dispositivo.
                Obtem um objeto que representa o dispositivo Bluetooth descoberto.
                Exibe seu nome e endereço na lista.
             */
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    protected void onDestroy() {

        super.onDestroy();

        /*  Remove o filtro de descoberta de dispositivos do registro.
         */
        unregisterReceiver(receiver);
    }

    public void ListarDispositivos()
    {
        this.arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        /*  Pede permissao de localizaçao ao usuario.
         *   Necessario para API > 22 */

        /*  Usa o adaptador Bluetooth padrão para iniciar o processo de descoberta.
         */
        //BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        this.bluetoothAdapter.startDiscovery();

        /*  Cria um filtro que captura o momento em que um dispositivo é descoberto.
        Registra o filtro e define um receptor para o evento de descoberta.
        */

        this.lv.setAdapter(arrayAdapter);

    }

    public void OcultarDispositivos()
    {
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
        this.bluetoothAdapter.disable();
        this.arrayAdapter.clear();
        this.lv.setAdapter(this.arrayAdapter);
        //unregisterReceiver(receiver);
        //receiver.clearAbortBroadcast();
        Toast.makeText(getApplicationContext(), "Blootooth Desativado", Toast.LENGTH_SHORT).show();
    }



}

