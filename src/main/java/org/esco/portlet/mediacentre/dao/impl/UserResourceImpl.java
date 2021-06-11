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

import com.google.common.collect.Maps;
import org.esco.portlet.mediacentre.dao.IUserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Profile("!mock")
public class UserResourceImpl implements IUserResource{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //private IUserResource mockedUserResource;
    
    /**
     * Retrieve the user info attribute from portlet context, or the Mocked user info
     *
     * @param request the portlet request
     * @param attributeName the attribute to retrieve
     * @return the user info attribute values
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserInfo(@NotNull final HttpServletRequest request, @NotNull final String attributeName) {
    	log.debug("getUserInfo attributeName={}", attributeName);
    	
        if (attributeName.isEmpty()) return Collections.EMPTY_LIST;
        Map<String, List<String>> userInfo = (Map<String, List<String>>) request.getAttribute("org.jasig.portlet.USER_INFO_MULTIVALUED");
        log.debug("getUserInfo userInfo = {}", userInfo );
        
        List<String> attributeValues = null;

        if (userInfo != null) {
            if (userInfo.containsKey(attributeName)) {
                attributeValues = userInfo.get(attributeName);
            } else {
                log.warn("User attribute '{}' wasn't retrieved, check if the file portlet.xml contains the attribute shared by the portal !!", attributeName);
            }
        } else {
            log.error("Unable to retrieve Portal UserInfo !");
            //throw new IllegalStateException("Unable to retrieve Portal UserInfo !");
        }

        if (attributeValues == null) {
            attributeValues = Collections.EMPTY_LIST;
        }
        log.debug("getUserInfo attributeValues = {}", attributeValues );
        return attributeValues;
    }

    /**
     * Retrieve the user info Map from portlet context, or the Mocked user info
     *
     * @param request the portlet request
     * @return  the user info map with association attribute values
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getUserInfoMap(@NotNull final HttpServletRequest request) {

        Map<String, List<String>> userInfo = (Map<String, List<String>>) request.getAttribute("org.jasig.portlet.USER_INFO_MULTIVALUED");
        log.debug("getUserInfoMap userInfo = {}", userInfo );
        if (userInfo != null) {
            return userInfo;
        } else {
            log.error("Unable to retrieve Portal UserInfo !");
            //throw new IllegalStateException("Unable to retrieve Portal UserInfo !");
        }

        return Maps.newLinkedHashMap();

    }

    /**public IUserResource getMockedUserResource() {
        return mockedUserResource;
    }

    public void setMockedUserResource(IUserResource mockedUserResource) {
        this.mockedUserResource = mockedUserResource;
    }*/
}
