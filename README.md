# TV Alarm
Alarm Clock that uses infrared to turn on TV

Allows for the creation of alarms; set the alarm time and days.

When the alarm is due, the app will transmit the infrared signal to turn on the TV and then regularly transmits infrared signal to turn up the volume. 

This project has been developed using a Samsung S4 and a Samsung TV. The infrared codes might differ for other TV models. 
You can get frequencies for other models at http://www.remotecentral.com/cgi-bin/codes/samsung/tv_functions/ and use the convertToIrFrequency() method in class Util to convert it to the appropriate frequency.

Minimum SDK version is 19 due to the use of infrared. 
