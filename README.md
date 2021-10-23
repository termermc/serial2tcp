# serial2tcp
A simple serial port to TCP server bridge

# Purpose
To allow TCP applications to communicate with applications running over a serial port, such as Arduino boards.

# Build
On Windows, run `gradlew.bat build`. On Linux, run `./gradlew build`.

The built jar can be found at `app/build/libs/app-all.jar`.

# Usage
Run `java -jar app-all.jar` to start the server. If you do not have a `config.json` file in the same directory, it will be created.

The config.json file will look like this:

```json
{
    "tcpPort": "8080",
    "tcpHost": "0.0.0.0",
    "waitForConnect": false,
    "serialPort": "COM4",
    "serialBaud": 9600
}
```

`tcpPort` defines which port the TCP server will run on

`tcpHost` defines which host the TCP server will bind to

`waitForConnect` defines whether the server will wait until there is a TCP connection before opening the serial port

`serialPort` defines the logical descriptor of the serial port to use (if you're using Arduino, you can it in the IDE under Tools > Port)

`serialBaud` defines the baud rate to use for communicating with the device connected to the serial port (higher is faster, but device and server need to match, and higher requires a shorter wire)