package com.aplikacjazespolowa.BESTTECH.models;

import com.aplikacjazespolowa.BESTTECH.dto.RaportSprzedazyDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
/**
 * Encja reprezentująca zamówienie w systemie sklepu internetowego.
 * <p>
 * Przechowuje informacje o dacie zamówienia, statusie, koszcie, powiązanych użytkowniku, adresie dostawy,
 * szczegółach zamówienia, reklamacjach, fakturze, dostawie oraz ewentualnych zwrotach.
 */
@Entity
@Getter
@Setter
public class Zamowienie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer zamowienieID;
    private LocalDate dataZamowienia;
    private String status;
    private Float kosztCalkowity;

    @ManyToOne
    @JoinColumn(name = "userID")
    private DBUser klient;

    @ManyToOne
    @JoinColumn(name = "adresDostawyID")
    private AdresDostawy adresDostawy;

    @OneToMany(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private List<SzczegolyZamowienia> szczegolyZamowienia;

    @OneToMany(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private List<Reklamacja> reklamacje;

    //faktury sa przechowywane nawet po usunieciu zamowienia
    @OneToOne(mappedBy = "zamowienie", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Faktura faktura;

    @OneToOne(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private Dostawa dostawa;

    @OneToMany(mappedBy = "zamowienie", cascade = CascadeType.ALL)
    private List<Zwrot> zwroty;

    public Integer getZamowienieID() {
        return zamowienieID;
    }

    public void setZamowienieID(Integer zamowienieID) {
        this.zamowienieID = zamowienieID;
    }

    public LocalDate getDataZamowienia() {
        return dataZamowienia;
    }

    public void setDataZamowienia(LocalDate dataZamowienia) {
        this.dataZamowienia = dataZamowienia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getKosztCalkowity() {
        return kosztCalkowity;
    }

    public void setKosztCalkowity(Float kosztCalkowity) {
        this.kosztCalkowity = kosztCalkowity;
    }

    public DBUser getKlient() {
        return klient;
    }

    public void setKlient(DBUser klient) {
        this.klient = klient;
    }

    public AdresDostawy getAdresDostawy() {
        return adresDostawy;
    }

    public void setAdresDostawy(AdresDostawy adresDostawy) {
        this.adresDostawy = adresDostawy;
    }

    public List<SzczegolyZamowienia> getSzczegolyZamowienia() {
        return szczegolyZamowienia;
    }

    public void setSzczegolyZamowienia(List<SzczegolyZamowienia> szczegolyZamowienia) {
        this.szczegolyZamowienia = szczegolyZamowienia;
    }

    public List<Reklamacja> getReklamacje() {
        return reklamacje;
    }

    public void setReklamacje(List<Reklamacja> reklamacje) {
        this.reklamacje = reklamacje;
    }

    public Faktura getFaktura() {
        return faktura;
    }

    public void setFaktura(Faktura faktura) {
        this.faktura = faktura;
    }

    public Dostawa getDostawa() {
        return dostawa;
    }

    public void setDostawa(Dostawa dostawa) {
        this.dostawa = dostawa;
    }

    public List<Zwrot> getZwroty() {
        return zwroty;
    }

    public void setZwroty(List<Zwrot> zwroty) {
        this.zwroty = zwroty;
    }


    @Transient
    public long getDniOdZamowienia() {
        if (dataZamowienia == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(dataZamowienia, LocalDate.now());
    }

}
