import java.sql.*;
import java.util.Scanner;

public class LibraryOperations {
    // Add a new book to the database
    public void addBook(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            System.out.print("Enter book title: ");
            String title = scanner.nextLine();
            System.out.print("Enter book author: ");
            String author = scanner.nextLine();
            System.out.print("Enter book publisher: ");
            String publisher = scanner.nextLine();
            System.out.print("Enter publication year: ");
            int year = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String sql = "INSERT INTO books (title, author, publisher, year) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setString(3, publisher);
                stmt.setInt(4, year);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Book added successfully.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public void viewAllBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            String sql = "SELECT * FROM books";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--- All Books ---");
                System.out.println("ID | Title | Author | Publisher | Year");
                while (rs.next()) {
                    System.out.printf("%-3d | %-20s | %-15s | %-15s | %d\n",
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("publisher"),
                            rs.getInt("year"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing books: " + e.getMessage());
        }
    }

    public void searchBookByTitle(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            System.out.print("Enter book title to search: ");
            String title = scanner.nextLine();

            String sql = "SELECT * FROM books WHERE title LIKE ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + title + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("\n--- Search Results ---");
                    System.out.println("ID | Title | Author | Publisher | Year");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.printf("%-3d | %-20s | %-15s | %-15s | %d\n",
                                rs.getInt("id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("publisher"),
                                rs.getInt("year"));
                    }
                    if (!found) {
                        System.out.println("No books found with the title \"" + title + "\".");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching book: " + e.getMessage());
        }
    }

    public void updateBook(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            System.out.print("Enter book ID to update: ");
            int bookId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String checkSql = "SELECT * FROM books WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\nBook Found: ");
                        System.out.printf("ID: %d, Title: %s, Author: %s, Publisher: %s, Year: %d\n",
                                rs.getInt("id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("publisher"),
                                rs.getInt("year"));

                        System.out.println("\nWhat do you want to update?");
                        System.out.println("1. Title\n2. Author\n3. Publisher\n4. Year");
                        System.out.print("Enter your choice: ");
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        String updateSql = null;
                        switch (choice) {
                            case 1:
                                System.out.print("Enter new title: ");
                                String newTitle = scanner.nextLine();
                                updateSql = "UPDATE books SET title = ? WHERE id = ?";
                                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                                    stmt.setString(1, newTitle);
                                    stmt.setInt(2, bookId);
                                    stmt.executeUpdate();
                                    System.out.println("Title updated successfully.");
                                }
                                break;
                            case 2:
                                System.out.print("Enter new author: ");
                                String newAuthor = scanner.nextLine();
                                updateSql = "UPDATE books SET author = ? WHERE id = ?";
                                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                                    stmt.setString(1, newAuthor);
                                    stmt.setInt(2, bookId);
                                    stmt.executeUpdate();
                                    System.out.println("Author updated successfully.");
                                }
                                break;
                            case 3:
                                System.out.print("Enter new publisher: ");
                                String newPublisher = scanner.nextLine();
                                updateSql = "UPDATE books SET publisher = ? WHERE id = ?";
                                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                                    stmt.setString(1, newPublisher);
                                    stmt.setInt(2, bookId);
                                    stmt.executeUpdate();
                                    System.out.println("Publisher updated successfully.");
                                }
                                break;
                            case 4:
                                System.out.print("Enter new year: ");
                                int newYear = scanner.nextInt();
                                updateSql = "UPDATE books SET year = ? WHERE id = ?";
                                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                                    stmt.setInt(1, newYear);
                                    stmt.setInt(2, bookId);
                                    stmt.executeUpdate();
                                    System.out.println("Year updated successfully.");
                                }
                                break;
                            default:
                                System.out.println("Invalid choice.");
                        }
                    } else {
                        System.out.println("No book found with ID " + bookId);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    public void issueBook(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            System.out.print("Enter book ID to issue: ");
            int bookId = scanner.nextInt();
            scanner.nextLine(); 
            System.out.print("Enter name of person issuing to: ");
            String issuedTo = scanner.nextLine();

            String checkSql = "SELECT * FROM books WHERE id = ? AND id NOT IN (SELECT book_id FROM book_issues WHERE status = 'issued')";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String issueSql = "INSERT INTO book_issues (book_id, issued_to, issue_date, status) VALUES (?, ?, CURDATE(), 'issued')";
                        try (PreparedStatement issueStmt = conn.prepareStatement(issueSql)) {
                            issueStmt.setInt(1, bookId);
                            issueStmt.setString(2, issuedTo);
                            issueStmt.executeUpdate();
                            System.out.println("Book issued successfully.");
                        }
                    } else {
                        System.out.println("Book is not available for issue.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    public void returnBook(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            System.out.print("Enter issue ID to return the book: ");
            int issueId = scanner.nextInt();
            scanner.nextLine();

            String checkSql = "SELECT * FROM book_issues WHERE issue_id = ? AND status = 'issued'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, issueId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String returnSql = "UPDATE book_issues SET status = 'available' WHERE issue_id = ?";
                        try (PreparedStatement returnStmt = conn.prepareStatement(returnSql)) {
                            returnStmt.setInt(1, issueId);
                            returnStmt.executeUpdate();
                            System.out.println("Book returned successfully.");
                        }
                    } else {
                        System.out.println("Invalid issue ID or book is not currently issued.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    public void viewIssuedBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            String sql = "SELECT * FROM book_issues WHERE status = 'issued'";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--- Issued Books ---");
                System.out.println("Issue ID | Book ID | Issued To | Issue Date");
                while (rs.next()) {
                    System.out.printf("%-8d | %-6d | %-10s | %s\n",
                            rs.getInt("issue_id"),
                            rs.getInt("book_id"),
                            rs.getString("issued_to"),
                            rs.getDate("issue_date"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing issued books: " + e.getMessage());
        }
    }

    public void viewAvailableBooks() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            String sql = "SELECT * FROM books WHERE id NOT IN (SELECT book_id FROM book_issues WHERE status = 'issued')";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--- Available Books ---");
                System.out.println("ID | Title | Author | Publisher | Year");
                while (rs.next()) {
                    System.out.printf("%-3d | %-20s | %-15s | %-15s | %d\n",
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("publisher"),
                            rs.getInt("year"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing available books: " + e.getMessage());
        }
    }
}
