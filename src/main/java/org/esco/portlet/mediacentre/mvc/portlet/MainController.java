/**
 * Copyright © 2017 GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esco.portlet.mediacentre.mvc.portlet;

import org.esco.portlet.mediacentre.model.affectation.GestionAffectation;
import org.esco.portlet.mediacentre.model.filtres.CategorieFiltres;
import org.esco.portlet.mediacentre.model.ressource.IdEtablissement;
import org.esco.portlet.mediacentre.model.ressource.Ressource;
import org.esco.portlet.mediacentre.service.IFiltrageService;
import org.esco.portlet.mediacentre.service.IMediaCentreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.annotation.Resource;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Main portlet view.
 */
@Controller
@RequestMapping("VIEW")
public class MainController {

	/* 
	 * ===============================================
	 * Propriétés de la classe 
	 * =============================================== 
	 */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IMediaCentreService mediaCentreService;

    @Autowired
    private IFiltrageService filtrageService;    
    
    @Resource
    private List<CategorieFiltres> categoriesFiltres;
    
    @Resource
    private List<GestionAffectation> gestionAffectation;
    
    
	/* 
	 * ===============================================
	 * Constructeurs de la classe 
	 * =============================================== 
	 */

	/* 
	 * ===============================================
	 * Getter / Setter de la classe 
	 * =============================================== 
	 */

	/* 
	 * ===============================================
	 * Méthodes privées de la classe 
	 * =============================================== 
	 */
 
	/* 
	 * ===============================================
	 * Méthodes publiques de la classe 
	 * =============================================== 
	 */	
    
    @RenderMapping
    public ModelAndView showMainView(final RenderRequest request, final RenderResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("main");

        // case to request to redirect to a resource directly
        final boolean isBase64 = Boolean.parseBoolean(request.getParameter("base64"));
        final String redirParam = request.getParameter("redirect");
        String redirectParam = redirParam;
        if (isBase64 && StringUtils.hasText(redirParam)) {
            redirectParam = new String(Base64.getDecoder().decode(redirParam));
        }
        if (StringUtils.hasText(redirectParam)) {
            mav = new ModelAndView("redirect");
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Using view name " + mav.getViewName() + " for main view");
        }
        
        final Map<String, List<String>> userInfo = mediaCentreService.getUserInfos(request);
        List<Ressource> listeRessources = mediaCentreService.retrieveListRessource(request);

        if (StringUtils.hasText(redirectParam)) {
            final List<String> etablissementsCourants = mediaCentreService.getUserCurrentEtablissement(request);
            String etablissementCourant = null;
            if (etablissementsCourants != null && !etablissementsCourants.isEmpty()) {
                etablissementCourant = etablissementsCourants.get(0);
            }

            String redirectUrl = null;
            for (Ressource rs : listeRessources) {
                // On cherche à associer la ressource qui correspond à l'établissement courant
                if (rs.getNomRessource().equalsIgnoreCase(redirectParam) && !StringUtils.isEmpty(etablissementCourant)
                        && !StringUtils.isEmpty(rs.getIdEtablissement())) {
                    for (IdEtablissement etab : rs.getIdEtablissement()) {
                        if (etablissementCourant.equals(etab.getId())) {
                            redirectUrl = rs.getUrlAccesRessource();
                            break;
                        }
                    }
                    if (redirectUrl != null) break;
                } else if (rs.getNomRessource().equalsIgnoreCase(redirectParam) && StringUtils.isEmpty(rs.getIdEtablissement())) {
                    // quand la ressource est globale sans établissement associé
                    log.warn("Redirect to resource without organization associated {}", rs);
                    redirectUrl = rs.getUrlAccesRessource();
                    break;
                }
            }
            mav.addObject("redirectTo", redirectUrl);
            if(log.isDebugEnabled()) {
                log.debug("Entering on case redirecting to resource url " + redirectUrl);
            }
        } else {

            List<CategorieFiltres> categoriesFiltresCandidats = new ArrayList<CategorieFiltres>();
            List<Ressource> ressourcesCandidates = new ArrayList<Ressource>();

            final String ressourcesParFiltre = filtrageService.preparerFiltrage(userInfo, categoriesFiltres, listeRessources, categoriesFiltresCandidats, ressourcesCandidates);

            mav.addObject("ressourcesParFiltre", ressourcesParFiltre);
            mav.addObject("ressources", ressourcesCandidates);
            mav.addObject("categoriesFiltres", categoriesFiltresCandidats);
            mav.addObject("gestionAffectation", filtrageService.filtrerGestionAffectation(gestionAffectation, userInfo));
        }
        
        if(log.isDebugEnabled()) {
            log.debug("Rendering " + mav.getViewName() + " view");
        }

        response.getCacheControl().setExpirationTime(0);
        
        return mav;
    }
 
    //  @ActionMapping
    public void doAction() {
        // no-op action mapping to prevent accidental calls to this URL from
        // crashing the portlet
    	log.debug("action mapping");
    	
    }

    @ResourceMapping(value="ajouterFavori")
    public void ajouterFavori(ResourceRequest request, ResourceResponse response) throws IOException {
    	String id = request.getParameter("id");
    	mediaCentreService.addToUserFavorites(request, id);
    	response.getWriter().write("{\"success\":true}");
    }
    
    @ResourceMapping(value="retirerFavori")
    public void retirerFavori(ResourceRequest request, ResourceResponse response) throws IOException {
    	String id = request.getParameter("id");
    	mediaCentreService.removeToUserFavorites(request, id);
    	response.getWriter().write("{\"success\":true}");
    }
    
}
