package com.melardev.spring.blogapi.services;


import com.melardev.spring.blogapi.entities.Role;
import com.melardev.spring.blogapi.entities.User;
import com.melardev.spring.blogapi.errors.exceptions.ResourceNotFoundException;
import com.melardev.spring.blogapi.repository.RolesRepository;
import com.melardev.spring.blogapi.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsersService implements UserDetailsService {
    /**
     * WARNING: if I use constructor DJ then circular dependecy : userService wants password encoder, which
     * is exposed by SecurityConfig, this triggers the creation of SecurityConfig,
     * at the same time SecurityConfig wants UsersService which is being created and remember he is asking
     * about password encoder, so this is a circular issue and app crashes.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UsersRepository userRepository;

    private final RolesRepository rolesRepository;
    @Autowired
    private SettingsService settingsService;


    @Autowired
    public UsersService(UsersRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // TODO: this works because properties yaml has lazy_trans = true, change it to use @Transactional instead
//        User user = findByEmailNoException(email);

        User user = findByUsernameOrEmailNoException(usernameOrEmail);
        if (user == null) {

            /* Should I throw? or return a user with ROLE_ANONYMOUS? or with ROLE_USER?
            if (user == null) {
                return new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        getAuthoritiesFromRolesString(Arrays.asList(
                                roleRepository.findByNameIgnoreCase("ROLE_USER"))));
            }
            */
            // return null;
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        // User exists, we have to return an Implementation of UserDetails, let's use the default
        /*return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                getAuthoritiesFromRoles(user.getRoles()));*/
        return user;
    }


    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = privileges.stream().map(roleStr -> new SimpleGrantedAuthority(roleStr))
                .collect(Collectors.toList());
        return authorities;
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesFromRoles(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesFromRolesString(Collection<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getCurrentLoggedInUser() {
        // TODO: Refractor this, no need for getUsername() unless in other part of our code we manually sign users without being in the db
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String username = ((User) auth.getPrincipal()).getUsername();

        return findByUsernameOrEmailNoException(username);
    }

    public boolean changePassword(User user, String password, String newPassword) {
        if (password == null || newPassword == null || password.isEmpty() || newPassword.isEmpty())
            return false;

        boolean match = passwordEncoder.matches(password, user.getPassword());
        if (!match)
            return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    public void resetPassword(String password) {
        resetPassword(getCurrentLoggedInUser(), password);
    }

    public void resetPassword(User user, String password) {
        resetPassword(user.getId(), password);
    }

    public void resetPassword(Long id, String password) {
        this.userRepository.changePassword(id, password);
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            return true;
        return false;
    }

    public void loginManually(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                getSpringUserFromUser(user), user.getPassword(), getAuthoritiesFromRoles(user.getRoles()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }

    public boolean isAnonymous(final Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.size() == 0)
            return true;

        String anonymous = this.settingsService.getAnonymousRoleName();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(anonymous));
    }

    private org.springframework.security.core.userdetails.User getSpringUserFromUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthoritiesFromRoles(user.getRoles()));
    }


    public User loggedInUser() {

        if (!isLoggedIn()) {
            return null;
        }

        // Get Spring User, it contains the username, now findById our User model object from the repository
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return userRepository.findByEmail(user.getUsername()).orElse(null);
        //return userRepository.findByUsernameOrEmail(user.getUsername());
    }


    // Edit controls are being showed up if the user is logged in and it's the same user viewing the file
    public boolean canEditProfile(User profileUser) {
        return isLoggedIn() && (profileUser.getId() == loggedInUser().getId());
    }


    public Page<User> getLatest(int page, int count) {
        PageRequest pageRequest = PageRequest.of(page, count, Sort.Direction.DESC, "createdAt");
        Page<User> result = this.userRepository.findAll(pageRequest);
        return result;
    }


    public User findByEmailNoException(String email) {
        return findByEmail(email, false);
    }

    public User findByEmail(String email) {
        return findByEmail(email, true);
    }


    public User findByUsernameOrEmailNoException(String username) {
        return findByUsernameOrEmail(username, false);
    }

    public User findByUsernameOrEmail(String username) {
        return findByUsernameOrEmail(username, true);
    }

    private User findByUsernameOrEmail(String usernameOrEmail, boolean throwException) {
        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail);
        if (throwException)
            return user.orElseThrow(ResourceNotFoundException::new);
        return user.orElse(null);
    }

    /**
     * Returns the user using an email as a lookup field, if throwException
     * is true then throws exception if user not found, otherwise
     * returns the user or null if such user does not exist
     *
     * @param email
     * @param throwException
     * @return
     */
    private User findByEmail(String email, boolean throwException) {
        Optional<User> user = userRepository.findByEmail(email);
        if (throwException)
            return user.orElseThrow(ResourceNotFoundException::new);
        return user.orElse(null);
    }

    public User findById(Long id) {
        return findByIdThrowException(id);
    }

    public User findByIdThrowException(Long id) {
        return findById(id, true);
    }

    public User findById(Long id, boolean throwException) {
        Optional<User> user = this.userRepository.findById(id);
        if (throwException)
            return user.orElseThrow(ResourceNotFoundException::new);

        return user.orElse(null);
    }

    public User getById(Long id) {
        return findByIdThrowException(id);
    }

    public User updateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public void delete(User user) {
        this.userRepository.delete(user);
    }


    public Authentication getCurrentLoggedInAuthenticationObject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            return authentication;
        return null;
    }


    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> saveAll(Iterable<User> users) {
        return this.userRepository.saveAll(users);
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public void loginManually(Long id) {
        User user = findById(id);
        loginManually(user);
    }

    public void loginManually(int id) {
        loginManually((long) id);
    }

    public User getRandom() {
        return userRepository.findRandom();

        /*Query countQuery = em.createNativeQuery("select count(*) from User");
        long count = (Long) countQuery.getSingleResult();

        Random random = new Random();
        int number = random.nextInt((int) count);

        Query selectQuery = em.createQuery("select q from User q");
        selectQuery.setFirstResult(number);
        selectQuery.setMaxResults(1);
        return (User) selectQuery.getSingleResult();*/
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return this.userRepository.findByUsername(username).isPresent();
    }

    public boolean isAnonymous() {
        return this.isAnonymous(SecurityContextHolder.getContext().getAuthentication());
    }


    public int getAuthorsCount() {
        return userRepository.countAuthors();
    }

    public List<User> getAuthors() {
        return userRepository.findRandomAuthor();
    }

    public boolean isAuthor() {
        return this.isAuthor(getCurrentLoggedInUser());
    }

    public boolean isAuthor(User user) {
        return this.isInRole(user, settingsService.getAuthorRoleName());
    }

    private boolean isInRole(User user, String authorRoleName) {
        return user.getRoles().stream().anyMatch(r -> r.getName().equals(authorRoleName));
    }

    public User getFromPrincipal(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        }
        return null;
    }

    public User findById(int id) {
        return findById((long) id);
    }


}
