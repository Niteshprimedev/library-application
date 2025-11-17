package com.book.library.borrower.dto;

import com.book.library.borrowRecord.dto.BorrowRecordResponse;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordData {

    private List<BorrowRecordResponse> records;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}

