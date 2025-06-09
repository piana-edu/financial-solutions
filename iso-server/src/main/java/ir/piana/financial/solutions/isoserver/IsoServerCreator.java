package ir.piana.financial.solutions.isoserver;

import ir.piana.financial.solutions.common.tools.NatsMsgBrokerService;
import ir.piana.financial.solutions.isoserver.channels.BinaryChannel;
import ir.piana.financial.solutions.isoserver.config.IsoServerConfig;
import org.jpos.iso.*;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.channel.RawChannel;
import org.jpos.iso.packager.ISO87APackager;
import org.jpos.iso.packager.ISO87BPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public static Runnable create(IsoServerConfig isoServerConfig, NatsMsgBrokerService msgBrokerService) {
        try {
            if (isoServerConfig.getIsoPackagerQualifiedClassName().equalsIgnoreCase("RAW")) {
                /*ServerChannel channel = switch (isoServerConfig.getChannelType()) {
                    case ASCII -> new ASCIIChannel(new ISO87BPackager());
                    case BINARY -> new BinaryChannel(new ISO87BPackager());
                };*/
                ServerSocket serverSocket = new ServerSocket(isoServerConfig.getPort());
                SocketRequestListener socketRequestListener = new SocketRequestListener(msgBrokerService);

                return () -> {
                    while (true) {
                        try {
                            Socket accept = serverSocket.accept();
                            Executors.newVirtualThreadPerTaskExecutor().execute(() -> {
                                socketRequestListener.process(accept);
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

            } else {
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
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 InstantiationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Future<?> start(IsoServerConfig isoServerConfig) {
        ISOServer isoServer = create(isoServerConfig);
        return Executors.newSingleThreadExecutor().submit(isoServer);
//        new Thread(isoServer).start();
//        return isoServer;
    }

    public static Future<?> start(IsoServerConfig isoServerConfig, NatsMsgBrokerService msgBrokerService) {
        Runnable isoServer = create(isoServerConfig, msgBrokerService);
        //        new Thread(isoServer).start();
        return Executors.newSingleThreadExecutor().submit(isoServer);
    }
}
