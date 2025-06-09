package ir.piana.financial.solutions.serverclientraw;

import ir.piana.financial.solutions.common.config.NatsConfig;
import ir.piana.financial.solutions.common.tools.NatsMsgBrokerService;
import ir.piana.financial.solutions.common.utilities.YamlConfigUtility;
import ir.piana.financial.solutions.serverclientraw.config.ServerSocketConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;

public class ServerSocketTest {
    @Test
    void test() {
        NatsConfig natsConfig = YamlConfigUtility.load("nats.conf.yaml", NatsConfig.class);
        NatsMsgBrokerService natsMsgBrokerService = new NatsMsgBrokerService(natsConfig);
        ServerSocketConfig serverSocketConfig = YamlConfigUtility.load("server-socket.conf.yaml", ServerSocketConfig.class);
        Future<?> start = ServerSocketCreator.start(serverSocketConfig, natsMsgBrokerService);
        Assertions.assertNotNull(start);
    }
}
