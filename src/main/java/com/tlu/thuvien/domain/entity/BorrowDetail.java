package com.tlu.thuvien.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private BorrowTransaction transaction;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "returned_date")
    private LocalDate returnedDate;
}
