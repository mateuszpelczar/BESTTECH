package com.aplikacjazespolowa.BESTTECH.Jednostkowe;

import com.aplikacjazespolowa.BESTTECH.models.Produkt;
import com.aplikacjazespolowa.BESTTECH.models.ProduktRepository;
import com.aplikacjazespolowa.BESTTECH.services.ProduktService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProduktRepository produktRepository;

    @InjectMocks
    private ProduktService produktService;

    @Test
    void testGetLosoweProdukty_GdyBrakProduktow() {
        when(produktRepository.findAll()).thenReturn(Collections.emptyList());

        List<Produkt> produkty = produktService.getLosoweProdukty(5);

        assertTrue(produkty.isEmpty());
        verify(produktRepository).findAll();
    }

    @Test
    void testGetLosoweProdukty_GdySaDostepneProdukty() {
        Produkt produkt1 = new Produkt("Laptop", "Opis", 3000f, 10, "Dell", LocalDate.now(), 1, "laptop.jpg");
        Produkt produkt2 = new Produkt("Telefon", "Opis", 1500f, 5, "Samsung", LocalDate.now(), 2, "telefon.jpg");
        Produkt produkt3 = new Produkt("Tablet", "Opis", 1000f, 1, "Apple", LocalDate.now(), 3, "tablet.jpg");

        when(produktRepository.findAll()).thenReturn(Arrays.asList(produkt1, produkt2, produkt3));

        List<Produkt> produkty = produktService.getLosoweProdukty(2);

        assertFalse(produkty.isEmpty());
        assertEquals(2, produkty.size());
        verify(produktRepository).findAll();
    }
}