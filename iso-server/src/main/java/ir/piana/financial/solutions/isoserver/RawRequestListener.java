package ir.piana.financial.solutions.isoserver;

import ir.piana.financial.solutions.common.tools.NatsMsgBrokerService;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class RawRequestListener implements ISORequestListener {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, CompletableFuture<byte[]>> pending = new ConcurrentHashMap<>();
    private final NatsMsgBrokerService natsMsgBrokerService;

    public RawRequestListener(NatsMsgBrokerService natsMsgBrokerService) {
        this.natsMsgBrokerService = natsMsgBrokerService;
    }

    @Override
    public boolean process(ISOSource source, ISOMsg isoMsg) {
        executor.submit(() -> {
            try {
                byte[] raw = (byte[]) isoMsg.getValue();  // Raw message

                String correlationId = UUID.randomUUID().toString();
                CompletableFuture<byte[]> future = new CompletableFuture<>();
                pending.put(correlationId, future);

                sendToBroker(correlationId, raw);  // Send raw data to broker

                byte[] response = future.get(30, TimeUnit.SECONDS);

                ISOMsg reply = new ISOMsg();
                reply.setValue(response); // Just store raw bytes
                source.send(reply);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    public void onBrokerResponse(String correlationId, byte[] response) {
        CompletableFuture<byte[]> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }

    private byte[] sendToBroker(String correlationId, byte[] raw) throws Exception {
        // Implement your Kafka or other broker logic here
        // Include correlationId for tracking

        return natsMsgBrokerService.requestReply("financial.msg",
                raw, Duration.ofSeconds(30));
    }
}
