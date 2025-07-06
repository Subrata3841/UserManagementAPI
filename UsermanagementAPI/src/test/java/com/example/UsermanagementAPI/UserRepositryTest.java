package com.example.UsermanagementAPI;


import java.util.Optional;
import com.example.UsermanagementAPI.model.User;
import com.example.UsermanagementAPI.repositry.UserRepositry;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

        // Create and save initial users
        user1 = new User("Alice wonderland", "alice@ex.com");
        user2 = new User("Bob Builder", "bob@ex.com");
        user3 = new User("Shubham", "Shubam@example.com");

        user1 = userRepositry.save(user1);
        user2 = userRepositry.save(user2);
        user3 = userRepositry.save(user3);
    }

    @Test
    void testFindByEmailFound(){
        Optional<User> foundUser = userRepositry.findByEmail(user1.getEmail());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user1.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user1.getEmail());
        assertThat(foundUser.get().getId()).isEqualTo(user1.getId());
    }

    @Test
    void testSaveUser() {
        User newUser = new User("Charlie Chaplin", "charlie@example.com");
        User saveduser = userRepositry.save(newUser);
        assertThat(saveduser).isNotNull();
        assertThat(saveduser.getName()).isEqualTo("Charlie Chaplin");
        assertThat(saveduser.getEmail()).isEqualTo("charlie@example.com");
    }
    @Test
    void testUpdateUser() {
        String newName = "Subrata";
        String newemail = "subrata@gmail.com";

        user3.setName(newName);
        user3.setEmail(newemail);

        User updateUser = userRepositry.save(user3);
        assertThat(updateUser.getName()).isEqualTo(newName);
        assertThat(updateUser.getEmail()).isEqualTo(newemail);
    }
}
