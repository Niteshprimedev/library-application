package com.book.library.borrower.model;

import com.book.library.borrowRecord.model.BorrowRecord;
import com.book.library.borrower.enums.MembershipType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "borrowers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrower {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType;

    @Column(nullable = false)
    private int maxBorrowLimit;

    @JsonIgnore
    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public void assignLimitFromType() {
        if (membershipType == MembershipType.BASIC) {
            this.maxBorrowLimit = 2;
        } else if (membershipType == MembershipType.PREMIUM) {
            this.maxBorrowLimit = 5;
        }
    }
}