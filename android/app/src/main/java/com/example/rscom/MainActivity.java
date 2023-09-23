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
//        setContentView(R.layout.activity_serial_usb);
        getPortsList();
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
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbPermissionReceiver, filter);

        // Check if a USB device is connected and request permission.
        if (usbDevice != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(usbDevice, permissionIntent);
        }
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

    private String listSerialPorts() {
        StringBuilder portList = new StringBuilder("Available Serial Ports:\n");

        // Get a list of available USB serial drivers
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

        if (availableDrivers.isEmpty()) {
            return "No USB serial devices found.";
        }

        for (UsbSerialDriver driver : availableDrivers) {
            List<UsbSerialPort> ports = driver.getPorts();
            for (UsbSerialPort port : ports) {
                portList.append(port.getDriver().getDevice());
            }
        }

        return portList.toString();
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_NAME).setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("showToast")) {
//                        getPortsList();
                        String sPort = listSerialPorts();
                        Toast.makeText(MainActivity.this, sPort, Toast.LENGTH_LONG).show();
                        result.success(sPort); // Indicate that the method call was successful
                    }
                }
        );
    }
}
