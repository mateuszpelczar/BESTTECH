package com.aplikacjazespolowa.BESTTECH.dto;
/**
 * Data Transfer Object (DTO) służący do przekazywania danych raportu sprzedaży.
 *
 * Zawiera informacje o nazwie produktu oraz liczbie sprzedanych sztuk w danym okresie.
 */
public class RaportSprzedazyDTO {
    private String nazwa;
    private int sprzedaneSztuki;
    /**
     * Tworzy nowy obiekt RaportSprzedazyDTO.
     *
     * @param nazwa          nazwa produktu
     * @param sprzedaneSztuki liczba sprzedanych sztuk
     */
    public RaportSprzedazyDTO(String nazwa, int sprzedaneSztuki) {
        this.nazwa = nazwa;
        this.sprzedaneSztuki = sprzedaneSztuki;
    }

    // Gettery i Settery
    public String getNazwa() { return nazwa; }
    public int getSprzedaneSztuki() { return sprzedaneSztuki; }
}
