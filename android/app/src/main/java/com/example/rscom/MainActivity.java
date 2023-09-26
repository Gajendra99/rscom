
package com.example.rscom;
import android.os.Bundle;
import android.app.PendingIntent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    String verifyConnection(){
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
//                                    Toast.makeText(MainActivity.this, "Can access USB", Toast.LENGTH_LONG).show();

                                } else {
                                    // Permission denied.
                                    // Handle denial here.
                                    requestUsbPermission();
//                                    Toast.makeText(MainActivity.this, "USB Permission Denied!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
            };

            String conStatus = getUsbPermission();
            return conStatus;
        }else{
            return "Connection Error";
        }
    }

    String getUsbPermission(){
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
                        return "Connected Successfully";
                    } else {
                        return "Connection Error";
                    }
                }
            }
            return "Connection Error";
        }catch (Exception e) {
            return "Connection Error";
        }
    }

    private String openUsbConnection() {
        try {
            // Open the connection to the USB serial device
            usbConnection = usbManager.openDevice(usbSerialDriver.getDevice());
            if (usbConnection != null) {
                return "Connected Successfully";
            } else {
                return "Connected Successfully";
            }
        } catch (Exception e) {
            return "Connection Error " + e;
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

    boolean open(){
        if (usbSerialDriver != null) {
            usbConnection = usbManager.openDevice(usbSerialDriver.getDevice());

            if (usbConnection != null) {
                if (usbSerialPort != null) {
                    try {
                        usbSerialPort.open(usbConnection);
                        return true;
                    }catch (IOException e) {
                            return false;
                    }
                }else{
                    return  false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    private void openUsbSerialPort() {
        if (usbSerialDriver != null) {
            usbConnection = usbManager.openDevice(usbSerialDriver.getDevice());

            if (usbConnection != null) {
                if (usbSerialPort != null) {
                    try {
                        usbSerialPort.open(usbConnection);
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
                    Toast.makeText(MainActivity.this, "Connection not found!"+usbConnection, Toast.LENGTH_LONG).show();
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
            usbConnection = usbManager.openDevice(usbSerialDriver.getDevice());
            if (usbConnection != null) {
                for (UsbSerialDriver driver : availableDrivers) {

                    List<UsbSerialPort> ports = driver.getPorts();
                    for (UsbSerialPort port : ports) {
                        usbSerialPort = port;
                    }
                }
                return "Connected Successfully";
            } else {
                return "Connection Error";
            }
        } catch (Exception e) {
            return "Connection Error!" +e;
        }
    }

    private String write(byte[] hexCommand ) {

            if (usbConnection != null) {
                if (usbSerialPort != null) {
                    try {
                        usbSerialPort.setParameters(1200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                            int data = 10;
                            String msgRet = "";

                            usbSerialPort.write(hexCommand, 10); // Timeout in milliseconds

                            // Example: Read data from the USB serial port
                            byte[] receivedData = new byte[8192];
                            int x = 10;
                            String outputData = "";
                            while (x>0){

                                int bytesRead = usbSerialPort.read(receivedData, 1000); // Timeout in milliseconds
                                String receivedDataString = new String(receivedData, 0, bytesRead);

                                if (bytesRead > 0) {
                                    outputData = outputData + ""+ receivedDataString;
                                }
                                x = bytesRead;
                            }

                        Toast.makeText(MainActivity.this, outputData , Toast.LENGTH_LONG).show();
                            return outputData;


                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Connection Error 3" + e, Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Connection Error 2", Toast.LENGTH_LONG).show();
                    return "Error";
                }
            }
        Toast.makeText(MainActivity.this, "Connection Error 1", Toast.LENGTH_LONG).show();
        return "Error";
    }

    String create(){
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if(usbManager != null){
            usbPermissionReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        synchronized (this) {
                            if(!isUsbPermissionGranted()){
                                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                    Toast.makeText(MainActivity.this, "Can access USB", Toast.LENGTH_LONG).show();
                                } else {
                                    requestUsbPermission();
                                }
                            }
                        }
                    }
                }
            };

            return getUsbPermission();
        }
        else{
            return "Connection Error";
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

                    if(call.method.equals("create")){
                        String conStatus = verifyConnection();
                        Toast.makeText(MainActivity.this, "Verify Connection : "+conStatus, Toast.LENGTH_LONG).show();
                        if(conStatus == "Connected Successfully"){
                            conStatus = listSerialPorts();
                            Toast.makeText(MainActivity.this, "ListSerialPorts : "+conStatus, Toast.LENGTH_LONG).show();
                            if (conStatus == "Connected Successfully") {
                            conStatus = openUsbConnection();
                                Toast.makeText(MainActivity.this, "OpenUsbConnection : "+conStatus, Toast.LENGTH_LONG).show();
                                if (conStatus == "Connected Successfully") {
                                 result.success("Connected Successfully");
                                }else{
                                    result.success("Connection Error");
                                }
                            }else{
                                result.success("Not a Serial Device");
                            }
                        }else{
                            result.success("Check Device Connectivity!");
                        }

                        result.success(conStatus);
                    }

                    if(call.method.equals("open")){
                        boolean openStatus = open();
                        Toast.makeText(MainActivity.this, "Open Status : "+openStatus, Toast.LENGTH_LONG).show();
                        result.success(openStatus);
                    }

                    if(call.method.equals("write")){
                        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                        long delayMilliseconds = 1000;

                        byte[] byteData = call.argument("data");
                        String retData = write(byteData);

                        executor.schedule(() -> {
                            // Code to execute after the delay
                            result.success(retData);
                        }, delayMilliseconds, TimeUnit.MILLISECONDS);

                        // Don't forget to shut down the executor when you're done with it
                        executor.shutdown();
                    }
                }
        );
    }
}