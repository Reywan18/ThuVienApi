package com.tlu.thuvien.infrastructure.repository;

import com.tlu.thuvien.domain.entity.User;
import com.tlu.thuvien.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Logic:
    // 1. Nếu keyword rỗng -> Bỏ qua điều kiện tên/email
    // 2. Nếu role là null -> Bỏ qua điều kiện role (lấy tất cả role)
    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:role IS NULL OR u.role = :role)")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") UserRole role);
}