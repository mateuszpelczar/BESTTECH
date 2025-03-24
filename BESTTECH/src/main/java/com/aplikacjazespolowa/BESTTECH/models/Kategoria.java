package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Kategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kategoriaID;

    private String nazwa;
    private String opis;

    @OneToMany(mappedBy = "kategoria")
    private List<Produkt> produkty;

}
