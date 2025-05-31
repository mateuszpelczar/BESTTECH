package com.aplikacjazespolowa.BESTTECH.Jednostkowe;

import com.aplikacjazespolowa.BESTTECH.dto.RaportSprzedazyDTO;
import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.SzczegolyZamowienia;
import com.aplikacjazespolowa.BESTTECH.models.Zamowienie;
import com.aplikacjazespolowa.BESTTECH.models.ZamowienieRepository;
import com.aplikacjazespolowa.BESTTECH.services.ZamowienieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * Testy jednostkowe dla serwisu ZamowienieService.
 * Testuje generowanie raportu sprzedaży na podstawie zamówień z repozytorium.
 */
@ExtendWith(MockitoExtension.class)
class ZamowienieServiceTest {

    @Mock
    private ZamowienieRepository zamowienieRepository;

    @InjectMocks
    private ZamowienieService zamowienieService;
    /**
     * Inicjalizuje mocki przed każdym testem.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    /**
     * Testuje przypadek, gdy w repozytorium nie ma żadnych zamówień w zadanym zakresie dat.
     * Oczekuje pustej listy raportu sprzedaży.
     */
    @Test
    void testGenerujRaportSprzedazy_GdyBrakZamowien() {
        // Symulacja pustego repozytorium zamówień
        when(zamowienieRepository.findByDataZamowieniaBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        List<RaportSprzedazyDTO> raport = zamowienieService.generujRaportSprzedazy(LocalDate.now().minusDays(7), LocalDate.now());

        assertTrue(raport.isEmpty());
        verify(zamowienieRepository).findByDataZamowieniaBetween(any(LocalDate.class), any(LocalDate.class));
    }
    /**
     * Testuje przypadek, gdy repozytorium zwraca jedno zamówienie z jednym produktem.
     * Oczekuje raportu z jednym wpisem, odpowiadającym temu produktowi.
     */
    @Test
    void testGenerujRaportSprzedazy_GdySaZamowienia() {
        // Tworzymy przykładowe zamówienie
        Produkt produkt = new Produkt();
        produkt.setNazwa("Laptop");

        SzczegolyZamowienia szczegoly = new SzczegolyZamowienia();
        szczegoly.setProdukt(produkt);
        szczegoly.setIlosc(3);

        Zamowienie zamowienie = new Zamowienie();
        zamowienie.setDataZamowienia(LocalDate.now().minusDays(2));
        zamowienie.setSzczegolyZamowienia(Collections.singletonList(szczegoly));

        when(zamowienieRepository.findByDataZamowieniaBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(zamowienie));

        List<RaportSprzedazyDTO> raport = zamowienieService.generujRaportSprzedazy(LocalDate.now().minusDays(7), LocalDate.now());

        assertEquals(1, raport.size());
        assertEquals("Laptop", raport.get(0).getNazwa());
        assertEquals(3, raport.get(0).getSprzedaneSztuki());
        verify(zamowienieRepository).findByDataZamowieniaBetween(any(LocalDate.class), any(LocalDate.class));
    }
}