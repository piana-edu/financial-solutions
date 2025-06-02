package ir.piana.financial.solutions.isoserver;

import org.jpos.iso.*;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.ISO87APackager;

import java.io.IOException;

public class Jpos3AsciiServer {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        // Create ASCIIChannel with ISO87APackager
        ASCIIChannel channel = new ASCIIChannel(new ISO87APackager());

        // Create server
        ISOServer server = new ISOServer(port, channel, 100);

        // Add listener to process incoming messages
        server.addISORequestListener(new ISORequestListener() {
            @Override
            public boolean process(ISOSource source, ISOMsg m) {
                try {
                    System.out.println("Received: " + m);

                    // Create response message
                    ISOMsg response = (ISOMsg) m.clone();
                    response.setMTI("0110");        // Response MTI
                    response.set(39, "00");          // Response code = Approved

                    source.send(response);
                    System.out.println("Sent: " + response);
                    return true;
                } catch (ISOException | IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });

        System.out.println("Starting jPOS 3.0.0 ASCII ISO-8583 server on port " + port);
        new Thread(server).start();
    }
}
