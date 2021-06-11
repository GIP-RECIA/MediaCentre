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
package org.esco.portlet.mediacentre.dao.impl;

import org.esco.portlet.mediacentre.dao.IMediaCentreResource;
import org.esco.portlet.mediacentre.model.ressource.Ressource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jgribonvald on 13/09/16.
 */
public class MediaCentreResourceGsonImpl implements IMediaCentreResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private List<Ressource> getServiceMedias(String urlWS, HttpServletRequest request, Map<String, List<String>> userInfos) {
        if (log.isDebugEnabled()) {
            log.debug("Requesting MediaCentre on URL {}", urlWS );
        }
        
        List<Ressource> listRessourceMediaCentre = new ArrayList<>();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(urlWS);
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        System.out.println( response.getStatus() );
        if (response != null && Status.Family.SUCCESSFUL == response.getStatusInfo().getFamily()) {

        	listRessourceMediaCentre = response.readEntity(new GenericType<List<Ressource>>(){});
        	
        }
        else {
            log.warn("Problems on reading json element, needed attributes aren't found, check json source !");
        }
        
        return listRessourceMediaCentre;
    }

    public List<Ressource> retrieveListRessource(String mediaCentreUrl, HttpServletRequest request, Map<String, List<String>> userInfos) {
        return this.getServiceMedias(mediaCentreUrl, request, userInfos);
    }
}
