package com.example.UsermanagementAPI;

import com.example.UsermanagementAPI.model.User;
import com.example.UsermanagementAPI.repositry.UserRepositry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositryTest {

    @Autowired
    private UserRepositry userRepositry;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        userRepositry.deleteAll();

        // Create and save initial users (now with password)
        user1 = new User("Alice Wonderland", "alice@ex.com", "alice123");
        user2 = new User("Bob Builder", "bob@ex.com", "bob123");
        user3 = new User("Shubham", "shubham@example.com", "shubham123");

        user1 = userRepositry.save(user1);
        user2 = userRepositry.save(user2);
        user3 = userRepositry.save(user3);
    }

    @Test
    void testFindByEmailFound() {
        Optional<User> foundUser = userRepositry.findByEmail(user1.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user1.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user1.getEmail());
        assertThat(foundUser.get().getPassword()).isEqualTo(user1.getPassword());
    }

    @Test
    void testSaveUser() {
        User newUser = new User("Charlie Chaplin", "charlie@example.com", "charlie123");
        User savedUser = userRepositry.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Charlie Chaplin");
        assertThat(savedUser.getEmail()).isEqualTo("charlie@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("charlie123");
    }

    @Test
    void testUpdateUser() {
        String newName = "Subrata";
        String newEmail = "subrata@gmail.com";
        String newPassword = "newpassword123";

        user3.setName(newName);
        user3.setEmail(newEmail);
        user3.setPassword(newPassword);

        User updatedUser = userRepositry.save(user3);

        assertThat(updatedUser.getName()).isEqualTo(newName);
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        assertThat(updatedUser.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> user = userRepositry.findByEmail("nonexistent@example.com");
        assertThat(user).isNotPresent();
    }
}
