package com.example.books;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
@CrossOrigin(origins = "http://localhost:5173") // React frontend (port 5173) ki CORS permissions
public class BookController {

    private List<Book> books = new ArrayList<>(Arrays.asList(
        new Book(1, "The Alchemist", "Paulo Coelho", "Fiction", 1988),
        new Book(2, "To Kill a Mockingbird", "Harper Lee", "Fiction", 1960),
        new Book(3, "1984", "George Orwell", "Dystopian", 1949),
        new Book(4, "A Brief History of Time", "Stephen Hawking", "Science", 1988),
        new Book(5, "The Great Gatsby", "F. Scott Fitzgerald", "Classic", 1925),
        new Book(6, "Animal Farm", "George Orwell", "Satire", 1945),
        new Book(7, "Clean Code", "Robert C. Martin", "Programming", 2008),
        new Book(8, "Effective Java", "Joshua Bloch", "Programming", 2001),
        new Book(9, "The Pragmatic Programmer", "Andrew Hunt", "Programming", 1999)
    ));

    // Get all books
    @GetMapping
    public List<Book> getAllBooks() {
        return books;
    }

    // Get book by ID
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    // Add a new book (POST method for frontend submission)
    @PostMapping
    public Book addBook(@RequestBody Book newBook) {
        books.add(newBook);
        return newBook;
    }

    // Search by author
    @GetMapping("/search")
    public List<Book> searchByAuthor(@RequestParam String author) {
        return books.stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Get books by genre
    @GetMapping("/genre/{genre}")
    public List<Book> getBooksByGenre(@PathVariable String genre) {
        return books.stream()
                .filter(b -> b.getGenre() != null && b.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    // Publication statistics
    @GetMapping("/stats/publication")
    public Map<String, Object> getPublicationStats() {
        List<Integer> years = books.stream().map(Book::getPublicationYear).sorted().collect(Collectors.toList());
        int earliest = years.get(0);
        int latest = years.get(years.size()-1);
        double average = years.stream().mapToInt(Integer::intValue).average().orElse(0);
        double median = years.size() % 2 == 0 ?
                (years.get(years.size()/2 -1) + years.get(years.size()/2)) / 2.0 :
                years.get(years.size()/2);
        double std = Math.sqrt(years.stream().mapToDouble(y -> Math.pow(y - average,2)).average().orElse(0));
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("earliestPublicationYear", earliest);
        m.put("latestPublicationYear", latest);
        m.put("averagePublicationYear", average);
        m.put("medianPublicationYear", median);
        m.put("stdDeviationPublicationYear", std);
        return m;
    }

    // Author statistics
    @GetMapping("/stats/authors")
    public Map<String, Object> getAuthorStats() {
        Map<String, Long> counts = books.stream()
                .filter(b -> b.getAuthor() != null && !b.getAuthor().isBlank())
                .collect(Collectors.groupingBy(Book::getAuthor, Collectors.counting()));

        int totalAuthors = counts.size();
        String topAuthor = counts.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        String leastAuthor = counts.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        List<Long> values = counts.values().stream().sorted().collect(Collectors.toList());
        double averageBooksPerAuthor = values.stream().mapToLong(Long::longValue).average().orElse(0);
        double medianBooksPerAuthor = values.size() % 2 == 0 ?
                (values.get(values.size()/2 -1) + values.get(values.size()/2)) / 2.0 :
                values.get(values.size()/2);

        Map<String,Object> out = new LinkedHashMap<>();
        out.put("totalAuthors", totalAuthors);
        out.put("topAuthor", topAuthor);
        out.put("leastAuthor", leastAuthor);
        out.put("averageBooksPerAuthor", averageBooksPerAuthor);
        out.put("medianBooksPerAuthor", medianBooksPerAuthor);
        return out;
    }
}