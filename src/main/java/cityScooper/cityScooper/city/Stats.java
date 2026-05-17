package cityScooper.cityScooper.city;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
public class Stats {
    @JsonDeserialize(using = SafeDoubleDeserializer.class)
    private Double averageAge;
    @JsonDeserialize(using = SafeDoubleDeserializer.class)
    private Double activePopulation;
    @JsonDeserialize(using = SafeDoubleDeserializer.class)
    private Double unemploymentRate;
    @JsonDeserialize(using = SafeDoubleDeserializer.class)
    private Double averageIncome;
    @JsonDeserialize(using = SafeDoubleDeserializer.class)
    private Double housePriceM2;
    @JsonDeserialize(using = SafeDoubleDeserializer.class)
    private Double apartmentPriceM2;
    
    public Double getApartmentPriceM2() { return apartmentPriceM2; }
    public Double getHousePriceM2() { return housePriceM2; }
}