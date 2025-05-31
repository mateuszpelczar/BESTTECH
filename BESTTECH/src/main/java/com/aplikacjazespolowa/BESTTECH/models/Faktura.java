package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
/**
 * Encja reprezentująca fakturę wystawioną do zamówienia w systemie sklepu internetowego.
 *
 * Przechowuje informacje o zamówieniu, dacie wystawienia faktury oraz jej całkowitej kwocie.
 */
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
