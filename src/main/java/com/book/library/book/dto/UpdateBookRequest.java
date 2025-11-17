package com.book.library.book.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookRequest {
    private String title;
    private String author;
    private String category;
    private Integer totalCopies;
}
