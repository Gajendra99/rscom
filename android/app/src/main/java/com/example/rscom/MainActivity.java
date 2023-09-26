//package com.example.rscom;
//
//import android.os.Bundle;
//import android.app.PendingIntent;
//import androidx.fragment.app.FragmentManager;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.annotation.NonNull;
//import io.flutter.embedding.android.FlutterActivity;
//import io.flutter.embedding.engine.FlutterEngine;
//import io.flutter.plugin.common.MethodCall;
//import io.flutter.plugin.common.MethodChannel;
//import java.util.Collection;
//import android.widget.Toast;
//import android.hardware.usb.UsbDeviceConnection;
//import java.io.IOException;
//import com.hoho.android.usbserial.driver.UsbSerialDriver;
//import com.hoho.android.usbserial.driver.UsbSerialPort;
//import com.hoho.android.usbserial.driver.UsbSerialProber;
//import java.util.List;
//import gnu.io.CommPort;
//import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;
//import java.io.InputStream;
//import java.io.OutputStream;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbManager;
//
//public class MainActivity extends FlutterActivity {
//    private AppCompatActivity appCompatActivity;
//
//    private static final String ACTION_USB_PERMISSION = "com.example.rscom.USB_PERMISSION";
//
//    private UsbManager usbManager;
//    private PendingIntent permissionIntent;
//    private static final String CHANNEL_NAME = "acchhuUsbCom";
//    private UsbDevice usbDevice;
//    private UsbDeviceConnection usbConnection;
//    private UsbSerialDriver usbSerialDriver;
//    private UsbSerialPort usbSerialPort;
//    private BroadcastReceiver usbPermissionReceiver;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    String initializePort(){
//        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        final boolean[] canAccess = {false};
//        if(usbManager != null){
//            usbPermissionReceiver = new BroadcastReceiver() {
//                public void onReceive(Context context, Intent intent) {
//                    String action = intent.getAction();
//                    if (ACTION_USB_PERMISSION.equals(action)) {
//                        synchronized (this) {
//                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                                canAccess[0] = true;
//                            } else {
//                                canAccess[0] = false;
//                            }
//                        }
//                    }
//                }
//            };
//            if(canAccess[0]){
//                return "USB Permission Denied!";
//            }
//            String retValue = getUsbPermission();
//
//            return retValue;
//        }
//        else{
//            return "Connectivity issue";
//        }
//    }
//
//    String getUsbPermission(){
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(usbPermissionReceiver, filter);
//        try{
//            Collection<UsbDevice> usbDeviceList = usbManager.getDeviceList().values();
//            if (!usbDeviceList.isEmpty()) {
//                usbDevice = usbDeviceList.iterator().next();
//                if (usbDevice != null) {
//                    permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
//                    usbManager.requestPermission(usbDevice, permissionIntent);
//
//                    // Request permission for the USB device (this will trigger the BroadcastReceiver)
//                    if (usbDevice != null) {
//                        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
//                        usbManager.requestPermission(usbDevice, permissionIntent);
//                        return "USB Device Found";
//                    } else {
//                        return "No USB Device Found!";
//                    }
//                }else{
//                    return  "Not a Serial Device";
//                }
//            }else{
//                return  "No Device Connected";
//            }
//        }catch (Exception e) {
//            return "Connection Error";
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    private boolean open() {
//        try {
//            // Open the connection to the USB serial device
//            UsbDeviceConnection connection = usbManager.openDevice(usbSerialDriver.getDevice());
//            if (connection != null) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//
//        }
//    }
//
//    private void getPortsList(){
//        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        usbPermissionReceiver = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (ACTION_USB_PERMISSION.equals(action)) {
//                    synchronized (this) {
//                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                            // Permission granted, you can now open and communicate with the USB device.
//                            // Perform USB operations here.
//                            sendDataOverUsb();
//                        } else {
//                            // Permission denied.
//                            // Handle denial here.
//                        }
//                    }
//                }
//            }
//        };
//
//        // List available serial ports and show in a toast message
//
//    }
//
//    private void requestUsbPermission() {
//        // Create a PendingIntent with FLAG_IMMUTABLE
//        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
//
//        // Request permission for the USB device
//        usbManager.requestPermission(usbDevice, permissionIntent);
//    }
//
//    private boolean isUsbPermissionGranted() {
//        return usbManager.hasPermission(usbDevice);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(usbPermissionReceiver);
//    }
//
//    private void sendDataOverUsb() {
//        try {
//            // Example: Write data to the USB serial port
//            byte[] dataToSend = "Hello, USB!".getBytes();
//            usbSerialPort.write(dataToSend, 1000); // Timeout in milliseconds
//
//            // Example: Read data from the USB serial port
//            byte[] receivedData = new byte[1024];
//            int bytesRead = usbSerialPort.read(receivedData, 1000); // Timeout in milliseconds
//            // Process the received data
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void openUsbSerialPort() {
//        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//
//        boolean portStatus = getPort();
//        if(!portStatus){
//            android.widget.Toast.makeText(MainActivity.this, "Port issue", Toast.LENGTH_SHORT).show();
//        }
//        if (usbSerialDriver != null) {
//            runOnUiThread(() -> {
//                Toast.makeText(MainActivity.this, "Driver found " + usbManager, Toast.LENGTH_LONG).show();
//            });
//            UsbDeviceConnection connection = usbManager.openDevice(usbSerialDriver.getDevice());
//
//            if (connection != null) {
//                if (usbSerialPort != null) {
//                    try {
//                        usbSerialPort.open(connection);
//                        usbSerialPort.setParameters(1200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//                        byte[] hexCommand = {0x53, 0x0D};
//
//                        try {
//                            usbSerialPort.write(hexCommand, 1000); // Timeout in milliseconds
//
//                            // Example: Read data from the USB serial port
//                            byte[] receivedData = new byte[1024];
//                            int bytesRead = usbSerialPort.read(receivedData, 1000); // Timeout in milliseconds
//
//                            if (bytesRead > 0) {
//                                // Process the received data
//                                String receivedDataString = new String(receivedData, 0, bytesRead);
//
//                                // Show received data in a toast message
//                                runOnUiThread(() -> {
//                                    Toast.makeText(MainActivity.this, "Received Data: " + receivedDataString, Toast.LENGTH_LONG).show();
//                                });
//                            }
//                        } catch (IOException e) {
//                            runOnUiThread(() -> {
//                                Toast.makeText(MainActivity.this, "Cannot write command " + e, Toast.LENGTH_LONG).show();
//                            });
//                            e.printStackTrace();
//                        }
//                    } catch (IOException e) {
//                        runOnUiThread(() -> {
//                            Toast.makeText(MainActivity.this, "Port not connected : " + e, Toast.LENGTH_LONG).show();
//                        });
//                        e.printStackTrace();
//                    }
//                } else {
//                    runOnUiThread(() -> {
//                        Toast.makeText(MainActivity.this, "Connection not found!", Toast.LENGTH_LONG).show();
//                    });
//                }
//            } else {
//                runOnUiThread(() -> {
//                    Toast.makeText(MainActivity.this, "Connection not found!"+connection, Toast.LENGTH_LONG).show();
//                });
//            }
//        }else{
//            runOnUiThread(() -> {
//                Toast.makeText(MainActivity.this, "Driver not found", Toast.LENGTH_LONG).show();
//            });
//        }
//    }
//
//
//    private boolean getPort() {
//        StringBuilder portList = new StringBuilder("Available Serial Ports:\n");
//
//        // Get a list of available USB serial drivers
//        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
//
//        if (availableDrivers.isEmpty()) {
//            return false;
//        }
//
//        try {
//            usbSerialDriver = availableDrivers.get(0);
//            UsbDeviceConnection connection = usbManager.openDevice(usbSerialDriver.getDevice());
//            if (connection != null) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//
//    }
//
//    void setDTR() {
//        try {
//            usbSerialPort.setDTR(true);
//        } catch (IOException e) {
//            // Handle the IOException here, e.g., log the error or show a message to the user
//            e.printStackTrace();
//        }
//    }
//
//    void setRTS() {
//        try {
//            usbSerialPort.setRTS(true);
//        } catch (IOException e) {
//            // Handle the IOException here, e.g., log the error or show a message to the user
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
//        super.configureFlutterEngine(flutterEngine);
//
//        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_NAME).setMethodCallHandler(
//                (call, result) -> {
//                    if (call.method.equals("create")) {
//                        String portRet = initializePort();
//                        result.success(portRet);
//                    }
//                    if(call.method.equals("open")){
//                        boolean retVal = open();
//
//                        result.success(retVal);
//                    }
//                    if(call.method.equals("close")){}
//                    if(call.method.equals("setDTR")){
//                        setDTR();
//                    }
//                    if(call.method.equals("setRTS")){
//                        setRTS();
//                    }
//                    if(call.method.equals("write")){
//                        openUsbSerialPort();
//                    }
//                    if(call.method.equals("setPortParameters")){}
//                    if(call.method.equals("getPorts")){
//                        boolean port = getPort();
//                        result.success(port);
//                    }
//                }
//        );
//    }
//}


package com.example.rscom;
import android.os.Bundle;
import android.app.PendingIntent;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.util.Collection;
import android.widget.Toast;
import android.hardware.usb.UsbDeviceConnection;
import java.io.IOException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import java.util.List;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public class MainActivity extends FlutterActivity {
    private AppCompatActivity appCompatActivity;

    private static final String ACTION_USB_PERMISSION = "com.example.rscom.USB_PERMISSION";

    private UsbManager usbManager;
    private PendingIntent permissionIntent;
    private static final String CHANNEL_NAME = "acchhuUsbCom";
    private UsbDevice usbDevice;
    private UsbDeviceConnection usbConnection;
    private UsbSerialDriver usbSerialDriver;
    private UsbSerialPort usbSerialPort;
    private BroadcastReceiver usbPermissionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        verifyConnection();

    }

    void verifyConnection(){
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if(usbManager != null){
            usbPermissionReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        synchronized (this) {
                            if(!isUsbPermissionGranted()){
                                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                    // Permission granted, you can now open and communicate with the USB device.
                                    Toast.makeText(MainActivity.this, "Can access USB", Toast.LENGTH_LONG).show();

                                } else {
                                    // Permission denied.
                                    // Handle denial here.
                                    requestUsbPermission();
                                    Toast.makeText(MainActivity.this, "USB Permission Denied!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
            };

            getUsbPermission();
        }
        else{

            // Handle exceptions here
            Toast.makeText(MainActivity.this, "Usb manager not initialized ", Toast.LENGTH_LONG).show();


        }
    }

    void getUsbPermission(){
        // Register the receiver for USB permission
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbPermissionReceiver, filter);
        try{
            Collection<UsbDevice> usbDeviceList = usbManager.getDeviceList().values();
            if (!usbDeviceList.isEmpty()) {
                usbDevice = usbDeviceList.iterator().next();
                if (usbDevice != null) {
                    permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                    usbManager.requestPermission(usbDevice, permissionIntent);


                    // Request permission for the USB device (this will trigger the BroadcastReceiver)
                    if (usbDevice != null) {
                        Toast.makeText(MainActivity.this, "USB Device is not null", Toast.LENGTH_LONG).show();
                        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                        usbManager.requestPermission(usbDevice, permissionIntent);
                    } else {
                        Toast.makeText(MainActivity.this, "Usb Devices : " + usbDevice, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }catch (Exception e) {
            // Handle exceptions here
            Toast.makeText(MainActivity.this, "Connection Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void openUsbConnection() {
        try {
            // Open the connection to the USB serial device
            usbConnection = usbManager.openDevice(usbSerialDriver.getDevice());
            if (usbConnection != null) {
                Toast.makeText(MainActivity.this, "Connection made successfully! " + usbConnection, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Connection not made!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void getPortsList(){
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbPermissionReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            // Permission granted, you can now open and communicate with the USB device.
                            // Perform USB operations here.
                            sendDataOverUsb();
                        } else {
                            // Permission denied.
                            // Handle denial here.
                        }
                    }
                }
            }
        };

        // List available serial ports and show in a toast message
        String serialPorts = listSerialPorts();
        Toast.makeText(MainActivity.this, serialPorts, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//
//        // Check if USB permission is already granted
//        if (isUsbPermissionGranted()) {
//            openUsbConnection();
//        } else {
//            requestUsbPermission();
//        }
    }

    private void requestUsbPermission() {
        if(!isUsbPermissionGranted()){
            // Create a PendingIntent with FLAG_IMMUTABLE
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

            // Request permission for the USB device
            usbManager.requestPermission(usbDevice, permissionIntent);
        }
    }

    private boolean isUsbPermissionGranted() {
        return usbManager.hasPermission(usbDevice);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(usbPermissionReceiver);
    }

    private void sendDataOverUsb() {
        try {
            // Example: Write data to the USB serial port
            byte[] dataToSend = "Hello, USB!".getBytes();
            usbSerialPort.write(dataToSend, 1000); // Timeout in milliseconds

            // Example: Read data from the USB serial port
            byte[] receivedData = new byte[1024];
            int bytesRead = usbSerialPort.read(receivedData, 1000); // Timeout in milliseconds
            // Process the received data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openUsbSerialPort() {
        if (usbSerialDriver != null) {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Driver found " + usbSerialDriver, Toast.LENGTH_LONG).show();
            });
            UsbDeviceConnection connection = usbManager.openDevice(usbSerialDriver.getDevice());

            if (connection != null) {
                if (usbSerialPort != null) {
                    try {
                        usbSerialPort.open(connection);
                        usbSerialPort.setParameters(1200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                        byte[] hexCommand = {0x53, 0x0D};

                        try {
                            int data = 10;
                            String msgRet = "";

                            usbSerialPort.write(hexCommand, 0); // Timeout in milliseconds

                            // Example: Read data from the USB serial port
                            byte[] receivedData = new byte[1024];
                            while (data>0){
                                int bytesRead = usbSerialPort.read(receivedData, 1000); // Timeout in milliseconds

                                if (bytesRead > 0) {
                                    // Process the received data
                                    String receivedDataString = new String(receivedData, 0, bytesRead);
                                    msgRet = msgRet+""+ receivedDataString;
                                    // Show received data in a toast message
//                                    Toast.makeText(MainActivity.this, "Received Data: " + receivedDataString, Toast.LENGTH_SHORT).show();
                                }
                                data = bytesRead;
                            }
                            Toast.makeText(MainActivity.this, "Output " + msgRet, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Cannot write command " + e, Toast.LENGTH_LONG).show();
                            });
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Port not connected : " + e, Toast.LENGTH_LONG).show();
                        });
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Connection not found!", Toast.LENGTH_LONG).show();
                    });
                }
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Connection not found!"+connection, Toast.LENGTH_LONG).show();
                });
            }
        }else{
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Driver not found", Toast.LENGTH_LONG).show();
            });
        }
    }
    private String listSerialPorts() {
        StringBuilder portList = new StringBuilder("Available Serial Ports:\n");

        // Get a list of available USB serial drivers
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);


        if (availableDrivers.isEmpty()) {
            return "No USB serial devices found.";
        }

        try {
//            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            usbSerialDriver = availableDrivers.get(0);
            UsbDeviceConnection connection = usbManager.openDevice(usbSerialDriver.getDevice());
            if (connection != null) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Connection made successfully! " + connection, Toast.LENGTH_LONG).show();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Connection not made!", Toast.LENGTH_LONG).show();
                });
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
            e.printStackTrace();
        }


        for (UsbSerialDriver driver : availableDrivers) {

            List<UsbSerialPort> ports = driver.getPorts();
            for (UsbSerialPort port : ports) {
                usbSerialPort = port;
                portList.append(port.getDriver().getDevice());
            }
        }

        return portList.toString();
    }

    void getPorts(){
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if(usbManager != null){
            usbPermissionReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        synchronized (this) {
                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                Toast.makeText(MainActivity.this, "Can access USB", Toast.LENGTH_LONG).show();
                            } else {
                                requestUsbPermission();
                            }
                        }
                    }
                }
            };

            getUsbPermission();
        }
        else{
            Toast.makeText(MainActivity.this, "Usb manager not initialized ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_NAME).setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("showToast")) {
                        verifyConnection();
                        openUsbConnection();
                        listSerialPorts();
                        openUsbSerialPort();

                        result.success("No Device found"); // Indicate that the method call was successful
                    }

                    if(call.method.equals("getPorts")){
                        getPorts();
                        result.success("Device Found");
                    }
                }
        );
    }
}