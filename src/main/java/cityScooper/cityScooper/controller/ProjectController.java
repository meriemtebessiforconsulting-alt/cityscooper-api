package cityScooper.cityScooper.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cityScooper.cityScooper.dto.SearchCriteriaInput;
import cityScooper.cityScooper.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@Tag(
    name = "Recherche de villes",
    description = "API de recherche intelligente de villes selon budget, projet de vie, prix immobilier et temps de trajet."
)
public class ProjectController {

    private final SearchService searchService;

    public ProjectController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(
        summary = "Recherche de villes",
        description =
            """
            Permet de rechercher des villes françaises selon plusieurs critères :
            
            - type de projet (family, investment, retirement, student)
            - budget immobilier
            - surface souhaitée
            - type de bien (A = appartement, H = maison)
            - ville de référence pour le trajet
            - temps de trajet maximum
            - rayon kilométrique
            - recherche par nom de ville
            - filtres avancés sur les prix au m²
            
            Règles métier :
            - Le budget est converti en prix max au m².
            - Le rayon est appliqué AVANT le calcul ORS.
            - Le temps de trajet utilise OpenRouteService.
            - Les notes proviennent de BDMVille.
            """
    )
    @GetMapping("/cities/byproject")
    public Map<String, Object> byProject(

        @Parameter(
            description = "Type de projet de vie",
            example = "family"
        )
        @Schema(
        	    allowableValues = {
        	        "family",
        	        "investment",
        	        "retirement",
        	        "student"
        	    }
        	)
        @RequestParam(required = false)
        String projectType,

        @Parameter(
            description = "Budget total du projet immobilier",
            example = "350000 pour 350MK"
        )
        @RequestParam(required = false)
        Double budget,

        @Parameter(
            description = "Surface souhaitée en m²",
            example = "80"
        )
        @RequestParam(required = false)
        Double surface,

        @Parameter(
            description = "Note moyenne minimale",
            example = "3"
        )
        @RequestParam(required = false)
        Double note,

        @Parameter(
            description = 
            """
            Type de bien : A = Appartement, H = Maison. 
            Dans le cas où aucune valeur n'est indiquée, nous nous basons sur une valeur moyenne 
            commune aux 2 types récupérée depuis pricePerM2.average.",		
            		""",
            example = "A"
        )
        @Schema(
        	    allowableValues = {
        	        "A",
        	        "H"
        	    }
        	)
        @RequestParam(required = false)
        String propertyType,

        @Parameter(
            description = "Ville de référence pour les calculs de distance",
            example = "Paris"
        )
        @RequestParam(required = false)
        String workCity,

        @Parameter(
            description = "Temps maximum de trajet en voiture (minutes)",
            example = "45"
        )
        @RequestParam(required = false)
        Double maxCarMinutes,

        @Parameter(
            description = "Rayon maximum autour de la ville de référence (km)",
            example = "30"
        )
        @RequestParam(required = false)
        Double radiusKm,

        @Parameter(
            description = "Recherche par nom de ville",
            example = "nanterre"
        )
        @RequestParam(required = false)
        String cityName,

        @Parameter(
            description =
                """
                Champ de prix à filtrer.
                
                Valeurs possibles :
                - average
                - min
                - max
                - house
                - apartment
                """,
            example = "apartment"
        )
        @Schema(
        	    allowableValues = {
        	        "pricePerm2.average",
        	        "pricePerm2.min",
        	        "pricePerm2.max",
        	        "house",
        	        "apartment"
        	    }
        	)
        @RequestParam(required = false)
        String priceField,

        @Parameter(
            description =
                """
                Opérateur de comparaison :
                
                - gt = supérieur à
                - lt = inférieur à
                """,
            example = "lt"
        )
        @RequestParam(required = false)
        String priceOperator,

        @Parameter(
            description = "Valeur du prix au m²",
            example = "5000"
        )
        @RequestParam(required = false)
        Double priceValue
    ) {

    	SearchCriteriaInput c = new SearchCriteriaInput();

        c.setProjectType(projectType);
        c.setBudget(budget);
        c.setSurface(surface);
        c.setPropertyType(propertyType);
        c.setWorkCity(workCity);
        c.setMaxCarMinutes(maxCarMinutes);
        c.setRadiusKm(radiusKm);
        c.setCityName(cityName);
        c.setPriceField(priceField);
        c.setPriceOperator(priceOperator);
        c.setPriceValue(priceValue);
        c.setNote(note);
        
        if(cityName == null) {
        	if (priceValue != null) {
        System.out.println(
        	    "Recherche d’un logement de type " + c.getPropertyType()
        	    + " pour un projet " + c.getProjectType()
        	    + ", avec un prix "+c.getPriceField()+" "+c.getPriceOperator()+" "+ c.getPriceValue() + "€"
        	    + ", situé autour de " + c.getWorkCity()
        	    + " avec un temps de trajet maximum de " + c.getMaxCarMinutes() + " minutes"
        	    + (c.getRadiusKm() != null ? ", une note minimale de "+c.getNote()+" et un rayon de recherche de " + c.getRadiusKm() + " km" : "")
        	    + "."
        	);}
        	else { System.out.println(
            	    "Recherche d’un logement de type " + c.getPropertyType()
            	    + " pour un projet " + c.getProjectType()
            	    + ", avec un budget de " + c.getBudget() + "€"
            	    + " et une surface de " + c.getSurface() + " m²"
            	    + ", situé autour de " + c.getWorkCity()
            	    + " avec un temps de trajet maximum de " + c.getMaxCarMinutes() + " minutes"
            	    + (c.getRadiusKm() != null ? ", une note minimale de "+c.getNote()+" et un rayon de recherche de " + c.getRadiusKm() + " km" : "")
            	    + "."
            	);}
        } 
        else { System.out.print("Recherche de la ville "+c.getCityName());}
        return searchService.search(c);
    }
}