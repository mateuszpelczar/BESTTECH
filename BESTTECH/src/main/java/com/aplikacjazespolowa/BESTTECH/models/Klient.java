package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Klient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer klientID;

    private String login;
    private String haslo;
    private String imie;
    private String nazwisko;
    private String email;
    private String telefon;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<AdresDostawy> adresyDostawy;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Zamowienie> zamowienia;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Recenzja> recenzje;
}