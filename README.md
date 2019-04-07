# WhammychatScreenMirror
This is an Android App which uses native screen mirroring paradigm to stream a screen. 

## Instructions for integrating this functionality in your application
The folder screenAlike as a whole needs to be pasted.(rename according to your liking)

Containing files:

1. **Appdata.java**: Contains all the getter and setter functions

2. **Client.java**: Contains methods used to send data to the server

3. **ForgroundServiceHandler.java**: Contains cases for handling streaming.

4. **ImageGenerator.java**: Creates the images to be sent to the server.

5. **ImageDispatcher.java**: Sends the image which was created using ImageGenerator.java

6. **HttpServer.java**: Starts and handles the server.

7. **NotifyImageGenerator.java**:Setting screen for different scenarios.


**ScreenAlike.java**:  *Default file to be called when application starts. This would be your application_name.java file.*

*Every method needs to be copied and relevant lines from onCreate*

**MainActivity.java**:  *Every method needs to be copied and relevant lines from onCreate*

onPause() method detects every time control goes out of application.

The following line of code is used to Stop Streaming. Can be used according to your requirement:
```
mForegroundServiceTaskHandler.obtainMessage(ForegroundServiceHandler.HANDLER_STOP_STREAMING).sendToTarget()
```

The following line of code is used to Start Streaming. Can be used according to your requirement:
```
mForegroundServiceTaskHandler.obtainMessage(ForegroundServiceHandler.HANDLER_START_STREAMING).sendToTarget()
```

All the required code needs to be copied from res folder

**IMPORTANT!**

**index.html**  page is the page that is loaded with the streaming images to the hosted server.





