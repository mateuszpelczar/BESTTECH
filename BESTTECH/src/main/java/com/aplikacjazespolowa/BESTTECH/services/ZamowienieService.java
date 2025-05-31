package com.aplikacjazespolowa.BESTTECH.services;

import com.aplikacjazespolowa.BESTTECH.dto.RaportSprzedazyDTO; // Poprawiona ścieżka importu
import com.aplikacjazespolowa.BESTTECH.models.SzczegolyZamowienia;
import com.aplikacjazespolowa.BESTTECH.models.Zamowienie;
import com.aplikacjazespolowa.BESTTECH.models.ZamowienieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serwis do obsługi zamówień i generowania raportów sprzedaży.
 */
@Service
public class ZamowienieService {

    @Autowired
    private ZamowienieRepository zamowienieRepository;
    /**
     * Generuje raport sprzedaży dla produktów w wybranym przedziale dat.
     *
     * @param od      data początkowa (włącznie)
     * @param doDaty  data końcowa (włącznie)
     * @return lista DTO raportu sprzedaży (nazwa produktu, ilość sprzedanych sztuk)
     */
    public List<RaportSprzedazyDTO> generujRaportSprzedazy(LocalDate od, LocalDate doDaty) {
        if (od == null || doDaty == null) {
            return List.of();
        }



        List<Zamowienie> szczegoly = zamowienieRepository.findByDataZamowieniaBetween(od, doDaty);

        Map<String, Integer> raport = new HashMap<>();

        for (Zamowienie zamowienie : szczegoly) {
            for (SzczegolyZamowienia szczegol : zamowienie.getSzczegolyZamowienia()) {
                String nazwaProduktu = szczegol.getProdukt().getNazwa();
                raport.put(nazwaProduktu, raport.getOrDefault(nazwaProduktu, 0) + szczegol.getIlosc());
            }
        }

        return raport.entrySet().stream()
                .map(entry -> new RaportSprzedazyDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
