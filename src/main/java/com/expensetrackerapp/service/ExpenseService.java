package com.expensetrackerapp.service;

import com.expensetrackerapp.exception.ExpenseNotFoundException;
import com.expensetrackerapp.modal.Expense;
import com.expensetrackerapp.modal.Filter;
import com.expensetrackerapp.repository.ExpenseFilterRepository;
import com.expensetrackerapp.repository.ExpenseRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseFilterRepository expenseFilterRepository;

    @Autowired
    private UserService userService;

    public Expense addExpense(Expense expense) {
        expense.setUserId(userService.getLoggedInUserId());
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByUserId(userService.getLoggedInUserId());
    }

    public Expense getExpenseByIdOfLoggedInUser(int id) throws ExpenseNotFoundException {
        Expense expense = expenseRepository.findByExpenseIdAndUserId(id, userService.getLoggedInUserId()).orElseThrow(() -> new ExpenseNotFoundException("Invalid Expense id - '" + id + "' . Please enter valid expense id."));
        return expense;
    }

    public Expense getExpenseById(int id) throws ExpenseNotFoundException {
        return expenseRepository.findById(id).orElseThrow(() -> new ExpenseNotFoundException("Invalid Expense id - '" + id + "' . Please enter valid expense id."));
    }

    public void deleteExpenseById(int id) throws ExpenseNotFoundException {
        Expense existingExpenses = getExpenseById(id);
        expenseRepository.deleteById(id);
    }

    public Expense updateExpense(@Valid Expense expense) throws ExpenseNotFoundException {
        Expense existingExpense = getExpenseById(expense.getExpenseId());

        // allowed only following filed to be updated
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setDate(expense.getDate());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setTitle(expense.getTitle());

        return expenseRepository.save(existingExpense);
    }

    public Page<Expense> getFilteredExpenses(List<Filter> filters, Pageable pageable) {
        filters.add(new Filter("userId", userService.getLoggedInUserId(), "EQ"));
        return expenseFilterRepository.getExpenses(filters, pageable);
    }

}
