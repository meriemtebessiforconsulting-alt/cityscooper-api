package cityScooper.cityScooper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cityScooper.cityScooper.city.RootData;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class AppConfig {

    @Bean
    public RootData rootData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        InputStream is = getClass().getResourceAsStream("/citiesList.json");

        if (is == null) {
            throw new IOException("Fichier JSON introuvable dans resources");
        }

        RootData rootData = mapper.readValue(is, RootData.class);

        System.out.println("✅ Chargement terminé : " +
                rootData.getCities().getCount() + " villes trouvées");

        return rootData;
    }
}