package main.java.com.library.service;

import java.time.LocalDate;

import main.java.com.library.model.Book;
import main.java.com.library.model.Patron;
import main.java.com.library.model.Transaction;

// Factory Pattern to create Transaction objects
public class TransactionFactory {
    public static Transaction createNewTransaction(Patron patron, Book book) {
        // Enforce Transaction creation to be at current date
        return new Transaction(patron, book, LocalDate.now());
    }
}