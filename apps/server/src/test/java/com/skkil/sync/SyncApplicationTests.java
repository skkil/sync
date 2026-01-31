package com.skkil.sync;

import com.skkil.sync.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfig.class)
@SpringBootTest
@ActiveProfiles("dev")
class SyncApplicationTests {

  @Test
  void contextLoads() {}
}
