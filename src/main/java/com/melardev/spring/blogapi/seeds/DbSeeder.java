package com.melardev.spring.blogapi.seeds;

import com.github.javafaker.Faker;
import com.melardev.spring.blogapi.entities.*;
import com.melardev.spring.blogapi.enums.ContentType;
import com.melardev.spring.blogapi.repository.UserSubscriptionsRepository;
import com.melardev.spring.blogapi.services.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Profile("seeds")
@Component
public class DbSeeder implements CommandLineRunner {

    private final UsersService usersService;
    private final SettingsService settingsService;
    private final ArticlesService articlesService;
    private final RolesService rolesService;
    private final Faker faker;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private LikesService likesService;

    @Autowired
    TagService tagService;

    @Autowired
    CategoriesService categoriesService;

    @Autowired
    private UserSubscriptionsRepository userSubscriptionsRepository;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UserSubscriptionsService usersSubscriptionsService;


    @Transactional
    public Object test(String hql) {
        Session session = (Session) em.getDelegate();
        List<?> list = session.createQuery(hql).list();
        return list;
    }

    @Autowired
    public DbSeeder(UsersService userService, RolesService rolesService,
                    ArticlesService articlesService, SettingsService settingsService,
                    PasswordEncoder passwordEncoder) {
        this.usersService = userService;
        this.articlesService = articlesService;
        this.settingsService = settingsService;
        faker = new Faker(Locale.getDefault());
        this.rolesService = rolesService;
    }

    @Override
    public void run(String... args) {
        // https://www.javabullets.com/access-entitymanager-spring-data-jpa/

        // test("select t from Tag t");

        createAdminFeature();
        seedAuthors();
        seedUsers();
        seedTags();
        seedCategories();
        seedArticles();
        seedComments();
        seedLikes();
        seedSubscriptions();

        System.exit(0);
    }


    public void createAdminFeature() {
        createAdminRole();
        createAdminUser();
    }

    private void createAdminRole() {
        this.rolesService.getOrCreate(settingsService.getAdminRoleName(), "For admin users");
    }

    private void createAdminUser() {
        User user = this.usersService.findByUsernameOrEmailNoException(settingsService.getDefaultAdminUsername());
        if (user == null) {
            HashSet<Role> roles = new HashSet<>();
            roles.add(this.rolesService.getRoleOrThrow(this.settingsService.getAdminRoleName()));

            this.usersService.createUser(new User(settingsService.getDefaultAdminFirstName(),
                    settingsService.getDefaultAdminLastName(),
                    settingsService.getDefaultAdminEmail(),
                    settingsService.getDefaultAdminUsername(),
                    settingsService.getDefaultAdminPassword(),
                    roles));
        }
    }


    private void seedAuthors() {
        Role authorRole = this.rolesService.getOrCreate(settingsService.getAuthorRoleName(), "For Authors");
        HashSet<Role> roles = new HashSet<>();
        roles.add(authorRole);

        int max = this.settingsService.getAuthorsCountToSeed();
        int current = this.usersService.getAuthorsCount();

        List<User> users = IntStream.range(current, max)
                .mapToObj(i -> {
                    User user = new User(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress(),
                            faker.name().name(), "password", roles);
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return user;
                }).collect(Collectors.toList());

        this.usersService.saveAll(users);
    }


    public void seedUsers() {
        if (this.rolesService.getRoleDontThrow(this.settingsService.getAuthenticatedRoleName()) == null)
            this.rolesService.save(new Role(this.settingsService.getAuthenticatedRoleName()));

        String encodedPassword = passwordEncoder.encode("password");

        int max = this.settingsService.getMaxUsersToSeed();
        int current = this.usersService.findAll().size();
        HashSet<Role> roles = new HashSet<>();
        roles.add(this.rolesService.getRoleOrThrow(this.settingsService.getAuthenticatedRoleName()));

        List<User> users = IntStream.range(current, max)
                .mapToObj(i -> {
                    User user = new User(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress(),
                            faker.name().name(), "password", roles);
                    user.setPassword(encodedPassword);
                    return user;
                }).collect(Collectors.toList());

        this.usersService.saveAll(users);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private void seedTags() {

        if (tagService.getAllCount() >= 4)
            return;

        Map<String, String> tags = Map.of(
                "Spring", "Spring articles",
                "Rails", "Rails articles",
                "Laravel", "Laravel articles",
                "Apache Struts", "Struts 2 Articles"
        );

        tags.forEach((tagName, tagDescription) -> {
            tagService.findOrCreate(tagName, tagDescription);
        });
    }

    private void seedCategories() {

        Map<String, String> categories = Map.of(
                "Java", "Java articles",
                "PHP", "PHP articles",
                "Ruby", "Ruby articles",
                "C++", "C++ Articles"
        );

        categories.forEach((name, description) -> {
            categoriesService.findOrCreate(name, description);

        });
    }

    public void seedArticles() {

        int maxProductsToSeed = this.settingsService.getMaxArticlesToSeed();
        int currentProducts = this.articlesService.findAll().size();
        List<Tag> tags = tagService.findAll();
        List<Category> categories = categoriesService.findAll();
        List<User> authors = usersService.getAuthors();
        Random random = new Random();
        Set<Article> articles = IntStream.range(currentProducts, maxProductsToSeed)
                .mapToObj(i -> {
                    Article article = new Article();
                    article.setTitle(StringHelper.join(faker.lorem().words(faker.random().nextInt(3, 5)), " "));
                    article.setDescription((StringHelper.join(faker.lorem().sentences(faker.random().nextInt(1, 2)), ". ")));
                    article.setBody(StringHelper.join(faker.lorem().paragraphs(faker.random().nextInt(10, 20)), "."));
                    article.setUser(authors.get(random.nextInt(authors.size())));
                    article.setViews(faker.random().nextInt(0, 10000));
                    article.setTags(new HashSet<Tag>(Arrays
                            .asList( //TODO: Fix the range
                                    tags.get(faker.random().nextInt(1, tags.size() - 1))
                            )));
                    article.setCategories(new HashSet<Category>(Arrays
                            .asList( //TODO: Fix the range
                                    categories.get(faker.random().nextInt(1, categories.size() - 1))
                            )));
                    article.setContentType(ContentType.RAW);
                    return article;
                })
                .collect(toSet());

        this.articlesService.saveAll(articles);
    }


    private void seedComments() {
        int maxCommentsToSeed = this.settingsService.getMaxCommentsToSeed();
        int currentCommentsCount = this.commentsService.findAll().size();
        Set<Comment> products = IntStream.range(currentCommentsCount, maxCommentsToSeed)
                .mapToObj(i -> {
                    Comment comment = new Comment();
                    comment.setContent(faker.lorem().sentence());
                    comment.setArticle(articlesService.getRandom());
                    comment.setUser(usersService.getRandom());

                    return comment;
                })
                .collect(toSet());

        this.commentsService.saveAll(products);


        maxCommentsToSeed = this.settingsService.getRepliesCountToSeed();
        currentCommentsCount = this.commentsService.getRepliesCount();
        Set<Comment> comments = IntStream.range(currentCommentsCount, maxCommentsToSeed)
                .mapToObj(i -> {
                    Comment comment = new Comment();
                    comment.setContent(faker.lorem().sentence());
                    comment.setArticle(articlesService.getRandom());
                    comment.setUser(usersService.getRandom());
                    comment.setRepliedComment(commentsService.getRandom());
                    return comment;
                })
                .collect(toSet());

        this.commentsService.saveAll(comments);
    }

    private void seedLikes() {

        int likesCountToSeed = this.settingsService.getLikesCountToSeed();
        long currentLikesCount = this.likesService.count();
        AtomicReference<List<Long>> allArticleIds = new AtomicReference<>(articlesService.fetchAllIds());
        List<Long> allArticleIdsCopy = new ArrayList<>(allArticleIds.get());
        IntStream.range((int) currentLikesCount, likesCountToSeed)
                .forEach(i -> {
                    Like like = new Like();
                    User user = usersService.getRandom();
                    like.setUser(user);

                    allArticleIds.set(new ArrayList<>(allArticleIdsCopy));
                    List<Long> articleIds = likesService.findArticlesLikedBy(user.getId());
                    allArticleIds.get().removeAll(articleIds);

                    if (allArticleIds.get().size() == 0)
                        return;

                    Article article = articlesService.getProxyForSave(allArticleIds.get().get(faker.random().nextInt(0, allArticleIds.get().size() - 1)));
                    like.setArticle(article);
                    like.setUser(user);
                    this.likesService.save(like);

                });
    }

    private void seedSubscriptions() {
        long count = usersSubscriptionsService.getAllCount();
        if (count > 10)
            return;
        List<User> users = usersService.findAll();

        List<Long> userIdsCopy = users.stream().map(u -> u.getId()).collect(toList());


        List<User> authors = usersService.getAuthors();
        Random rand = new Random();

        List<UserSubscription> subscriptions = null;
        int size = users.size();
        for (int i = 0; i < size; i++) {
            User follower = users.get(i);
            List<Long> userIds = new ArrayList<>(userIdsCopy);
            // Remove follower id, we do not want a user to follow himself
            userIds.remove(follower.getId());
            List<Long> userIdsSubscribedTo = usersSubscriptionsService.fetchAllIdsSubscribedTo(follower.getId());
            userIds.removeAll(userIdsSubscribedTo);
            IntStream.range(0, faker.random().nextInt(0, 5))
                    .forEach(in -> {
                        User following = authors.get(rand.nextInt(authors.size()));
                        UserSubscription rel = new UserSubscription(following, follower);


                        userSubscriptionsRepository.save(rel);
                        // Now the user is already following, it is no longer a valid option to follow in the future
                        userIds.remove(following.getId());
                    });

        }


    }

}
