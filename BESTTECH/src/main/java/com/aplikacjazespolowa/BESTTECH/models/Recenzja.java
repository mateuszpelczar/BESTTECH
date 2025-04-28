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
    @JoinColumn(name = "userID")
    private DBUser klient;

    private Integer ocena;
    private String tresc;
    private Date dataDodania;

    @Column(nullable = false)
    private String status = "OCZEKUJACA"; // Domyślnie nowa recenzja ma status OCZEKUJACA

    // Gettery i settery

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

    public DBUser getKlient() {
        return klient;
    }

    public void setKlient(DBUser klient) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
