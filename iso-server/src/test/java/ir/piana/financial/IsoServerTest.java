package ir.piana.financial;

import ir.piana.financial.solutions.common.utilities.YamlConfigUtility;
import ir.piana.financial.solutions.isoserver.IsoServerCreator;
import ir.piana.financial.solutions.isoserver.config.IsoServerConfig;
import org.jpos.iso.ISOServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IsoServerTest {
    @Test
    void test() {
        IsoServerConfig isoServerConfig = YamlConfigUtility.load("iso-server.conf.yaml", IsoServerConfig.class);
        ISOServer isoServer = IsoServerCreator.start(isoServerConfig);
        Assertions.assertNotNull(isoServer);
    }
}
