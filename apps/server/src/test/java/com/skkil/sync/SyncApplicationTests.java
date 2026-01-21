package com.skkil.sync;

import com.skkil.sync.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
class SyncApplicationTests {

  @Test
  void contextLoads() {}
}
