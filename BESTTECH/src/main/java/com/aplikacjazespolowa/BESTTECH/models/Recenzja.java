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

    public Integer getRecenzjaID() {
        return recenzjaID;
    }

    public void setRecenzjaID(Integer recenzjaID) {
        this.recenzjaID = recenzjaID;
    }

    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    public Klient getKlient() {
        return klient;
    }

    public void setKlient(Klient klient) {
        this.klient = klient;
    }

    public Integer getOcena() {
        return ocena;
    }

    public void setOcena(Integer ocena) {
        this.ocena = ocena;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public Date getDataDodania() {
        return dataDodania;
    }

    public void setDataDodania(Date dataDodania) {
        this.dataDodania = dataDodania;
    }

    public Pracownik getPracownik() {
        return pracownik;
    }

    public void setPracownik(Pracownik pracownik) {
        this.pracownik = pracownik;
    }
}
