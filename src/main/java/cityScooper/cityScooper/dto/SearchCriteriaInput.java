// Objet d'entrée input
package cityScooper.cityScooper.dto;

public class SearchCriteriaInput {

    private String projectType;
    private Double budget;
    private Double surface;
    private String propertyType;
    private String workCity;
    private Double maxCarMinutes;
    private Double radiusKm;
    private String cityName;
    private String priceField;      // average | min | max | house | apartment
    private String priceOperator;   // gt | lt
    private Double priceValue; 
    private Double note;
    
    public String getCityName() {return cityName;}
    public void setCityName(String cityName) { this.cityName = cityName;}    
    public String getPriceOperator() {return priceOperator;}
    public void setPriceOperator(String priceOperator) { this.priceOperator = priceOperator;}   
    public Double getPriceValue() {return priceValue;}
    public void setPriceValue(Double priceValue) { this.priceValue = priceValue;}  
    public String getPriceField() {return priceField;}
    public void setPriceField(String priceField) { this.priceField = priceField;}
    public String getProjectType() {return projectType;}
    public void setProjectType(String projectType) {this.projectType = projectType;}
    public Double getBudget() {return budget;}
    public void setBudget(Double budget) {this.budget=budget;}
    public Double getSurface() {return surface;}
    public void setSurface(Double surface) {this.surface=surface;}    
    public String getPropertyType() {return propertyType;}
    public void setPropertyType(String propertyType) {this.propertyType=propertyType;}    
    public String getWorkCity() {return workCity;}
    public void setWorkCity(String workCity) {this.workCity=workCity;}   
    public Double getMaxCarMinutes() {return maxCarMinutes;}
    public void setMaxCarMinutes(Double maxCarMinutes) {this.maxCarMinutes=maxCarMinutes;}   
    public Double getRadiusKm() {return radiusKm;}
    public void setRadiusKm(Double radiusKm) {this.radiusKm=radiusKm;}
    public Double getNote() {return note;}
    public void setNote(Double note) {this.note=note;}
}