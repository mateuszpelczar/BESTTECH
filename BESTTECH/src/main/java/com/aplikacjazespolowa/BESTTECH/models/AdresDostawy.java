package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;

@Entity
@Getter
@Setter
public class AdresDostawy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adresDostawyID;

    @ManyToOne
    @JoinColumn(name="klientID", nullable=false)
    private Klient klient;

    private String ulica;
    private String miasto;
    private String kodPocztowy;
    private String kraj;

    @OneToMany(mappedBy = "adresDostawy")
    private List<Zamowienie> zamowienia;


}
