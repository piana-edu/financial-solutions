package ir.piana.financial.solutions.serverclientraw;

import ir.piana.financial.solutions.common.tools.NatsMsgBrokerService;
import ir.piana.financial.solutions.serverclientraw.config.ServerSocketConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerSocketCreator {
    private static final Logger log = LoggerFactory.getLogger(ServerSocketCreator.class);

    public static ServerSocket create(ServerSocketConfig serverSocketConfig) {
        try {
            InetAddress bindAddress = InetAddress.getByName(serverSocketConfig.getHost());
            ServerSocket serverSocket = new ServerSocket(
                    serverSocketConfig.getPort(), serverSocketConfig.getBacklog(), bindAddress);
            return serverSocket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Future<?> start(ServerSocketConfig serverSocketConfig, NatsMsgBrokerService msgBrokerService) {
        ServerSocket serverSocket = create(serverSocketConfig);
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        return Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Socket acceptedClientSocket = serverSocket.accept();
                Future<?> submit = executorService.submit(() -> {
                    try (PianaServerSocket pianaServerSocket = PianaServerSocket.initializeSocket(
                            acceptedClientSocket,
                            serverSocketConfig.getDestinationSubject(), serverSocketConfig.isBigEndian(),
                            serverSocketConfig.getReadTimeoutMillis())) {
                        pianaServerSocket.handleSocketCommunication(msgBrokerService);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                submit.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static byte[] readOneRequest() {
        return null;
    }

    static void writeOneResponse(byte[] response) {

    }
}
