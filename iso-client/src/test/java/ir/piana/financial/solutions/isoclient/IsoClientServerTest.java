package ir.piana.financial.solutions.isoclient;

import ir.piana.financial.solutions.common.utilities.YamlConfigUtility;
import ir.piana.financial.solutions.isoserver.IsoServerCreator;
import ir.piana.financial.solutions.isoserver.config.IsoServerConfig;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOServer;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.ISO87APackager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class IsoClientServerTest {
    @Test
    void test() {
        IsoServerConfig isoServerConfig = YamlConfigUtility.load("iso-server.conf.yaml", IsoServerConfig.class);
        ISOServer isoServer = IsoServerCreator.start(isoServerConfig);
        Assertions.assertNotNull(isoServer);

        // Create ASCIIChannel with ISO87APackager
        ASCIIChannel channel = new ASCIIChannel(
                isoServerConfig.getHost(),
                isoServerConfig.getPort(),
                new ISO87APackager());
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
        } catch (IOException | ISOException e) {
            throw new RuntimeException(e);
        }
    }
}
