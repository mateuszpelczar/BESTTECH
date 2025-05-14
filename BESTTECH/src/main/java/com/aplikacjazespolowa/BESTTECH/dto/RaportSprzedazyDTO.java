package com.aplikacjazespolowa.BESTTECH.dto;

public class RaportSprzedazyDTO {
    private String nazwa;
    private int sprzedaneSztuki;

    public RaportSprzedazyDTO(String nazwa, int sprzedaneSztuki) {
        this.nazwa = nazwa;
        this.sprzedaneSztuki = sprzedaneSztuki;
    }

    // Gettery i Settery
    public String getNazwa() { return nazwa; }
    public int getSprzedaneSztuki() { return sprzedaneSztuki; }
}
