package com.daxwell.expensetracker.controller;

import com.daxwell.expensetracker.dto.BudgetAlert;
import com.daxwell.expensetracker.dto.CategorySummary;
import com.daxwell.expensetracker.dto.MonthlySummary;
import com.daxwell.expensetracker.service.SummaryService;
import java.time.YearMonth;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "*")
public class SummaryController {
    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/monthly")
    public List<MonthlySummary> getMonthlySummary(@RequestParam int year) {
        return summaryService.getMonthlySummary(year);
    }

    @GetMapping("/categories")
    public List<CategorySummary> getCategorySummary(@RequestParam String month) {
        return summaryService.getCategorySummary(YearMonth.parse(month));
    }

    @GetMapping("/alerts")
    public List<BudgetAlert> getAlerts(@RequestParam String month) {
        return summaryService.getBudgetAlerts(YearMonth.parse(month));
    }
}
