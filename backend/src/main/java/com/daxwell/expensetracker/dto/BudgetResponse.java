package com.daxwell.expensetracker.dto;

import java.math.BigDecimal;

public class BudgetResponse {
    private Long id;
    private String category;
    private BigDecimal monthlyLimit;

    public BudgetResponse(Long id, String category, BigDecimal monthlyLimit) {
        this.id = id;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }
}
