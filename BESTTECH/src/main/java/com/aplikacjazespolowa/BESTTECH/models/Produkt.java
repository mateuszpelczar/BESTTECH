package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
//@Getter
//@Setter
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

    @Column(name = "zdjecie_url", columnDefinition = "text")
    private String zdjecieUrl;

    public String getZdjecieUrl() {
        return zdjecieUrl;
    }

    public void setZdjecieUrl(String zdjecieUrl) {
        this.zdjecieUrl = zdjecieUrl;
    }

    @ManyToOne
    @JoinColumn(name = "kategoriaID")
    private Kategoria kategoria;

    @OneToMany(mappedBy = "produkt")
    private List<SzczegolyZamowienia> szczegolyZamowien;

    @OneToMany(mappedBy = "produkt")
    private List<Recenzja> recenzje;

    public Produkt(){}

    public Produkt(String nazwa, String opis, float cena, Integer stanMagazynowy, String marka, Date dataDodania, Kategoria kategoria){

        this.nazwa=nazwa;
        this.opis=opis;
        this.cena=cena;
        this.stanMagazynowy=stanMagazynowy;
        this.marka=marka;
        this.dataDodania=dataDodania;
        this.kategoria=kategoria;

    }

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

    public List<Recenzja> getRecenzje() {
        return recenzje;
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
