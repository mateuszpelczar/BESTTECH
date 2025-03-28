package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Produkt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer produktID;

    private String nazwa;
    private String opis;
    private float cena;
    private Integer stanMagazynowy;
    private String marka;
    private Date dataDodania;
    @ManyToOne
    @JoinColumn(name = "kategoriaID")
    private Kategoria kategoria;

    @OneToMany(mappedBy = "produkt")
    private List<SzczegolyZamowienia> szczegolyZamowien;

    @OneToMany(mappedBy = "produkt")
    private List<Recenzja> recenzje;

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
    public void setOpis(String opis) {
        this.opis = opis;
    }

    public void setCena(float cena) {
        this.cena = cena;
    }
    public void setStanMagazynowy(int stanMagazynowy) {
        this.stanMagazynowy = stanMagazynowy;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public void setDataDodania(Date dataDodania) {
        this.dataDodania = dataDodania;
    }

    public void setKategoria(Kategoria kategoria) {
        this.kategoria = kategoria;
    }

    public Integer getProduktID() {
        return produktID;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getOpis() {
        return opis;
    }

    public float getCena() {
        return cena;
    }

    public Integer getStanMagazynowy() {
        return stanMagazynowy;
    }

    public String getMarka() {
        return marka;
    }

    public Date getDataDodania() {
        return dataDodania;
    }

    public Kategoria getKategoria() {
        return kategoria;
    }


}
