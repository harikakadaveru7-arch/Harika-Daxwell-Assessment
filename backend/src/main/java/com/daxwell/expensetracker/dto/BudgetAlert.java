package com.daxwell.expensetracker.dto;

import java.math.BigDecimal;

public class BudgetAlert {
    private String category;
    private BigDecimal budget;
    private BigDecimal spent;
    private int percentUsed;
    private String status;

    public BudgetAlert(String category, BigDecimal budget, BigDecimal spent, int percentUsed, String status) {
        this.category = category;
        this.budget = budget;
        this.spent = spent;
        this.percentUsed = percentUsed;
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public int getPercentUsed() {
        return percentUsed;
    }

    public String getStatus() {
        return status;
    }
}
