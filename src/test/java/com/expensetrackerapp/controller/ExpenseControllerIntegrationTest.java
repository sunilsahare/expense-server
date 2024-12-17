package com.expensetrackerapp.controller;

import com.expensetrackerapp.modal.Expense;
import com.expensetrackerapp.modal.User;
import com.expensetrackerapp.repository.ExpenseRepository;
import com.expensetrackerapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class ExpenseControllerIntegrationTest {


    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private String token;

    private static final String API_URL = "/api/expenses";


    public User saveUser() {
        User user = new User();
        user.setUsername("sunil");
        user.setPassword("Sunil@123"); 
        user.setFullName("Sunil Sahare");
        user.setRole("USER");

        return userRepository.save(user);
    }

    @AfterEach
    public void after() {
        expenseRepository.deleteAll();
    }

    @BeforeEach
    public void loginTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                    "username" : "sunil",
                                    "password" : "Sunil@123"
                                 }
                         """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("User Successfully Authenticated"))
                .andExpect(jsonPath("$.responseData.token").exists())
                .andExpect(jsonPath("$.responseData.user.username").value("sunil"))
                .andExpect(jsonPath("$.responseData.user.fullName").value("Sunil Sahare"))
                .andReturn();

        System.err.println("Login response -> "+mvcResult.getResponse().getContentAsString());
        token = mvcResult.getResponse().getContentAsString()
                .replaceAll(".*\"token\":\"(.*?)\".*", "$1");

        System.out.println("Extracted Token: " + token);

        Expense savedEmp = expenseRepository.save(getExpense());
    }

    @Test
    public void loginBadCredentialTest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                    "username" : "sunil",
                                    "password" : "Sunil"
                                 }
                         """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Bad credentials - Enter valid username/password"))
                .andReturn();

    }

    @Test
    public void registerUserTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                     "username" : "shan",
                                     "role" : "USER",
                                     "password" : "Shan@123",
                                     "fullName" : "Shan Belankar"
                                 }
                         """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("User Successfully registered."))
                .andExpect(jsonPath("$.responseData.username").value("shan"))
                .andExpect(jsonPath("$.responseData.fullName").value("Shan Belankar"))
                .andReturn();

    }

    @Test
    public void registerUserWithExistingUsernameAndItShouldFailTest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {
                                     "username" : "sunil",
                                     "role" : "USER",
                                     "password" : "Shan@123",
                                     "fullName" : "Shan Belankar"
                                 }
                         """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Please use other username."))
                .andReturn();

    }

    @Test
    void addExpenseSuccessTest() throws Exception {
        mockMvc.perform(post(API_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                    "title" : "Traveled to Goa",
                                    "category" : "Travel",
                                    "amount" : "9770",
                                    "date" : "2024-02-10"
                                 }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Traveled to Goa"))
                .andExpect(jsonPath("$.category").value("Travel"))
                .andExpect(jsonPath("$.amount").value("9770.0"))
                .andExpect(jsonPath("$.date").value("2024-02-10"));
    }

    void updateExpenseSuccessTest() throws Exception {
        mockMvc.perform(put(API_URL +"/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "expenseId" : 1,
                                    "title" : "Travel to Kashmir",
                                    "category" : "Travel",
                                    "amount" : "23000",
                                    "date" : "2023-10-09",
                                    "userId" : 1
                                 }
                               \s""")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(1))
                .andExpect(jsonPath("$.title").value("Travel to Kashmir"))
                .andExpect(jsonPath("$.amount").value("23000.0"));
    }

    @Test
    @Transactional
    void getExpenseListSuccessTest() throws Exception {
        Expense savedEmp = expenseRepository.save(getExpense());

        mockMvc.perform(get(API_URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getExpenseByIdSuccessTest() throws Exception {
        assert token != null;
        System.err.println("token ->"+token);
        mockMvc.perform(get(API_URL+"/{id}", 1).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(1));
//                .andExpect(jsonPath("$.title").value(savedEmp.getTitle()))
//                .andExpect(jsonPath("$.amount").value(savedEmp.getAmount()));
    }

    @Test
    void getExpenseByIdNotFoundTest() throws Exception {
        mockMvc.perform(get(API_URL+"/{id}", 56).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invalid Expense id - '56' . Please enter valid expense id."));
    }

    @Test
    void deleteExpenseSuccessTest() throws Exception {
        Expense savedEmp = expenseRepository.save(getExpense());

        mockMvc.perform(delete(API_URL+"/{id}", savedEmp.getExpenseId()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Expense with id - "+savedEmp.getExpenseId()+" has been successfully deleted."));
    }

    @Test
    void deleteExpenseNotFoundTest() throws Exception {
        mockMvc.perform(delete(API_URL+"/{id}", 12).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invalid Expense id - '12' . Please enter valid expense id."));
    }

    public Expense getExpense() {
        Expense expense = new Expense();
        expense.setTitle("Traveled to Goa");
        expense.setCategory("Travel");
        expense.setAmount(12300);
        expense.setUserId(1);
        expense.setDate(LocalDate.of(2022,10,20));
        return expense;
    }

}
