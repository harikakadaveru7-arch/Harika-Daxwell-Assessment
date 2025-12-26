package com.daxwell.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.daxwell.expensetracker.dto.BudgetAlert;
import com.daxwell.expensetracker.dto.CategorySummary;
import com.daxwell.expensetracker.dto.MonthlySummary;
import com.daxwell.expensetracker.model.Budget;
import com.daxwell.expensetracker.model.Expense;
import com.daxwell.expensetracker.repository.BudgetRepository;
import com.daxwell.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SummaryServiceTest {

    @Test
    void getMonthlySummaryAggregatesTotals() {
        ExpenseRepository expenseRepository = Mockito.mock(ExpenseRepository.class);
        BudgetRepository budgetRepository = Mockito.mock(BudgetRepository.class);
        SummaryService service = new SummaryService(expenseRepository, budgetRepository);

        Expense janExpense = new Expense();
        janExpense.setAmount(new BigDecimal("45.50"));
        janExpense.setCategory("Food");
        janExpense.setExpenseDate(LocalDate.of(2025, 1, 10));

        Expense febExpense = new Expense();
        febExpense.setAmount(new BigDecimal("20.00"));
        febExpense.setCategory("Travel");
        febExpense.setExpenseDate(LocalDate.of(2025, 2, 5));

        when(expenseRepository.findByExpenseDateBetween(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 12, 31)
        )).thenReturn(List.of(janExpense, febExpense));

        List<MonthlySummary> summaries = service.getMonthlySummary(2025);

        assertEquals(2, summaries.size());
        assertEquals("2025-01", summaries.get(0).getMonth());
        assertEquals(new BigDecimal("45.50"), summaries.get(0).getTotal());
        assertEquals("2025-02", summaries.get(1).getMonth());
        assertEquals(new BigDecimal("20.00"), summaries.get(1).getTotal());
    }

    @Test
    void getCategorySummaryAggregatesByCategory() {
        ExpenseRepository expenseRepository = Mockito.mock(ExpenseRepository.class);
        BudgetRepository budgetRepository = Mockito.mock(BudgetRepository.class);
        SummaryService service = new SummaryService(expenseRepository, budgetRepository);

        Expense first = new Expense();
        first.setAmount(new BigDecimal("15.00"));
        first.setCategory("Food");
        first.setExpenseDate(LocalDate.of(2025, 3, 2));

        Expense second = new Expense();
        second.setAmount(new BigDecimal("10.00"));
        second.setCategory("Food");
        second.setExpenseDate(LocalDate.of(2025, 3, 15));

        when(expenseRepository.findByExpenseDateBetween(
            LocalDate.of(2025, 3, 1),
            LocalDate.of(2025, 3, 31)
        )).thenReturn(List.of(first, second));

        List<CategorySummary> summaries = service.getCategorySummary(YearMonth.of(2025, 3));

        assertEquals(1, summaries.size());
        assertEquals("Food", summaries.get(0).getCategory());
        assertEquals(new BigDecimal("25.00"), summaries.get(0).getTotal());
    }

    @Test
    void getBudgetAlertsReturnsStatus() {
        ExpenseRepository expenseRepository = Mockito.mock(ExpenseRepository.class);
        BudgetRepository budgetRepository = Mockito.mock(BudgetRepository.class);
        SummaryService service = new SummaryService(expenseRepository, budgetRepository);

        Budget budget = new Budget();
        budget.setCategory("Food");
        budget.setMonthlyLimit(new BigDecimal("100.00"));

        Expense expense = new Expense();
        expense.setCategory("Food");
        expense.setAmount(new BigDecimal("85.00"));
        expense.setExpenseDate(LocalDate.of(2025, 4, 20));

        when(budgetRepository.findAll()).thenReturn(List.of(budget));
        when(expenseRepository.findByExpenseDateBetween(
            LocalDate.of(2025, 4, 1),
            LocalDate.of(2025, 4, 30)
        )).thenReturn(List.of(expense));

        List<BudgetAlert> alerts = service.getBudgetAlerts(YearMonth.of(2025, 4));

        assertEquals(1, alerts.size());
        BudgetAlert alert = alerts.get(0);
        assertEquals("Food", alert.getCategory());
        assertEquals("WARNING", alert.getStatus());
        assertTrue(alert.getPercentUsed() >= 80);
    }
}
