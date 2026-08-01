package com.fabrixa.backend.comercial;

import com.fabrixa.backend.entidades.model.Ciudad;
import com.fabrixa.backend.entidades.model.Provincia;
import com.fabrixa.backend.entidades.repository.CiudadRepository;
import com.fabrixa.backend.entidades.repository.ProvinciaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeorefSeeder implements CommandLineRunner {

    private final ProvinciaRepository provinciaRepository;
    private final CiudadRepository ciudadRepository;
    private final RestClient restClient = RestClient.create();

    public GeorefSeeder(ProvinciaRepository provinciaRepository, CiudadRepository ciudadRepository) {
        this.provinciaRepository = provinciaRepository;
        this.ciudadRepository = ciudadRepository;
    }

    @Override
    public void run(String... args) {
        if (provinciaRepository.count() > 0) return; // ya está cargado, no repetir

        System.out.println(">>> Cargando provincias y ciudades desde Georef...");

        var respProvincias = restClient.get()
                .uri("https://apis.datos.gob.ar/georef/api/provincias?campos=id,nombre&max=30")
                .retrieve()
                .body(Map.class);

        List<Map<String, String>> provinciasData = (List<Map<String, String>>) respProvincias.get("provincias");

        for (var p : provinciasData) {
            Provincia provincia = new Provincia();
            provincia.setId(p.get("id"));
            provincia.setNombre(p.get("nombre"));
            provinciaRepository.save(provincia);

            var respMunicipios = restClient.get()
                    .uri("https://apis.datos.gob.ar/georef/api/municipios?provincia=" + p.get("id") + "&campos=id,nombre&max=500")
                    .retrieve()
                    .body(Map.class);

            List<Map<String, String>> municipiosData = (List<Map<String, String>>) respMunicipios.get("municipios");

            for (var m : municipiosData) {
                Ciudad ciudad = new Ciudad();
                ciudad.setId(m.get("id"));
                ciudad.setNombre(m.get("nombre"));
                ciudad.setProvincia(provincia);
                ciudadRepository.save(ciudad);
            }
        }

        System.out.println(">>> Provincias y ciudades cargadas: " + provinciaRepository.count() + " provincias, " + ciudadRepository.count() + " ciudades");
    }
}