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

import lombok.NonNull;
import org.esco.portlet.mediacentre.dao.IUserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Profile("mock")
public class MockUserResourceImpl implements IUserResource, InitializingBean {

    private static final String SPLIT_SEP = ",";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @NonNull
    @Value("${userInfo.key.uid}")
    private String uidInfoKey;

    @NonNull
    @Value("${userInfo.key.etabIds}")
    private String etabCodesInfoKey;
    
    @NonNull
    @Value("${userInfo.key.currentEtabId}")
    private String currentEtabCodeInfoKey;
    
    @NonNull
    @Value("${userInfo.key.profils}")
    private String profilsInfoKey;

    @NonNull
    @Value("${userInfo.key.groups}")
    private String userGroupsInfokey;

    private final Map<String, List<String>> userInfoMap = new HashMap<>();

	/**
     * Retrieve the user info attribute from portlet context, or the Mocked user info
     *
     * @param request the portlet request
     * @param attributeName the attribute to retrieve
     * @return the user info attribute values
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserInfo(@NonNull final HttpServletRequest request, @NonNull final String attributeName) {
        if (attributeName.isEmpty()) return Collections.EMPTY_LIST;

        final Map<String, List<String>> userInfo = userInfoMap;

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

        return attributeValues;
    }
    
    public Map<String, List<String>> getUserInfoMap(final HttpServletRequest request) {
        return userInfoMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.etabCodesInfoKey, "No Etab Codes user info key configured !");
        Assert.hasText(this.currentEtabCodeInfoKey, "No Current Etab Code user info key configured !");
        Assert.hasText(this.userGroupsInfokey, "No Group user info key configured !");

//        final String[] etabs = System.getProperty("mediacentre.userEtabs", "0450822X,0333333Y,0377777U,0291595B")
//                .split(SPLIT_SEP);
//        final String current = System.getProperty("mediacentre.userCurrentEtab", "0450822X");

        final String[] etabs = System.getProperty("mediacentre.userEtabs", "0111111111100,0111111111111,0111111111122,0111111111133").split(SPLIT_SEP);
        final String current = System.getProperty("mediacentre.userCurrentEtab", "0111111111111");

        final String[] groups = System.getProperty("mediacentre.userMemberOf",
                "esco:Applications:MediaCentre:GAR:RespAff:Etab_0450822X,esco:Applications:MediaCentre:GAR:user:Etab_0450822X,esco:Applications:MediaCentre:GAR:user:Etab_0333333Y"
        + ",esco:Etablissements:Etab_0450822X:Profs,esco:Etablissements:Etab_0333333Y:Profs,esco:Etablissements:Etab_0377777U:Profs").split(SPLIT_SEP);

        final String[] profiles =   System.getProperty("mediacentre.userProfiles", "National_ENS, National_EVS").split(SPLIT_SEP);
        final String uid = System.getProperty("mediacentre.userId", "F16X001m");
        
        this.userInfoMap.put(this.etabCodesInfoKey, Arrays.asList(etabs));
        this.userInfoMap.put(this.currentEtabCodeInfoKey, Arrays.asList(current));
        this.userInfoMap.put(this.userGroupsInfokey, Arrays.asList(groups));
        this.userInfoMap.put(this.profilsInfoKey, Arrays.asList(profiles));
        this.userInfoMap.put(this.uidInfoKey, Arrays.asList(uid));

        log.debug("userInfoMap : {}", this.userInfoMap);

    }
}
