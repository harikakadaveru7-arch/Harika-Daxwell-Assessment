package com.daxwell.expensetracker.service;

import com.daxwell.expensetracker.dto.BudgetAlert;
import com.daxwell.expensetracker.dto.CategorySummary;
import com.daxwell.expensetracker.dto.MonthlySummary;
import com.daxwell.expensetracker.model.Budget;
import com.daxwell.expensetracker.model.Expense;
import com.daxwell.expensetracker.repository.BudgetRepository;
import com.daxwell.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public SummaryService(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<MonthlySummary> getMonthlySummary(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(start, end);

        Map<YearMonth, BigDecimal> totals = expenses.stream()
            .collect(Collectors.groupingBy(
                expense -> YearMonth.from(expense.getExpenseDate()),
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));

        return totals.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> new MonthlySummary(entry.getKey().toString(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public List<CategorySummary> getCategorySummary(YearMonth month) {
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(
            month.atDay(1),
            month.atEndOfMonth()
        );

        Map<String, BigDecimal> totals = expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));

        return totals.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> new CategorySummary(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public List<BudgetAlert> getBudgetAlerts(YearMonth month) {
        List<Budget> budgets = budgetRepository.findAll();
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(
            month.atDay(1),
            month.atEndOfMonth()
        );

        Map<String, BigDecimal> spentByCategory = expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));

        List<BudgetAlert> alerts = new ArrayList<>();
        for (Budget budget : budgets) {
            BigDecimal spent = spentByCategory.getOrDefault(budget.getCategory(), BigDecimal.ZERO);
            int percentUsed = spent.compareTo(BigDecimal.ZERO) == 0
                ? 0
                : spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();

            String status;
            if (percentUsed >= 100) {
                status = "OVER";
            } else if (percentUsed >= 80) {
                status = "WARNING";
            } else {
                status = "OK";
            }

            alerts.add(new BudgetAlert(
                budget.getCategory(),
                budget.getMonthlyLimit(),
                spent,
                percentUsed,
                status
            ));
        }

        alerts.sort(Comparator.comparing(BudgetAlert::getCategory));
        return alerts;
    }
}
