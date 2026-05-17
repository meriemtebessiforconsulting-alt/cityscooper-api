//service métier pur sur les villes (filtres, notes, région, fuzzy search, etc)
package cityScooper.cityScooper.service;
import org.springframework.stereotype.Service;
import cityScooper.cityScooper.city.City;
import cityScooper.cityScooper.city.GeoCoordinates;
import cityScooper.cityScooper.city.Note;
import java.text.Normalizer;
import java.util.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.FuzzyScore;
@Service
public class CityService {

    // =====================================================
    // 🔵 REGION
    // =====================================================

    public List<City> filterByRegion(List<City> cities, String region) {
        if (region == null || region.isBlank()) return cities;

        String normalizedRegion = normalize(region);

        return cities.stream()
            .filter(city -> {
                String cityRegion = city.getAdminName1();
                return cityRegion != null && normalize(cityRegion).equals(normalizedRegion);
            })
            .toList();
    }

    public List<City> filterByExactName(List<City> cities, String name) {
        if (name == null) return cities;

        String normalized = normalize(name);

        return cities.stream()
            .filter(c -> normalize(c.getDefaultName()).equals(normalized))
            .toList();
    }
    public List<City> filterByNameContaining(List<City> cities, String searchTerm) {
        if (searchTerm == null) return cities;

        String normalized = normalize(searchTerm);

        return cities.stream()
            .filter(c -> normalize(c.getDefaultName()).contains(normalized))
            .toList();
    }
    public List<City> filterByNameFuzzy(List<City> cities, String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) return cities;

        String normalizedSearch = normalize(searchTerm);
        LevenshteinDistance distance = new LevenshteinDistance();

        return cities.stream()
            .filter(city -> {
                String cityName = normalize(city.getDefaultName());

                if (cityName.contains(normalizedSearch)) return true;

                int maxDistance = Math.max(1, normalizedSearch.length() / 4);
                return distance.apply(cityName, normalizedSearch) <= maxDistance;
            })
            .toList();
    }
    public List<City> filterByNearCityName(List<City> cities, String nearCityName) {
        if (nearCityName == null || nearCityName.isBlank()) return cities;

        String normalizedSearch = normalize(nearCityName);
        FuzzyScore fuzzyScore = new FuzzyScore(Locale.FRENCH);

        return cities.stream()
            .filter(city -> {
                String normalizedCity = normalize(city.getDefaultName());

                return normalizedCity.contains(normalizedSearch)
                    || fuzzyScore.fuzzyScore(normalizedCity, normalizedSearch) > 80;
            })
            .toList();
    }
    // =====================================================
    // 🔵 NOTES
    // =====================================================
    public List<City> filterByMinGlobalNote(List<City> cities, Double minNote) {
    if (minNote == null) return cities;
    return cities.stream()
        .filter(city -> {
            Double note = getProviderNote(city, "BDMVille", "note");
            if (note == null) return false;
            return Double.compare(note, minNote) >= 0;
        })
        .toList();
}

    public List<City> filterByNotes(List<City> cities,
                                    Double minNote,
                                    Double minEnvironmentNote) {

        if (minNote == null && minEnvironmentNote == null) return cities;

        return cities.stream()
            .filter(city -> {
                List<Note> notes = city.getNotes();
                if (notes == null || notes.isEmpty()) return false;

                return notes.stream()
                    .filter(n -> "BDMVille".equalsIgnoreCase(n.getSrvProviderCode()))
                    .anyMatch(n -> {

                        boolean ok = true;

                        if (minNote != null) {
                            ok &= n.getNote() != null && n.getNote() >= minNote;
                        }

                        if (minEnvironmentNote != null) {
                            ok &= n.getEnvironmentNote() != null
                               && n.getEnvironmentNote() >= minEnvironmentNote;
                        }

                        return ok;
                    });
            })
            .toList();
    }

    // =====================================================
    // 🔵 DISTANCE HELPERS (PURE UTILS)
    // =====================================================

    public double calculateDistanceKm(double lat1, double lon1,
                                      double lat2, double lon2) {

        final int R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // =====================================================
    // 🔵 BUDGET
    // =====================================================

    public List<City> filterByAverageBudget(List<City> cities,
                                             Double min,
                                             Double max) {

        if (min == null && max == null) return cities;

        return cities.stream()
            .filter(city -> {
                double avg = city.getSafeAveragePrice();

                boolean ok = true;

                if (min != null) ok &= avg >= min;
                if (max != null) ok &= avg <= max;

                return ok;
            })
            .toList();
    }

    public List<City> filterByPostalPrefix(List<City> cities,
                                            List<String> prefixes) {

        if (prefixes == null || prefixes.isEmpty()) return cities;

        return cities.stream()
            .filter(city -> {
                String postal = city.getPostalCode();
                if (postal == null) return false;

                return prefixes.stream().anyMatch(postal::startsWith);
            })
            .toList();
    }

    // =====================================================
    // 🔵 UTILITIES
    // =====================================================

    private String normalize(String text) {
        if (text == null) return "";

        String noAccent = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return noAccent.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    public Optional<City> findByName(List<City> cities, String name) {

        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        String normalizedSearch = normalize(name);

        System.out.println("SEARCH = " + normalizedSearch);

        return cities.stream()

            // sécurité
            .filter(Objects::nonNull)

            // nom obligatoire
            .filter(c -> c.getDefaultName() != null)

            // matching
            .filter(c -> {

                String cityName =
                    normalize(c.getDefaultName());

                return cityName.equals(normalizedSearch)
                    || cityName.contains(normalizedSearch);
            })

            .findFirst();
    }
    
    public Double getProviderNote(City city, String provider, String type) {

        if (city == null || city.getNotes() == null) {
            return null;
        }

        return city.getNotes().stream()
            .filter(n ->
                provider.equalsIgnoreCase(n.getSrvProviderCode())
            )
            .map(n -> switch (type.toLowerCase()) {

                case "security" ->
                    n.getSecurityNote();

                case "education" ->
                    n.getEducationNote();

                case "environment" ->
                    n.getEnvironmentNote();

                case "practical" ->
                    n.getPracticalLifeNote();

                default ->
                    n.getNote();
            })
            .filter(Objects::nonNull)
            .max(Double::compareTo)
            .orElse(null);
    }

    public List<City> filterInBox(List<City> cities, List<String> boxNames) {
        if (boxNames == null || boxNames.size() < 2) return cities;

        List<String> normalized = boxNames.stream()
            .map(this::normalize)
            .toList();

        List<City> refs = cities.stream()
            .filter(c -> normalized.contains(normalize(c.getDefaultName())))
            .filter(c -> c.getGeoCoordinates() != null)
            .toList();

        if (refs.size() < 2) return cities;

        double minLat = refs.stream().mapToDouble(c -> c.getGeoCoordinates().getLatitude()).min().orElse(0);
        double maxLat = refs.stream().mapToDouble(c -> c.getGeoCoordinates().getLatitude()).max().orElse(0);
        double minLon = refs.stream().mapToDouble(c -> c.getGeoCoordinates().getLongitude()).min().orElse(0);
        double maxLon = refs.stream().mapToDouble(c -> c.getGeoCoordinates().getLongitude()).max().orElse(0);

        return cities.stream()
            .filter(city -> {
                GeoCoordinates geo = city.getGeoCoordinates();
                if (geo == null) return false;

                double lat = geo.getLatitude();
                double lon = geo.getLongitude();

                return lat >= minLat && lat <= maxLat
                    && lon >= minLon && lon <= maxLon;
            })
            .toList();
    }
    
    public List<City> filterByRadius(List<City> cities,
            City origin,
            double radiusKm) {

			if (origin == null) return cities;
			
			double lat1 = origin.getGeoCoordinates().getLatitude();
			double lon1 = origin.getGeoCoordinates().getLongitude();
			
			return cities.stream()
			.filter(city -> {
			
			if (city.getGeoCoordinates() == null) return false;
			
			double lat2 = city.getGeoCoordinates().getLatitude();
			double lon2 = city.getGeoCoordinates().getLongitude();
			
			double distance = calculateDistanceKm(lat1, lon1, lat2, lon2);
			
			return distance <= radiusKm;
			})
			.toList();
}
    public List<City> filterByCityName(List<City> cities, String cityName) {

        if (cityName == null || cityName.isBlank()) {
            return cities;
        }

        String normalizedSearch = normalize(cityName);

        List<City> result = cities.stream()
            .filter(Objects::nonNull)
            .filter(c -> c.getDefaultName() != null)
            .filter(c -> {

                String normalizedCity =
                    normalize(c.getDefaultName());

                return normalizedCity.contains(normalizedSearch);
            })
            .toList();

        // IMPORTANT :
        // si aucune ville trouvée -> liste vide
        return result;
    }
}