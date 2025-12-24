package com.tlu.thuvien.api.controller.mvc;

import com.tlu.thuvien.domain.entity.BorrowStatus;
import com.tlu.thuvien.domain.entity.BorrowTransaction;
import com.tlu.thuvien.infrastructure.repository.BookRepository;
import com.tlu.thuvien.infrastructure.repository.BorrowTransactionRepository;
import com.tlu.thuvien.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowTransactionRepository transactionRepository;

    @GetMapping
    public String dashboard(Model model) {
        long totalBooks = bookRepository.count();
        long totalUsers = userRepository.count();
        long pendingRequests = transactionRepository.countByStatus(BorrowStatus.PENDING);
        long activeBorrows = transactionRepository.countByStatus(BorrowStatus.BORROWED);

        List<BorrowTransaction> overdueList = transactionRepository.findByStatusAndDueDateBefore(
                BorrowStatus.BORROWED, LocalDate.now()
        );

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("activeBorrows", activeBorrows);
        model.addAttribute("overdueList", overdueList);

        model.addAttribute("overdueCount", overdueList.size());

        return "admin/dashboard"; // Trả về file dashboard.html
    }
}
