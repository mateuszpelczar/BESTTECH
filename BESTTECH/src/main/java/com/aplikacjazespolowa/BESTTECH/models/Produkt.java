package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Produkt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer produktID;

    private String nazwa;
    private String opis;
    private float cena;
    private Integer stanMagazynowy;
    private String marka;
    private Date dataDodania;
    @ManyToOne
    @JoinColumn(name = "kategoriaID")
    private Kategoria kategoria;

    @OneToMany(mappedBy = "produkt")
    private List<SzczegolyZamowienia> szczegolyZamowien;

    @OneToMany(mappedBy = "produkt")
    private List<Recenzja> recenzje;
}
