package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Zamowienie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer zamowienieID;

    private Date dataZamowienia;
    private String status;
    private Float kosztCalkowity;

    @ManyToOne
    @JoinColumn(name = "klientID")
    private Klient klient;

    @ManyToOne
    @JoinColumn(name = "adresDostawyID")
    private AdresDostawy adresDostawy;

    @OneToMany(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private List<SzczegolyZamowienia> szczegolyZamowienia;

    @OneToMany(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private List<Reklamacja> reklamacje;

    //faktury sa przechowywane nawet po usunieciu zamowienia
    @OneToOne(mappedBy = "zamowienie", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Faktura faktura;

    @OneToOne(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private Dostawa dostawa;

    @OneToMany(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private List<Zwrot> zwroty;
}
