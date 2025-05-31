//package com.aplikacjazespolowa.BESTTECH.controllers;
//
//import com.aplikacjazespolowa.BESTTECH.models.DBRole;
//import com.aplikacjazespolowa.BESTTECH.models.DBRoleRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//
///**
// * Komponent inicjalizujący bazowe role użytkowników w bazie danych podczas startu aplikacji.
// *
// * Po uruchomieniu aplikacji sprawdza, czy istnieją role: ADMIN, EMPLOYEE oraz CLIENT.
// * Jeśli którejkolwiek z nich brakuje, dodaje ją do bazy.
// */
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final DBRoleRepository roleRepository;
//    /**
//     * Tworzy nowy obiekt DataInitializer z wstrzykniętym repozytorium ról.
//     *
//     * @param roleRepository repozytorium ról użytkowników
//     */
//    public DataInitializer(DBRoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//    }
//    /**
//     * Metoda wykonywana przy starcie aplikacji.
//     * Inicjuje role "ADMIN", "EMPLOYEE" oraz "CLIENT" w bazie danych, jeśli nie istnieją.
//     *
//     * @param args argumenty linii poleceń (nieużywane)
//     * @throws Exception obsługa wyjątku ogólnego
//     */
//    @Override
//    public void run(String... args) throws Exception {
//
//        if (roleRepository.findByName("ADMIN").isEmpty()) {
//            DBRole adminRole = new DBRole();
//            adminRole.setName("ADMIN");
//            roleRepository.save(adminRole);
//        }
//
//        if (roleRepository.findByName("EMPLOYEE").isEmpty()) {
//            DBRole employeeRole = new DBRole();
//            employeeRole.setName("EMPLOYEE");
//            roleRepository.save(employeeRole);
//        }
//
//        if (roleRepository.findByName("CLIENT").isEmpty()) {
//            DBRole clientRole = new DBRole();
//            clientRole.setName("CLIENT");
//            roleRepository.save(clientRole);
//        }
//    }
//}
