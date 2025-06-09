package ir.piana.financial;

import ir.piana.financial.solutions.common.config.NatsConfig;
import ir.piana.financial.solutions.common.tools.NatsMsgBrokerService;
import ir.piana.financial.solutions.common.utilities.YamlConfigUtility;
import ir.piana.financial.solutions.isoserver.IsoServerCreator;
import ir.piana.financial.solutions.isoserver.config.IsoServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IsoServerTest {
    @Test
    void test() {
        NatsConfig natsConfig = YamlConfigUtility.load("nats.conf.yaml", NatsConfig.class);
        NatsMsgBrokerService natsMsgBrokerService = new NatsMsgBrokerService(natsConfig);
        IsoServerConfig isoServerConfig = YamlConfigUtility.load("iso-server.conf.yaml", IsoServerConfig.class);
        Future<?> start = IsoServerCreator.start(isoServerConfig, natsMsgBrokerService);
        Assertions.assertNotNull(start);
    }

    @Test
    void testRaw() throws ExecutionException, InterruptedException, TimeoutException {
        NatsConfig natsConfig = YamlConfigUtility.load("nats.conf.yaml", NatsConfig.class);
        NatsMsgBrokerService natsMsgBrokerService = new NatsMsgBrokerService(natsConfig);
        IsoServerConfig isoServerConfig = YamlConfigUtility.load("iso-server-raw.conf.yaml", IsoServerConfig.class);
        Future<?> future = IsoServerCreator.start(isoServerConfig, natsMsgBrokerService);


        Object object = future.get(30, TimeUnit.SECONDS);
    }
}
