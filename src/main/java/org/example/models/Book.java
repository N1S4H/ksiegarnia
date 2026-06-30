package org.example.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "books")
public class Book {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    private String category;
    private String author;
    private String title;
    private int year;
    private String isbn;

    @Column(columnDefinition = "NUMERIC")
    private double price;

    @Builder
    public Book(String id, String category, String author, String title, int year, String isbn, double price) {
        this.id = id;
        this.category = category;
        this.author = author;
        this.title = title;
        this.year = year;
        this.isbn = isbn;
        this.price = price;
    }
}