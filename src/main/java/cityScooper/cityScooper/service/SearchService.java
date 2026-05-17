package cityScooper.cityScooper.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import cityScooper.cityScooper.city.City;
import cityScooper.cityScooper.city.RootData;
import cityScooper.cityScooper.dto.SearchCriteriaInput;
import cityScooper.cityScooper.mapper.CityMapper;
import cityScooper.cityScooper.exception.InvalidRequestException;

@Service
public class SearchService {

    private final RootData rootData;
    private final CityService cityService;
    private final DistanceMatrixService distanceMatrixService;
    private final CityMapper cityMapper;

    public SearchService(RootData rootData,
            CityService cityService,
            DistanceMatrixService distanceMatrixService,
            CityMapper cityMapper) {

this.rootData = rootData;
this.cityService = cityService;
this.distanceMatrixService = distanceMatrixService;
this.cityMapper = cityMapper;
}
    public Map<String, Object> search(SearchCriteriaInput c) {
    	validatePriceFilter(c); // Validation des données input
    	validateBudgetFilter(c);
    	validateRadiusKm(c);
    	validateDistanceFilter(c);
    	validateCityNameExclusive(c);
    	//System.out.println("PROJECT TYPE = " + c.getProjectType());
    	List<City> allCities =
    		    rootData.getCities().getCitiesList();
    	

    		City origin = null;

    		if (c.getWorkCity() != null) {
    		    origin = cityService.findByName(allCities, c.getWorkCity())
    		        .orElse(null);
    		}

    	//System.out.println("ORIGIN = " + origin);
        //System.out.println("TOTAL CITIES = " + allCities.size());

        // =====================================================
        // 1. PRICE FILTER
        // =====================================================

        List<City> cities = new ArrayList<>(allCities);
        cities = cityService.filterByCityName(
        	    cities,
        	    c.getCityName()
        	);
        cities = cityService.filterByMinGlobalNote(cities, c.getNote());

        //System.out.println("AFTER CITY NAME = " + cities.size());
        cities = applyPriceFilter(cities, c);
        //System.out.println("AFTER PRICE = " + cities.size());
        
       // 2. PROJECT TYPE
        cities = applyProjectFilter(cities, c);
        //System.out.println("AFTER PROJECT = " + cities.size());
        
      // 3. radius (NEW)
        if (origin != null && c.getRadiusKm() != null) {
            cities = cityService.filterByRadius(cities, origin, c.getRadiusKm());
        }
        //System.out.println("AFTER RADIUS = " + cities.size());
        
        cities = applyPriceM2Filter(cities, c);
        //System.out.println("AFTER PRICE M2 = " + cities.size());

        // =====================================================
        // 4. DISTANCE FILTER
        // =====================================================

        if (origin != null && c.getMaxCarMinutes() != null) {
            cities = distanceMatrixService.filterCitiesByCarDuration(
                origin,
                cities,
                c.getMaxCarMinutes()
            );
        }
        System.out.println("AFTER DISTANCE = " + cities.size());

        // =====================================================
        // RESULT
        // =====================================================
        
        Map<String, Double> distances =
        	    (origin != null)
        	        ? distanceMatrixService.getDistances(origin, cities)
        	        : Map.of();
        
        List<String> cityDetails = cities.stream()
        	    .map(city -> {

        	        String name =
        	            city.getDefaultName() != null
        	                ? city.getDefaultName()
        	                : "UNKNOWN";

        	        Double note =
        	            cityService.getProviderNote(
        	                city,
        	                "BDMVille",
        	                "note"
        	            );

        	        Double price =
        	            city.getStats() != null
        	                ? city.getStats().getApartmentPriceM2()
        	                : null;

        	        Double distance =
        	            distances.getOrDefault(name, null);

        	        return String.format(
        	            "%s, %.1f, %.0f, %s min",
        	            name,
        	            note != null ? note : 0,
        	            price != null ? price : 0,
        	            distance != null ? Math.round(distance) : 0
        	        );
        	    })
        	    .toList();

        return Map.of(
        	    "count", cities.size(),

        	    // 🟢 liste simple (rapide pour UI / autocomplete)
        	    "cityNames", cities.stream()
        	        .map(City::getDefaultName)
        	        .toList(),

        	    // 🔵 détails complets
        	    "cities", cities.stream()
        	        .map(cityMapper::toDto)
        	        .toList(),
        	        
        	    "citiesDetails", cityDetails
        	);
    }
    private List<City> applyPriceFilter(List<City> cities,
                                    SearchCriteriaInput c) {

    // ✅ aucun filtre budget demandé
    if (c.getBudget() == null
        || c.getSurface() == null
        || c.getSurface() <= 0) {

        return cities;
    }

    double maxPrice =
        c.getBudget() / c.getSurface();

    return cities.stream()
        .filter(city -> {

            Double price = resolvePrice(city, c);

            if (price == null || price < 0) {
                return false;
            }

            return price <= maxPrice;
        })
        .toList();
    }
    private Double resolvePrice(City city, SearchCriteriaInput c) {

        if (city == null) return null;

        if ("A".equalsIgnoreCase(c.getPropertyType())) {
            return city.getStats() != null
                ? city.getStats().getApartmentPriceM2()
                : null;
        }

        if ("H".equalsIgnoreCase(c.getPropertyType())) {
            return city.getStats() != null
                ? city.getStats().getHousePriceM2()
                : null;
        }

        if (city.getPricePerm2() == null) return null;

        return city.getPricePerm2().getAverage();
    }
    private List<City> applyProjectFilter(List<City> cities, SearchCriteriaInput c) {
		if (c.getProjectType() == null) { return cities;}
		return cities.stream()
				.filter(city -> {
						Double note =cityService.getProviderNote(city,"BDMVille","note");
						Double security =cityService.getProviderNote(city,"BDMVille","security");
						Double education =cityService.getProviderNote(city,"BDMVille","education");
						Double environment =cityService.getProviderNote(city,"BDMVille","environment");
						Double practical =cityService.getProviderNote(city,"BDMVille","practical");
						return switch (c.getProjectType().toLowerCase()) {
									case "family" ->
										safe(security) >= 3
										&& safe(education) >= 3
										&& safe(environment) >= 3;

									case "investment" ->
										safe(note) >= 3;
									
									case "retirement" ->
										safe(security) >= 3
										&& safe(environment) >= 3;
									
									case "student" ->
										safe(practical) >= 3;
									default -> true;
							};
							})
							.toList();
							}    
    private double safe(Double value) {
        return value == null ? 0 : value;
    }  
    private List<City> applyPriceM2Filter(List<City> cities, SearchCriteriaInput c) {
        if (c.getPriceValue() == null || c.getPriceOperator() == null) {
            return cities;
        }
        String field = c.getPriceField();
        String op = c.getPriceOperator();
        double value = c.getPriceValue();
        return cities.stream()
            .filter(city -> {
                Double cityValue = resolvePriceField(city, field);
                if (cityValue == null) return false;
                return switch (op.toLowerCase()) {
                    case "gt" -> cityValue > value;
                    case "lt" -> cityValue < value;
                    default -> true;
                };
            })
            .toList();
    }    
    private Double resolvePriceField(City city, String field) {

    if (city == null) return null;

    return switch (field == null ? "" : field.toLowerCase()) {

        case "average" ->
            city.getPricePerm2() != null
                ? city.getPricePerm2().getAverage()
                : null;

        case "min" ->
            city.getPricePerm2() != null
                ? city.getPricePerm2().getMin()
                : null;

        case "max" ->
            city.getPricePerm2() != null
                ? city.getPricePerm2().getMax()
                : null;

        case "apartment" ->
            city.getStats() != null
                ? city.getStats().getApartmentPriceM2()
                : null;

        case "house" ->
            city.getStats() != null
                ? city.getStats().getHousePriceM2()
                : null;

        default -> null;
    };
    }
    private void validatePriceFilter(SearchCriteriaInput c) {
        boolean hasField = c.getPriceField() != null && !c.getPriceField().isBlank();
        boolean hasOperator = c.getPriceOperator() != null && !c.getPriceOperator().isBlank();
        boolean hasValue = c.getPriceValue() != null;
        // aucun des 3 => OK
        if (!hasField && !hasOperator && !hasValue) { return;}
        // tous les 3 => OK
        if (hasField && hasOperator && hasValue) { return; }
        throw new InvalidRequestException(
                "Invalid_request",
                "priceField, priceOperator et priceValue doivent être fournis ensemble"
            );
    }
    private void validateBudgetFilter(SearchCriteriaInput c) {
        boolean hasBudget = c.getBudget() != null;
        boolean hasSurface = c.getSurface() != null;
        // aucun des 2 => OK
        if (!hasBudget && !hasSurface) { return;}
        // tous les 2 => OK
        if (hasBudget && hasSurface) { return; }
        throw new InvalidRequestException(
                "Invalid_request",
                "budget et surface doivent être fournis ensemble"
            );
    }
    private void validateRadiusKm(SearchCriteriaInput c) {
        boolean hasRadiusKm = c.getRadiusKm() != null;
        boolean hasWorkCity = c.getWorkCity() != null && !c.getWorkCity().isBlank();
        // aucun des 2 => OK
        if (!hasRadiusKm && !hasWorkCity) { return;}
        // tous les 2 => OK
        System.out.print(hasWorkCity);
        if (hasRadiusKm && hasWorkCity) { return; }
        throw new InvalidRequestException(
                "Invalid_request",
                "workCity et radiusKm doivent être fournis ensemble"
            );
    }
    private void validateDistanceFilter(SearchCriteriaInput c) {
        boolean hasMaxCarMinutes = c.getMaxCarMinutes() != null;
        boolean hasWorkCity = c.getWorkCity() != null && !c.getWorkCity().isBlank();
        // aucun des 2 => OK
        if (!hasMaxCarMinutes && !hasWorkCity) { return;}
        // tous les 2 => OK
        if (hasMaxCarMinutes && hasWorkCity) { return; }
        throw new InvalidRequestException(
                "Invalid_request",
                "workCity et maxCarMinutes doivent être fournis ensemble"
            );
    }
    private void validateCityNameExclusive(SearchCriteriaInput c) {

        boolean hasCityName =
            c.getCityName() != null
            && !c.getCityName().isBlank();

        if (!hasCityName) {
            return;
        }

        boolean hasOtherFilters =

            c.getProjectType() != null
            || c.getBudget() != null
            || c.getSurface() != null
            || c.getPropertyType() != null
            || c.getWorkCity() != null
            || c.getMaxCarMinutes() != null
            || c.getRadiusKm() != null
            || c.getPriceField() != null
            || c.getPriceOperator() != null
            || c.getPriceValue() != null
            || c.getNote() != null;

        if (hasOtherFilters) {

            throw new InvalidRequestException(
                "invalid_cityname_usage",
                "cityName ne peut pas être utilisé avec d'autres filtres"
            );
        }
    }
}