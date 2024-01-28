package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// Enum do reprezentacji statusu transakcji
enum TransactionStatus { ACCEPTED, DECLINED }

// Klasa reprezentująca konto
class Account {
    private final String pesel;
    private final double balance;
    private final String currency;
    private final String firstName;
    private final String lastName;

    // Konstruktor klasy Account
    public Account(String pesel, double initialBalance, String currency, String firstName, String lastName) {
        this.pesel = pesel;
        this.balance = initialBalance;
        this.currency = currency;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Metoda zwracająca PESEL konta
    public String getPesel() {
        return pesel;
    }

    // Metoda zwracająca saldo konta
    public double getBalance() {
        return balance;
    }

    // Metoda zwracająca walutę konta
    public String getCurrency() {
        return currency;
    }

    // Metoda zwracająca imię użytkownika
    public String getFirstName() {
        return firstName;
    }

    // Metoda zwracająca nazwisko użytkownika
    public String getLastName() {
        return lastName;
    }
}

// Klasa reprezentująca odpowiedź na transakcję
record TransactionResponse(TransactionStatus status, double newBalance) {
    // Konstruktor klasy TransactionResponse
}

// Klasa zarządzająca operacjami bankowymi
@Service
class BankingService {
    private final Map<String, Account> accounts;

    // Konstruktor klasy BankingService
    public BankingService() {
        this.accounts = new HashMap<>();
    }

    // Metoda rejestracji konta
    public TransactionResponse registerAccount(String pesel, double initialBalance, String currency, String firstName, String lastName) {
        if (initialBalance < 0) {
            return new TransactionResponse(TransactionStatus.DECLINED, 0);
        }

        Account account = new Account(pesel, initialBalance, currency, firstName, lastName);
        accounts.put(pesel, account);
        return new TransactionResponse(TransactionStatus.ACCEPTED, account.getBalance());
    }

    // Metoda pobierania danych z konkretnego konta
    public TransactionResponse getAccountById(String pesel) {
        Account account = accounts.get(pesel);
        if (account != null) {
            return new TransactionResponse(TransactionStatus.ACCEPTED, account.getBalance());
        } else {
            return new TransactionResponse(TransactionStatus.DECLINED, 0);
        }
    }

    // Metoda pobierania danych wszystkich kont posiadających saldo większe niż X
    public Map<String, Account> getAccountsWithBalanceGreaterThan(double threshold) {
        Map<String, Account> result = new HashMap<>();
        for (Account account : accounts.values()) {
            if (account.getBalance() > threshold) {
                result.put(account.getPesel(), account);
            }
        }
        return result;
    }
}

// Klasa główna z adnotacją @SpringBootApplication
@SpringBootApplication
public class SimpleBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleBankApplication.class, args);
    }

    // Kontroler obsługujący żądania HTTP
    @RestController
    @RequestMapping("/bank")
    static class BankController {

        private final BankingService bankingService;

        // Konstruktor klasy BankController
        public BankController(BankingService bankingService) {
            this.bankingService = bankingService;
        }

        // Endpoint do rejestracji konta
        @PostMapping("/register")
        public TransactionResponse registerAccount(@RequestParam String pesel,
                                                   @RequestParam double initialBalance,
                                                   @RequestParam String currency,
                                                   @RequestParam String firstName,
                                                   @RequestParam String lastName) {
            return bankingService.registerAccount(pesel, initialBalance, currency, firstName, lastName);
        }

        // Endpoint do pobierania danych z konkretnego konta
        @GetMapping("/account")
        public TransactionResponse getAccountById(@RequestParam String pesel) {
            return bankingService.getAccountById(pesel);
        }

        // Endpoint do pobierania danych wszystkich kont posiadających saldo większe niż X
        @GetMapping("/accounts")
        public Map<String, Account> getAccountsWithBalanceGreaterThan(@RequestParam double threshold) {
            return bankingService.getAccountsWithBalanceGreaterThan(threshold);
        }
    }
}
