# 📚 Library Management System (LMS)

This is a Java-based command-line Library Management System designed to help librarians efficiently manage books, patrons, and the lending process. It demonstrates strong adherence to Object-Oriented Programming (OOP) principles, SOLID principles, and utilizes relevant design patterns.

## 🌟 Features

* **Book Management:** Add, remove, update, and search books by Title, Author, or ISBN.
* **Patron Management:** Register new patrons and update their contact information.
* **Inventory Control:** Keep track of available and borrowed physical copies (`BookItem`).
* **Lending Process:** Seamless checkout and return functionality.
* **Fine System:** Automatically calculates a fine of **$10/day** for books returned **14 days** after checkout.
* **Patron History:** View a complete history of all borrow and return transactions for any patron.
* **Logging:** Uses `java.util.logging` to record important events and errors.

## 🛠️ Technical Design & Implementation

The project is structured into modular packages to ensure maintainability and adherence to best practices.

### Object-Oriented Programming (OOP)

* **Encapsulation:** All model class attributes (`Book`, `Patron`, `Transaction`) are private, with access managed via public getters and setters.
* **Inheritance/Interface (Polymorphism):** The core business logic is defined by the `LibraryService` interface, with the concrete implementation in `LibraryServiceImpl`.
* **Abstraction:** The system interacts with abstractions like `LibraryService` and `FineCalculationStrategy`.

### SOLID Principles

| Principle | Implementation Detail |
| :--- | :--- |
| **S**ingle **R**esponsibility | `Book`, `Patron`, and `Transaction` classes handle data; `LibraryServiceImpl` handles business logic; `LibraryApp` handles the user interface. |
| **O**pen/Closed | The `FineCalculationStrategy` allows new fine rules to be introduced (e.g., `PremiumPatronFineStrategy`) without modifying the `LibraryServiceImpl`. |
| **L**iskov **S**ubstitution | `DefaultFineStrategy` is a proper substitute for `FineCalculationStrategy`. |
| **I**nterface **S**egregation | `LibraryService` provides a comprehensive, focused interface for library operations. |
| **D**ependency **I**nversion | `LibraryApp` and `LibraryServiceImpl` depend on the abstraction `LibraryService` rather than a concrete implementation. |

### Design Patterns

1.  **Strategy Pattern:**
    * **Interface:** `FineCalculationStrategy`
    * **Concrete Strategy:** `DefaultFineStrategy`
    * **Usage:** The `LibraryServiceImpl` holds a reference to the strategy, allowing the fine calculation logic to be swapped dynamically without changing the core lending service.
2.  **Factory Pattern:**
    * **Factory:** `TransactionFactory`
    * **Usage:** Simplifies the creation of `Transaction` objects, enforcing the immediate capture of the current date for the checkout date.

## 📦 Project Structure
```
├── src/com/library 
│ ├── app/ 
│ │ └── LibraryApp.java <- Main class and menu-driven interface 
│ ├── model/ 
│ │ ├── Book.java <- Core immutable book details 
│ │ ├── BookItem.java <- Represents a physical copy (inventory) 
│ │ ├── Patron.java <- Library member details and history 
│ │ └── Transaction.java <- Records checkout/return details 
│ └── service/ 
│ ├── FineCalculationStrategy.java <- Strategy Interface (Strategy Pattern) 
│ ├── DefaultFineStrategy.java <- Concrete Strategy 
│ ├── TransactionFactory.java <- Factory Pattern 
│ ├── LibraryService.java <- Core Service Interface (DIP) 
│ └── LibraryServiceImpl.java <- Service Implementation 
└── docs/ 
	├── Library_LMS_Class_Diagram.png <- UML Class Diagram (Required Deliverable) 
	└── Library_LMS_Class_Diagram.jpg <- UML Class Diagram (Required Deliverable)	
```
	
## 📐 UML Class Diagram

The following diagram illustrates the relationships and structure of the core classes within the system.

![Class Diagram](docs/class-diagram.png)

## 🚀 Getting Started

### Prerequisites

* **Java Development Kit (JDK) 8** (or higher, but verified compatible with 8).

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/library-management-system.git](https://github.com/YOUR_USERNAME/library-management-system.git)
    cd library-management-system
    ```
2.  **Compile the Java files:**
    ```bash
    # Assuming you compile from the 'src' directory or use an IDE
    javac -d out src/com/library/app/LibraryApp.java src/com/library/model/*.java src/com/library/service/*.java
    ```
3.  **Run the application:**
    ```bash
    java -cp out com.library.app.LibraryApp
    ```

The application will start, load the **seed sample data**, and present the main menu.

### Seed Sample Data
```

The `LibraryApp` automatically loads the following data on startup:

| Entity | Details |
| :--- | :--- |
| **Books** | "Design Patterns" (3 copies), "The Lord of the Rings" (5 copies), "Clean Code" (2 copies) |
| **Patrons** | Alice Johnson, Bob Smith, Charlie Brown (IDs are automatically generated starting from 1000) |
| **Initial Transaction** | One copy of "Design Patterns" is checked out by Alice Johnson for testing the return function. |

---
```