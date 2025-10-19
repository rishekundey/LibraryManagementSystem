package main.java.com.library.app;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.com.library.model.Book;
import main.java.com.library.model.BookItem;
import main.java.com.library.model.Patron;
import main.java.com.library.model.Transaction;
import main.java.com.library.service.LibraryService;
import main.java.com.library.service.LibraryServiceImpl;

// Menu-driven solution for the Librarian
public class LibraryApp {
	private static final Logger LOGGER = Logger.getLogger(LibraryApp.class.getName());
	private final LibraryService libraryService;
	private final Scanner scanner;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public LibraryApp(LibraryService libraryService) {
		this.libraryService = libraryService;
		this.scanner = new Scanner(System.in);
		LOGGER.setLevel(Level.INFO); // Set default logging level
	}

	// --- Seed Data Setup ---
	private void seedData() {
		// Books
		Book b1 = new Book("Design Patterns", "Erich Gamma", "978-0201633610", 1994);
		Book b2 = new Book("The Lord of the Rings", "J.R.R. Tolkien", "978-0618260234", 1954);
		Book b3 = new Book("Clean Code", "Robert C. Martin", "978-0132350884", 2008);

		libraryService.addBook(b1, 3); // 3 copies
		libraryService.addBook(b2, 5); // 5 copies
		libraryService.addBook(b3, 2); // 2 copies

		// Patrons
		Patron p1 = new Patron("Alice Johnson", "alice@mail.com");
		Patron p2 = new Patron("Bob Smith", "bob@mail.com");
		Patron p3 = new Patron("Charlie Brown", "charlie@mail.com");

		libraryService.addPatron(p1);
		libraryService.addPatron(p2);
		libraryService.addPatron(p3);

		// Initial Checkout for testing (p1 borrows Design Patterns)
		libraryService.checkoutBook(b1.getIsbn(), p1.getId());

		System.out.println("\n--- Seed Data Loaded ---");
		System.out.printf("Patron IDs: %d (Alice), %d (Bob), %d (Charlie)\n", p1.getId(), p2.getId(), p3.getId());
		System.out.println("Book ISBNs: " + b1.getIsbn() + ", " + b2.getIsbn() + ", " + b3.getIsbn());
		System.out.println("One copy of 'Design Patterns' is currently borrowed by Alice.");
		System.out.println("--------------------------\n");
	}

	public void start() {
		seedData();
		int choice;
		do {
			displayMenu();
			try {
				System.out.print("Enter your choice: ");
				choice = scanner.nextInt();
				scanner.nextLine(); // Consume newline

				switch (choice) {
				case 1:
					manageBooksMenu();
					break;
				case 2:
					managePatronsMenu();
					break;
				case 3:
					lendingProcessMenu();
					break;
				case 4:
					inventoryMenu();
					break;
				case 5:
					searchBookMenu();
					break;
				case 0:
					System.out.println("Exiting Library Management System. Goodbye!");
					Thread.sleep(1000);
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
				}
			} catch (InputMismatchException e) {
				LOGGER.log(Level.SEVERE, "Invalid input. Please enter a number.", e);
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine(); // Clear the buffer
				choice = -1;
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "An unexpected error occurred.", e);
				System.out.println("An unexpected error occurred: " + e.getMessage());
				choice = -1;
			}
			System.out.println();
		} while (choice != 0);
	}

	private void displayMenu() {
		System.out.println("=========================================");
		System.out.println("    LIBRARY MANAGEMENT SYSTEM - MAIN MENU");
		System.out.println("=========================================");
		System.out.println("1. Manage Books");
		System.out.println("2. Manage Patrons");
		System.out.println("3. Lending Process (Checkout/Return)");
		System.out.println("4. Inventory & Patron History");
		System.out.println("5. Search Books");
		System.out.println("0. Exit");
		System.out.println("=========================================");
	}

	// --- Functional Requirement Implementations ---

	private void manageBooksMenu() {
		int choice;
		do {
			System.out.println("\n--- Manage Books ---");
			System.out.println("1. Add New Book & Copies");
			System.out.println("2. Remove Book (by ISBN)");
			System.out.println("3. List All Books");
			System.out.println("0. Back to Main Menu");
			System.out.print("Enter choice: ");
			choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				addBook();
				break;
			case 2:
				removeBook();
				break;
			case 3:
				listAllBooks();
				break;
			case 0:
				return;
			default:
				System.out.println("Invalid choice.");
			}
		} while (true);
	}

	private void addBook() {
		System.out.print("Enter Title: ");
		String title = scanner.nextLine();
		System.out.print("Enter Author: ");
		String author = scanner.nextLine();
		System.out.print("Enter ISBN: ");
		String isbn = scanner.nextLine();
		System.out.print("Enter Publication Year: ");
		int year = scanner.nextInt();
		System.out.print("Enter number of copies to add: ");
		int copies = scanner.nextInt();
		scanner.nextLine();

		Book book = new Book(title, author, isbn, year);
		libraryService.addBook(book, copies);
		System.out.println("Book and copies successfully added.");
	}

	private void removeBook() {
		System.out.print("Enter ISBN of the book to remove: ");
		String isbn = scanner.nextLine();
		libraryService.removeBook(isbn);
	}

	private void listAllBooks() {
		System.out.println("\n--- All Books in Catalog ---");
		libraryService.getAllBooks().forEach(System.out::println);
	}

	private void managePatronsMenu() {
		int choice;
		do {
			System.out.println("\n--- Manage Patrons ---");
			System.out.println("1. Add New Patron");
			System.out.println("2. Update Patron Info");
			System.out.println("3. List All Patrons");
			System.out.println("0. Back to Main Menu");
			System.out.print("Enter choice: ");
			choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				addPatron();
				break;
			case 2:
				updatePatron();
				break;
			case 3:
				listPatrons();
				break;
			case 0:
				return;
			default:
				System.out.println("Invalid choice.");
			}
		} while (true);
	}

	private void addPatron() {
		System.out.print("Enter Patron Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Contact Info: ");
		String contact = scanner.nextLine();

		Patron patron = new Patron(name, contact);
		libraryService.addPatron(patron);
		System.out.printf("Patron '%s' added with ID: %d\n", patron.getName(), patron.getId());
	}

	private void updatePatron() {
		System.out.print("Enter Patron ID to update: ");
		int id = scanner.nextInt();
		scanner.nextLine();

		Optional<Patron> patronOpt = libraryService.findPatronById(id);
		if (!patronOpt.isPresent()) {
			System.out.println("Patron not found.");
			return;
		}

		Patron patron = patronOpt.get();
		System.out.printf("Current Name: %s. Enter New Name (leave blank to keep): ", patron.getName());
		String newName = scanner.nextLine();
		if (newName.isEmpty())
			newName = patron.getName();

		System.out.printf("Current Contact: %s. Enter New Contact (leave blank to keep): ", patron.getContactInfo());
		String newContact = scanner.nextLine();
		if (newContact.isEmpty())
			newContact = patron.getContactInfo();

		libraryService.updatePatron(id, newName, newContact);
		System.out.println("Patron info updated successfully.");
	}

	private void listPatrons() {
		System.out.println("\n--- All Patrons ---");
		libraryService.getAllPatrons().forEach(System.out::println);
	}

	private void lendingProcessMenu() {
		int choice;
		do {
			System.out.println("\n--- Lending Process ---");
			System.out.println("1. Checkout Book");
			System.out.println("2. Return Book (with Fine System)");
			System.out.println("0. Back to Main Menu");
			System.out.print("Enter choice: ");
			choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				checkoutBook();
				break;
			case 2:
				returnBook();
				break;
			case 0:
				return;
			default:
				System.out.println("Invalid choice.");
			}
		} while (true);
	}

	private void checkoutBook() {
		System.out.print("Enter Patron ID: ");
		int patronId = scanner.nextInt();
		scanner.nextLine();
		System.out.print("Enter Book ISBN to checkout: ");
		String isbn = scanner.nextLine();

		System.out.println(libraryService.checkoutBook(isbn, patronId));
	}

	private void returnBook() {
		System.out.print("Enter Patron ID: ");
		int patronId = scanner.nextInt();
		scanner.nextLine();
		System.out.print("Enter Book ISBN to return: ");
		String isbn = scanner.nextLine();

		System.out.println(libraryService.returnBook(isbn, patronId));
	}

	private void inventoryMenu() {
		int choice;
		do {
			System.out.println("\n--- Inventory & History ---");
			System.out.println("1. List All Currently Borrowed Books");
			System.out.println("2. List Books with Copy Status (Inventory Management)");
			System.out.println("3. View Patron History");
			System.out.println("0. Back to Main Menu");
			System.out.print("Enter choice: ");
			choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				listBorrowedBooks();
				break;
			case 2:
				listBookCopyStatus();
				break;
			case 3:
				viewPatronHistory();
				break;
			case 0:
				return;
			default:
				System.out.println("Invalid choice.");
			}
		} while (true);
	}

	private void listBorrowedBooks() {
		List<Transaction> borrowed = libraryService.getBorrowedBooks();
		if (borrowed.isEmpty()) {
			System.out.println("No books are currently checked out.");
			return;
		}
		System.out.println("\n--- Currently Borrowed Books ---");
		// Using Java 8 forEach with lambda
		borrowed.forEach(t -> System.out.printf("Item: %s | Patron: %s | Due Date: %s\n", t.getBook().getTitle(),
				t.getPatron().getName(), t.getCheckoutDate().plusDays(14).format(DATE_FORMATTER)));
	}

	private void listBookCopyStatus() {
		System.out.print("Enter Book ISBN to check status: ");
		String isbn = scanner.nextLine();

		Optional<Book> bookOpt = libraryService.findBookByIsbn(isbn);
		if (!bookOpt.isPresent()) {
			System.out.println("Book with that ISBN not found.");
			return;
		}

		List<BookItem> items = libraryService.getBookItems(isbn);
		long available = items.stream().filter(BookItem::isAvailable).count();
		long total = items.size();

		System.out.printf("\n--- Book Copy Status for '%s' ---\n", bookOpt.get().getTitle());
		System.out.printf("Total Copies: %d\n", total);
		System.out.printf("Available Copies: %d\n", available);
		System.out.printf("Borrowed Copies: %d\n", total - available);
		System.out.println("------------------------------------------");
	}

	private void viewPatronHistory() {
		System.out.print("Enter Patron ID: ");
		int patronId = scanner.nextInt();
		scanner.nextLine();

		Optional<Patron> patronOpt = libraryService.findPatronById(patronId);
		if (!patronOpt.isPresent()) {
			System.out.println("Patron not found.");
			return;
		}

		System.out.println("\n--- Patron History for " + patronOpt.get().getName() + " (ID: " + patronId + ") ---");
		List<Transaction> history = libraryService.getPatronHistory(patronId);
		if (history.isEmpty()) {
			System.out.println("No transaction history found.");
			return;
		}
		history.forEach(System.out::println);
		System.out.println("----------------------------------------------------------");
	}

	private void searchBookMenu() {
		System.out.print("Enter Title, Author, or ISBN to search: ");
		String query = scanner.nextLine();

		List<Book> results = libraryService.searchBooks(query);
		if (results.isEmpty()) {
			System.out.println("No books found matching the search query.");
			return;
		}

		System.out.println("\n--- Search Results ---");
		results.forEach(System.out::println);
	}

	public static void main(String[] args) {
		// Logging configuration - Simple console logging for this environment
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

		LibraryService service = new LibraryServiceImpl();
		LibraryApp app = new LibraryApp(service);
		app.start();
	}
}