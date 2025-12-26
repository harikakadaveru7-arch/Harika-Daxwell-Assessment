package com.daxwell.expensetracker.controller;

import com.daxwell.expensetracker.dto.ExpenseRequest;
import com.daxwell.expensetracker.dto.ExpenseResponse;
import com.daxwell.expensetracker.service.CsvExportService;
import com.daxwell.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final CsvExportService csvExportService;

    public ExpenseController(ExpenseService expenseService, CsvExportService csvExportService) {
        this.expenseService = expenseService;
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public List<ExpenseResponse> getExpenses(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(required = false) String month
    ) {
        if (month != null && !month.isBlank()) {
            return expenseService.getByMonth(YearMonth.parse(month));
        }
        if (startDate != null && endDate != null) {
            return expenseService.getByDateRange(startDate, endDate);
        }
        return expenseService.getAll();
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpense(@PathVariable Long id) {
        return expenseService.getById(id);
    }

    @PostMapping
    public ExpenseResponse createExpense(@Valid @RequestBody ExpenseRequest request) {
        return expenseService.create(request);
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<String> exportCsv(@RequestParam String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        String csv = csvExportService.exportMonth(yearMonth);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expenses-" + month + ".csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
    }
}
