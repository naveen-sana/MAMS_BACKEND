package com.military.asset.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personnel_units")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
