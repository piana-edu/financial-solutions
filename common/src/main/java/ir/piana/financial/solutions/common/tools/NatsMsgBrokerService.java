package ir.piana.financial.solutions.common.tools;

import io.nats.client.*;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.impl.NatsMessage;
import ir.piana.financial.solutions.common.config.NatsConfig;
import ir.piana.financial.solutions.common.utilities.NatsUtility;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NatsMsgBrokerService {
    private final Connection connection;
    private final JetStream jetStream;
    private final JetStreamManagement jetStreamManagement;
    private final Map<String, JetStreamSubscription> subscriptionMap = new LinkedHashMap<>();

    public NatsMsgBrokerService(NatsConfig natsConfig) {
        this.connection = NatsUtility.createConnection(natsConfig);
        this.jetStream = NatsUtility.createJetStream(connection);
        this.jetStreamManagement = NatsUtility.createJetStreamManagement(connection);
        if (natsConfig.getJetStreams() != null) {
            natsConfig.getJetStreams().forEach(jetStream -> addStream(StreamConfiguration.builder()
                    .name(jetStream.getName())
                    .subjects(jetStream.getSubjects())
                    .storageType(switch (jetStream.getStorageType().toLowerCase()) {
                        case "memory" -> StorageType.Memory;
                        case "file" -> StorageType.File;
                        default -> StorageType.Memory;
                    }).build()));
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public JetStream getJetStream() {
        return jetStream;
    }

    public NatsMsgBrokerService addStream(StreamConfiguration streamConfig) {
        try {
            jetStreamManagement.addStream(streamConfig);
            return this;
        } catch (IOException | JetStreamApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void publish(String subject, byte[] payload) {
        try {
            jetStream.publish(subject, payload);
        } catch (IOException | JetStreamApiException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] requestReply(String subject, byte[] data, Duration timeout) {
        try {
            String inbox = connection.createInbox();
            CompletableFuture<Message> future = new CompletableFuture<>();

            Dispatcher dispatcher = connection.createDispatcher(future::complete);

            dispatcher.subscribe(inbox);
            dispatcher.unsubscribe(inbox, 1); // auto-unsubscribe after first message

            NatsMessage msg = NatsMessage.builder()
                    .subject(subject)
                    .replyTo(inbox)
                    .data(data)
                    .build();

            jetStream.publish(msg);
            Message response = future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            return response.getData();
        } catch (IOException | JetStreamApiException | InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribeOn(String subject, String durable, String group, MessageHandler handler) {
        try {
            PushSubscribeOptions durableOptions = PushSubscribeOptions.builder()
                    .durable(durable)                // 🧷 Persists consumer state
                    .deliverGroup(group)
                    .build();

            Dispatcher d = connection.createDispatcher();

            JetStreamSubscription subscribe = jetStream.subscribe(subject, d, handler, true, durableOptions);
            subscriptionMap.put(durable, subscribe);
        } catch (IOException | JetStreamApiException e) {
            throw new RuntimeException(e);
        }
    }
}
