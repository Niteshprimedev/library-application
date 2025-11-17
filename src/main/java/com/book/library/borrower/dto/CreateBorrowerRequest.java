package com.book.library.borrower.dto;

import com.book.library.borrower.enums.MembershipType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBorrowerRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email")
    private String email;

    @NotNull(message = "Membership type is required")
    private MembershipType membershipType;
}
