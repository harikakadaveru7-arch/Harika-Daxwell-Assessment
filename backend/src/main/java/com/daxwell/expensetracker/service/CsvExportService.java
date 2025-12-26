package com.daxwell.expensetracker.service;

import com.daxwell.expensetracker.model.Expense;
import com.daxwell.expensetracker.repository.ExpenseRepository;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CsvExportService {
    private final ExpenseRepository expenseRepository;

    public CsvExportService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public String exportMonth(YearMonth month) {
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(
            month.atDay(1),
            month.atEndOfMonth()
        );

        StringBuilder builder = new StringBuilder();
        builder.append("id,amount,category,description,expenseDate");

        for (Expense expense : expenses) {
            builder.append("\n");
            builder.append(expense.getId()).append(",");
            builder.append(expense.getAmount()).append(",");
            builder.append(escape(expense.getCategory())).append(",");
            builder.append(escape(expense.getDescription())).append(",");
            builder.append(expense.getExpenseDate());
        }

        return builder.toString();
    }

    private String escape(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String sanitized = value.replace("\"", "\"\"");
        return "\"" + sanitized + "\"";
    }
}
