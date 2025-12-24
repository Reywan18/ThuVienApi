package com.tlu.thuvien.infrastructure.repository;

import com.tlu.thuvien.domain.entity.BorrowStatus; // Nhớ import Enum
import com.tlu.thuvien.domain.entity.BorrowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, Long> {
    List<BorrowTransaction> findByUserId(Long userId);
    long countByStatus(BorrowStatus status);
    List<BorrowTransaction> findByStatusAndDueDateBefore(BorrowStatus status, LocalDate date);
    List<BorrowTransaction> findByUserIdAndStatus(Long userId, BorrowStatus status);
}