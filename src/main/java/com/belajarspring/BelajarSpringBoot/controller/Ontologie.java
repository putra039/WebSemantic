package com.belajarspring.BelajarSpringBoot.controller;

import com.belajarspring.BelajarSpringBoot.model.ontologyModel;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class Ontologie {

    private final ontologyModel model;

    @Autowired
    public Ontologie(ontologyModel model) {
        this.model = model;
    }

    @RequestMapping(value = "/query/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JSONObject> query(@PathVariable String key,
                                  @RequestParam(value = "wilayah", required = false) String wilayah,
                                  @RequestParam(value = "jenjangprodi", required = false) String jenjangprodi) {

        List<JSONObject> result = model.searchById(key, wilayah, jenjangprodi);
        return result;
    }

    @RequestMapping(value = "/jurusan/byId/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JSONObject> getJurusanById(@PathVariable String key){
        List<JSONObject> result = model.detailById(key);
        return result;
    }

    @RequestMapping(value = "/jenjangprodi", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JSONObject> jenjangProdi(
                                  @RequestParam(value = "wilayah", required = false) String wilayah,
                                  @RequestParam(value = "jenjang", required = false) String jenjangprodi,
                                  @RequestParam(value = "prodi", required = false) String prodi) {

        List<JSONObject> result = model.searchByJenjang(wilayah, jenjangprodi, prodi);
        return result;
    }
}
