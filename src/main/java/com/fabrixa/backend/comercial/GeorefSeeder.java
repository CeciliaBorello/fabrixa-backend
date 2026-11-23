package com.fabrixa.backend.comercial;

import com.fabrixa.backend.entidades.model.Ciudad;
import com.fabrixa.backend.entidades.model.Provincia;
import com.fabrixa.backend.entidades.repository.CiudadRepository;
import com.fabrixa.backend.entidades.repository.ProvinciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Carga provincias y ciudades desde la API Georef SOLO si la tabla está vacía.
 * Desde la migración V3__provincias_y_ciudades.sql los datos vienen con el esquema,
 * así que en condiciones normales este seeder no hace nada.
 * Si la API falla, se registra un aviso y la aplicación sigue arrancando:
 * un servicio externo nunca debe impedir que el backend levante.
 */
@Component
public class GeorefSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(GeorefSeeder.class);
    private static final String GEOREF = "https://apis.datos.gob.ar/georef/api";

    private final ProvinciaRepository provinciaRepository;
    private final CiudadRepository ciudadRepository;
    private final RestClient restClient;

    public GeorefSeeder(ProvinciaRepository provinciaRepository, CiudadRepository ciudadRepository) {
        this.provinciaRepository = provinciaRepository;
        this.ciudadRepository = ciudadRepository;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(20));
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Override
    public void run(String... args) {
        if (provinciaRepository.count() > 0) return; // ya está cargado (normalmente por Flyway V3)

        log.info("No hay provincias cargadas: se intentan cargar desde Georef...");
        try {
            cargarDesdeGeoref();
            log.info("Provincias y ciudades cargadas desde Georef: {} provincias, {} ciudades",
                    provinciaRepository.count(), ciudadRepository.count());
        } catch (Exception e) {
            log.warn("No se pudieron cargar provincias y ciudades desde Georef ({}). " +
                    "La aplicación sigue funcionando; revisar la migración V3.", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeGeoref() {
        Map<String, Object> respProvincias = restClient.get()
                .uri(GEOREF + "/provincias?campos=id,nombre&max=30")
                .retrieve()
                .body(Map.class);

        List<Map<String, String>> provinciasData = (List<Map<String, String>>) respProvincias.get("provincias");

        for (var p : provinciasData) {
            Provincia provincia = new Provincia();
            provincia.setId(p.get("id"));
            provincia.setNombre(p.get("nombre"));
            provinciaRepository.save(provincia);

            Map<String, Object> respMunicipios = restClient.get()
                    .uri(GEOREF + "/municipios?provincia=" + p.get("id") + "&campos=id,nombre&max=500")
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
    }
}