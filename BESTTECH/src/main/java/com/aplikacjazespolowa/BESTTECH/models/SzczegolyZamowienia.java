package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
/**
 * Encja reprezentująca szczegóły pozycji w zamówieniu.
 * <p>
 * Przechowuje informacje o zamówieniu, produkcie, ilości oraz cenie jednostkowej.
 */
@Entity
@Getter
@Setter
public class SzczegolyZamowienia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer szczegolyID;

    @ManyToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    @ManyToOne
    @JoinColumn(name = "produktID")
    private Produkt produkt;

    private Integer ilosc;
    private Float cenaJednostkowa;

    public Integer getSzczegolyID() {
        return szczegolyID;
    }

    public void setSzczegolyID(Integer szczegolyID) {
        this.szczegolyID = szczegolyID;
    }

    public Zamowienie getZamowienie() {
        return zamowienie;
    }

    public void setZamowienie(Zamowienie zamowienie) {
        this.zamowienie = zamowienie;
    }

    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    public Integer getIlosc() {
        return ilosc;
    }

    public void setIlosc(Integer ilosc) {
        this.ilosc = ilosc;
    }

    public Float getCenaJednostkowa() {
        return cenaJednostkowa;
    }

    public void setCenaJednostkowa(Float cenaJednostkowa) {
        this.cenaJednostkowa = cenaJednostkowa;
    }
}
