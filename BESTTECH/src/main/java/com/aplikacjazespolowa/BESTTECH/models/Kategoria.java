package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * Encja reprezentująca kategorię produktu w systemie sklepu internetowego.
 * <p>
 * Przechowuje informacje o nazwie, opisie oraz powiązane produkty należące do danej kategorii.
 */
@Entity
//@Getter
//@Setter
public class Kategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer kategoriaID;

    private String nazwa;
    private String opis;

    @OneToMany(mappedBy = "kategoria")
    private List<Produkt> produkty;

    public Kategoria(){}

    public Kategoria(String nazwa, String opis){
        this.nazwa= nazwa;
        this.opis=opis;
    }

    public Kategoria(int kategoria) {
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public void setOpis(String opis) {
        this.opis = opis;

    }

    public String getNazwa(){
        return this.nazwa;
    }

    public String getOpis(){
        return this.opis;
    }

    public Integer getKategoriaID() {
        return this.kategoriaID;
    }

}
