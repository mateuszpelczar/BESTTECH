package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Dostawa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dostawaID;

    @OneToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    private Date dataWysylki;
    private Date przewidywanaDostawy;
    private String status;
    private String metodaDostawy;



}
