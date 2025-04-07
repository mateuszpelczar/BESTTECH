package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Klient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer klientID;

    private String login;
    private String haslo;
    private String imie;
    private String nazwisko;
    private String email;
    private String telefon;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<AdresDostawy> adresyDostawy;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Zamowienie> zamowienia;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Recenzja> recenzje;

    public Integer getKlientID() {
        return klientID;
    }

    public void setKlientID(Integer klientID) {
        this.klientID = klientID;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public List<AdresDostawy> getAdresyDostawy() {
        return adresyDostawy;
    }

    public void setAdresyDostawy(List<AdresDostawy> adresyDostawy) {
        this.adresyDostawy = adresyDostawy;
    }

    public List<Zamowienie> getZamowienia() {
        return zamowienia;
    }

    public void setZamowienia(List<Zamowienie> zamowienia) {
        this.zamowienia = zamowienia;
    }

    public List<Recenzja> getRecenzje() {
        return recenzje;
    }

    public void setRecenzje(List<Recenzja> recenzje) {
        this.recenzje = recenzje;
    }
}