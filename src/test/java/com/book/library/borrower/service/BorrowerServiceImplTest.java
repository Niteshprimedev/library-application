package com.book.library.borrower.service;

import com.book.library.borrower.controller.BorrowerController;
import com.book.library.borrower.dto.BorrowerResponse;
import com.book.library.borrower.dto.CreateBorrowerRequest;
import com.book.library.borrower.enums.MembershipType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BorrowerServiceImplTest {

    private MockMvc mockMvc;
    private BorrowerService borrowerService;

    @BeforeEach
    void setUp() {
        borrowerService = mock(BorrowerService.class);

        BorrowerController controller = new BorrowerController(borrowerService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testRegisterBorrower() throws Exception {

        UUID borrowerId = UUID.randomUUID();

        BorrowerResponse borrowerResponse = BorrowerResponse.builder()
                .id(borrowerId)
                .name("Nitesh")
                .email("niteshprimedev@mail.com")
                .membershipType(MembershipType.PREMIUM)
                .maxBorrowLimit(5)
                .build();

        when(borrowerService.addBorrower(any(CreateBorrowerRequest.class)))
                .thenReturn(borrowerResponse);

        // trying with endpoint
        mockMvc.perform(post("/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Nitesh",
                          "email": "niteshprimedev@mail.com",
                          "membershipType": "PREMIUM"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(borrowerId.toString()))
                .andExpect(jsonPath("$.name").value("Nitesh"))
                .andExpect(jsonPath("$.email").value("niteshprimedev@mail.com"))
                .andExpect(jsonPath("$.membershipType").value("PREMIUM"))
                .andExpect(jsonPath("$.maxBorrowLimit").value(5));
    }
}