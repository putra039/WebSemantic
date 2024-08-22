package com.belajarspring.BelajarSpringBoot.service;

import com.belajarspring.BelajarSpringBoot.model.ontologyModel;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OntologyService {

    @Autowired
    private ontologyModel ontologyModel;

//    public List<JSONObject> searchPrograms(String programStudi, String singkatanPT, String wilayah, String jenjang) {
//        // Implement the method in ontologyModel to handle two parameters
//        return ontologyModel.searchByProgramAndAbbreviation(programStudi, singkatanPT, wilayah, jenjang);
//    }

    public List<JSONObject> getProgramDetails(String id) {
        return ontologyModel.detailById(id);
    }

    public List<JSONObject> filterJenjang(String wilayah, String jenjang, String prodi) {
        return ontologyModel.searchByJenjang(wilayah, jenjang, prodi);
    }

    public List<JSONObject> filterPrograms(String key, String wilayah, String jenjang) {
        return ontologyModel.searchById(key, wilayah, jenjang);
    }
}
