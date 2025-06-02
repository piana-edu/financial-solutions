package ir.piana.financial.solutions.common.utilities;

import io.nats.client.*;
import ir.piana.financial.solutions.common.config.NatsConfig;

import java.io.IOException;

public class NatsUtility {
    public static Connection createConnection(NatsConfig natsConfig) {
        try {
            Options options = Options.builder()
                    .server("nats://localhost:4222")
                    .build();
            return Nats.connect(options);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static JetStream createJetStream(NatsConfig natsConfig) {
        try {
            Connection connection = createConnection(natsConfig);
            return connection.jetStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JetStream createJetStream(Connection connection) {
        try {
            return connection.jetStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JetStreamManagement createJetStreamManagement(NatsConfig natsConfig) {
        try {
            Connection connection = createConnection(natsConfig);
            return connection.jetStreamManagement();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JetStreamManagement createJetStreamManagement(Connection connection) {
        try {
            return connection.jetStreamManagement();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
