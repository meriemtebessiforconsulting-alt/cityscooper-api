package cityScooper.cityScooper.city;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore les champs non définis
public class GeoCoordinates {
    private Double latitude;
    private Double longitude;
    public GeoCoordinates() {}
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    public void setLatitude(Double latitude) {this.latitude= latitude;}
    public void setLongitude(Double longitude ) {this.longitude= longitude;}
}
