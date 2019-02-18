package com.melardev.spring.blogapi.repository;

import com.melardev.spring.blogapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Modifying
    @Query("update User u set u.password = :password where u.id = :id")
    void changePassword(@Param("id") Long id, @Param("password") String password);

    @Query(value = "SELECT * FROM users order by rand() limit 1", nativeQuery = true)
    User findRandom();

    @Query("select count(u) from User u inner join u.roles r where r.name='ROLE_AUTHOR'")
    int countAuthors();

    @Query("select u from User u inner join u.roles r where r.name='ROLE_AUTHOR'")
    List<User> findRandomAuthor();

    default Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
}
