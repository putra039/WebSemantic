package com.belajarspring.BelajarSpringBoot.model;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ontologyModel {

    private OntModel getOntologyModel(String fileName) throws Exception {
        File file = new File(fileName);
        FileReader reader = new FileReader(file);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        model.read(reader, null);
        return model;
    }

    private String buildRegexFilter(String[] keywords, String... fields) {
        StringBuilder regexBuilder = new StringBuilder();
        for (String keyword : keywords) {
            for (String field : fields) {
                regexBuilder.append("regex(str(").append(field).append("), '").append(keyword).append("', 'i') || ");
            }
        }
        if (regexBuilder.length() > 0) {
            regexBuilder.setLength(regexBuilder.length() - 4); // Remove the last " || "
        }
        return regexBuilder.toString();
    }

    public List<JSONObject> searchById(String key, String wilayah, String jenjang) {
        List<JSONObject> list = new ArrayList<>();
        String fileName = "prodi.owl";
        try {
            OntModel model = getOntologyModel(fileName);

            StringBuilder sprql = new StringBuilder();
            sprql.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n")
                    .append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n")
                    .append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n")
                    .append("PREFIX prodi: <http://www.TAD306.com/ontologies/pencarianprodi#>\n")
                    .append("PREFIX onto: <http://www.ontotext.com/>\n")
                    .append("\n")
                    .append("SELECT ?programStudi ?prodi ?akreditasi ?fasilitas ?jenjangprodi ?jenjang ?wilayah ?singkatan ?pt ?id\n")
                    .append("\n")
                    .append("WHERE {\n")
                    .append("  ?programStudi prodi:memilikiAkreditasiProdi ?akreditasi;\n")
                    .append("                 prodi:memilikiProdiSejenis ?prodi;\n")
                    .append("                 prodi:menyediakanFasilitas ?fasilitas;\n")
                    .append("                 prodi:memilikiJenjangProdi ?jenjangprodi;\n")
                    .append("                 prodi:memilikiJenjang ?jenjang;\n")
                    .append("                 prodi:beradaDiWilayah ?wilayah;\n")
                    .append("                 prodi:memilikiSingkatan ?singkatan;\n")
                    .append("                 prodi:memilikiId ?id;\n")
                    .append("                 prodi:terdapatDi ?pt.\n");

            if (key != null && !key.isEmpty()) {
                String[] keywords = key.split(" ");
                String firstKeyword = keywords[0];
                sprql.append("  FILTER (regex(str(?prodi), '").append(firstKeyword).append("', 'i') || ")
                        .append("regex(str(?singkatan), '").append(firstKeyword).append("', 'i') || ")
                        .append("regex(str(?pt), '").append(firstKeyword).append("', 'i') || ")
                        .append("regex(str(?akreditasi), '").append(firstKeyword).append("', 'i') || ")
                        .append("regex(str(?fasilitas), '").append(firstKeyword).append("', 'i') || ")
                        .append("regex(str(?jenjangprodi), '").append(firstKeyword).append("', 'i') || ")
                        .append("regex(str(?jenjang), '").append(firstKeyword).append("', 'i') || ")
                        .append("regex(str(?wilayah), '").append(firstKeyword).append("', 'i'))\n");

                if (keywords.length > 1) {
                    String secondKeyword = keywords[1];
                    sprql.append("  FILTER (regex(str(?prodi), '").append(secondKeyword).append("', 'i') || ")
                            .append("regex(str(?singkatan), '").append(secondKeyword).append("', 'i') || ")
                            .append("regex(str(?pt), '").append(secondKeyword).append("', 'i') || ")
                            .append("regex(str(?akreditasi), '").append(secondKeyword).append("', 'i') || ")
                            .append("regex(str(?fasilitas), '").append(secondKeyword).append("', 'i') || ")
                            .append("regex(str(?jenjangprodi), '").append(secondKeyword).append("', 'i') || ")
                            .append("regex(str(?jenjang), '").append(firstKeyword).append("', 'i') || ")
                            .append("regex(str(?wilayah), '").append(secondKeyword).append("', 'i'))\n");
                }
            }
            if (wilayah != null && !wilayah.isEmpty()) {
                sprql.append("  FILTER regex(?pt, '").append(wilayah).append("', \"i\")\n");
            }
            if (jenjang != null && !jenjang.isEmpty()) {
                sprql.append("  FILTER regex(?jenjangprodi, '").append(jenjang).append("', \"i\")\n");
            }

            sprql.append("}\n")
                    .append("ORDER BY ASC(?akreditasi)");

            Query query = QueryFactory.create(sprql.toString());
            try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
                ResultSet resultSet = qe.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.nextSolution();
                    JSONObject obj = new JSONObject();
                    obj.put("programStudi", solution.get("programStudi").toString());
                    obj.put("prodi", solution.get("prodi").toString());
                    obj.put("akreditasi", solution.get("akreditasi").toString());
                    obj.put("fasilitas", solution.get("fasilitas").toString());
                    obj.put("jenjangprodi", solution.get("jenjangprodi").toString());
                    obj.put("jenjang", solution.get("jenjang").toString());
                    obj.put("wilayah", solution.get("wilayah").toString());
                    obj.put("singkatan", solution.get("singkatan").toString());
                    obj.put("pt", solution.get("pt").toString());
                    obj.put("id", solution.get("id").toString());
                    list.add(obj);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<JSONObject> detailById(String key) {
        List<JSONObject> list = new ArrayList<>();
        String fileName = "prodi.owl";
        try {
            OntModel model = getOntologyModel(fileName);

            String sprql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX prodi: <http://www.TAD306.com/ontologies/pencarianprodi#>\n" +
                    "PREFIX onto: <http://www.ontotext.com/>\n" +
                    "\n" +
                    "SELECT ?programStudi ?akreditasi ?akreditasiPt ?pt ?fasilitas ?jenjang ?jenjangprodi ?wilayah ?fakultas ?alamat ?id ?linkWeb \n" +
                    "WHERE\n" +
                    "  {\n" +
                    "    ?programStudi prodi:memilikiId ?id ;\n" +
                    "   prodi:memilikiAkreditasiProdi ?akreditasi;" +
                    "   prodi:terdapatDi ?pt;" +
                    "   prodi:memilikiJenjang ?jenjang;" +
                    "   prodi:memilikiJenjangProdi ?jenjangprodi;" +
                    "   prodi:beradaDiWilayah ?wilayah;" +
                    "   prodi:memilikiLink ?linkWeb;" +
                    "   prodi:memilikiAkreditasiPT ?akreditasiPt;" +
                    "   prodi:menyediakanFasilitas ?fasilitas;" +
                    "   prodi:beradaDi ?fakultas;" +
                    "   prodi:memilikiAlamat ?alamat;" +
                    "    FILTER (?id = '" + key + "')\n" +
                    "  }\n";

            Query query = QueryFactory.create(sprql);
            try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
                ResultSet resultSet = qe.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.next();
                    JSONObject obj = new JSONObject();
                    obj.put("programStudi", solution.get("programStudi").toString());
                    obj.put("akreditasi", solution.get("akreditasi").toString().split("#")[1]);
                    obj.put("akreditasiPt", solution.get("akreditasiPt").toString().split("#")[1]);
                    obj.put("fasilitas", solution.get("fasilitas").toString().split("#")[1]);
                    obj.put("fakultas", solution.get("fakultas").toString().split("#")[1]);
                    obj.put("pt", solution.get("pt").toString().split("#")[1]);
                    obj.put("alamat", solution.get("alamat").toString());
                    obj.put("jenjang", solution.get("jenjang").toString());
                    obj.put("wilayah", solution.get("wilayah").toString().split("#")[1]);
                    obj.put("linkWeb", solution.get("linkWeb").toString());
                    obj.put("id", key);
                    list.add(obj);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<JSONObject> searchByJenjang(String wilayah, String jenjang, String prodi) {
        List<JSONObject> list = new ArrayList<>();
        String fileName = "prodi.owl";
        try {
            OntModel model = getOntologyModel(fileName);

            StringBuilder sprql = new StringBuilder();
            sprql.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n")
                    .append("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n")
                    .append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n")
                    .append("PREFIX prodi: <http://www.TAD306.com/ontologies/pencarianprodi#>\n")
                    .append("PREFIX onto: <http://www.ontotext.com/>\n")
                    .append("\n")
                    .append("SELECT ?programStudi ?prodi ?akreditasi ?fasilitas ?jenjangprodi ?wilayah ?singkatan ?pt ?id\n")
                    .append("\n")
                    .append("WHERE {\n")
                    .append("  ?programStudi prodi:memilikiAkreditasiProdi ?akreditasi;\n")
                    .append("                 prodi:memilikiProdiSejenis ?prodi;\n")
                    .append("                 prodi:menyediakanFasilitas ?fasilitas;\n")
                    .append("                 prodi:memilikiJenjangProdi ?jenjangprodi;\n")
                    .append("                 prodi:beradaDiWilayah ?wilayah;\n")
                    .append("                 prodi:memilikiSingkatan ?singkatan;\n")
                    .append("                 prodi:memilikiId ?id;\n")
                    .append("                 prodi:terdapatDi ?pt.\n");

            if (wilayah != null && !wilayah.isEmpty()) {
                sprql.append("  FILTER regex(?pt, '").append(wilayah).append("', \"i\")\n");
            }
            if (jenjang != null && !jenjang.isEmpty()) {
                sprql.append("  FILTER regex(?jenjangprodi, '").append(jenjang).append("', \"i\")\n");
            }
            if (prodi != null && !prodi.isEmpty()) {
                sprql.append("  FILTER regex(?prodi, '").append(prodi).append("', \"i\")\n");
            }

            sprql.append("}\n")
                    .append("ORDER BY ASC(?akreditasi)");

            Query query = QueryFactory.create(sprql.toString());
            try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
                ResultSet resultSet = qe.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.nextSolution();
                    JSONObject obj = new JSONObject();
                    obj.put("programStudi", solution.get("programStudi").toString());
                    obj.put("prodi", solution.get("prodi").toString());
                    obj.put("akreditasi", solution.get("akreditasi").toString());
                    obj.put("fasilitas", solution.get("fasilitas").toString());
                    obj.put("jenjangprodi", solution.get("jenjangprodi").toString());
                    obj.put("wilayah", solution.get("wilayah").toString());
                    obj.put("singkatan", solution.get("singkatan").toString());
                    obj.put("pt", solution.get("pt").toString());
                    obj.put("id", solution.get("id").toString());
                    list.add(obj);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
