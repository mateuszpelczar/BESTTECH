package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter
public class Zwrot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer zwrotID;

    @ManyToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    private Date dataZwrotu;
    private String status;
    private String powod;


}
