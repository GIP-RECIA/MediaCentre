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
package fr.recia.mediacentre.mediacentre.web.rest;

import fr.recia.mediacentre.mediacentre.model.allocation.GestionAffectation;
import fr.recia.mediacentre.mediacentre.model.filter.category.CategorieFiltres;
import fr.recia.mediacentre.mediacentre.model.resource.Ressource;
import fr.recia.mediacentre.mediacentre.service.FiltrageService;
import fr.recia.mediacentre.mediacentre.service.MediaCentreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/resources")
public class MediaCentreController {

    @Autowired
    private MediaCentreService mediaCentreService;

    @Autowired
    private FiltrageService filtrageService;

    private List<CategorieFiltres> categoriesFiltres = new ArrayList<>();

    private List<GestionAffectation> gestionAffectation = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<Ressource>> getResources() throws Exception {
        List<Ressource> listeRessources = mediaCentreService.retrieveListRessource();
        //List<CategorieFiltres> categoriesFiltresCandidats = new ArrayList<CategorieFiltres>();
        //List<Ressource> ressourcesCandidates = new ArrayList<Ressource>();

        //final String ressourcesParFiltre = filtrageService.preparerFiltrage(categoriesFiltres, listeRessources, categoriesFiltresCandidats, ressourcesCandidates);

        return new ResponseEntity<>(listeRessources, HttpStatus.OK);
    }

    @GetMapping("/{filter}")
    public ResponseEntity<List<Ressource>> getResourcesByFilter(@PathVariable String filter) throws Exception {
        List<Ressource> userResources = getResources().getBody();
        List<Ressource> listeRessourcesByFilter = mediaCentreService.retrieveListRessourceByFilter(userResources, filter);

        return new ResponseEntity<>(listeRessourcesByFilter, HttpStatus.OK);
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, String>> getFilters() throws Exception {
        List<Ressource> userResources = getResources().getBody();
        return new ResponseEntity<>(mediaCentreService.retrieveListFilter(userResources), HttpStatus.OK);
    }

    @PostMapping("/favorite/{id}")
    public ResponseEntity<Object> ajouterFavori(@PathVariable String id) {
        boolean isAddFavorite = mediaCentreService.addToUserFavorites(id);
        if (isAddFavorite) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<Object> retirerFavori(@PathVariable String id) {
        boolean isRemoveFavorite = mediaCentreService.removeToUserFavorites(id);
        if (isRemoveFavorite) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}