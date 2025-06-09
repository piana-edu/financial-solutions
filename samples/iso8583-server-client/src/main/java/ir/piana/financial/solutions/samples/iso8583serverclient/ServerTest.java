package ir.piana.financial.solutions.samples.iso8583serverclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    /*public static void main(String[] args) throws IOException {
        NatsConfig natsConfig = YamlConfigUtility.load("nats.conf.yaml", NatsConfig.class);
        NatsMsgBrokerService natsMsgBrokerService = new NatsMsgBrokerService(natsConfig);
        IsoServerConfig isoServerConfig = YamlConfigUtility.load("iso-server-raw.conf.yaml", IsoServerConfig.class);
        Future<?> future = IsoServerCreator.start(isoServerConfig, natsMsgBrokerService);

        natsMsgBrokerService.subscribeOn("server.response.in", "t1", "group1", (MessageHandler) natsMsg -> {
            ISOMsg isoMsg = new ISOMsg();
            try {
                isoMsg.unpack(natsMsg.getData());
//                isoMsg.setValue(natsMsg.getData());
                isoMsg.setMTI("0210");
                isoMsg.set(39, "00");
                isoMsg.pack();
                natsMsg.getConnection().publish(natsMsg.getReplyTo(), isoMsg.pack());
            } catch (ISOException e) {
                throw new RuntimeException(e);
            }
        });

        *//*ASCIIChannel channel = new ASCIIChannel(
                isoServerConfig.getHost(),
                isoServerConfig.getPort(),
                new ISO87APackager());*//*
        BinaryChannel channel = new BinaryChannel(new ISO87BPackager());
        channel.setHost(isoServerConfig.getHost());
        channel.setPort(isoServerConfig.getPort());
//        RawChannel channel = new RawChannel(isoServerConfig.getHost(), isoServerConfig.getPort(), new ISO87BPackager(), new byte[] {0, 0, 0, 0});
//        RawChannel channel = new RawChannel(isoServerConfig.getHost(), isoServerConfig.getPort(), new RawPackager(), new byte[] {0, 0, 0, 0});

        try {
            // Connect to server
            channel.connect();

            // Create a new ISO message
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setMTI("0200");  // Financial request
            isoMsg.set(3, "14"); // Processing code
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
            future.get();
        } catch (IOException | ISOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }*/

    private static int port = 6001;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port + "...");

            // Server runs forever
            while (true) {
                try {
                    // Wait for a client to connect
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + "clientSocket.getInetAddress()");

                    // Handle client in a separate thread (optional for concurrency)
                    handleClient(clientSocket);

                } catch (IOException e) {
                    System.out.println("Error accepting connection: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Could not start server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            out.println("Hello from the server!");
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                out.println("Echo: " + inputLine);
                if ("bye".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
