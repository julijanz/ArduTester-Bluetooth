# ArduTester-Bluetooth
 ArduTester for Arduino Nano and HC-06
ArduTester is a device for identifying electronic components such as: transistor, diode, resistor, capacitor, etc... You can buy similar testers on AliExpress very cheaply.

If you have an Arduino Nano and a HC-05 or HC-06 bluetooth module, you can make an ArduTester yourself, even if you don't have a suitable display. Instead of a display, you can use a mobile phone or tablet with Android operating system.

There are several different examples of ArduTester code posted on the internet. I used the code based on the code of: Karl-Heinz Kubbeler (version 1.08k). I got the best results with this code, but I had to tweak the code a bit.

**Instructions**

1. On the Arduino Nano, you need to remove the resistor on pin 13. If pin 13 is connected to ground via a resistor and a diode, the ArduTester measurements are incorrect.

2. Open ArduTester104005.ino in Arduino IDE. Then upload the Sketch to the Arduino Nano when it is not connected to the HC-05.

3. After uploading the sketch, you can connect the HC-05 and other components to the Arduino Nano or insert it on your development board or circuit board.

4. The baud rate must be set to 9600 (factory default) on the HC-05.

5. Install ArduTester 1.04.005.apk on your mobile phone or tablet. The program with which you will install ArduTester 1.04.005.apk (File Manager, etc...) must have permission to install programs from unknown sources.

6. Turn on bluetooth on your phone and pair the HC-05 module.

7. The first time you run ArduTester on your phone, enable all permissions. Then connect the HC-05. Now you can test the electronic components.

8. Calibrate the ArduTester: Connect all three probe pins together and press the button. ArduTester will print "Selftest mode..." When it prints "isolate Probe!" remove the pin connections. When it says " >100nF!", connect a capacitor larger than 100nF (I use 1uF) to pin 1 and pin 3. When the calibration is finished, it will say "Test End".

9. That's it. **Enjoy**.
