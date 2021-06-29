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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.soffit.Headers;
import org.esco.portlet.mediacentre.dao.IUserResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
@Profile("!mock")
public class UserResourceImpl implements IUserResource {
    // Attributes
    @Value("${security-configuration.soffit.jwt.signatureKey:Changeme}")
    private String signatureKey;

    // Methods
    /*
    /**
     * Retrieve the user info attribute from portlet context, or the Mocked user info
     *
     * @param request the portlet request
     * @param attributeName the attribute to retrieve
     * @return the user info attribute values
     * /
    @SuppressWarnings("unchecked")
    public List<String> getUserInfo(@NonNull final HttpServletRequest request, @NonNull final String attributeName) {
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
    */

    /*
    /**
     * Retrieve the user info Map from portlet context, or the Mocked user info
     *
     * @param request the portlet request
     * @return  the user info map with association attribute values
     * /
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getUserInfoMap(@NonNull final HttpServletRequest request) {

        Map<String, List<String>> userInfo = (Map<String, List<String>>) request.getAttribute("org.jasig.portlet.USER_INFO_MULTIVALUED");
        log.debug("getUserInfoMap userInfo = {}", userInfo );
        if (userInfo != null) {
            return userInfo;
        } else {
            log.error("Unable to retrieve Portal UserInfo !");
            //throw new IllegalStateException("Unable to retrieve Portal UserInfo !");
        }

        return new LinkedHashMap<>();

    }
    */

    @Override
    public List<String> getUserInfo(@NonNull final HttpServletRequest request, @NonNull final String attributeName) {
        log.debug("getUserInfo attributeName={}", attributeName);
        return this.getUserInfoMap(request).getOrDefault(attributeName, new ArrayList<>());
    }

    @Override
    public Map<String, List<String>> getUserInfoMap(@NonNull final HttpServletRequest request) {
        final Jws<Claims> oidcToken = parseOidcToken(request);

        Map<String, List<String>> userInfo = new LinkedHashMap<>();

        if (oidcToken != null) {
            for (String claimName : oidcToken.getBody().keySet()) {
                final Object claimValue = oidcToken.getBody().get(claimName);
                final List<String> claimValueList = new ArrayList<>();
                userInfo.put(claimName, claimValueList);

                if (claimValue == null) {
                    log.debug("claim value of claim name {} is null", claimName);
                } else if (claimValue instanceof List<?>) {
                    for (Object claimValueElement : (List<?>) claimValue) {
                        claimValueList.add(claimValueElement.toString());
                    }
                } else {
                    claimValueList.add(claimValue.toString());
                }
            }
        }

        return userInfo;
    }

    /**
     * Obtains information about the user from the Authorization header in the form of an OIDC Id
     * token.  In the future, it would be better to provide a standard tool (bean) for this job in
     * the <code>uPortal-soffit-renderer</code> component.
     */
    private Jws<Claims> parseOidcToken(HttpServletRequest req) {

        final String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader.isBlank() || !authHeader.startsWith(Headers.BEARER_TOKEN_PREFIX)) {
            /*
             * No OIDC token available
             */
            return null;
        }

        final String bearerToken = authHeader.substring(Headers.BEARER_TOKEN_PREFIX.length());

        try {
            // Validate & parse the JWT
            final Jws<Claims> rslt = Jwts.parserBuilder().setSigningKey(this.signatureKey).build().parseClaimsJws(bearerToken);

            log.debug("Found the following OIDC Id token:  {}", rslt.toString());

            return rslt;
        } catch (Exception e) {
            log.info("The following Bearer token is unusable:  '{}'", bearerToken);
            log.debug("Failed to validate and/or parse the specified Bearer token", e);
        }

        return null;

    }
}
