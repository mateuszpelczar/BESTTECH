package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Faktura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fakturaID;

    @OneToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    private Date dataWystawienia;
    private Float kwotaCalkowita;

}
