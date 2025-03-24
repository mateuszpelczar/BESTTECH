package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SzczegolyZamowienia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer szczegolyID;

    @ManyToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    @ManyToOne
    @JoinColumn(name = "produktID")
    private Produkt produkt;

    private Integer ilosc;
    private Float cenaJednostkowa;
}
