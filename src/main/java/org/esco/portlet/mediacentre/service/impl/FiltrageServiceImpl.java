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
package org.esco.portlet.mediacentre.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.esco.portlet.mediacentre.model.affectation.GestionAffectation;
import org.esco.portlet.mediacentre.model.filtres.CategorieFiltres;
import org.esco.portlet.mediacentre.model.filtres.CategorieFiltresCalcules;
import org.esco.portlet.mediacentre.model.filtres.Filtre;
import org.esco.portlet.mediacentre.model.ressource.Ressource;
import org.esco.portlet.mediacentre.service.IFiltrageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FiltrageServiceImpl implements IFiltrageService {

	/* 
	 * ===============================================
	 * Propriétés de la classe 
	 * =============================================== 
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
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
	
	/* (non-Javadoc)
	 * @see org.esco.portlet.mediacentre.service.IFiltrageService#preparerFiltrage(java.util.Map, java.util.List, java.util.List, java.util.List, java.util.List)
	 */
	public String preparerFiltrage(
			Map<String, List<String>> userInfoMap, 
			List<CategorieFiltres> categoriesFiltres,  
			List<Ressource> ressources, 
			List<CategorieFiltres> categoriesFiltresCandidats, 
			List<Ressource> ressourcesCandidates) throws Exception {
		
    	//------------------------------------------------------------
    	// Calcul des ressources candidates et des filtres a afficher
    	//------------------------------------------------------------
    	Map<Integer, Ressource> mapRessourcesCandidates = new HashMap<Integer, Ressource>();
    	Map<String, CategorieFiltres> mapCategoriesCandidates = new HashMap<String, CategorieFiltres>();
    	
    	Map<String, Map<String, List<Integer>>> mapCategories = new HashMap<>();
    	if (log.isDebugEnabled()) {
    		log.debug("Ressources avant filtrage");
    		ObjectMapper mapper = new ObjectMapper();
        	log.debug(mapper.writeValueAsString(ressources));
    	}
    	
    	for (CategorieFiltres categorie : categoriesFiltres ) {
    		if ( !categorie.concerneUtilisateur(userInfoMap)) {
    			continue;
    		}
    				
    		if (categorie.estCategorieCalculee()) {
    			categorie = (CategorieFiltres)categorie.clone();
    			((CategorieFiltresCalcules)categorie).initialiser(userInfoMap, ressources);	
    		} 
    		
    		Map<String, List<Integer>> mapFiltre = new HashMap<String, List<Integer>>();
    		List<Filtre> filtresClone = new ArrayList<Filtre>();
    		
    		// Pour chaque filtre de la catégorie, on vérifie s'il existe des ressources passantes 
    		int nbFiltreSelectAll=0;
    		for (Filtre filtre : categorie.getFiltres()) {
    			if (!filtre.concerneUtilisateur(userInfoMap)) {
    				continue;
    			}
    			
    			List<Integer> listeRessources = new ArrayList<Integer>();
    			if (!filtre.isCaseSelectAll()) {
        			for (Ressource ressource : ressources) {
        				if (filtre.estPassante(ressource)) {
        					listeRessources.add(ressource.getIdInterne());
        					mapRessourcesCandidates.put(ressource.getIdInterne(), ressource);
        				}
        			}
    			} else {
    				nbFiltreSelectAll++;
    			}
    			
    			// S'il existe au moins une ressource passante, on conserve le filtre
    			if (!listeRessources.isEmpty() || filtre.isCaseSelectAll() || "favoris".equalsIgnoreCase(filtre.getId())) {
    				mapFiltre.put(filtre.getId(), listeRessources);
    				
    				filtresClone.add((Filtre)filtre.clone());
    			}
    		}
    		
    		// La catégorie est affichée si elle comporte plusieurs filtres (dont le type est different de "selectAll") 
    		boolean estAffichable = filtresClone.size() > (1 + nbFiltreSelectAll);
    		if (estAffichable) {
    			mapCategories.put(categorie.getId(), mapFiltre);

    			CategorieFiltres categorieClone = (CategorieFiltres)categorie.clone();
    			mapCategoriesCandidates.put(categorieClone.getId(), categorieClone);
    			categorieClone.setFiltres(filtresClone);
    		}
    	}
    	
    	//------------------------------------------------------------ 
    	// Calcul les ressources qui peuvent être affichées
    	//  (présentes dans chaque catégorie)
    	//------------------------------------------------------------ 
    	List<Integer> ressourcesConservees = null;
    	for (String categorie: mapCategories.keySet() ) {
    		Map<String, List<Integer>> listeFiltres = mapCategories.get(categorie);
    		Set<Integer> ressourceDeLaCategorie = new HashSet<Integer>();
    		for (String filtre : listeFiltres.keySet()) {
    			ressourceDeLaCategorie.addAll(listeFiltres.get(filtre));
    		}
    		if (ressourcesConservees == null) {
    			ressourcesConservees = new ArrayList<Integer>();
    			ressourcesConservees.addAll(ressourceDeLaCategorie);
    		} else {
    			ressourcesConservees.retainAll(ressourceDeLaCategorie);
    		}
    	}
    	
    	//------------------------------------------------------------
    	// Retire les ressources ne pouvant pas être affichées ainsi 
    	// que le filtres ne ciblant que ces ressources
    	//------------------------------------------------------------
    	Map<String, Map<String, List<Integer>>> categoriesFinales = new HashMap<String, Map<String, List<Integer>>>();    	
    	for (String categorie: mapCategories.keySet() ) {
    		Map<String, List<Integer>> listeFiltres = mapCategories.get(categorie);
    		Map<String, List<Integer>> listeFiltresFinale = new HashMap<String, List<Integer>>();
    		for (String filtre : listeFiltres.keySet()) {
    			List<Integer> listeRessources = listeFiltres.get(filtre);
    			if (listeRessources.isEmpty()) {
    				continue;
    			}
    			if (!Collections.disjoint(listeRessources, ressourcesConservees)) {
    				listeRessources.retainAll(ressourcesConservees);
    				listeFiltresFinale.put(filtre, listeRessources);
    			} else {
    				Filtre filtreARetirer = null; 
    				for (Filtre f : mapCategoriesCandidates.get(categorie).getFiltres()) {
    					if (f.getId() != null && f.getId().equals(filtre)) {
    						filtreARetirer = f;
    					}
    				}
    				if (filtreARetirer != null) {
    					mapCategoriesCandidates.get(categorie).getFiltres().remove(filtreARetirer);
    				}
    			}
    		}
    		if (!listeFiltresFinale.isEmpty()) {
    			categoriesFinales.put(categorie, listeFiltresFinale);
    		} else {
    			mapCategoriesCandidates.remove(categorie);
    		}
    	}
    	
    	for (Integer id : mapRessourcesCandidates.keySet()) {
    		if (ressourcesConservees.contains(id)) {
    			ressourcesCandidates.add(mapRessourcesCandidates.get(id));
    		}
    	}
    	
    	//------------------------------------------------------------
    	// construction de la liste des categories candidates en 
    	// conservant l'ordre initial
    	//------------------------------------------------------------
    	for (CategorieFiltres categorie: categoriesFiltres) {
    		CategorieFiltres categorieFinale = mapCategoriesCandidates.get(categorie.getId());
    		if (categorieFinale != null) {
    			categoriesFiltresCandidats.add(categorieFinale);
    		}
    	}
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Ressources filtrées");
    		ObjectMapper mapper = new ObjectMapper();
        	log.debug(mapper.writeValueAsString(ressourcesCandidates));
    	}    	
    	
    	// Transformation en JSON du paramétrage
    	ObjectMapper mapper = new ObjectMapper();
    	String json = mapper.writeValueAsString(categoriesFinales);
    	log.debug("parametres de filtrage : " + json);
    	log.debug("nombre de ressources candidates : " + ressourcesCandidates.size());
    	return json;
	}
	

	/* (non-Javadoc)
	 * @see org.esco.portlet.mediacentre.service.IFiltrageService#filtrerGestionAffectation(java.util.List, java.util.Map)
	 */
	public List<GestionAffectation> filtrerGestionAffectation(List<GestionAffectation> listeGestionAffectation, Map<String, List<String>> userInfo) {
		List<GestionAffectation> gestionAffectationFiltrees = new ArrayList<GestionAffectation>();
		for (GestionAffectation gestionAffectation: listeGestionAffectation) {
			if (gestionAffectation.concerneUtilisateur(userInfo)) {
				gestionAffectationFiltrees.add(gestionAffectation);
			}
		}
		return gestionAffectationFiltrees;
	}
}

