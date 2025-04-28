package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class DBUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;
    private String password;

    private String imie;
    private String nazwisko;
    private String telefon;

    // dla klienta
    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<AdresDostawy> adresyDostawy;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Zamowienie> zamowienia;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Recenzja> recenzje_klient;

    // role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<DBRole> roles = new HashSet<>();

    // Gettery i settery

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public List<AdresDostawy> getAdresyDostawy() { return adresyDostawy; }
    public void setAdresyDostawy(List<AdresDostawy> adresyDostawy) { this.adresyDostawy = adresyDostawy; }

    public List<Zamowienie> getZamowienia() { return zamowienia; }
    public void setZamowienia(List<Zamowienie> zamowienia) { this.zamowienia = zamowienia; }

    public List<Recenzja> getRecenzje_klient() { return recenzje_klient; }
    public void setRecenzje_klient(List<Recenzja> recenzje_klient) { this.recenzje_klient = recenzje_klient; }

    public Set<DBRole> getRoles() { return roles; }
    public void setRoles(Set<DBRole> roles) { this.roles = roles; }
}
