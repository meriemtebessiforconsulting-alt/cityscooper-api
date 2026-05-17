// Classe pour parser le JSON global
package cityScooper.cityScooper.city;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorer les champs inconnus dans le json (potentiels nouveaux champs)
public class RootData {
    private Cities cities; 

    public Cities getCities() { return cities;}
    public void setCities(Cities cities) {this.cities = cities;}
}

