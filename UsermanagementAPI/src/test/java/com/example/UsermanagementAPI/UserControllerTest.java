package com.example.UsermanagementAPI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;

import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.UsermanagementAPI.model.User;
import com.example.UsermanagementAPI.repositry.UserRepositry;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepositry userRepositry;

    private User user1;
    private User user2;

    private List<User> manyUsers;

    private Long initialCount;

    @BeforeEach
    public void setUp() {
        userRepositry.deleteAll();

        // Register the JavaTimeModule to handle Java 8 date/time types
        // This is necessary for serializing/deserializing LocalDate, LocalDateTime, etc.
        // If you are using LocalDate or LocalDateTime in your User model, this is  necessary.
        objectMapper.registerModule(new JavaTimeModule());

        // Create and save initial users
        // These users will be used in the tests to ensure that the database is not empty
        User initialUser1 = new User("John Doe","john@example.com");
        User initialUser2 = new User("Jane Smith","janesmit@example.com");

        // Save the initial users to the repository
        // This ensures that the database has some data to work with during tests
        List<User> savedInitialUsers = userRepositry.saveAll(List.of(initialUser1, initialUser2));
        this.user1 = savedInitialUsers.get(0);
        this.user2 = savedInitialUsers.get(1);

        manyUsers = IntStream.rangeClosed(1,25)
                .mapToObj(i -> new User("User" + i, "user" + i + "@example.com"))
                .collect(Collectors.toList());
        userRepositry.saveAll(manyUsers);

        initialCount = userRepositry.count();
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        // This test will check if the user with ID is found in the database
        // It will use the MockMvc to perform a GET request to the endpoint that retrieves a user by ID

        mockMvc.perform(get("/api/users/{id}", user1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));


    }

    @Test
    void testCreateUser() throws Exception {
        // This test will check if a new user can be created successfully
        // It will perform a POST request to the endpoint that creates a new user
        User newUser1 = new User("Alice Brown", "alice.brown@example.com");
        User newUser2 = new User("Bob White", "bob@example.com");
        List<User> newUsers = Arrays.asList(newUser1,newUser2);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUsers)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", is(newUser1.getName())))
                .andExpect(jsonPath("$[0].email", is(newUser1.getEmail())))
                .andExpect(jsonPath("$[1].id", notNullValue()))
                .andExpect(jsonPath("$[1].name", is(newUser2.getName())))
                .andExpect(jsonPath("$[1].email", is(newUser2.getEmail())));

    }
    // void testDeleteUserFound()
    // void testDeleteUserNotFound()

    @Test
    void testDeleteUserFound() throws Exception {
        // This test checks if the user is deleted successfully when the user exists

        mockMvc.perform(delete("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        // This test checks the behavior when attempting to delete a non-existing user

        Long noUserId = 9999L;

        mockMvc.perform(delete("/api/users/{id}", noUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

