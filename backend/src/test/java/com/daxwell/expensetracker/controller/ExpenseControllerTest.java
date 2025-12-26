package com.daxwell.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.daxwell.expensetracker.dto.ExpenseRequest;
import com.daxwell.expensetracker.dto.ExpenseResponse;
import com.daxwell.expensetracker.service.CsvExportService;
import com.daxwell.expensetracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private CsvExportService csvExportService;

    @Test
    void getExpensesReturnsList() throws Exception {
        ExpenseResponse response = new ExpenseResponse(
            1L,
            new BigDecimal("12.50"),
            "Food",
            "Lunch",
            LocalDate.of(2025, 6, 10)
        );

        when(expenseService.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/expenses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].category").value("Food"));
    }

    @Test
    void createExpenseReturnsCreatedExpense() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("40.00"));
        request.setCategory("Travel");
        request.setDescription("Ride share");
        request.setExpenseDate(LocalDate.of(2025, 6, 12));

        ExpenseResponse response = new ExpenseResponse(
            10L,
            request.getAmount(),
            request.getCategory(),
            request.getDescription(),
            request.getExpenseDate()
        );

        when(expenseService.create(any(ExpenseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.category").value("Travel"));
    }
}
