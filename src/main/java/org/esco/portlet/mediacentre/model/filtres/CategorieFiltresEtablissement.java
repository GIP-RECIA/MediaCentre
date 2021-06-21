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
package org.esco.portlet.mediacentre.model.filtres;

import lombok.*;
import org.esco.portlet.mediacentre.model.ressource.IdEtablissement;
import org.esco.portlet.mediacentre.model.ressource.Ressource;

import java.util.*;

/**
 * @author elecaude
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CategorieFiltresEtablissement extends CategorieFiltresCalcules {
	@NonNull
	private String attributUtilisateurDefaut;


	/* (non-Javadoc)
	 * @see org.esco.portlet.mediacentre.model.filtres.CategorieFiltresCalcules#initialiser(java.util.Map, java.util.List)
	 */
	@Override
	public void initialiser(Map<String, List<String>> userInfoMap, List<Ressource> ressources) throws Exception {
		if (ressources == null) {
			return;
		}
		Set<IdEtablissement> etablissements = new HashSet<IdEtablissement>();
		for (Ressource ressource: ressources) {
			if (ressource.getIdEtablissement() != null && !ressource.getIdEtablissement().isEmpty()) {
				 etablissements.addAll(ressource.getIdEtablissement());
			}
		}
		if (etablissements.isEmpty()) {
			return;
		}
		final List<String> etablissementsCourants = userInfoMap.get(getAttributUtilisateurDefaut());
		String etablissementCourant = "";
		if (etablissementsCourants != null && !etablissementsCourants.isEmpty()) {
			etablissementCourant = etablissementsCourants.get(0);
		}

		List<Filtre> filtres = new ArrayList<Filtre>();
		
		if (isValeursMultiples() && !getLibelleTous().isBlank()) {
			Filtre filtre = new Filtre();
    		filtre.setId(getId());
    		filtre.setLibelle(getLibelleTous());
    		filtre.setDefaultEmptyValue(getDefaultEmptyValue());
			filtre.setNomAttribut(getNomAttributFiltre());
    		filtre.setActif(false);
    		filtre.setCaseSelectAll(true);
			filtre.setPopulation(getPopulation());
			filtre.setRegexpPopulation(this.getRegexpPopulation());
			filtre.setAddEmptyFilteredValues(true);
    		filtres.add(filtre);
		}
		
		for (IdEtablissement etablissement : etablissements) {
			Filtre filtre = new Filtre();
			filtre.setActif(false);
			filtre.setId(etablissement.getId());
			filtre.setLibelle(etablissement.getNom());
			filtre.setDefaultEmptyValue(getDefaultEmptyValue());
			filtre.setNomAttribut(getNomAttributFiltre());
			filtre.setPopulation(getPopulation());
			filtre.setRegexpPopulation(this.getRegexpPopulation());
			filtre.setRegexpAttribut(etablissement.getId());
			if (etablissement.getId().equalsIgnoreCase(etablissementCourant)) {
				filtre.setActif(true);
			}
			filtre.setAddEmptyFilteredValues(true);
			filtres.add(filtre);
	    }
		setFiltres(filtres);
	}
	
}
