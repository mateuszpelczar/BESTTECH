package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Reklamacja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reklamacjaID;

    @ManyToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    private String status;

    @Temporal(TemporalType.DATE)
    private Date dataZgloszenia;

    private String opis;

    private String komentarzAdmina;

    public String getKomentarzAdmina() {
        return komentarzAdmina;
    }

    public void setKomentarzAdmina(String komentarzAdmina) {
        this.komentarzAdmina = komentarzAdmina;
    }


    public Integer getReklamacjaID() {
        return reklamacjaID;
    }

    public Zamowienie getZamowienie() {
        return zamowienie;
    }

    public String getStatus() {
        return status;
    }

    public Date getDataZgloszenia() {
        return dataZgloszenia;
    }

    public String getOpis() {
        return opis;
    }



    public void setReklamacjaID(Integer reklamacjaID) {
        this.reklamacjaID = reklamacjaID;
    }

    public void setZamowienie(Zamowienie zamowienie) {
        this.zamowienie = zamowienie;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDataZgloszenia(Date dataZgloszenia) {
        this.dataZgloszenia = dataZgloszenia;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
