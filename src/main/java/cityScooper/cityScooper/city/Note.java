package cityScooper.cityScooper.city;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Note {

    private Double note;
    private String srvProviderCode;
    private Double securityNote;
    private Double educationNote;
    private Double leisureNote;
    private Double environmentNote;
    private Double practicalLifeNote;

    public Double getNote() { return note; }
    public String getSrvProviderCode() { return srvProviderCode; }
    public Double getSecurityNote() { return securityNote; }
    public Double getEducationNote() { return educationNote; }
    public Double getLeisureNote() { return leisureNote; }
    public Double getEnvironmentNote() { return environmentNote; }
    public Double getPracticalLifeNote() { return practicalLifeNote; }
}