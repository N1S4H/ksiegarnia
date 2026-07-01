package org.example.controllers;


import org.example.models.Book;
import org.example.repositories.BookRepository;
import org.example.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;

    public BookController(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> getAll() {
        return bookService.findAllBooks();
    }

    @GetMapping("/{id}")
    public Book get(@PathVariable String id) {
        return bookService.findById(id);
    }

    @PostMapping
    public Book create(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setCategory(updatedBook.getCategory());
                    existingBook.setAuthor(updatedBook.getAuthor());
                    existingBook.setTitle(updatedBook.getTitle());
                    existingBook.setYear(updatedBook.getYear());
                    existingBook.setIsbn(updatedBook.getIsbn());
                    existingBook.setPrice(updatedBook.getPrice());
                    Book savedBook = bookRepository.save(existingBook);
                    return ResponseEntity.ok(savedBook);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}