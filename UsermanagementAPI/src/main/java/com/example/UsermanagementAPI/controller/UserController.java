package com.example.UsermanagementAPI.controller;

import com.example.UsermanagementAPI.model.User;
import com.example.UsermanagementAPI.repositry.UserRepositry;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://127.0.0.1:5500"
})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepositry userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ POST: Create multiple users (used in testCreateUser)
    @PostMapping
    public ResponseEntity<List<User>> createUser(@Valid @RequestBody List<@Valid User> users){
        users.forEach(user -> user.setPassword(passwordEncoder.encode(user.getPassword())));
        List<User> savedUsers = userRepository.saveAll(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsers);
    }

    // ✅ GET all users
    @GetMapping
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // ✅ GET: By ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // ✅ PUT: Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails){
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            return ResponseEntity.ok(userRepository.save(existingUser));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ DELETE: (used in testDeleteUserFound / testDeleteUserNotFound)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Register single user (POST /api/users/register)
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // ✅ Pagination
    @GetMapping("/page")
    public Page<User> getUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }
}
