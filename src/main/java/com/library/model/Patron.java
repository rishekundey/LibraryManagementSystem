package main.java.com.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Patron {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1000);
    private final int id;
    private String name;
    private String contactInfo;
    private final List<Transaction> history; // Patron History

    public Patron(String name, String contactInfo) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.name = name;
        this.contactInfo = contactInfo;
        this.history = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public List<Transaction> getHistory() { return history; }

    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void addTransaction(Transaction transaction) { this.history.add(transaction); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patron patron = (Patron) o;
        return id == patron.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s, Contact: %s", id, name, contactInfo);
    }
}