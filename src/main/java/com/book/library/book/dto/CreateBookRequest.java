package com.book.library.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookRequest {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Author cannot be blank")
    private String author;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotNull(message = "Total copies is required")
    @Range(min = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
}
