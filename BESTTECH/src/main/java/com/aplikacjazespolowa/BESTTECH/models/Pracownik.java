package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Pracownik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pracownikID;

    private String imie;
    private String nazwisko;
    private String login;
    private String haslo;
    private String rola;

    @OneToMany(mappedBy = "pracownik")
    private List<Recenzja> recenzje;

}
