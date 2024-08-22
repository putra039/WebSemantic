package com.belajarspring.BelajarSpringBoot.controller;

import com.belajarspring.BelajarSpringBoot.service.OntologyService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/jurusan")
public class JurusanController {

    @Autowired
    private OntologyService ontologyService;

    @GetMapping
    public String getJurusanPage() {
        return "jurusan";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<JSONObject> searchJurusan(@RequestParam String key) {
        return ontologyService.filterPrograms(key, null, null);
    }

    @GetMapping("/id/{id}")
    public String getJurusanDetail(@PathVariable String id, Model model) {
        List<JSONObject> detailList = ontologyService.getProgramDetails(id);
        model.addAttribute("jurusanDetails", detailList);
        return "detail_jurusan";
    }

    @GetMapping("/search/jenjang")
    @ResponseBody
    public List<JSONObject> searchJenjang(
            @RequestParam(required = false) String wilayah,
            @RequestParam(required = false) String jenjang,
            @RequestParam(required = false) String prodi,
            Model model) {
        List<JSONObject> searchResults = ontologyService.filterJenjang(wilayah, jenjang, prodi);
        return searchResults;
    }
}
