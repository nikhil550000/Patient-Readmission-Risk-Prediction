package com.healthcare.readmission.auth.repository;

import com.healthcare.readmission.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username (for login)
    Optional<User> findByUsername(String username);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find all users by role
    List<User> findByRole(String role);

    // Find active users
    List<User> findByIsActive(boolean isActive);

    // Find users by role and active status
    List<User> findByRoleAndIsActive(String role, boolean isActive);

    // Check if username exists
    boolean existsByUsername(String username);

    // Check if email exists
    boolean existsByEmail(String email);
}
