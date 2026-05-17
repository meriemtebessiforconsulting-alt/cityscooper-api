package cityScooper.cityScooper.city;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignore les champs JSON non mappés
public class City {
    private String geonameId;
    private String defaultName;
    private String postalCode;
    private GeoCoordinates geoCoordinates;
    private String featureClass;
    private String featureCode;
    private String countryCode;
    private String cc2;
    private String admin1Code;
    private String admin2Code;
    private String admin3Code;
    private String admin4Code;
    private Integer population;
    private String elevation;
    private String dem;
    private String timezone;
    private String modificationDate;
    private List<Note> notes;
    private PricePerm2 pricePerm2;
    private Stats stats;
    private String adminName1;

    // --- Getters / Setters ---
    public String getGeonameId() { return geonameId; }
    public void setGeonameId(String geonameId) { this.geonameId = geonameId; }

    public String getDefaultName() { return defaultName; }
    public void setDefaultName(String defaultName) { this.defaultName = defaultName; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public GeoCoordinates getGeoCoordinates() { return geoCoordinates; }
    public void setGeoCoordinates(GeoCoordinates geoCoordinates) { this.geoCoordinates = geoCoordinates; }

    public String getFeatureClass() { return featureClass; }
    public void setFeatureClass(String featureClass) { this.featureClass = featureClass; }

    public String getFeatureCode() { return featureCode; }
    public void setFeatureCode(String featureCode) { this.featureCode = featureCode; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCc2() { return cc2; }
    public void setCc2(String cc2) { this.cc2 = cc2; }

    public String getAdmin1Code() { return admin1Code; }
    public void setAdmin1Code(String admin1Code) { this.admin1Code = admin1Code; }

    public String getAdmin2Code() { return admin2Code; }
    public void setAdmin2Code(String admin2Code) { this.admin2Code = admin2Code; }

    public String getAdmin3Code() { return admin3Code; }
    public void setAdmin3Code(String admin3Code) { this.admin3Code = admin3Code; }

    public String getAdmin4Code() { return admin4Code; }
    public void setAdmin4Code(String admin4Code) { this.admin4Code = admin4Code; }

    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }

    public String getElevation() { return elevation; }
    public void setElevation(String elevation) { this.elevation = elevation; }

    public String getDem() { return dem; }
    public void setDem(String dem) { this.dem = dem; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getModificationDate() { return modificationDate; }
    public void setModificationDate(String modificationDate) { this.modificationDate = modificationDate; }

    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }

    public PricePerm2 getPricePerm2() { return pricePerm2; }
    public void setPricePerm2(PricePerm2 pricePerm2) { this.pricePerm2 = pricePerm2; }
    
    public Stats getStats() { return stats; }
    public void setStats(Stats stats) { this.stats = stats; }

    public String getAdminName1() { return adminName1; }
    public void setAdminName1(String adminName1) { this.adminName1 = adminName1; }

    /**
     * Retourne la moyenne sécurisée du prix au m².
     * Si pricePerm2 ou average est null → 404.
     */
    public double getSafeAveragePrice() {
        if (pricePerm2 == null || pricePerm2.getAverage() == null) {
            return 404;
        }
        return pricePerm2.getAverage();
    }

    /**
     * Vérifie si la ville est abordable selon un budget maximum
     * ou si son prix minimum est en-dessous du budget.
     */
    public boolean isAffordable(double maxBudget) {
        if (pricePerm2 == null) return false;

        Double avg = pricePerm2.getAverage();
        Double min = pricePerm2.getMin();

        boolean avgOk = avg != null && avg <= maxBudget;
        boolean minOk = min != null && min <= maxBudget;

        return avgOk || minOk;
    }
    
    /**
     * Retourne le prix minimum au m² (minBudget) de manière sécurisée.
     * Si non défini → retourne null.
     */
    public Double getMinBudget() {
        if (pricePerm2 == null || pricePerm2.getMin() == null) {
            return null;
        }
        return pricePerm2.getMin();
    }

    
}
