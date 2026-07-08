package com.skkil.sync;

import com.skkil.sync.common.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Import(TestcontainersConfig.class)
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = "app.seed.enabled=false")
class SyncApplicationTests {

  @Test
  void contextLoads() {}
}
