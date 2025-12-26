package com.daxwell.expensetracker.dto;

import java.math.BigDecimal;

public class MonthlySummary {
    private String month;
    private BigDecimal total;

    public MonthlySummary(String month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
