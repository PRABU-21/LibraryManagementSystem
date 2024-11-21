import java.util.Scanner;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibraryOperations operations = new LibraryOperations();

        int choice;
        do {
            System.out.println("\n--- Library Management System ---");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book by Title");
            System.out.println("4. Update Book");
            System.out.println("5. Issue Book");
            System.out.println("6. Return Book");
            System.out.println("7. View Issued Books");
            System.out.println("8. View Available Books");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    operations.addBook(scanner);
                    break;
                case 2:
                    operations.viewAllBooks();
                    break;
                case 3:
                    operations.searchBookByTitle(scanner);
                    break;
                case 4:
                    operations.updateBook(scanner);
                    break;
                case 5:
                    operations.issueBook(scanner);
                    break;
                case 6:
                    operations.returnBook(scanner);
                    break;
                case 7:
                    operations.viewIssuedBooks();
                    break;
                case 8:
                    operations.viewAvailableBooks();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 9);

        scanner.close();
    }
}
