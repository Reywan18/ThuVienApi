package com.tlu.thuvien.application.service;

import com.tlu.thuvien.domain.entity.*;
import com.tlu.thuvien.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final CartRepository cartRepository;
    private final BorrowTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long createBorrowRequest() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể mượn!");
        }

        BorrowTransaction transaction = BorrowTransaction.builder()
                .user(user)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(BorrowStatus.PENDING)
                .details(new ArrayList<>())
                .build();

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();

            if(book.getAvailableQuantity() > 0) {
                book.setAvailableQuantity(book.getAvailableQuantity() - 1);
                bookRepository.save(book);
            } else {
                throw new RuntimeException("Sách " + book.getTitle() + " đã hết hàng!");
            }

            BorrowDetail detail = BorrowDetail.builder()
                    .transaction(transaction)
                    .book(book)
                    .returnedDate(null)
                    .build();

            transaction.getDetails().add(detail);
        }

        BorrowTransaction savedTrans = transactionRepository.save(transaction);

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedTrans.getId();
    }

    public BorrowTransaction getPendingTransactionByUserId(Long userId) {
        return transactionRepository.findByUserIdAndStatus(userId, BorrowStatus.PENDING)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("User này không có yêu cầu mượn nào!"));
    }

    @Transactional
    public void approveBorrow(Long transactionId) {
        BorrowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));

        transaction.setStatus(BorrowStatus.BORROWED);
        transactionRepository.save(transaction);
    }
}