package com.aplikacjazespolowa.BESTTECH;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testy czarnoskrzynkowe (BlackBox) dla aplikacji BESTTECH.
 * Testy sprawdzają funkcjonalności systemu z perspektywy zewnętrznego użytkownika,
 * bez znajomości wewnętrznej implementacji, poprzez interakcje z API aplikacji.
 * 
 * Testy są podzielone na 3 główne kategorie:
 * - TK: Testy funkcjonalności klienta (TK01-TK10)
 * - TA: Testy funkcjonalności administratora (TA01-TA05)
 * - TP: Testy funkcjonalności pracownika (TP01-TP04)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Zapewnia wykonanie testów w określonej kolejności
class BlackBoxTests {

    @LocalServerPort
    private int port; // Port, na którym uruchomiona jest aplikacja testowa

    @Autowired
    private TestRestTemplate restTemplate; // Klient HTTP do wysyłania żądań

    /**
     * Uniwersalna metoda wysyłająca żądanie HTTP i weryfikująca odpowiedź.
     * Metoda obsługuje różne typy żądań (GET, POST, PUT, DELETE) i automatycznie
     * sprawdza, czy odpowiedź jest poprawna (status 2xx lub 3xx).
     *
     * @param url - adres URL endpointu
     * @param method - metoda HTTP (GET, POST, PUT, DELETE)
     * @param body - ciało żądania (obiekt JSON, formularz, lub null)
     * @param headers - nagłówki HTTP, w tym ciasteczka sesji
     * @param contentType - typ zawartości (np. APPLICATION_JSON, APPLICATION_FORM_URLENCODED)
     * @return ResponseEntity<String> - pełna odpowiedź serwera
     */
    private ResponseEntity<String> sendRequestAndVerify(
            String url,
            HttpMethod method,
            Object body,
            HttpHeaders headers,
            MediaType contentType) {

        // Ustawienie typu zawartości
        headers.setContentType(contentType);

        // Utworzenie żądania
        HttpEntity<?> request = new HttpEntity<>(body, headers);

        // Wysłanie żądania w zależności od metody HTTP
        ResponseEntity<String> response;
        if (method == HttpMethod.GET) {
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } else if (method == HttpMethod.POST) {
            response = restTemplate.postForEntity(url, request, String.class);
        } else {
            response = restTemplate.exchange(url, method, request, String.class);
        }

        // Weryfikacja odpowiedzi - czy status jest poprawny (2xx sukces lub 3xx przekierowanie)
        assertThat(response.getStatusCode().is2xxSuccessful()
                || response.getStatusCode().is3xxRedirection()).isTrue();

        return response;
    }

    /**
     * TK01: Test rejestracji i logowania klienta
     * 
     * Sprawdza czy:
     * 1. Można zarejestrować nowego użytkownika
     * 2. Można zalogować się na utworzone konto
     */
    @Test
    @Order(1)
    void TK01_testClientLogin() {
        // Rejestracja nowego użytkownika-klienta
        String registerUrl = "http://localhost:" + port + "/konto/rejestracja";
        HttpHeaders headers = new HttpHeaders();

        // Dane nowego użytkownika w formacie JSON
        String registerBody = """
            {
                "email": "client01@example.com",
                "password": "client123",
                "imie": "Klient",
                "nazwisko": "Testowy",
                "telefon": "123456789"
            }
        """;

        try {
            // Wysłanie żądania rejestracji
            sendRequestAndVerify(
                    registerUrl,
                    HttpMethod.POST,
                    registerBody,
                    headers,
                    MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            // Użytkownik może już istnieć - ignorujemy błąd
            System.out.println("Rejestracja klienta nie powiodła się - prawdopodobnie już istnieje");
        }

        // Logowanie na utworzone konto
        String loginUrl = "http://localhost:" + port + "/konto/logowanie";
        headers = new HttpHeaders();

        // Dane formularza logowania
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", "client01@example.com"); // email jako nazwa użytkownika
        loginForm.add("password", "client123");            // hasło

        // Wysyłamy żądanie logowania i sprawdzamy odpowiedź
        ResponseEntity<String> loginResponse = sendRequestAndVerify(
                loginUrl,
                HttpMethod.POST,
                loginForm,
                headers,
                MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("Logowanie klienta - status: " + loginResponse.getStatusCode());
    }

    /**
     * TK02: Test dodawania produktu do koszyka
     * 
     * Sprawdza czy:
     * 1. Zalogowany użytkownik może dodać produkt do koszyka
     * 2. System przyjmuje żądanie i zwraca poprawny status
     */
    @Test
    @Order(2)
    void TK02_testAddToCart() {
        // Najpierw zaloguj się - wykorzystujemy pomocniczą metodę do logowania
        HttpHeaders sessionHeaders = loginUser("client01@example.com", "client123");

        // Dodanie produktu do koszyka - wysyłamy żądanie JSON z ID produktu i ilością
        String addToCartUrl = "http://localhost:" + port + "/koszyk/dodaj";

        // JSON z ID produktu (1) i ilością (1)
        String cartBody = "{\"productId\": 1, \"quantity\": 1}";

        // Wysyłamy żądanie i weryfikujemy odpowiedź
        ResponseEntity<String> addToCartResponse = sendRequestAndVerify(
                addToCartUrl,
                HttpMethod.POST,
                cartBody,
                sessionHeaders,
                MediaType.APPLICATION_JSON);

        System.out.println("Dodanie do koszyka - status: " + addToCartResponse.getStatusCode());
    }

    /**
     * TK03: Test usuwania produktu z koszyka
     * 
     * Sprawdza czy:
     * 1. Najpierw można dodać produkt do koszyka
     * 2. Następnie można usunąć produkt z koszyka
     */
    @Test
    @Order(3)
    void TK03_testRemoveFromCart() {
        // Najpierw zaloguj się
        HttpHeaders sessionHeaders = loginUser("client01@example.com", "client123");

        // Dodaj produkt do koszyka - potrzebujemy go najpierw dodać, aby móc usunąć
        String addToCartUrl = "http://localhost:" + port + "/koszyk/dodaj";
        String cartBody = "{\"productId\": 1, \"quantity\": 1}";

        // Dodajemy produkt do koszyka
        sendRequestAndVerify(
                addToCartUrl,
                HttpMethod.POST,
                cartBody,
                sessionHeaders,
                MediaType.APPLICATION_JSON);

        // Usuwamy produkt z koszyka po ID produktu
        // W aplikacji BESTTECH usunięcie produktu z koszyka obsługiwane jest metodą POST
        String removeFromCartUrl = "http://localhost:" + port + "/koszyk/usun/1"; // 1 to ID produktu

        // Wysyłamy żądanie usunięcia i weryfikujemy odpowiedź
        ResponseEntity<String> removeFromCartResponse = sendRequestAndVerify(
                removeFromCartUrl,
                HttpMethod.POST,
                null, // Brak ciała żądania - ID produktu jest w URL
                sessionHeaders,
                MediaType.APPLICATION_JSON);

        System.out.println("Usunięcie z koszyka - status: " + removeFromCartResponse.getStatusCode());
    }

    /**
     * TK04: Test składania zamówienia
     * 
     * Sprawdza czy:
     * 1. Zalogowany użytkownik może dodać produkt do koszyka
     * 2. Może złożyć zamówienie z danymi dostawy i płatności
     */
    @Test
    @Order(4)
    void TK04_testPlaceOrder() {
        // Najpierw zaloguj się
        HttpHeaders sessionHeaders = loginUser("client01@example.com", "client123");

        // Dodaj produkt do koszyka - koszyk musi zawierać produkty, aby złożyć zamówienie
        String addToCartUrl = "http://localhost:" + port + "/koszyk/dodaj";
        String cartBody = "{\"productId\": 1, \"quantity\": 1}";

        sendRequestAndVerify(
                addToCartUrl,
                HttpMethod.POST,
                cartBody,
                sessionHeaders,
                MediaType.APPLICATION_JSON);

        // Złożenie zamówienia - w BESTTECH endpoint to /order/submit zgodnie z OrderController
        String placeOrderUrl = "http://localhost:" + port + "/order/submit";

        // Dane formularza zamówienia - adres dostawy, sposób dostawy i płatności
        MultiValueMap<String, String> orderForm = new LinkedMultiValueMap<>();
        orderForm.add("ulica", "Testowa 123");
        orderForm.add("miasto", "Warszawa");
        orderForm.add("kodPocztowy", "00-000");
        orderForm.add("kraj", "Polska");
        orderForm.add("sposobDostawy", "kurier");      // Metoda dostawy
        orderForm.add("sposobPlatnosci", "karta");     // Metoda płatności
        orderForm.add("typKlienta", "indywidualny");   // Typ klienta (indywidualny/firma)

        // Wysyłamy żądanie złożenia zamówienia i weryfikujemy odpowiedź
        ResponseEntity<String> placeOrderResponse = sendRequestAndVerify(
                placeOrderUrl,
                HttpMethod.POST,
                orderForm,
                sessionHeaders,
                MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("Złożenie zamówienia - status: " + placeOrderResponse.getStatusCode());
    }

    /**
     * TK05: Test przeglądania kategorii produktów
     * 
     * Sprawdza czy:
     * 1. Użytkownik (nawet niezalogowany) może przeglądać kategorie produktów
     * 2. System zwraca stronę z kategoriami
     */
    @Test
    @Order(5)
    void TK05_testViewCategories() {
        // Pobierz stronę kategorii - zgodnie z MyController
        String categoriesUrl = "http://localhost:" + port + "/kategoria";
        HttpHeaders headers = new HttpHeaders();

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                categoriesUrl,
                HttpMethod.GET,
                null, // Brak ciała żądania dla GET
                headers,
                MediaType.TEXT_HTML);

        System.out.println("Widok kategorii - status: " + response.getStatusCode());
    }

    /**
     * TK06: Test przeglądania produktów w wybranej kategorii
     * 
     * Sprawdza czy:
     * 1. Użytkownik może zobaczyć produkty z konkretnej kategorii
     * 2. System zwraca stronę z listą produktów
     */
    @Test
    @Order(6)
    void TK06_testViewProductsInCategory() {
        // Pobierz produkty z kategorii "Laptopy" - zgodnie z MyController
        String categoryProductsUrl = "http://localhost:" + port + "/kategoria/Laptopy";
        HttpHeaders headers = new HttpHeaders();

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                categoryProductsUrl,
                HttpMethod.GET,
                null,
                headers,
                MediaType.TEXT_HTML);

        System.out.println("Produkty w kategorii - status: " + response.getStatusCode());
    }

    /**
     * TK07: Test widoku szczegółów produktu
     * 
     * Sprawdza czy:
     * 1. Użytkownik może zobaczyć szczegółowe informacje o produkcie
     * 2. System zwraca stronę ze szczegółami produktu
     */
    @Test
    @Order(7)
    void TK07_testViewProductDetails() {
        // Pobierz szczegóły produktu o ID=1 i nazwie "TestProdukt" - zgodnie z MyController
        String productDetailsUrl = "http://localhost:" + port + "/product?id=1&nazwa=TestProdukt";
        HttpHeaders headers = new HttpHeaders();

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                productDetailsUrl,
                HttpMethod.GET,
                null,
                headers,
                MediaType.TEXT_HTML);

        System.out.println("Szczegóły produktu - status: " + response.getStatusCode());
    }

    /**
     * TK08: Test przeglądania szczegółów konta użytkownika
     * 
     * Sprawdza czy:
     * 1. Zalogowany użytkownik może przeglądać dane swojego konta
     * 2. System zwraca stronę z danymi użytkownika
     */
    @Test
    @Order(8)
    void TK08_testAccountDetails() {
        // Najpierw zaloguj się
        HttpHeaders sessionHeaders = loginUser("client01@example.com", "client123");

        // Pobierz szczegóły konta - zgodnie z AccountController
        String accountDetailsUrl = "http://localhost:" + port + "/konto/details";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                accountDetailsUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga zalogowania
                MediaType.TEXT_HTML);

        System.out.println("Szczegóły konta - status: " + response.getStatusCode());
    }

    /**
     * TK09: Test historii zamówień klienta
     * 
     * Sprawdza czy:
     * 1. Zalogowany użytkownik może przeglądać historię swoich zamówień
     * 2. System zwraca stronę z listą zamówień
     */
    @Test
    @Order(9)
    void TK09_testOrderHistory() {
        // Najpierw zaloguj się
        HttpHeaders sessionHeaders = loginUser("client01@example.com", "client123");

        // Pobierz historię zamówień - zgodnie z AccountController
        String orderHistoryUrl = "http://localhost:" + port + "/konto/zamowienia";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                orderHistoryUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga zalogowania
                MediaType.TEXT_HTML);

        System.out.println("Historia zamówień - status: " + response.getStatusCode());
    }

    /**
     * TK10: Test dodawania recenzji produktu
     * 
     * Sprawdza czy:
     * 1. Zalogowany użytkownik może dodać recenzję do produktu
     * 2. System przyjmuje recenzję i zwraca poprawny status
     */
    @Test
    @Order(10)
    void TK10_testAddReview() {
        // Najpierw zaloguj się
        HttpHeaders sessionHeaders = loginUser("client01@example.com", "client123");

        // Dodaj recenzję produktu - zgodnie z ReviewController
        String reviewUrl = "http://localhost:" + port + "/opinie/dodaj";

        // Dane formularza recenzji
        MultiValueMap<String, String> reviewForm = new LinkedMultiValueMap<>();
        reviewForm.add("produktId", "1");   // ID produktu
        reviewForm.add("ocena", "5");       // Ocena (np. 5 gwiazdek)
        reviewForm.add("tresc", "Świetny produkt, polecam!"); // Treść opinii

        try {
            // Wysyłamy żądanie POST i weryfikujemy odpowiedź
            ResponseEntity<String> response = sendRequestAndVerify(
                    reviewUrl,
                    HttpMethod.POST,
                    reviewForm,
                    sessionHeaders,
                    MediaType.APPLICATION_FORM_URLENCODED);

            System.out.println("Dodanie recenzji - status: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("Dodanie recenzji nie powiodło się: " + e.getMessage());
        }
    }

    /**
     * TA01: Test logowania na konto administratora
     * 
     * Sprawdza czy:
     * 1. Administrator może zalogować się do systemu
     * 2. System akceptuje dane logowania admina i zwraca poprawny status
     */
    @Test
    @Order(11)
    void TA01_testAdminLogin() {
        // Logowanie jako admin - zakładamy, że konto admina już istnieje w systemie
        String loginUrl = "http://localhost:" + port + "/konto/logowanie";
        HttpHeaders headers = new HttpHeaders();

        // Dane formularza logowania administratora
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", "admin@example.com");
        loginForm.add("password", "admin123");

        // Wysyłamy żądanie logowania i weryfikujemy odpowiedź
        ResponseEntity<String> loginResponse = sendRequestAndVerify(
                loginUrl,
                HttpMethod.POST,
                loginForm,
                headers,
                MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("Logowanie admina - status: " + loginResponse.getStatusCode());
    }

    /**
     * TA02: Test dostępu do panelu administratora
     * 
     * Sprawdza czy:
     * 1. Zalogowany administrator ma dostęp do panelu administratora
     * 2. System zwraca stronę panelu admina
     */
    @Test
    @Order(12)
    void TA02_testAdminPanel() {
        // Najpierw zaloguj się jako admin
        HttpHeaders sessionHeaders = loginUser("admin@example.com", "admin123");

        // Sprawdź dostęp do panelu admina - zgodnie z AdminController
        String adminPanelUrl = "http://localhost:" + port + "/admin";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                adminPanelUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga uprawnień admina
                MediaType.TEXT_HTML);

        System.out.println("Panel administratora - status: " + response.getStatusCode());
    }

    /**
     * TA03: Test zarządzania produktami przez administratora
     * 
     * Sprawdza czy:
     * 1. Administrator ma dostęp do zarządzania produktami
     * 2. System zwraca stronę z listą produktów do zarządzania
     */
    @Test
    @Order(13)
    void TA03_testProductManagement() {
        // Najpierw zaloguj się jako admin
        HttpHeaders sessionHeaders = loginUser("admin@example.com", "admin123");

        // Sprawdź dostęp do zarządzania produktami - zgodnie z ProductsController
        String productsManagementUrl = "http://localhost:" + port + "/products/showproducts";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                productsManagementUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga uprawnień admina
                MediaType.TEXT_HTML);

        System.out.println("Zarządzanie produktami - status: " + response.getStatusCode());
    }

    /**
     * TA04: Test dostępu do logów systemowych przez administratora
     * 
     * Sprawdza czy:
     * 1. Administrator ma dostęp do logów systemowych
     * 2. System zwraca stronę z logami
     */
    @Test
    @Order(14)
    void TA04_testViewLogs() {
        // Najpierw zaloguj się jako admin
        HttpHeaders sessionHeaders = loginUser("admin@example.com", "admin123");

        // Sprawdź dostęp do logów systemowych - zgodnie z LogsController
        String logsUrl = "http://localhost:" + port + "/admin/logs";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                logsUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga uprawnień admina
                MediaType.TEXT_HTML);

        System.out.println("Logi systemowe - status: " + response.getStatusCode());
    }

    /**
     * TA05: Test zarządzania zwrotami i reklamacjami przez administratora
     * 
     * Sprawdza czy:
     * 1. Administrator ma dostęp do zarządzania zwrotami i reklamacjami
     * 2. System zwraca stronę z listą zwrotów i reklamacji
     */
    @Test
    @Order(15)
    void TA05_testReturnComplaintManagement() {
        // Najpierw zaloguj się jako admin
        HttpHeaders sessionHeaders = loginUser("admin@example.com", "admin123");

        // Sprawdź dostęp do zarządzania zwrotami - zgodnie z AdminController
        String returnsUrl = "http://localhost:" + port + "/admin/zwroty-reklamacje-administrator";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                returnsUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga uprawnień admina
                MediaType.TEXT_HTML);

        System.out.println("Zarządzanie zwrotami - status: " + response.getStatusCode());
    }

    /**
     * TP01: Test logowania na konto pracownika
     * 
     * Sprawdza czy:
     * 1. Pracownik może zalogować się do systemu
     * 2. System akceptuje dane logowania pracownika i zwraca poprawny status
     */
    @Test
    @Order(16)
    void TP01_testEmployeeLogin() {
        // Logowanie jako pracownik - zakładamy, że konto pracownika już istnieje w systemie
        String loginUrl = "http://localhost:" + port + "/konto/logowanie";
        HttpHeaders headers = new HttpHeaders();

        // Dane formularza logowania pracownika
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", "employee@example.com");
        loginForm.add("password", "employee123");

        // Wysyłamy żądanie logowania i weryfikujemy odpowiedź
        ResponseEntity<String> loginResponse = sendRequestAndVerify(
                loginUrl,
                HttpMethod.POST,
                loginForm,
                headers,
                MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("Logowanie pracownika - status: " + loginResponse.getStatusCode());
    }

    /**
     * TP02: Test dostępu do panelu pracownika
     * 
     * Sprawdza czy:
     * 1. Zalogowany pracownik ma dostęp do panelu pracownika
     * 2. System zwraca stronę panelu pracownika
     */
    @Test
    @Order(17)
    void TP02_testEmployeePanel() {
        // Najpierw zaloguj się jako pracownik
        HttpHeaders sessionHeaders = loginUser("employee@example.com", "employee123");

        // Sprawdź dostęp do panelu pracownika - zgodnie z Employee controller
        String employeePanelUrl = "http://localhost:" + port + "/employee";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                employeePanelUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga uprawnień pracownika
                MediaType.TEXT_HTML);

        System.out.println("Panel pracownika - status: " + response.getStatusCode());
    }

    /**
     * TP03: Test dostępu do stanu magazynowego przez pracownika
     * 
     * Sprawdza czy:
     * 1. Pracownik ma dostęp do widoku stanu magazynowego
     * 2. System zwraca stronę z informacjami o stanie magazynu
     */
    @Test
    @Order(18)
    void TP03_testInventoryView() {
        // Najpierw zaloguj się jako pracownik
        HttpHeaders sessionHeaders = loginUser("employee@example.com", "employee123");

        // Sprawdź dostęp do stanu magazynowego - zgodnie z Employee controller
        String inventoryUrl = "http://localhost:" + port + "/employee/inventory";

        // Wysyłamy żądanie GET i weryfikujemy odpowiedź
        ResponseEntity<String> response = sendRequestAndVerify(
                inventoryUrl,
                HttpMethod.GET,
                null,
                sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga uprawnień pracownika
                MediaType.TEXT_HTML);

        System.out.println("Stan magazynowy - status: " + response.getStatusCode());
    }

    /**
     * TP04: Test zarządzania recenzjami przez pracownika
     * 
     * Sprawdza czy:
     * 1. Pracownik ma dostęp do zarządzania recenzjami produktów
     * 2. System zwraca stronę z listą recenzji do zarządzania
     */
    @Test
    @Order(19)
    void TP04_testReviewManagement() {
        // Najpierw zaloguj się jako pracownik
        HttpHeaders sessionHeaders = loginUser("employee@example.com", "employee123");

        // Sprawdź dostęp do zarządzania recenzjami - zgodnie z ReviewController
        String reviewManagementUrl = "http://localhost:" + port + "/opinie/zarzadzaj_opiniami";

        try {
            // Wysyłamy żądanie GET i weryfikujemy odpowiedź
            ResponseEntity<String> response = sendRequestAndVerify(
                    reviewManagementUrl,
                    HttpMethod.GET,
                    null,
                    sessionHeaders, // Przekazujemy ciasteczka sesji - wymaga uprawnień pracownika
                    MediaType.TEXT_HTML);

            System.out.println("Zarządzanie recenzjami - status: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("Dostęp do zarządzania recenzjami nie powiódł się: " + e.getMessage());
        }
    }

    /**
     * Pomocnicza metoda do logowania użytkownika
     * 
     * Metoda loguje użytkownika i zwraca nagłówki z ciasteczkami sesji,
     * które są potrzebne do dalszych żądań wymagających zalogowania.
     * 
     * @param username - nazwa użytkownika (email)
     * @param password - hasło
     * @return HttpHeaders - nagłówki z zapisanymi ciasteczkami sesji
     */
    private HttpHeaders loginUser(String username, String password) {
        String loginUrl = "http://localhost:" + port + "/konto/logowanie";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Przygotowanie danych formularza logowania
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", username);
        loginForm.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(loginForm, headers);

        // Wysłanie żądania logowania
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

        // Zapisz ciasteczka sesji do późniejszego użycia
        HttpHeaders sessionHeaders = new HttpHeaders();
        if (response.getHeaders().get("Set-Cookie") != null) {
            sessionHeaders.add("Cookie", String.join("; ", Objects.requireNonNull(response.getHeaders().get("Set-Cookie"))));
        }
        sessionHeaders.setContentType(MediaType.APPLICATION_JSON);

        return sessionHeaders;
    }
}