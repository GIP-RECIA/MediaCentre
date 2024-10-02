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
package fr.recia.mediacentre.api.web.rest;

import fr.recia.mediacentre.api.web.rest.exception.MediacentreWSException;
import fr.recia.mediacentre.api.web.rest.exception.YmlPropertyNotFoundException;
import fr.recia.mediacentre.api.model.filter.FilterEnum;
import fr.recia.mediacentre.api.model.pojo.IsMemberOf;
import fr.recia.mediacentre.api.model.resource.Ressource;
import fr.recia.mediacentre.api.service.MediaCentreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/resources")
public class MediaCentreController {

    @Autowired
    private MediaCentreService mediaCentreService;

    @PostMapping
    public ResponseEntity<List<Ressource>> getResources(@RequestBody IsMemberOf isMemberOf) {
        try{
            List<Ressource> resourcesList = mediaCentreService.retrieveListRessource(isMemberOf.getIsMemberOf());
            return new ResponseEntity<>(resourcesList, HttpStatus.OK);
        }catch (YmlPropertyNotFoundException e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MediacentreWSException e) {
          return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @GetMapping(path = "/filters")
    public ResponseEntity<List<FilterEnum>> getFilters(){
        try{
            List<FilterEnum> filterEnumList = mediaCentreService.retrieveFiltersList();
            return new ResponseEntity<>(filterEnumList, HttpStatus.OK);
        }catch(YmlPropertyNotFoundException e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
