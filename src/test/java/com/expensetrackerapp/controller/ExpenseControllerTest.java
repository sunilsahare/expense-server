package com.expensetrackerapp.controller;

import com.expensetrackerapp.exception.ExpenseIdMissMatchException;
import com.expensetrackerapp.exception.ExpenseNotFoundException;
import com.expensetrackerapp.modal.Expense;
import com.expensetrackerapp.modal.Filter;
import com.expensetrackerapp.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private Expense testExpense;
    private Integer testId = 1;

    @BeforeEach
    public void setup() {
        testExpense = getExpense();
    }

    // Helper method to generate a test expense object
    public Expense getExpense() {
        Expense expense = new Expense();
        expense.setTitle("Traveled to Goa");
        expense.setCategory("Travel");
        expense.setAmount(12300);
        expense.setUserId(1);
        expense.setDate(LocalDate.of(2022, 10, 20));
        return expense;
    }

    // Test for getting expense by valid ID
//    @Test
    public void getExpenseByIdValidIdTest() throws ExpenseNotFoundException {
        when(expenseService.getExpenseById(testId)).thenReturn(testExpense);

        ResponseEntity<Expense> response = expenseController.getExpenseById(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testExpense.getAmount(), response.getBody().getAmount());
        verify(expenseService).getExpenseById(testId);
    }

    // Test for getting expense by invalid ID
//    @Test
    public void getExpenseByIdInvalidIdTest() throws ExpenseNotFoundException {
        when(expenseService.getExpenseById(56)).thenThrow(new ExpenseNotFoundException("Invalid Expense id - '56' . Please enter valid expense id."));

        ExpenseNotFoundException thrown = assertThrows(ExpenseNotFoundException.class, () -> {
            expenseController.getExpenseById(56);
        });

        assertEquals("Invalid Expense id!!. Please provide valid id", thrown.getMessage());
    }

    // Test for invalid ID (null)
//    @Test
    public void getExpenseByIdIdValidationTest() {
        Integer invalidId = null;
        assertThrows(NullPointerException.class, () -> expenseController.getExpenseById(invalidId));
    }

    // Test for adding a valid expense
//    @Test
    public void addExpenseShouldSaveExpenseTest() {
        when(expenseService.addExpense(any(Expense.class))).thenReturn(testExpense);

        ResponseEntity<Expense> response = expenseController.addExpense(testExpense);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testExpense, response.getBody());
        verify(expenseService).addExpense(testExpense);
    }

    // Test for handling service exception during add expense
//    @Test
    public void addExpenseServiceException() {
        when(expenseService.addExpense(any(Expense.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            expenseController.addExpense(testExpense);
        });

        assertEquals("Database error", thrown.getMessage());
    }

    // Test for getting all expenses
//    @Test
    public void getExpensesTest() {
        List<Expense> mockExpenses = Collections.singletonList(testExpense);
        when(expenseService.getAllExpenses()).thenReturn(mockExpenses);

        ResponseEntity<List<Expense>> response = expenseController.getAllExpenses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(testExpense, response.getBody().get(0));
        verify(expenseService).getAllExpenses();
    }

    // Test for successful expense update
//    @Test
    public void updateExpenseShouldBeSuccessTest() throws ExpenseNotFoundException, ExpenseIdMissMatchException {
        when(expenseService.updateExpense(any(Expense.class))).thenReturn(testExpense);

        Expense expenseToUpdate = new Expense();
        expenseToUpdate.setExpenseId(testId);
        expenseToUpdate.setTitle("Holiday");

        ResponseEntity<Expense> response = expenseController.updateExpense(testId, expenseToUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testExpense, response.getBody());
        verify(expenseService).updateExpense(any(Expense.class));
    }

    // Test for getting expense by ID
//    @Test
    public void getExpenseByIdTest() throws ExpenseNotFoundException {
        when(expenseService.getExpenseById(testId)).thenReturn(testExpense);

        ResponseEntity<Expense> response = expenseController.getExpenseById(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testExpense, response.getBody());
        verify(expenseService).getExpenseById(testId);
    }

    // Test for filtering expenses by category
//    @Test
    public void getExpenseListByCategoryTest() {
        Filter filter = new Filter("category", "Travel", "LIKE");
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "asc";
        Sort sort = Sort.by(sortBy).ascending();

        List<Expense> mockExpenses = Collections.singletonList(testExpense);
        when(expenseService.getFilteredExpenses(List.of(filter), PageRequest.of(page, size, sort)).getContent()).thenReturn(mockExpenses);

        ResponseEntity<Page<Expense>> response = expenseController.getAllFilteredExpenses(List.of(filter), page, size, sortBy, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getContent().size());
        assertEquals(testExpense, response.getBody().getContent().get(0));
        verify(expenseService).getFilteredExpenses(List.of(filter), PageRequest.of(page, size, sort));
    }

    // Test for filtering expenses before a certain date
//    @Test
    public void getExpenseBeforeDateTest() {
        Filter filter = new Filter("date", "01-01-2025", "LE");
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "asc";
        Sort sort = Sort.by(sortBy).ascending();

        List<Expense> mockExpenses = Collections.singletonList(testExpense);
        when(expenseService.getFilteredExpenses(List.of(filter), PageRequest.of(page, size, sort)).getContent()).thenReturn(mockExpenses);

        ResponseEntity<Page<Expense>> response = expenseController.getAllFilteredExpenses(List.of(filter), page, size, sortBy, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getContent().size());
        assertEquals(testExpense, response.getBody().getContent().get(0));
        verify(expenseService).getFilteredExpenses(List.of(filter), PageRequest.of(page, size, sort));
    }
}
