package cityScooper.cityScooper.mapper;

import org.springframework.stereotype.Component;

import cityScooper.cityScooper.city.City;
import cityScooper.cityScooper.city.Note;
import cityScooper.cityScooper.dto.CityResponseSearchDTO;

@Component
public class CityMapper {

    public CityResponseSearchDTO toDto(City city) {

        if (city == null) return null;

        Note note = (city.getNotes() != null && !city.getNotes().isEmpty())
            ? city.getNotes().get(0)
            : null;

        CityResponseSearchDTO dto = new CityResponseSearchDTO();

        // IDENTITÉ
        dto.setGeonameId(city.getGeonameId());
        dto.setName(city.getDefaultName());
        dto.setPostalCode(city.getPostalCode());

        // GEO
        if (city.getGeoCoordinates() != null) {
            dto.setLatitude(city.getGeoCoordinates().getLatitude());
            dto.setLongitude(city.getGeoCoordinates().getLongitude());
        }

        // NOTES
        if (note != null) {
            dto.setGlobalNote(note.getNote());
            dto.setSecurityNote(note.getSecurityNote());
            dto.setEducationNote(note.getEducationNote());
            dto.setEnvironmentNote(note.getEnvironmentNote());
            dto.setLeisureNote(note.getLeisureNote());
            dto.setPracticalLifeNote(note.getPracticalLifeNote());
        }

        // PRIX
        if (city.getStats() != null) {
            dto.setApartmentPerM2(city.getStats().getApartmentPriceM2());
            dto.setHousePerM2(city.getStats().getHousePriceM2());
        }
        
        if (city.getPricePerm2() != null) {
            dto.setAverage(city.getPricePerm2().getAverage());
            dto.setMin(city.getPricePerm2().getMin());
            dto.setMax(city.getPricePerm2().getMax());
        }

        // RÉGION
        dto.setRegion(city.getAdminName1());

        return dto;
    }
}