package com.expensetrackerapp.controller;

import com.expensetrackerapp.exception.ExpenseNotFoundException;
import com.expensetrackerapp.exception.GlobalExceptionHandler;
import com.expensetrackerapp.exception.HttpApiResponse;
import com.expensetrackerapp.modal.Expense;
import com.expensetrackerapp.modal.Filter;
import com.expensetrackerapp.service.ExpenseService;
import com.expensetrackerapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody @Valid Expense expense) {
        return ResponseEntity.ok(expenseService.addExpense(expense));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable int id, @RequestBody @Valid Expense expense) throws ExpenseNotFoundException {
        return ResponseEntity.ok(expenseService.updateExpense(expense));
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable int id) throws ExpenseNotFoundException {
        return ResponseEntity.ok(expenseService.getExpenseByIdOfLoggedInUser(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Expense>> getAllFilteredExpenses(@RequestBody List<Filter> filters, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc")String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(expenseService.getFilteredExpenses(filters, PageRequest.of(page, size, sort)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpApiResponse<Object>> deleteExpense(@PathVariable int id) throws ExpenseNotFoundException {
        expenseService.deleteExpenseById(id);
        HttpApiResponse<Object> httpApiResponse = GlobalExceptionHandler.buildApiResponse("Expense with id - " + id + " has been successfully deleted.");
        return new ResponseEntity<>(httpApiResponse, HttpStatus.OK);
    }


}
