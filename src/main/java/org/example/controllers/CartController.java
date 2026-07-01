package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.models.Book;
import org.example.models.CartItem;
import org.example.models.User;
import org.example.repositories.BookRepository;
import org.example.repositories.CartItemRepository;
import org.example.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    private User getAuthenticatedUser(Principal principal) {
        return userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getMyCart(Principal principal) {
        User user = getAuthenticatedUser(principal);
        List<CartItem> myCart = cartItemRepository.findByUser(user);
        return ResponseEntity.ok(myCart);
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<CartItem> addBookToCart(@PathVariable String bookId, Principal principal) {
        User user = getAuthenticatedUser(principal);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki"));

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndBook(user, book);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            return ResponseEntity.ok(cartItemRepository.save(item));
        } else {
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .book(book)
                    .quantity(1)
                    .build();
            return ResponseEntity.ok(cartItemRepository.save(newItem));
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeBookFromCart(@PathVariable String bookId, Principal principal) {
        User user = getAuthenticatedUser(principal);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki"));

        cartItemRepository.findByUserAndBook(user, book).ifPresent(cartItem -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItemRepository.save(cartItem);
            } else {
                cartItemRepository.delete(cartItem);
            }
        });

        return ResponseEntity.noContent().build();
    }
}