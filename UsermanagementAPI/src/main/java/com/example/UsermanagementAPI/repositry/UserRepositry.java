package com.example.UsermanagementAPI.repositry;

import com.example.UsermanagementAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepositry extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContainingIgnoreCase(String name);

}
