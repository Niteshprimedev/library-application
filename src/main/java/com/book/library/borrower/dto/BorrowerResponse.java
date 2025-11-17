package com.book.library.borrower.dto;

import com.book.library.borrower.enums.MembershipType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowerResponse {
    private UUID id;
    private String name;
    private String email;
    private MembershipType membershipType;
    private int maxBorrowLimit;
}


