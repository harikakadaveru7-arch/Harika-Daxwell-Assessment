package com.daxwell.expensetracker.service;

import com.daxwell.expensetracker.dto.BudgetRequest;
import com.daxwell.expensetracker.dto.BudgetResponse;
import com.daxwell.expensetracker.model.Budget;
import com.daxwell.expensetracker.repository.BudgetRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<BudgetResponse> getAll() {
        return budgetRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public BudgetResponse upsert(BudgetRequest request) {
        Budget budget = budgetRepository.findByCategory(request.getCategory())
            .orElseGet(Budget::new);
        budget.setCategory(request.getCategory());
        budget.setMonthlyLimit(request.getMonthlyLimit());
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(Long id) {
        budgetRepository.deleteById(id);
    }

    private BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(budget.getId(), budget.getCategory(), budget.getMonthlyLimit());
    }
}
