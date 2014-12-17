pfSenseWOL
==========

WOL Android Tasker plugin

This purpose of this app is to allow users to wake a device on a different interface than the device sending the request.
The Android app uses the pfSense web interface to send a WOL packet to a device on any interface.

For example: An Android device on the WAN/VPN/WiFi network can send a magic packet to a device on the LAN.

Known issues: 
* Login information is stored in plain text in the Tasker bundle
* Allowing untrusted certificates may not actually work
* Only tested/working on pfSense 2.1.5-RELEASE
