package ir.piana.financial.solutions.isoserver;

import ir.piana.financial.solutions.common.tools.NatsMsgBrokerService;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class SocketRequestListener {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, CompletableFuture<byte[]>> pending = new ConcurrentHashMap<>();
    private final NatsMsgBrokerService natsMsgBrokerService;

    public SocketRequestListener(NatsMsgBrokerService natsMsgBrokerService) {
        this.natsMsgBrokerService = natsMsgBrokerService;
    }

    public boolean process(Socket socket) {
        System.out.println();
        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            byte[] len = new byte[2];
            in.read(len, 0, 2);

            int lens = len[0] & 0x00FF << 8 | len[1] & 0xFF - 2;

            byte[] body = new byte[lens];
            in.read(body, 2, lens);
            String correlationId = UUID.randomUUID().toString();
            CompletableFuture<byte[]> future = new CompletableFuture<>();
            pending.put(correlationId, future);

            sendToBroker(correlationId, body);  // Send raw data to broker

            byte[] response = future.get(30, TimeUnit.SECONDS);
            out.write(response);
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public void onBrokerResponse(String correlationId, byte[] response) {
        CompletableFuture<byte[]> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }

    private byte[] sendToBroker(String correlationId, byte[] raw) {
        // Implement your Kafka or other broker logic here
        // Include correlationId for tracking

        return natsMsgBrokerService.requestReply("financial.msg",
                raw, Duration.ofSeconds(30));
    }
}
