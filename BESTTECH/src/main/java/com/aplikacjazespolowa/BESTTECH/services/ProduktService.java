package com.aplikacjazespolowa.BESTTECH.services;

import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Serwis obsługujący operacje na produktach.
 */
@Service
public class ProduktService {
    private final ProduktRepository produktRepository;

    public ProduktService(ProduktRepository produktRepository){
        this.produktRepository=produktRepository;
    }

    /**
     * Pobiera losowe produkty dostępne w magazynie i z przypisanym obrazkiem.
     *
     * @param ile liczba produktów do pobrania
     * @return lista losowych produktów
     */
    public List<Produkt> getLosoweProdukty(int ile){
        //Pobranie wszystkich produktow
        List<Produkt> wszystkie = produktRepository.findAll();

        // Filtrowanie tylko dostępnych produktów z obrazkiem
        List<Produkt> dostepneZObrazkiem = wszystkie.stream()
                .filter(produkt -> produkt.getStanMagazynowy() != 0 && produkt.getZdjecieUrl() != null)
                .collect(Collectors.toList());

        // Tasowanie listy
        Collections.shuffle(dostepneZObrazkiem);

        // Zwracanie losowych produktów
        return dostepneZObrazkiem.stream().limit(ile).collect(Collectors.toList());
    }

    /**
     * Znajduje produkt po identyfikatorze.
     *
     * @param id identyfikator produktu
     * @return produkt lub null jeśli nie znaleziono
     */
    public Produkt findById(Integer id){
        Optional<Produkt> produktOptional=produktRepository.findById(id);
        return  produktOptional.orElse(null);
    }



}
