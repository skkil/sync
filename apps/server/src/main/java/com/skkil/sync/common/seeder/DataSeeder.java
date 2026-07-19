package com.skkil.sync.common.seeder;

import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
class DataSeeder implements ApplicationRunner {

  private final ObjectMapper objectMapper;
  private final TagSeeder tagSeeder;

  @Value("classpath:/seed/global-tags.json")
  private Resource tags;

  DataSeeder(ObjectMapper objectMapper, TagSeeder tagSeeder) {
    this.objectMapper = objectMapper;
    this.tagSeeder = tagSeeder;
  }

  @Override
  public void run(ApplicationArguments args) throws IOException {
    if (!tagSeeder.hasGlobalTags()) {
      log.info("Seeding global tags");

      Arrays.stream(objectMapper.readValue(tags.getInputStream(), TagSeeder.TagSeed[].class))
          .forEach(tag -> tagSeeder.seed(tag));

      log.info("Finished seeding global tags");
    } else {
      log.info("Global tags already exist, skipping seeding");
    }
  }
}
