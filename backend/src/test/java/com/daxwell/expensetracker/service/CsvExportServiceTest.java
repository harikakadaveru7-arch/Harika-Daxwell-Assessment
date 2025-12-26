package com.daxwell.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.daxwell.expensetracker.model.Expense;
import com.daxwell.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CsvExportServiceTest {

    @Test
    void exportMonthCreatesCsvWithEscapedValues() {
        ExpenseRepository expenseRepository = Mockito.mock(ExpenseRepository.class);
        CsvExportService service = new CsvExportService(expenseRepository);

        Expense expense = new Expense();
        expense.setId(10L);
        expense.setAmount(new BigDecimal("12.34"));
        expense.setCategory("Food");
        expense.setDescription("Lunch \"combo\", cafe");
        expense.setExpenseDate(LocalDate.of(2025, 5, 3));

        when(expenseRepository.findByExpenseDateBetween(
            LocalDate.of(2025, 5, 1),
            LocalDate.of(2025, 5, 31)
        )).thenReturn(List.of(expense));

        String csv = service.exportMonth(YearMonth.of(2025, 5));

        assertTrue(csv.startsWith("id,amount,category,description,expenseDate"));
        assertTrue(csv.contains("10,12.34,\"Food\",\"Lunch \"\"combo\"\", cafe\",2025-05-03"));
    }
}
