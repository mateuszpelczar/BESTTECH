package com.aplikacjazespolowa.BESTTECH.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class DBRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<DBUser> users = new HashSet<>();


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<DBUser> getUsers() { return users; }
    public void setUsers(Set<DBUser> users) { this.users = users; }
}
