package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SimpleBankApplication.class)
@AutoConfigureMockMvc
public class SimpleBankApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankingService bankingService;

    // Testuje poprawność rejestracji konta
    @Test
    void shouldRegisterAccountSuccessfully() throws Exception {
        mockMvc.perform(post("/bank/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"pesel\": \"12345678901\", \"initialBalance\": 100.0, \"currency\": \"PLN\", \"firstName\": \"Jan\", \"lastName\": \"Kowalski\" }"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"status\": \"ACCEPTED\", \"newBalance\": 100.0 }"));
    }

    // Testuje poprawność pobrania danych z istniejącego konta
    @Test
    void shouldGetAccountByIdSuccessfully() throws Exception {
        // Rejestruje konto, aby mieć dane do pobrania
        bankingService.registerAccount("12345678901", 100.0, "PLN", "Jan", "Kowalski");

        mockMvc.perform(get("/bank/account")
                        .param("pesel", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"status\": \"ACCEPTED\", \"newBalance\": 100.0 }"));
    }

    // Testuje poprawność obsługi przypadku, gdy konto nie istnieje
    @Test
    void shouldHandleNonExistingAccount() throws Exception {
        mockMvc.perform(get("/bank/account")
                        .param("pesel", "99999999999"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"status\": \"DECLINED\", \"newBalance\": 0 }"));
    }

}
