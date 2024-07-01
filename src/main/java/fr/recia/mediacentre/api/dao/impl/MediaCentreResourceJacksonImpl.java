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
package fr.recia.mediacentre.api.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fr.recia.mediacentre.api.dao.MediaCentreResource;
import fr.recia.mediacentre.api.model.resource.Ressource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jgribonvald on 13/09/16.
 */

@Slf4j
@Service
public class MediaCentreResourceJacksonImpl implements MediaCentreResource {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RestTemplate restTemplate;


    public List<Ressource> retrieveListRessource(String mediaUrl,Map<String, List<String>> userInfos) {
        return this.getServiceMediaCentre(mediaUrl,userInfos);
    }

    @Cacheable(cacheNames = "userResourcesCache", key = "#userInfos.uid[0]")
    private List<Ressource> getServiceMediaCentre(String url,Map<String, List<String>> userInfos) {
        if (log.isDebugEnabled()) {
        log.debug("Requesting mediacentre on URL {}", url );
    }
        List<Ressource> listRessourceMediaCentre = null;
        ObjectMapper objectMapper = new ObjectMapper();

        Cache cache = cacheManager.getCache("userResourcesCache");
        List<Ressource> userResources = (List<Ressource>) cache.get(userInfos.get("uid").get(0), List.class);
        if(!Objects.isNull(userResources)){
            return userResources;
        }
//        try {
//            listRessourceMediaCentre = objectMapper.readValue(new File("src/test/resources/json/resources-example.json"),new TypeReference<>(){});
//            cache.putIfAbsent(userInfos.get("uid").get(0),listRessourceMediaCentre);
//        } catch (IOException ex) {
//            log.info("Unable to convert given json value into resource list.");
//            throw new RuntimeException(ex);
//        }

        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<Map<String, List<String>>> requestEntity = new HttpEntity<Map<String, List<String>>>(userInfos, requestHeaders);
            ResponseEntity<Ressource[]> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Ressource[].class);
            listRessourceMediaCentre = Lists.newArrayList(response.getBody());
            cache.putIfAbsent(userInfos.get("uid").get(0),listRessourceMediaCentre);
        } catch (HttpClientErrorException e) {
            // providing the error stacktrace only on debug as the custom logged error should be suffisant.
            log.warn("Error client request on URL {}, returned status {}, with response {}", url, e.getStatusCode(), e.getResponseBodyAsString(),e);
            return Lists.newArrayList();
        } catch (RestClientException ex) {
            log.warn("Error getting MediaCentre from url '{}'", url, ex.getLocalizedMessage(), ex);
            return Lists.newArrayList();
        } catch (HttpMessageNotReadableException ex) {
            log.warn("Error getting MediaCentre from url '{}' the object doesn't map MediaCentre Object properties with a such response {}", url, ex.getLocalizedMessage(), ex);
            return Lists.newArrayList();
        }

        return listRessourceMediaCentre;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}

