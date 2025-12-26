package com.daxwell.expensetracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.daxwell.expensetracker.model.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    void findByExpenseDateBetweenReturnsMatchingExpenses() {
        Expense inRange = new Expense();
        inRange.setAmount(new BigDecimal("19.99"));
        inRange.setCategory("Food");
        inRange.setDescription("Lunch");
        inRange.setExpenseDate(LocalDate.of(2025, 7, 10));
        expenseRepository.save(inRange);

        Expense outOfRange = new Expense();
        outOfRange.setAmount(new BigDecimal("50.00"));
        outOfRange.setCategory("Travel");
        outOfRange.setDescription("Ticket");
        outOfRange.setExpenseDate(LocalDate.of(2025, 8, 1));
        expenseRepository.save(outOfRange);

        List<Expense> results = expenseRepository.findByExpenseDateBetween(
            LocalDate.of(2025, 7, 1),
            LocalDate.of(2025, 7, 31)
        );

        assertEquals(1, results.size());
        assertEquals("Food", results.get(0).getCategory());
    }
}
