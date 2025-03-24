package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Reklamacja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reklamacjaID;

    @ManyToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    private String status;
    private Date dataZgloszenia;
    private String opis;
    private String statuss;
}
