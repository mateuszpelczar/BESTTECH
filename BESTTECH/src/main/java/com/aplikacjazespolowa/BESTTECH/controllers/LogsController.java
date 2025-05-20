package com.aplikacjazespolowa.BESTTECH.controllers;


import org.springframework.ui.Model;
import com.aplikacjazespolowa.BESTTECH.models.LogsRepository;
import com.aplikacjazespolowa.BESTTECH.models.LogsSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
/**
 * Kontroler odpowiedzialny za wyświetlanie logów systemowych dla administratora.
 *
 * Udostępnia widok zawierający listę logów pobranych z repozytorium.
 */
@Controller
@RequestMapping("/admin/logs")
public class LogsController {

    @Autowired
    private LogsRepository logsRepository;

    /**
     * Pobiera wszystkie logi systemowe i przekazuje je do widoku.
     *
     * @param model model MVC do przekazania danych do widoku
     * @return widok z logami (admin/show_logs)
     */

    @GetMapping
    public String showLogs(Model model){
        List<LogsSystem>logs= logsRepository.findAll();
        model.addAttribute("logs", logs);
        return "admin/show_logs";
    }
}
