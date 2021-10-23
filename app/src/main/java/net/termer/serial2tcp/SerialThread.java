package net.termer.serial2tcp;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class SerialThread extends Thread {
    public static final SerialPort serialPort = SerialPort.getCommPort(Config.instance.serialPort);

    private static boolean threadOpen = false;

    public static void open() {
        serialPort.setBaudRate(Config.instance.serialBaud);
        if(!serialPort.openPort()) {
            System.err.println("Failed to open serial port");
            System.exit(1);
        }
        System.out.println("Opened serial port");
    }

    public static void openAndListen() {
        if(!serialPort.isOpen()) {
            // Set baud rate and try to open
            open();

            // Start thread if not already started
            if(!threadOpen)
                new SerialThread().start();
        }
    }

    public SerialThread() {
        // Set thread name
        setName("SerialThread");
    }

    @Override
    public void run() {
        threadOpen = true;

        while(true) {
            try {
                final InputStream in = serialPort.getInputStream();
                while(true) {
                    // Read one byte at a time, since this is going to be slow anyway
                    while(in.available() < 1);
                    final int b = in.read();

                    // Grab current socket
                    final Socket sock = TCPThread.currentSocket;

                    if (b < 0 && TCPThread.isSockOpen()) {
                        // There was an error, disconnect the socket if it exists
                        TCPThread.closeSock();
                    } else {
                        // Write byte to TCP client if connected
                        if (TCPThread.isSockOpen()) {
                            try {
                                System.out.print((char) b);
                                sock.getOutputStream().write(b);
                            } catch (SocketException ignore) {}
                        }
                    }
                }
            } catch(IOException e) {
                System.err.println("Error occurred in SerialThread loop:");
                e.printStackTrace();
            }
        }
    }
}
