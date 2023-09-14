package com.example.bluetoothapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int BT_AND_WIFI_PERMISSIONS = 124;
    private BluetoothServerSocket mmServerSocket;
    // private String MY_UUID = UUID.fromString("2cbaaf88-5195-11ee-be56-0242ac120002");
    private final String NAME = "DanielHandy";
    // step 1: create the objects
    Button bluetoothon, bluetoothoff, bluetooth_conntection, listOffoundDevices;
    TextView bluetoothActive, withDeviceConnected;
    ListView listofDevices;
    private ArrayList list = new ArrayList();
    private ArrayList list2 = new ArrayList();
    private final String TAG = this.getClass().getSimpleName();

    //step 2: declaring the constangs of Bluetooth Adapter class
    private static final int REQUEST_ENABLE_BLUETOOTH = 2;
    private BluetoothAdapter bluetoothAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForPermissions();

        bluetoothon = findViewById(R.id.bluetoothON);
        bluetoothoff = findViewById(R.id.bluetoothOFF);
        bluetooth_conntection = findViewById(R.id.BluetoothConntection);
        listOffoundDevices = findViewById(R.id.listOfDevices);
        listofDevices = findViewById(R.id.ListOfDevices);
        bluetoothActive = findViewById(R.id.active);
        withDeviceConnected = findViewById(R.id.conntect);

        //step 4: create the object of Bluetooth adapter class;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth not available");
        }


        //step 7: disable Bluetooth
        bluetoothoff.setOnClickListener(v -> {
            bluetoothAdapter.disable();
            list.clear();
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list2);
            listofDevices.setAdapter(adapter);
            bluetoothActive.setText("");
        });

    }

    public void turnOn(View view) {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Not supported for your device", Toast.LENGTH_SHORT).show();// Device doesn't support Bluetooth
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent myBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                Log.i(TAG, "turnOn: Missing");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Bluetooth conntect not granted");
                    return;
                }
                startActivityForResult(myBluetooth, REQUEST_ENABLE_BLUETOOTH);
                bluetoothActive.setText("active");
            }
        }

    }


    public void askForPermissions() {
        Log.d(TAG, "askForPermissions: checking permissions");
        List<String> permissions = new ArrayList<>();
        String message = "Demo App permissions missing:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "askForPermissions: missing fine location permission");
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            message += "\nLocation access";
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "askForPermissions: missing fine location permission");
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            message += "\nLocation access";
        }


        if (!permissions.isEmpty()) {
            Log.e(TAG, "askForPermissions: missing permissions, requesting");
            String[] params = permissions.toArray(new String[permissions.size()]);
            requestPermissions(params, BT_AND_WIFI_PERMISSIONS);
        } else {
            Log.d(TAG, "askForPermissions: has all permissions");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BT_AND_WIFI_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  Toast.makeText(this, "Thanks, permission granted, scotty start the engines", Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(this, "Well okay - but it wont work then ", Toast.LENGTH_LONG).show();
            }
        }
    }


    //----------- looking for devices with Bluetooth-Function --------//

    public void pairedBluetoothDevices(View view) {
        BluetoothDevice targetDevice = null; // Das ausgewählte Bluetooth-Gerät


        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please activate Bluetooth!", Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String nameOfDevice = device.getName();
                list.add(nameOfDevice);
                if(device.getName().equals("DANNY-PC")){
                    Log.i(TAG, "device name was found");
                    targetDevice = device;
                }
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
                listofDevices.setAdapter(adapter);
                startConnection(targetDevice);
            }
        }
    }

    public void startConnection(BluetoothDevice device){
        Log.i(TAG, "startConnection was opened");
        BluetoothSocket socket = null;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("c5c3f4ff-bedb-4275-8596-d67299c7ec99"));
            socket.connect();
            Log.i(TAG,"is connected: " + socket.isConnected());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    //----------- connection with an other device --------//

    /*
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            this.mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
        public void manageMyConnectedSocket(BluetoothSocket socket){

        }
    }
*/
}