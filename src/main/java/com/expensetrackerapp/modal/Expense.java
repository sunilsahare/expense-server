package com.expensetrackerapp.modal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Expense implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer expenseId;

    @NotBlank(message = "title should not be empty or null")
    @Size(min = 4, max = 20, message = "title value should be min 4 and max 20 characters.")
    private String title;

    @NotNull(message = "amount should not be null")
    @Positive(message = "amount should be a positive number")
    private double amount;

    @NotBlank(message = "category should not be empty or null")
    @Size(min = 4, max = 20, message = "category value should be min 4 and max 20 characters.")
    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Integer userId;

    public Integer getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // toString method

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId=" + expenseId +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", userId=" + userId +
                '}';
    }
}