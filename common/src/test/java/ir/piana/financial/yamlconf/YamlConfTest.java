package ir.piana.financial.yamlconf;

import ir.piana.financial.solutions.common.utilities.YamlConfigUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class YamlConfTest {
    @Test
    void testYamlConf() {
        AppConfig load = YamlConfigUtility.load("config.yaml", AppConfig.class);
        Assertions.assertNotNull(load);
    }
}
