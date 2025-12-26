package com.daxwell.expensetracker.service;

import com.daxwell.expensetracker.dto.ExpenseRequest;
import com.daxwell.expensetracker.dto.ExpenseResponse;
import com.daxwell.expensetracker.model.Expense;
import com.daxwell.expensetracker.repository.ExpenseRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseResponse> getAll() {
        return expenseRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ExpenseResponse> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByExpenseDateBetween(startDate, endDate).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ExpenseResponse> getByMonth(YearMonth month) {
        return expenseRepository.findByExpenseDateBetween(month.atDay(1), month.atEndOfMonth()).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public ExpenseResponse getById(Long id) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        return toResponse(expense);
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        Expense expense = new Expense();
        applyRequest(expense, request);
        Expense saved = expenseRepository.save(expense);
        return toResponse(saved);
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        applyRequest(expense, request);
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new IllegalArgumentException("Expense not found");
        }
        expenseRepository.deleteById(id);
    }

    private void applyRequest(Expense expense, ExpenseRequest request) {
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getAmount(),
            expense.getCategory(),
            expense.getDescription(),
            expense.getExpenseDate()
        );
    }
}
