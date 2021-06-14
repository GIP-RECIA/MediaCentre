package org.esco.portlet.mediacentre.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.esco.portlet.mediacentre.model.affectation.GestionAffectation;
import org.esco.portlet.mediacentre.model.apiresponse.ApiError;
import org.esco.portlet.mediacentre.model.apiresponse.ApiResponse;
import org.esco.portlet.mediacentre.model.filtres.CategorieFiltres;
import org.esco.portlet.mediacentre.model.ressource.IdEtablissement;
import org.esco.portlet.mediacentre.model.ressource.Ressource;
import org.esco.portlet.mediacentre.service.IFiltrageService;
import org.esco.portlet.mediacentre.service.IMediaCentreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "api/")
public class ApiMediacentreController {
    // Beans & ressources
    @Autowired
    private IMediaCentreService mediaCentreService;

    @Autowired
    private IFiltrageService filtrageService;

    @Resource
    private List<CategorieFiltres> categoriesFiltres;

    @Resource
    private List<GestionAffectation> gestionAffectation;

    // Méthodes
    @GetMapping(value = "/mediacentre")
    public ResponseEntity<ApiResponse> mediacentre(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        if(log.isDebugEnabled()) log.debug("Mediacentre request.");

        HashMap<String, Object> payload = new HashMap<>();

        // case to request to redirect to a ressource directly
        final String redirectParam = request.getParameter("redirect");

        final Map<String, List<String>> userInfo = mediaCentreService.getUserInfos(request);
        List<Ressource> listeRessources = mediaCentreService.retrieveListRessource(request);

        if (redirectParam != null) {
            final List<String> etablissementsCourants = mediaCentreService.getUserCurrentEtablissement(request);
            String etablissementCourant = null;
            if (etablissementsCourants != null && !etablissementsCourants.isEmpty()) {
                etablissementCourant = etablissementsCourants.get(0);
            }

            String redirectUrl = null;
            for (Ressource rs : listeRessources) {
                // On cherche à associer la ressource qui correspond à l'établissement courant
                if (rs.getNomRessource().equalsIgnoreCase(redirectParam) && etablissementCourant != null && !etablissementCourant.isEmpty()
                        && !rs.getIdEtablissement().isEmpty()) {
                    for (IdEtablissement etab : rs.getIdEtablissement()) {
                        if (etablissementCourant.equals(etab.getId())) {
                            redirectUrl = rs.getUrlAccesRessource();
                            break;
                        }
                    }
                    if (redirectUrl != null) break;
                } else if (rs.getNomRessource().equalsIgnoreCase(redirectParam) && rs.getIdEtablissement().isEmpty()) {
                    // quand la ressource est globale sans établissement associé
                    log.warn("Redirect to resource without organization associated {}", rs);
                    redirectUrl = rs.getUrlAccesRessource();
                    break;
                }
            }
            payload.put("redirectTo", redirectUrl);
            if(log.isDebugEnabled()) {
                log.debug("Entering on case redirecting to resource url " + redirectUrl);
            }
        } else {

            List<CategorieFiltres> categoriesFiltresCandidats = new ArrayList<>();
            List<Ressource> ressourcesCandidates = new ArrayList<>();

            final String ressourcesParFiltre = filtrageService.preparerFiltrage(userInfo, categoriesFiltres, listeRessources, categoriesFiltresCandidats, ressourcesCandidates);

            payload.put("ressourcesParFiltre", ressourcesParFiltre);
            payload.put("ressources", ressourcesCandidates);
            payload.put("categoriesFiltres", categoriesFiltresCandidats);
            payload.put("gestionAffectation", filtrageService.filtrerGestionAffectation(gestionAffectation, userInfo));
        }

        // response.getCacheControl().setExpirationTime(0); <- Deprecated due to replacement of Portlet by Spring Boot.

        return new ResponseEntity<>(
                new ApiResponse(
                        "Mediacentre ressources request successfully processed.",
                        payload
                ),
                HttpStatus.BAD_REQUEST // 400
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse> handleExceptionMissingParameter(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        return new ResponseEntity<>(
                new ApiResponse(
                        "Api request failed: bad request.",
                        new ApiError(exception)
                ),
                HttpStatus.BAD_REQUEST // 400
        );
    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiResponse> handleExceptionElse(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        return new ResponseEntity<>(
                new ApiResponse(
                        "Api request failed: unknown internal server error.",
                        new ApiError(exception)
                ),
                HttpStatus.INTERNAL_SERVER_ERROR // 500
        );
    }
}
