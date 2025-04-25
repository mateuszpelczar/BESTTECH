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



    //

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<DBRole> roles = new HashSet<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<DBRole> getRoles() { return roles; }
    public void setRoles(Set<DBRole> roles) { this.roles = roles; }
}
