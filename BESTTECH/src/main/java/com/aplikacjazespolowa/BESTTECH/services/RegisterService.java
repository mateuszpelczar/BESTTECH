package com.aplikacjazespolowa.BESTTECH.services;

import com.aplikacjazespolowa.BESTTECH.models.DBUser;
import com.aplikacjazespolowa.BESTTECH.models.DBRole;
import com.aplikacjazespolowa.BESTTECH.models.DBUserRepository;
import com.aplikacjazespolowa.BESTTECH.models.DBRoleRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final DBUserRepository userRepo;
    private final DBRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(DBUserRepository userRepo, DBRoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String email, String password, String imie, String nazwisko, String telefon) {

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Użytkownik z podanym adresem e-mail już istnieje.");
        }

        DBUser user = new DBUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setImie(imie);
        user.setNazwisko(nazwisko);
        user.setTelefon(telefon);


        DBRole clientRole = roleRepo.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Brak roli CLIENT w bazie"));

        user.getRoles().add(clientRole);
        userRepo.save(user);
    }
}
