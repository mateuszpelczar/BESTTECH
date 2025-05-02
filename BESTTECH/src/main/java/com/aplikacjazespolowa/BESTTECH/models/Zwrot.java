package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter
public class Zwrot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer zwrotID;

    @ManyToOne
    @JoinColumn(name = "zamowienieID")
    private Zamowienie zamowienie;

    @Temporal(TemporalType.DATE)
    private Date dataZwrotu;

    private String status;
    private String powod;

    public Integer getZwrotID() {
        return zwrotID;
    }

    public void setZwrotID(Integer zwrotID) {
        this.zwrotID = zwrotID;
    }

    public Zamowienie getZamowienie() {
        return zamowienie;
    }

    public void setZamowienie(Zamowienie zamowienie) {
        this.zamowienie = zamowienie;
    }

    public Date getDataZwrotu() {
        return dataZwrotu;
    }

    public void setDataZwrotu(Date dataZwrotu) {
        this.dataZwrotu = dataZwrotu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPowod() {
        return powod;
    }

    public void setPowod(String powod) {
        this.powod = powod;
    }
}

