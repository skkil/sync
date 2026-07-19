package com.skkil.sync.common.seeder;

import com.skkil.sync.common.util.text.Slugify;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
@Slf4j
class DevDataSeeder implements ApplicationRunner {

  private static final long RANDOM_SEED = 42L;
  private static final int USER_COUNT = 60;
  private static final int PROJECT_COUNT = 20;
  private static final int POST_COUNT = 80;
  private static final int LIKE_COUNT = 150;
  private static final int COMMENT_COUNT = 100;
  private static final int USER_FOLLOW_COUNT = 100;
  private static final int PROJECT_FOLLOW_COUNT = 60;

  private final UserRepository userRepository;
  private final UserSeeder userSeeder;
  private final ProjectSeeder projectSeeder;
  private final PostSeeder postSeeder;
  private final SocialGraphSeeder socialGraphSeeder;
  private final String testAccountEmail;
  private final String testAccountPassword;

  DevDataSeeder(
      UserRepository userRepository,
      UserSeeder userSeeder,
      ProjectSeeder projectSeeder,
      PostSeeder postSeeder,
      SocialGraphSeeder socialGraphSeeder,
      @Value("${app.seed.account.email}") String testAccountEmail,
      @Value("${app.seed.account.password}") String testAccountPassword) {
    this.userRepository = userRepository;
    this.userSeeder = userSeeder;
    this.projectSeeder = projectSeeder;
    this.postSeeder = postSeeder;
    this.socialGraphSeeder = socialGraphSeeder;
    this.testAccountEmail = testAccountEmail;
    this.testAccountPassword = testAccountPassword;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (!shouldRun()) {
      log.info("Data seeding skipped");
      return;
    }

    log.info("Seeding local dev data");

    Faker faker = new Faker(new Random(RANDOM_SEED));
    Random random = new Random(RANDOM_SEED);

    List<User> users = seedUsers(faker, random);
    Map<String, List<User>> projectMembers = seedProjects(faker, random, users);
    List<String> publicPostSlugs = seedPosts(faker, random, users, projectMembers);
    seedSocialGraph(
        faker, random, users, new ArrayList<>(projectMembers.keySet()), publicPostSlugs);

    log.info("Finished seeding local dev data");
  }

  private List<User> seedUsers(Faker faker, Random random) {
    List<User> users = new ArrayList<>(USER_COUNT + 1);
    users.add(
        userSeeder.seed(
            testAccountEmail, testAccountPassword, "tester", "테스트 계정", "테스터", "로컬 개발용 테스트 계정입니다."));

    for (int i = 0; i < USER_COUNT; i++) {
      String email = "user" + i + "@example.com";
      String handle = uniqueHandle(faker);
      String fullName = faker.name().fullName();
      String profession = faker.job().title();
      String bio = faker.lorem().sentence(random.nextInt(8) + 5);

      users.add(userSeeder.seed(email, "password1234!", handle, fullName, profession, bio));
    }
    return users;
  }

  private Map<String, List<User>> seedProjects(Faker faker, Random random, List<User> users) {
    Map<String, List<User>> projectMembers = new LinkedHashMap<>(PROJECT_COUNT);
    for (int i = 0; i < PROJECT_COUNT; i++) {
      User owner = randomElement(users, random);
      String handle = "project" + i;
      String name = faker.app().name();
      String description = faker.lorem().sentence(random.nextInt(15) + 8);

      projectSeeder.seed(owner, handle, name, description);

      List<User> members = new ArrayList<>();
      members.add(owner);
      Set<Long> teammateIds = new HashSet<>();
      teammateIds.add(owner.getId());

      int teammateCount = random.nextInt(3) + 1;
      for (int j = 0; j < teammateCount; j++) {
        User teammate = randomElement(users, random);
        if (teammateIds.add(teammate.getId())) {
          projectSeeder.addTeammate(owner, handle, teammate.getHandle());
          members.add(teammate);
        }
      }
      projectMembers.put(handle, members);
    }
    return projectMembers;
  }

  private List<String> seedPosts(
      Faker faker, Random random, List<User> users, Map<String, List<User>> projectMembers) {
    List<String> publicPostSlugs = new ArrayList<>(POST_COUNT);
    List<String> projectHandles = new ArrayList<>(projectMembers.keySet());
    PostType[] postTypes = PostType.values();

    for (int i = 0; i < POST_COUNT; i++) {
      String title = faker.lorem().sentence(random.nextInt(6) + 3);
      PostType type = postTypes[random.nextInt(postTypes.length)];
      String content = faker.lorem().paragraph(random.nextInt(4) + 2);
      String projectHandle = random.nextBoolean() ? randomElement(projectHandles, random) : null;
      User author =
          projectHandle == null
              ? randomElement(users, random)
              : randomElement(projectMembers.get(projectHandle), random);

      String slug = postSeeder.seed(author, title, type, content, List.of(), projectHandle);
      if (projectHandle == null) {
        publicPostSlugs.add(slug);
      }
    }
    return publicPostSlugs;
  }

  private void seedSocialGraph(
      Faker faker,
      Random random,
      List<User> users,
      List<String> projectHandles,
      List<String> publicPostSlugs) {
    for (int i = 0; i < LIKE_COUNT; i++) {
      User user = randomElement(users, random);
      String postSlug = randomElement(publicPostSlugs, random);
      socialGraphSeeder.like(user, postSlug);
    }

    for (int i = 0; i < COMMENT_COUNT; i++) {
      User author = randomElement(users, random);
      String postSlug = randomElement(publicPostSlugs, random);
      String content = faker.lorem().sentence(random.nextInt(10) + 4);
      socialGraphSeeder.comment(author, postSlug, content);
    }

    for (int i = 0; i < USER_FOLLOW_COUNT; i++) {
      User follower = randomElement(users, random);
      User followee = randomElement(users, random);
      if (!follower.getId().equals(followee.getId())) {
        socialGraphSeeder.followUser(follower, followee);
      }
    }

    for (int i = 0; i < PROJECT_FOLLOW_COUNT; i++) {
      User follower = randomElement(users, random);
      String projectHandle = randomElement(projectHandles, random);
      socialGraphSeeder.followProject(follower, projectHandle);
    }
  }

  private String uniqueHandle(Faker faker) {
    return Slugify.slugify(faker.name().firstName() + faker.name().lastName());
  }

  private <T> T randomElement(List<T> list, Random random) {
    return list.get(random.nextInt(list.size()));
  }

  private boolean shouldRun() {
    return userRepository.count() == 0;
  }
}
