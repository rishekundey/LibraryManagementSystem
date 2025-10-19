package main.java.com.library.model;

import java.time.LocalDate;

public class Transaction {
    private final Patron patron;
    private final Book book;
    private final LocalDate checkoutDate;
    private LocalDate returnDate;
    private double fineAmount;

    public Transaction(Patron patron, Book book, LocalDate checkoutDate) {
        this.patron = patron;
        this.book = book;
        this.checkoutDate = checkoutDate;
        this.returnDate = null; // Initially null
        this.fineAmount = 0.0;
    }

    // Getters and Setters
    public Patron getPatron() { return patron; }
    public Book getBook() { return book; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFineAmount() { return fineAmount; }

    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public boolean isReturned() {
        return returnDate != null;
    }

    @Override
    public String toString() {
        return String.format(
            "Book: %s | Patron ID: %d | Checkout: %s | Return: %s | Fine: $%.2f",
            book.getTitle(), patron.getId(), checkoutDate,
            returnDate == null ? "N/A (BORROWED)" : returnDate.toString(), fineAmount
        );
    }
}