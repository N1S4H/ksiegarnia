package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.models.*;
import org.example.repositories.CartItemRepository;
import org.example.repositories.OrderRepository;
import org.example.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Principal principal) {
        return userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(Principal principal) {
        User user = getAuthenticatedUser(principal);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Nie można złożyć zamówienia - koszyk jest pusty!");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .book(cartItem.getBook())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getBook().getPrice())
                    .build();
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(orderRepository.findByUserOrderByCreatedAtDesc(user));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            orderRepository.save(order);
            return ResponseEntity.ok(order);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}