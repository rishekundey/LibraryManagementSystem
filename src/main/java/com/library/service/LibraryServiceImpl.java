package main.java.com.library.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import main.java.com.library.model.Book;
import main.java.com.library.model.BookItem;
import main.java.com.library.model.Patron;
import main.java.com.library.model.Transaction;

// Implements LibraryService, follows SRP (focus on business logic)
public class LibraryServiceImpl implements LibraryService {
    private static final Logger LOGGER = Logger.getLogger(LibraryServiceImpl.class.getName());
    
    // Maps: ISBN -> Book
    private final Map<String, Book> bookCatalog; 
    // Maps: ISBN -> List of physical copies (BookItem)
    private final Map<String, List<BookItem>> inventory;
    // Maps: Patron ID -> Patron
    private final Map<Integer, Patron> patrons;
    // Tracks current checkouts: BookItem ID -> Transaction
    private final Map<Integer, Transaction> currentCheckouts;

    private final FineCalculationStrategy fineStrategy;

    public LibraryServiceImpl() {
        this.bookCatalog = new HashMap<>();
        this.inventory = new HashMap<>();
        this.patrons = new HashMap<>();
        this.currentCheckouts = new HashMap<>();
        this.fineStrategy = new DefaultFineStrategy(); // Strategy Pattern usage
    }

    // --- Book Management ---

    @Override
    public void addBook(Book book, int copies) {
        if (bookCatalog.containsKey(book.getIsbn())) {
            LOGGER.log(Level.INFO, "Book with ISBN {0} already exists. Adding {1} more copies.", 
                       new Object[]{book.getIsbn(), copies});
        } else {
            bookCatalog.put(book.getIsbn(), book);
        }
        
        // Java 8 equivalent of computeIfAbsent
        List<BookItem> bookItems;
        if (inventory.containsKey(book.getIsbn())) {
            bookItems = inventory.get(book.getIsbn());
        } else {
            bookItems = new ArrayList<>();
            inventory.put(book.getIsbn(), bookItems);
        }
        
        for (int i = 0; i < copies; i++) {
            bookItems.add(new BookItem(book));
        }
        LOGGER.log(Level.INFO, "Added {0} copies of book: {1}", new Object[]{copies, book.getTitle()});
    }
    
    @Override
    public void removeBook(String isbn) {
        if (inventory.containsKey(isbn)) {
            List<BookItem> items = inventory.get(isbn);
            
            // Remove copies that are not currently checked out (Java 8 filter and remove)
            List<BookItem> removableItems = items.stream()
                .filter(item -> !currentCheckouts.containsKey(item.getId()))
                .collect(Collectors.toList());
            
            items.removeAll(removableItems);

            if (items.isEmpty()) {
                inventory.remove(isbn);
                bookCatalog.remove(isbn);
                LOGGER.log(Level.INFO, "Book with ISBN {0} fully removed from catalog.", isbn);
            } else {
                LOGGER.log(Level.WARNING, "Cannot remove all copies of ISBN {0}. {1} copies are still checked out.", 
                           new Object[]{isbn, items.size()});
            }
        }
    }
    
    @Override
    public void updateBook(String isbn, String newTitle, String newAuthor, Integer newYear) {
        // Note: ISBN is final, cannot be updated. Log warning as per initial design.
        LOGGER.log(Level.WARNING, "Book class is immutable (ISBN is final). Update functionality skipped for ISBN: {0}.", isbn);
    }
    
    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(bookCatalog.get(isbn));
    }
    
    @Override
    public List<Book> searchBooks(String query) {
        final String lowerQuery = query.toLowerCase();
        return bookCatalog.values().stream()
            .filter(book -> book.getTitle().toLowerCase().contains(lowerQuery) ||
                           book.getAuthor().toLowerCase().contains(lowerQuery) ||
                           book.getIsbn().contains(lowerQuery))
            .collect(Collectors.toList());
    }

    @Override
    public List<BookItem> getBookItems(String isbn) {
        return inventory.getOrDefault(isbn, Collections.emptyList());
    }

    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookCatalog.values());
    }

    // --- Patron Management ---

    @Override
    public void addPatron(Patron patron) {
        if (patrons.containsKey(patron.getId())) {
            LOGGER.log(Level.WARNING, "Patron with ID {0} already exists.", patron.getId());
        } else {
            patrons.put(patron.getId(), patron);
            LOGGER.log(Level.INFO, "Patron added: {0}", patron.getName());
        }
    }
    
    @Override
    public void updatePatron(int id, String newName, String newContactInfo) {
        Patron patron = patrons.get(id);
        if (patron != null) {
            patron.setName(newName);
            patron.setContactInfo(newContactInfo);
            LOGGER.log(Level.INFO, "Patron ID {0} updated.", id);
        } else {
            LOGGER.log(Level.WARNING, "Patron with ID {0} not found.", id);
        }
    }
    
    @Override
    public Optional<Patron> findPatronById(int id) {
        return Optional.ofNullable(patrons.get(id));
    }

    @Override
    public List<Patron> getAllPatrons() {
        return new ArrayList<>(patrons.values());
    }
    
    @Override
    public List<Transaction> getPatronHistory(int patronId) {
        Patron patron = patrons.get(patronId);
        if (patron == null) {
            return Collections.emptyList();
        }
        
        return patron.getHistory().stream()
                .sorted(Comparator.comparing(Transaction::getCheckoutDate).reversed()) // Latest first
                .collect(Collectors.toList());
    }

    // --- Lending Process (Checkout and Return) ---

    @Override
    public String checkoutBook(String isbn, int patronId) {
        Patron patron = patrons.get(patronId);
        if (patron == null) return "Error: Patron not found.";

        List<BookItem> availableCopies = inventory.getOrDefault(isbn, new ArrayList<BookItem>()).stream()
            .filter(BookItem::isAvailable)
            .collect(Collectors.toList());

        if (availableCopies.isEmpty()) {
            return "Error: Book is out of stock or all copies are currently borrowed.";
        }

        // Get the first available copy
        BookItem bookItem = availableCopies.get(0);
        bookItem.setAvailable(false); // Update inventory status

        // Create transaction using Factory Pattern
        Transaction transaction = TransactionFactory.createNewTransaction(patron, bookItem.getBook());
        
        // Track the current checkout by BookItem ID
        currentCheckouts.put(bookItem.getId(), transaction);
        patron.addTransaction(transaction); // Update patron history

        LOGGER.log(Level.INFO, "Checkout successful: {0} by Patron {1}", 
                   new Object[]{bookItem.getBook().getTitle(), patron.getName()});
        return String.format("Success: Book '%s' (Item ID: %d) checked out by Patron '%s'. Due date: %s", 
                             bookItem.getBook().getTitle(), bookItem.getId(), patron.getName(), 
                             transaction.getCheckoutDate().plusDays(14));
    }

	@Override
    public String returnBook(String isbn, int patronId) {
        Patron patron = patrons.get(patronId);
        if (patron == null) return "Error: Patron not found.";

        // Find the active transaction for this patron and book
        Optional<Transaction> transactionOpt = currentCheckouts.values().stream()
            .filter(t -> t.getPatron().getId() == patronId && 
                         t.getBook().getIsbn().equals(isbn))
            .findFirst();

        if (transactionOpt.empty() != null) {
            return "Error: No active checkout found for this book and patron combination.";
        }

        Transaction transaction = transactionOpt.get();
        
        // Find the corresponding BookItem to set availability
        Optional<BookItem> returnedItemOpt = inventory.getOrDefault(isbn, new ArrayList<BookItem>()).stream()
            .filter(item -> item.getBook().equals(transaction.getBook()))
            .findFirst();
        
        if (returnedItemOpt.isPresent()) {
            returnedItemOpt.get().setAvailable(true); // Update inventory
        }

        // Complete the transaction
        transaction.setReturnDate(LocalDate.now());
        
        // Fine System (Strategy Pattern)
        double fine = fineStrategy.calculateFine(transaction.getCheckoutDate(), transaction.getReturnDate());
        transaction.setFineAmount(fine);
        
        // Remove from current checkouts: Find the key (BookItem ID) for the transaction
        Integer bookItemIdToRemove = currentCheckouts.entrySet().stream()
                .filter(entry -> entry.getValue().equals(transaction))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (bookItemIdToRemove != null) {
            currentCheckouts.remove(bookItemIdToRemove);
        }

        LOGGER.log(Level.INFO, "Return successful: {0} by Patron {1}. Fine: ${2}", 
                   new Object[]{transaction.getBook().getTitle(), patron.getName(), fine});
        
        String fineMessage = fine > 0 ? String.format("A fine of $%.2f was assessed.", fine) : "No fine assessed.";
        return String.format("Success: Book '%s' returned by Patron '%s'. %s", 
                             transaction.getBook().getTitle(), patron.getName(), fineMessage);
    }
    
    // --- Inventory Management ---
    
    @Override
    public List<Transaction> getBorrowedBooks() {
        return new ArrayList<>(currentCheckouts.values());
    }
}