package com.cesarvaldivia.banktransactions;

import com.cesarvaldivia.banktransactions.models.Transaction;
import com.cesarvaldivia.banktransactions.repositories.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BankTransactionsApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    private List<Transaction> getTestData() {
        List<Transaction> transactionList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Transaction transaction = new Transaction();
            transaction.setDescription("Test " + i);
            transaction.setDate(LocalDate.of(2023, Month.FEBRUARY, i + 1));
            transactionList.add(transaction);
        }

        Collections.sort(transactionList, Comparator.comparing(Transaction::getDate));
        Collections.reverse(transactionList);
        return transactionList;
    }

    @Test
    public void invalidRequest() throws Exception{
        String path = "/transaction/list";

        ResultActions response = mockMvc.perform(get(path));

        response.andExpect(status().isBadRequest());

    }

    @Test
    public void getFullData() throws Exception{
        LocalDate startDate = LocalDate.of(2023, Month.FEBRUARY, 1);
        LocalDate endDate = LocalDate.of(2023, Month.FEBRUARY, 28);
        int pageSize = 20;
        String path = "/transaction/list?startDate=" + startDate +
                "&endDate=" + endDate +
                "&pageSize=" + pageSize;
        List<Transaction> data = getTestData();
        repository.saveAll(data);

        ResultActions response = mockMvc.perform(get(path));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(data.size())));

    }

    @Test
    public void getFirstTenRecords() throws Exception{
        LocalDate startDate = LocalDate.of(2023, Month.FEBRUARY, 1);
        LocalDate endDate = LocalDate.of(2023, Month.FEBRUARY, 28);
        String path = "/transaction/list?startDate=" + startDate +
                "&endDate=" + endDate;
        List<Transaction> data = getTestData();
        repository.saveAll(data);

        ResultActions response = mockMvc.perform(get(path));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id", is(data.get(0).getId())))
                .andExpect(jsonPath("$[9].id", is(data.get(9).getId())))
                .andExpect(jsonPath("$.size()", is(10)));

    }

    @Test
    public void getSecondPageFiveRecords() throws Exception{
        LocalDate startDate = LocalDate.of(2023, Month.FEBRUARY, 1);
        LocalDate endDate = LocalDate.of(2023, Month.FEBRUARY, 28);
        int pageSize = 5;
        int pageNumber = 1;
        String path = "/transaction/list?startDate=" + startDate +
                "&endDate=" + endDate +
                "&pageSize=" + pageSize +
                "&pageNumber=" + pageNumber;
        List<Transaction> data = getTestData();
        repository.saveAll(data);

        ResultActions response = mockMvc.perform(get(path));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id", is(data.get(5).getId())))
                .andExpect(jsonPath("$[4].id", is(data.get(9).getId())))
                .andExpect(jsonPath("$.size()", is(5)));

    }

    @Test
    public void getDefaultPaginationEndDateWillReturnSevenRecords() throws Exception{
        LocalDate startDate = LocalDate.of(2023, Month.FEBRUARY, 1);
        LocalDate endDate = LocalDate.of(2023, Month.FEBRUARY, 7);
        String path = "/transaction/list?startDate=" + startDate +
                "&endDate=" + endDate;
        List<Transaction> data = getTestData();
        repository.saveAll(data);

        ResultActions response = mockMvc.perform(get(path));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id", is(data.get(13).getId())))
                .andExpect(jsonPath("$[6].id", is(data.get(19).getId())))
                .andExpect(jsonPath("$.size()", is(7)));

    }

}
