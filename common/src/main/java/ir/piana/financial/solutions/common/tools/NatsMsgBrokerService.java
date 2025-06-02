package ir.piana.financial.solutions.common.tools;

import io.nats.client.*;
import io.nats.client.api.StreamConfiguration;
import ir.piana.financial.solutions.common.config.NatsConfig;
import ir.piana.financial.solutions.common.utilities.NatsUtility;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class NatsMsgBrokerService {
    private final Connection connection;
    private final JetStream jetStream;
    private final JetStreamManagement jetStreamManagement;
    private final Map<String, JetStreamSubscription> subscriptionMap = new LinkedHashMap<>();

    public NatsMsgBrokerService(NatsConfig natsConfig) {
        this.connection = NatsUtility.createConnection(natsConfig);
        this.jetStream = NatsUtility.createJetStream(connection);
        this.jetStreamManagement = NatsUtility.createJetStreamManagement(connection);
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
