import 'package:flutter/material.dart';
import 'package:store_redirect/store_redirect.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Store Redirect Example')),
        body: Center(
          child: ElevatedButton(
            onPressed: () {
              StoreRedirect.redirect(
                androidAppId: 'com.iyaffle.rangoli',
                iOSAppId: '585027354',
              );
            },
            child: const Text('Redirect App'),
          ),
        ),
      ),
    );
  }
}
