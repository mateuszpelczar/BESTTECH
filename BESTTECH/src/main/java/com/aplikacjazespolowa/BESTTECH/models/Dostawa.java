package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Dostawa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dostawaID;

    @OneToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    private Date dataWysylki;
    private Date przewidywanaDostawy;
    private String status;
    private String metodaDostawy;

    public Integer getDostawaID() {
        return dostawaID;
    }

    public void setDostawaID(Integer dostawaID) {
        this.dostawaID = dostawaID;
    }

    public Zamowienie getZamowienie() {
        return zamowienie;
    }

    public void setZamowienie(Zamowienie zamowienie) {
        this.zamowienie = zamowienie;
    }

    public Date getDataWysylki() {
        return dataWysylki;
    }

    public void setDataWysylki(Date dataWysylki) {
        this.dataWysylki = dataWysylki;
    }

    public Date getPrzewidywanaDostawy() {
        return przewidywanaDostawy;
    }

    public void setPrzewidywanaDostawy(Date przewidywanaDostawy) {
        this.przewidywanaDostawy = przewidywanaDostawy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetodaDostawy() {
        return metodaDostawy;
    }

    public void setMetodaDostawy(String metodaDostawy) {
        this.metodaDostawy = metodaDostawy;
    }
}
