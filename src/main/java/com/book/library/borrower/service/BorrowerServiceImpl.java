package com.book.library.borrower.service;

import com.book.library.borrowRecord.dto.BorrowRecordResponse;
import com.book.library.borrowRecord.model.BorrowRecord;
import com.book.library.borrowRecord.repository.BorrowRecordRepository;
import com.book.library.borrower.dto.BorrowRecordData;
import com.book.library.borrower.dto.BorrowerData;
import com.book.library.borrower.dto.BorrowerResponse;
import com.book.library.borrower.dto.CreateBorrowerRequest;
import com.book.library.borrower.model.Borrower;
import com.book.library.borrower.repository.BorrowerRepository;
import com.book.library.exceptions.APIException;
import com.book.library.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BorrowerServiceImpl implements BorrowerService{

    private final BorrowerRepository borrowerRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final ModelMapper modelMapper;

    @Override
    public BorrowerResponse addBorrower(CreateBorrowerRequest createBorrowerRequest) {

        borrowerRepository.findByEmail(createBorrowerRequest.getEmail())
                .ifPresent(b -> {throw new APIException("Borrower with this email already exists");});

        Borrower borrower = modelMapper.map(createBorrowerRequest, Borrower.class);
        borrower.assignLimitFromType();

        Borrower savedBorrower = borrowerRepository.save(borrower);
        return modelMapper.map(savedBorrower, BorrowerResponse.class);
    }

    @Override
    public BorrowRecordData getRecords(UUID borrowerId, int page, int size, String sortBy, String sortDir) {

        borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower", "id", borrowerId));

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BorrowRecord> recordPage = borrowRecordRepository.findByBorrower_Id(borrowerId, pageable);

        List<BorrowRecordResponse> borrowRecords = recordPage.getContent().stream()
                .map(borrowRecord -> modelMapper.map(borrowRecord, BorrowRecordResponse.class))
                .toList();

        BorrowRecordData borrowRecordData = BorrowRecordData.builder()
                .records(borrowRecords)
                .pageNumber(recordPage.getNumber())
                .pageSize(recordPage.getSize())
                .totalElements(recordPage.getTotalElements())
                .totalPages(recordPage.getTotalPages())
                .lastPage(recordPage.isLast())
                .build();

        return borrowRecordData;
    }

    @Override
    public BorrowerData getOverdueBorrowers(int page, int size, String sortBy, String sortDir) {

        List<Borrower> overdueBorrowers =
                borrowRecordRepository.findBorrowersWithOverdueBooks(LocalDate.now());

        Comparator<Borrower> comparator = getBorrowerComparator(sortBy, sortDir);

        overdueBorrowers = overdueBorrowers.stream()
                .sorted(comparator)
                .toList();

        int start = page * size;
        int end = Math.min(start + size, overdueBorrowers.size());
        List<Borrower> paged = overdueBorrowers.subList(start, end);

        List<BorrowerResponse> borrowerResponses = paged.stream()
                .map(borrower -> modelMapper.map(borrower, BorrowerResponse.class))
                .toList();

        BorrowerData borrowerData = BorrowerData.builder()
                .borrowers(borrowerResponses)
                .pageNumber(page)
                .pageSize(size)
                .totalElements((long) overdueBorrowers.size())
                .totalPages((int) Math.ceil((double) overdueBorrowers.size() / size))
                .lastPage(end == overdueBorrowers.size())
                .build();

        return borrowerData;
    }

    private Comparator<Borrower> getBorrowerComparator(String sortBy, String sortDir) {

        Comparator<Borrower> comparator = switch (sortBy) {
            case "email" ->
                    Comparator.comparing(b -> b.getEmail().toLowerCase());
            case "membershipType" ->
                    Comparator.comparing(b -> b.getMembershipType().name());
            default ->
                    Comparator.comparing(b -> b.getName().toLowerCase());
        };

        if (sortDir.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}
