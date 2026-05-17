package cityScooper.cityScooper.dto;

public class CityResponseSearchDTO {

    private String geonameId;
    private String name;
    private String postalCode;

    private double latitude;
    private double longitude;

    private Double globalNote;

    private Double securityNote;
    private Double educationNote;
    private Double environmentNote;
    private Double leisureNote;
    private Double practicalLifeNote;

    private Double average;
    private Double min;
    private Double max;
    private Double apartmentPerM2;
    private Double housePerM2;

    private String region;

    // GETTERS & SETTERS

    public String getGeonameId() { return geonameId; }
    public void setGeonameId(String geonameId) { this.geonameId = geonameId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Double getGlobalNote() { return globalNote; }
    public void setGlobalNote(Double globalNote) { this.globalNote = globalNote; }

    public Double getSecurityNote() { return securityNote; }
    public void setSecurityNote(Double securityNote) { this.securityNote = securityNote; }

    public Double getEducationNote() { return educationNote; }
    public void setEducationNote(Double educationNote) { this.educationNote = educationNote; }

    public Double getEnvironmentNote() { return environmentNote; }
    public void setEnvironmentNote(Double environmentNote) { this.environmentNote = environmentNote; }

    public Double getLeisureNote() { return leisureNote; }
    public void setLeisureNote(Double leisureNote) { this.leisureNote = leisureNote; }

    public Double getPracticalLifeNote() { return practicalLifeNote; }
    public void setPracticalLifeNote(Double practicalLifeNote) { this.practicalLifeNote = practicalLifeNote; }

    public Double getAverage() { return average; }
    public void setAverage(Double average) { this.average = average; }
    public Double getMin() { return min; }
    public void setMin(Double max) { this.max = max; }
    public Double getMax() { return max; }
    public void setMax(Double average) { this.average = average; }
    public Double getApartmentPerM2() { return apartmentPerM2; }
    public void setApartmentPerM2(Double apartmentPerM2) { this.apartmentPerM2 = apartmentPerM2; }
    public Double getHousePerM2() { return housePerM2; }
    public void setHousePerM2(Double housePerM2) { this.housePerM2 = housePerM2; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}