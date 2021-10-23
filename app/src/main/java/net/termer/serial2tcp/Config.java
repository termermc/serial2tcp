package net.termer.serial2tcp;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Class that contains application configuration
 * @since 1.0.0
 * @author termer
 */
public class Config {
    /**
     * The configuration instance
     * @since 1.0.0
     */
    public static final Config instance = loadOrCreate();

    private static Config loadOrCreate() {
        final File confFile = new File("config.json");

        // Create file if it doesn't exist
        if(!confFile.exists()) {
            final JSONObject json = new JSONObject();
            json.put("serialPort", "COM4");
            json.put("serialBaud", 9600);
            json.put("tcpHost", "0.0.0.0");
            json.put("tcpPort", "9600");
            json.put("waitForConnect", false);

            try {
                Files.write(confFile.toPath(), json.toString(4).getBytes(), StandardOpenOption.CREATE);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        // Read and return config
        try {
            final JSONObject json = new JSONObject(
                    new JSONTokener(
                            new String(
                                    Files.readAllBytes(
                                            confFile.toPath()
                                    )
                            )
                    )
            );

            return new Config(
                    json.getString("serialPort"),
                    json.getInt("serialBaud"),
                    json.getString("tcpHost"),
                    json.getInt("tcpPort"),
                    json.getBoolean("waitForConnect")
            );
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public final String serialPort;
    public final int serialBaud;
    public final String tcpHost;
    public final int tcpPort;
    public final boolean waitForConnection;

    private Config(
            String serialPort,
            int serialBaud,
            String tcpHost,
            int tcpPort,
            boolean waitForConnection
    ) {
        this.serialPort = serialPort;
        this.serialBaud = serialBaud;
        this.tcpHost = tcpHost;
        this.tcpPort = tcpPort;
        this.waitForConnection = waitForConnection;
    }
}
