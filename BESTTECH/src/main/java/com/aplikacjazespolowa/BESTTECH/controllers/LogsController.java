package com.aplikacjazespolowa.BESTTECH.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import com.aplikacjazespolowa.BESTTECH.models.LogsRepository;
import com.aplikacjazespolowa.BESTTECH.models.LogsSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
/**
 * Kontroler odpowiedzialny za wyświetlanie logów systemowych dla administratora.
 *
 * Udostępnia widok zawierający listę logów pobranych z repozytorium.
 */
@Controller
@RequestMapping("/admin/logs")
public class LogsController {

    private static final Logger log = LoggerFactory.getLogger(LogsController.class);

    /**
     * Logger do rejestrowania zdarzeń w tym kontrolerze.
     */
    @Autowired
    private LogsRepository logsRepository;

    /**
     * Pobiera i wyświetla listę logów systemowych.
     *
     * Pozwala filtrować logi po nazwie użytkownika oraz poziomie logowania (np. INFO, WARN, ERROR).
     * Jeżeli żadne filtry nie są ustawione, wyświetla wszystkie logi posortowane malejąco po dacie utworzenia.
     *
     * @param model    model MVC do przekazania danych do widoku
     * @param username (opcjonalnie) nazwa użytkownika, po której logi mają być filtrowane
     * @param level    (opcjonalnie) poziom logowania, po którym logi mają być filtrowane
     * @return widok z logami ("admin/show_logs")
     */

    @GetMapping
    public String showLogs(Model model, @RequestParam(required = false) String username, @RequestParam(required = false) String level){
        List<LogsSystem>logs;

        //trim-usuwa biale znaki(spacja,tabulator itd)
        boolean usernameEmpty=(username ==null || username.trim().isEmpty());
        boolean levelEmpty=(level ==null || level.trim().isEmpty());

        Sort sortByTimestampDesc=Sort.by(Sort.Direction.DESC,"timestamp");

        if(!usernameEmpty && !levelEmpty){
            logs=logsRepository.findByUsernameAndLevel(username,level,sortByTimestampDesc);
        } else if (!usernameEmpty) {
            logs=logsRepository.findByUsername(username,sortByTimestampDesc);
        } else if (!levelEmpty) {
            logs = logsRepository.findByLevel(level,sortByTimestampDesc);
        }else {
            logs=logsRepository.findAll(sortByTimestampDesc);
        }
        model.addAttribute("logs", logs);
        return "admin/show_logs";
    }


}
