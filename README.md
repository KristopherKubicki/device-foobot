# Foobot Device Type
This is a device type for the Foobot air quality monitor integration to SmartThings.  

<img src='https://cloud.githubusercontent.com/assets/478212/10330763/641cb914-6c95-11e5-9196-2c0e187ac426.jpg'>

This is a rough cut of the excellent <a href='http://www.amazon.com/Foobot-Indoor-Air-Quality-Monitor/dp/B00XI32QYE'>Airboxlab Foobot indoor air quality sensor</a> (previously known as Alima).  SmartThings does not have capabilities for the Foobot inputs, with the exceptions of the Humidity and Temperature sensor.  For now, these are denoted as attributes so developers can create their own SmartApps using these variables (CO2, VOC, Gloabl Pollution Index and P2.5 particulate count).  

Please keep in mind the Foobot only samples the environment every 15 minutes or so.  In addition, the device type interacts with the Foobot API via a poll() call.  This means that results may be very delayed (more than an hour?) before your Hub sees the results.  I recommend using Pollster or another polling SmartApp to reduce the latency between SmartThings and Foobot.

<img src='https://cloud.githubusercontent.com/assets/478212/10330990/404531fe-6c97-11e5-93d8-d36b17f7a954.png'>

##Installation

1. Create a new device type (https://graph.api.smartthings.com/ide/devices)
  * Paste this groovy code

2. Create a new device (https://graph.api.smartthings.com/device/list)
 
3. Update device preferences
    * Click on the new device to see the details.
    * Click the edit button next to Preferences
    * Fill in the Username and Password from your Foobot account
    * Add the 16 digit serial number of your device.  *It should be the same as the hostname of your Foobot + "0"*.  Your router should be able to assist you in getting the hostname. 

##License 
Copyright (c) 2015, Kristopher Kubicki
All rights reserved.
