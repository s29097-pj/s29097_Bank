package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BankingServiceTest {

    @Test
    void testRegisterAccount() {
        BankingService bankingService = new BankingService();

        // Test rejestracji konta z pozytywnym saldem
        TransactionResponse response = bankingService.registerAccount
                ("12345678901", 100.0, "PLN", "Jan", "Kowalski");
        Assertions.assertEquals(TransactionStatus.ACCEPTED, response.status());
        Assertions.assertEquals(100.0, response.newBalance());

        // Test rejestracji konta z negatywnym saldem
        TransactionResponse responseNegativeBalance = bankingService.registerAccount
                ("98765432109", -50.0, "USD", "Alice", "Smith");
        Assertions.assertEquals(TransactionStatus.DECLINED, responseNegativeBalance.status());
        Assertions.assertEquals(0, responseNegativeBalance.newBalance());
    }

    @Test
    void testGetAccountById() {
        BankingService bankingService = new BankingService();

        // Test pobrania danych z istniejącego konta
        bankingService.registerAccount
                ("12345678901", 100.0, "PLN", "Jan", "Kowalski");
        TransactionResponse response = bankingService.getAccountById("12345678901");
        Assertions.assertEquals(TransactionStatus.ACCEPTED, response.status());
        Assertions.assertEquals(100.0, response.newBalance());

        // Test pobrania danych z nieistniejącego konta
        TransactionResponse responseNonExisting = bankingService.getAccountById("99999999999");
        Assertions.assertEquals(TransactionStatus.DECLINED, responseNonExisting.status());
        Assertions.assertEquals(0, responseNonExisting.newBalance());
    }

    @Test
    void testGetAccountsWithBalanceGreaterThan() {
        BankingService bankingService = new BankingService();

        // Test pobrania kont z saldo większym niż X
        bankingService.registerAccount("12345678901", 100.0, "PLN", "Jan", "Kowalski");
        bankingService.registerAccount("98765432109", 200.0, "USD", "Alice", "Smith");
        Map<String, Account> accounts = bankingService.getAccountsWithBalanceGreaterThan(150.0);

        Assertions.assertEquals(1, accounts.size());
        Assertions.assertTrue(accounts.containsKey("98765432109"));
        Assertions.assertFalse(accounts.containsKey("12345678901"));
    }
}

    