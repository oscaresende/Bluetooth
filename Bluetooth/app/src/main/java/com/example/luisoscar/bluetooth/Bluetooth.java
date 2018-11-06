package com.example.luisoscar.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bluetooth extends BroadcastReceiver {
    private BluetoothListener listener;
    private ArrayList lista;
    private BluetoothAdapter dispositivo;

    private List<BluetoothDevice> listaDispostivo = new ArrayList<>();

    private Bluetooth(BluetoothListener listener) {
        this.listener = listener;
        lista = new ArrayList();
    }

    public static Bluetooth startFindDevices(Context context, BluetoothListener listener) throws IOException {
        Bluetooth bluetooth = new Bluetooth(listener);

        //Pego o adapter
        bluetooth.dispositivo = BluetoothAdapter.getDefaultAdapter();

        // verifico se o bluetooth esta desabilitado, se tiver habilito
        //retorno null (tratar)
        if (!bluetooth.dispositivo.isEnabled()) {
            bluetooth.dispositivo.enable();
            return null;
        }

        // Registro os Broadcast necessarios para a busca de dispositivos
        IntentFilter filter = new IntentFilter(BluetoothListener.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothListener.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter3 = new IntentFilter(BluetoothListener.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(bluetooth, filter);
        context.registerReceiver(bluetooth, filter2);
        context.registerReceiver(bluetooth, filter3);

        //inicio a busca e retorno a classe instanciada.
        bluetooth.dispositivo.startDiscovery();
        return bluetooth;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //caso encontre um dispositivo:
        if (action.compareTo(BluetoothDevice.ACTION_FOUND) == 0 ) {
            //pega as o dispositivo encontrado:
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            //se a lista já tiver esse dispositivo eu retorno para o proximo
            //isso permite que sejá mostrado somente uma vez meu dispositivo
            //problema muito comum em exemplos
            if (lista.contains(device)) {
                return;
            }

            //adiciono o dispositivo na minha lista:
            lista.add(device);

        } else if (action.compareTo(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) == 0) {
            //caso o discovery chegue ao fim eu desregistro meus broadcasts
            //SEMPRE FAÇA ISSO quando terminar de usar um broadcast
            context.unregisterReceiver(this);
        }

        if (listener != null) {
            //se foi definido o listener eu aviso a quem ta gerenciando.
            listener.action(action);
        }
    }

    public List<BluetoothDevice> getListaDispostivo() {
        return lista;
    }

    public static interface BluetoothListener {
        public static final String ACTION_DISCOVERY_STARTED = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
        public static final String ACTION_FOUND = BluetoothDevice.ACTION_FOUND;
        public static final String ACTION_DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
        public void action(String action);
    }
}


