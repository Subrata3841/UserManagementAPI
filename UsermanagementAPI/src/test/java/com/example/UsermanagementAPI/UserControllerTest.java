package com.example.UsermanagementAPI;

import com.example.UsermanagementAPI.model.User;
import com.example.UsermanagementAPI.repositry.UserRepositry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        // Register JavaTimeModule (if needed for date/time fields)
        objectMapper.registerModule(new JavaTimeModule());

        // Create initial users WITH password (to satisfy validation)
        User initialUser1 = new User("John Doe", "john@example.com", "john123");
        User initialUser2 = new User("Jane Smith", "janesmit@example.com", "jane123");

        List<User> savedInitialUsers = userRepositry.saveAll(List.of(initialUser1, initialUser2));
        this.user1 = savedInitialUsers.get(0);
        this.user2 = savedInitialUsers.get(1);

        // Many test users (with password)
        manyUsers = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> new User("User" + i, "user" + i + "@example.com", "pass" + i))
                .collect(Collectors.toList());
        userRepositry.saveAll(manyUsers);

        initialCount = userRepositry.count();
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void testCreateUser() throws Exception {
        // Create users WITH password
        User newUser1 = new User("Alice Brown", "alice.brown@example.com", "alice123");
        User newUser2 = new User("Bob White", "bob@example.com", "bob123");

        List<User> newUsers = Arrays.asList(newUser1, newUser2);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUsers)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", is(newUser1.getName())))
                .andExpect(jsonPath("$[0].email", is(newUser1.getEmail())))
                .andExpect(jsonPath("$[1].id", notNullValue()))
                .andExpect(jsonPath("$[1].name", is(newUser2.getName())))
                .andExpect(jsonPath("$[1].email", is(newUser2.getEmail())));
    }

    @Test
    void testDeleteUserFound() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        Long noUserId = 9999L;

        mockMvc.perform(delete("/api/users/{id}", noUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
