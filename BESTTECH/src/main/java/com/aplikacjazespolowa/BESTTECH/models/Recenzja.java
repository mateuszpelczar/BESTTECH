package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Recenzja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recenzjaID;

    @ManyToOne
    @JoinColumn(name = "produktID")
    private Produkt produkt;

    @ManyToOne
    @JoinColumn(name = "klientID")
    private Klient klient;

    private Integer ocena;
    private String tresc;
    private Date dataDodania;

    @ManyToOne
    @JoinColumn(name = "pracownikID")
    private Pracownik pracownik;




}
