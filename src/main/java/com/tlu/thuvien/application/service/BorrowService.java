package com.tlu.thuvien.application.service;

import com.tlu.thuvien.domain.entity.*;
import com.tlu.thuvien.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
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

    public List<BorrowTransaction> getAllTransactions() {
        return transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    //Tao yeu cau muon sach
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

    //Lay trang thai theo Id
    public BorrowTransaction getPendingTransactionByUserId(Long userId) {
        return transactionRepository.findByUserIdAndStatus(userId, BorrowStatus.PENDING)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("User này không có yêu cầu mượn nào!"));
    }

    //Tim phieu co trang thai Borrowed
    public BorrowTransaction getBorrowedTransactionByUserId(Long userId) {
        return transactionRepository.findByUserIdAndStatus(userId, BorrowStatus.BORROWED)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Người dùng này không có sách nào đang mượn!"));
    }

    //Duyet muon sach
    @Transactional
    public void approveBorrow(Long transactionId) {
        BorrowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));

        transaction.setStatus(BorrowStatus.BORROWED);
        transactionRepository.save(transaction);
    }

    //Tra sach
    @Transactional
    public String processReturn(Long transactionId) {
        BorrowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));

        if (transaction.getStatus() == BorrowStatus.PENDING) {
            throw new RuntimeException("Phiếu mượn chưa được duyệt, không thể trả!");
        }

        if (transaction.getStatus() != BorrowStatus.BORROWED) {
            return "Phiếu mượn đã được trả hoặc bị hủy.";
        }

        LocalDate currentDate = LocalDate.now();
        boolean isOverdue = false;

        for (BorrowDetail detail : transaction.getDetails()) {
            if (detail.getReturnedDate() == null) {
                detail.setReturnedDate(currentDate);

                Book book = detail.getBook();
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
                bookRepository.save(book);
            }

            if (currentDate.isAfter(transaction.getDueDate())) {
                isOverdue = true;
            }
        }

        transaction.setStatus(BorrowStatus.RETURNED);
        transactionRepository.save(transaction);

        if (isOverdue) {
            return "Trả sách thành công, nhưng bị trễ hạn (" + transaction.getDueDate() + ").";
        }

        return "Trả sách thành công!";
    }

    @Transactional
    public void rejectBorrow(Long transactionId) {
        BorrowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));

        if (transaction.getStatus() != BorrowStatus.PENDING) {
            throw new RuntimeException("Chỉ được hủy các phiếu đang chờ duyệt!");
        }

        for (BorrowDetail detail : transaction.getDetails()) {
            Book book = detail.getBook();
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookRepository.save(book);
        }

        transactionRepository.delete(transaction);
    }

    public List<BorrowTransaction> getPendingTransactions() {
        return transactionRepository.findByStatus(BorrowStatus.PENDING);
    }

    public List<BorrowTransaction> getBorrowedTransactions() {
        return transactionRepository.findByStatus(BorrowStatus.BORROWED);
    }
}