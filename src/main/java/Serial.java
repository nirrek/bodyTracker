import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Instances of Serial are a higher-level wrapper of a SerialPort. They abstract
 * connection logic, as well as being able to consume/send messages rather
 * than bytes.
 */
public class Serial {
    // Current number of Serial objects instantiated. Used to create the
    // owner name for the particular serial connection.
    private static int serialCount = 0;

    // Number of milliseconds to wait to obtain port ownership
    private static final int MAX_CONNECT_WAIT = 2000;

    // The character set used to interpret inbound stream data.
    private static final String CHARSET = "US-ASCII";

    // Connection status of the Serial
    private boolean isConnected = false;

    // The underlying SerialPort
    private SerialPort serialPort;

    // The sentinel character used to indicate a message boundary
    private String messageBoundary = "$";

    // The Serial's inbound stream.
    private BufferedReader in;

    // Generates a new name for a given serial instance.
    private static String generateName() {
        return "Serial" + serialCount++;
    }

    /**
     * Connect to the the given port using the specified communication speed.
     * If the connection is successful this.isConnected() returns true, false
     * otherwise.
     * @param portName The name of the port to connect to
     * @param baudRate Communicate rate in symbols per second (ie. BAUD rate)
     */
    public void connect(String portName, int baudRate) {
        // Open a serial connection
        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
            if (portId.isCurrentlyOwned()) return;

            serialPort = (SerialPort) portId.open(generateName(), MAX_CONNECT_WAIT);
            serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (NoSuchPortException|PortInUseException|UnsupportedCommOperationException e) {
            e.printStackTrace();
        }

        // Setup the communication streams
        try {
            in = new BufferedReader(
                    new InputStreamReader(serialPort.getInputStream(), CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }

        isConnected = true;
    }

    /**
     * Closes the serial port connection.
     */
    public void close() {
        serialPort.close();
    }

    /**
     * Returns the next message received over the Serial. If there is not
     * currently a complete message received, this call will block until
     * it receives one.
     * @throws IOException If reading the inputstream fails. If the device
     *                     is removed, this will be thrown.
     * @return The next message received. If any.
     */
    public String getNextMessage() throws IOException {
        StringBuilder message = new StringBuilder();

        // TODO double check the EOS return value
        String line = in.readLine();
        while ((line != null)) {
            if (line.equals(messageBoundary)) break;
            message.append(line)
                   .append("\n"); // readLine() strips newline.
            line = in.readLine();
        }

        if (message.length() == 0) return null;
        return message.toString();
    }

    /**
     * Sends a message over the serial
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        // Implement if we want to control the Arduino remotely
    }

    /**
     * Sets the message boundary string, which is used to detect the end of a
     * message. The message boundary must exist on a newline by itself.
     * @param messageBoundary The message boundary string.
     */
    public void setMessageBoundary(String messageBoundary) {
        this.messageBoundary = messageBoundary;
    }

    /**
     * Check if the Serial is connected.
     * @return True of the Serial is connected, false otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }
}
