package ir.piana.financial.solutions.isoclient;

import org.jpos.iso.*;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.ISO87APackager;

import java.io.IOException;

public class Jpos3AsciiClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8000;

        // Create ASCIIChannel with ISO87APackager
        ASCIIChannel channel = new ASCIIChannel(host, port, new ISO87APackager());

        try {
            // Connect to server
            channel.connect();

            // Create a new ISO message
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setMTI("0100");  // Financial request
            isoMsg.set(3, "000000"); // Processing code
            isoMsg.set(4, "1000");   // Amount: 10.00
            isoMsg.set(7, "110722180"); // Transmission date & time (MMDDhhmmss)
            isoMsg.set(11, "123456");  // STAN
            isoMsg.set(41, "12345678"); // Terminal ID

            System.out.println("Sending ISO message: " + isoMsg);

            // Send ISO message and wait for response
            channel.send(isoMsg);
            ISOMsg receive = channel.receive();

            System.out.println("Received response: " + receive);

            // Disconnect after use
            channel.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ISOException e) {
            throw new RuntimeException(e);
        }
    }
}
