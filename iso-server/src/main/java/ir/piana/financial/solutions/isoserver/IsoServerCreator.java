package ir.piana.financial.solutions.isoserver;

import ir.piana.financial.solutions.isoserver.channels.BinaryChannel;
import ir.piana.financial.solutions.isoserver.config.IsoServerConfig;
import org.jpos.iso.*;
import org.jpos.iso.channel.ASCIIChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class IsoServerCreator {
    private static final Logger log = LoggerFactory.getLogger(IsoServerCreator.class);

    public static ISOServer create(IsoServerConfig isoServerConfig) {
        try {
            Class<?> aClass = Class.forName(
                    isoServerConfig.getIsoPackagerQualifiedClassName());
            ISOPackager isoPackager = (ISOPackager) aClass.getDeclaredConstructor().newInstance();
            ServerChannel channel = switch (isoServerConfig.getChannelType()) {
                case ASCII -> new ASCIIChannel(isoPackager);
                case BINARY -> new BinaryChannel(isoPackager);
            };
            ISOServer isoServer = new ISOServer(isoServerConfig.getPort(), channel, isoServerConfig.getMaxSessionCount());
            isoServer.addISORequestListener(new ISORequestListener() {
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
                        log.error(e.getMessage(), e);
                        return false;
                    }
                }
            });
            return isoServer;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 InstantiationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ISOServer start(IsoServerConfig isoServerConfig) {
        ISOServer isoServer = create(isoServerConfig);
        new Thread(isoServer).start();
        return isoServer;
    }
}
