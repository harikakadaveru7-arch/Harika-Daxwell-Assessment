package com.daxwell.expensetracker.repository;

import com.daxwell.expensetracker.model.Budget;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByCategory(String category);
}
