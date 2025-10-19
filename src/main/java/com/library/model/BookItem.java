package main.java.com.library.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

// Represents a specific physical copy of a Book (for inventory tracking)
public class BookItem {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(100);
    private final int id;
    private final Book book;
    private boolean isAvailable;

    public BookItem(Book book) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.book = book;
        this.isAvailable = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public Book getBook() { return book; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookItem bookItem = (BookItem) o;
        return id == bookItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}