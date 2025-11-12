package com.book.library.finePolicy.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "fine_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FinePolicy {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String category;

    @Column(nullable = false)
    private double finePerDay;
}
