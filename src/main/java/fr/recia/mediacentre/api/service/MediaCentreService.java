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
package fr.recia.mediacentre.api.service;

import fr.recia.mediacentre.api.web.rest.exception.YmlPropertyNotFoundException;
import fr.recia.mediacentre.api.model.filter.FilterEnum;
import fr.recia.mediacentre.api.model.resource.Ressource;
import java.util.List;


public interface MediaCentreService {

    /***
     * Function that returns the list of current user's resources.
     * @param isMemberOf - a list of user's groups
     * @return listeRessources - The list of current user's resources.
     */
    List<Ressource> retrieveListRessource(List<String> isMemberOf) throws YmlPropertyNotFoundException;

    /***
     * Function that check the user's profile and returns a list of FilterEnum corresponding to the yml properties file.
     *
     * @return a List<FilterEnum>
     */
    List<FilterEnum> retrieveFiltersList() throws YmlPropertyNotFoundException;
}
