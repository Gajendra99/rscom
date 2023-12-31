import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:rscom/usbport.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  UsbPort port = UsbPort("acchhuUsbCom");
  @override
  Widget build(BuildContext context) {
    var retValue = "nothing to display".obs;

    listDevices() async {}

    create() async {
      String portStatus = await port.create();
      print("Port Connection Status : $portStatus");
    }

    showToast() async {
      await port.showToast();
    }

    open() async {
      bool portval = await port.open();
      print("Port OPen status : $port");
    }

    setDTR() async {
      port.setDTR(true);
    }

    setRTS() async {
      await port.setRTS(true);
    }

    write(String data) async {
      List<int> intList =
          data.split(' ').map((String str) => int.parse(str.trim())).toList();
      String result = await port.write(Uint8List.fromList(intList));
      print(
          "==================================================================");
      print("Return Data = $result");
      print("=========================================================");
    }

    connectPort() async {}
    TextEditingController txtController = TextEditingController();

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
                onPressed: () => showToast(), child: Text("Show Toast")),
            ElevatedButton(onPressed: () => create(), child: Text("Create")),
            ElevatedButton(onPressed: () => open(), child: Text("Open")),
            Container(
              decoration: BoxDecoration(
                border: Border.all(
                  color: Colors.black, // Border color
                  width: 1.0, // Border width
                ),
                borderRadius:
                    BorderRadius.circular(5.0), // Optional: Add rounded corners
              ),
              child: Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: 8.0), // Optional: Add padding
                child: TextField(
                  controller: txtController,
                  decoration: InputDecoration(
                    border: InputBorder.none, // Hide the default border
                    hintText: 'Enter text here',
                  ),
                ),
              ),
            ),
            ElevatedButton(
                onPressed: () => write(txtController.text),
                child: Text("Write")),
          ],
        ),
      ),
    );
  }
}
