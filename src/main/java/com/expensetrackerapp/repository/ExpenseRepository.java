package com.expensetrackerapp.repository;

import com.expensetrackerapp.modal.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findAllByUserId(Integer userId);
    Optional<Expense> findByExpenseIdAndUserId(int id, Integer userId);
}
