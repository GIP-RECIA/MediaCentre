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
package fr.recia.mediacentre.api.model.resource;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "distributeurTech",
    "domaineEnseignement",
    "idEditeur",
    "idRessource",
    "idEtablissement",
    "idType",
    "niveauEducatif",
    "nomEditeur",
    "nomRessource",
    "sourceEtiquette",
    "typePedagogique",
    "typePresentation",
    "typologieDocument",
    "urlAccesRessource",
    "urlVignette",
    "validateurTech",
    "description"
})
@Getter
@Setter
public class Ressource {

    @JsonProperty("distributeurTech")
    private String distributeurTech;

    @JsonProperty("domaineEnseignement")
    private List<DomaineEnseignement> domaineEnseignement;

    @JsonProperty("idEditeur")
    private String idEditeur;

    @JsonProperty("idRessource")
    private String idRessource;

    @JsonProperty("idEtablissement")
    private List<IdEtablissement> idEtablissement;

    @JsonProperty("idType")
    private String idType;

    @JsonProperty("niveauEducatif")
    private List<NiveauEducatif> niveauEducatif;

    @JsonProperty("nomEditeur")
    private String nomEditeur;

    @JsonProperty("nomRessource")
    private String nomRessource;

    @JsonProperty("sourceEtiquette")
    private String sourceEtiquette;

    @JsonProperty("typePedagogique")
    private List<TypePedagogique> typePedagogique;

    @JsonProperty("typePresentation")
    private TypePresentation typePresentation;

    @JsonProperty("typologieDocument")
    private List<TypologieDocument> typologieDocument;

    @JsonProperty("urlAccesRessource")
    private String urlAccesRessource;

    @JsonProperty("urlVignette")
    private String urlVignette;

    @JsonProperty("validateurTech")
    private String validateurTech;

    @JsonProperty("description")
    private String description;

    private boolean estAffichable = true;

    @Setter
    @JsonIgnore
    private boolean favorite;

    @Setter
    @JsonIgnore
    private int idInterne;
}

