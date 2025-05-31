package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;
/**
 * Encja reprezentująca adres dostawy w systemie sklepu internetowego.
 *
 * Zawiera informacje o adresie dostawy powiązanym z użytkownikiem oraz zamówieniami.
 */
@Entity
@Getter
@Setter
public class AdresDostawy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adresDostawyID;

    @ManyToOne
    @JoinColumn(name="userID", nullable=false)
    private DBUser klient;

    private String ulica;

    public Integer getAdresDostawyID() {
        return adresDostawyID;
    }

    public void setAdresDostawyID(Integer adresDostawyID) {
        this.adresDostawyID = adresDostawyID;
    }

    public DBUser getKlient() {
        return klient;
    }

    public void setKlient(DBUser klient) {
        this.klient = klient;
    }

    public String getUlica() {
        return ulica;
    }

    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    public String getMiasto() {
        return miasto;
    }

    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }

    public String getKodPocztowy() {
        return kodPocztowy;
    }

    public void setKodPocztowy(String kodPocztowy) {
        this.kodPocztowy = kodPocztowy;
    }

    public String getKraj() {
        return kraj;
    }

    public void setKraj(String kraj) {
        this.kraj = kraj;
    }

    public List<Zamowienie> getZamowienia() {
        return zamowienia;
    }

    public void setZamowienia(List<Zamowienie> zamowienia) {
        this.zamowienia = zamowienia;
    }

    private String miasto;
    private String kodPocztowy;
    private String kraj;

    @OneToMany(mappedBy = "adresDostawy")
    private List<Zamowienie> zamowienia;


}
