package main.java.com.library.service;

import java.util.List;
import java.util.Optional;

import main.java.com.library.model.Book;
import main.java.com.library.model.BookItem;
import main.java.com.library.model.Patron;
import main.java.com.library.model.Transaction;

public interface LibraryService {
    // Book Management
    void addBook(Book book, int copies);
    void removeBook(String isbn);
    void updateBook(String isbn, String newTitle, String newAuthor, Integer newYear);
    Optional<Book> findBookByIsbn(String isbn);
    List<Book> searchBooks(String query);
    List<BookItem> getBookItems(String isbn);

    // Patron Management
    void addPatron(Patron patron);
    void updatePatron(int id, String newName, String newContactInfo);
    Optional<Patron> findPatronById(int id);
    List<Patron> getAllPatrons();

    // Lending Process
    String checkoutBook(String isbn, int patronId);
    String returnBook(String isbn, int patronId);
    
    // Inventory
    List<Transaction> getBorrowedBooks();
    List<Book> getAllBooks();
    
    // Patron History
    List<Transaction> getPatronHistory(int patronId);
}