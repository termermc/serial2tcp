package net.termer.serial2tcp;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * The TCP connection handler thread
 * @since 1.0.0
 * @author termer
 */
public class TCPThread extends Thread {
    public static Socket currentSocket = null;

    /**
     * Returns whether the current socket exists and is open
     * @return Whether the current socket exists and is open
     * @since 1.0.0
     */
    public static boolean isSockOpen() {
        return currentSocket != null && !currentSocket.isClosed();
    }

    /**
     * Closes the current socket
     * @throws IOException If closing the socket fails
     * @since 1.0.0
     */
    public static void closeSock() throws IOException {
        if(isSockOpen()) {
            currentSocket.close();
            currentSocket = null;
        }
    }

    public TCPThread() {
        // Set thread name
        setName("TCPThread");
    }

    @Override
    public void run() {
        final Config conf = Config.instance;
        final SerialPort serial = SerialThread.serialPort;

        try {
            // Start TCP server
            final ServerSocket server = new ServerSocket(conf.tcpPort, 1, InetAddress.getByName(conf.tcpHost));
            System.out.println("Listening on "+conf.tcpHost+":"+conf.tcpPort);

            // Open serial port if waitForConnection is false
            if(!conf.waitForConnection) {
                SerialThread.openAndListen();
            }

            while(true) {
                currentSocket = server.accept();
                SerialThread.openAndListen();

                int b;
                try {
                    while (isSockOpen()) {
                        if ((b = currentSocket.getInputStream().read()) < 0) {
                            closeSock();
                            break;
                        } else if (serial.isOpen()) {
                            SerialThread.serialPort.getOutputStream().write(b);
                        }
                    }
                } catch(SocketException ignored) {
                    closeSock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
