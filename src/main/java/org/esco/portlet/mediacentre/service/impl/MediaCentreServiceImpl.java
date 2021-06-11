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
package org.esco.portlet.mediacentre.service.impl;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.esco.portlet.mediacentre.dao.IMediaCentreResource;
import org.esco.portlet.mediacentre.dao.IPreferenceResource;
import org.esco.portlet.mediacentre.dao.IUserResource;
import org.esco.portlet.mediacentre.model.ressource.Ressource;
import org.esco.portlet.mediacentre.service.IMediaCentreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.portlet.ReadOnlyException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by jgribonvald on 06/06/17.
 */
@Service
@NoArgsConstructor
public class MediaCentreServiceImpl implements IMediaCentreService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String PREF_MEDIA_URL = "mediaUrl";
    
    @NonNull
    @Value("${userInfo.key.uid}")
    @Setter
    private String uidInfoKey;

    @NonNull
    @Value("${userInfo.key.etabIds}")
    @Setter
    private String etabCodesInfoKey;
    
    @NonNull
    @Value("${userInfo.key.currentEtabId}")
    @Setter
    private String currentEtabCodeInfoKey;
    
    @NonNull
    @Value("${userInfo.key.profils}")
    @Setter
    private String profilsInfoKey;

    @NonNull
    @Value("${userInfo.key.groups}")
    @Setter
    private String userGroupsInfokey;

    @NonNull
    @Value("${url.ressources.mediacentre}")
    @Setter
    private String urlRessources;
    
    @Autowired
    @Setter
    private IUserResource userResource;

    @Autowired
    private IPreferenceResource preferenceResource;

//    @Autowired
//    private IMediaUrlBuilder mediaUrlBuilder;
    
    @Autowired
    private IMediaCentreResource mediaCentreResource;    
    
    @Override
    public List<String> getUserLinkedEtablissements(@NotNull HttpServletRequest request) {
        return userResource.getUserInfo(request, etabCodesInfoKey);
    }

    @Override
    public List<String> getUserCurrentEtablissement(@NotNull HttpServletRequest request) {
        List<String> userInfos = userResource.getUserInfo(request, currentEtabCodeInfoKey);
        /**if (userInfos.size() > 1) {
            // should not happen
            log.warn("User info has more than one value, the service will return only the first one !");
        }
        return userInfos.get(0);*/
        return userInfos;
    }

    @Override
    public List<String> getUserGroups(@NotNull HttpServletRequest request) {
        return userResource.getUserInfo(request, userGroupsInfokey);
    }

    @Override
    public String getCurrentUserId(@NotNull HttpServletRequest request) {
        return request.getRemoteUser();
    }

    @Override
    public List<String> getUserFavorites(@NotNull HttpServletRequest request) {
        return preferenceResource.getUserFavorites(request);
    }

    @Override
    public List<String> getUserInfoOnAttribute(@NotNull HttpServletRequest request, @NotNull String attributeKey) {
        if (!attributeKey.isEmpty()) {
            return userResource.getUserInfo(request,attributeKey);
        }
        return Lists.newArrayList();
    }

    @Override
    public Map<String, List<String>> getUserInfos(@NotNull HttpServletRequest request) {
        return userResource.getUserInfoMap(request);
    }

    @Override
    public void setAndSaveUserFavorites(@NotNull HttpServletRequest request, @NotNull List<String> favorites) {
        try {
            preferenceResource.setUserFavorites(request,favorites);
        } catch (ReadOnlyException e) {
            log.error("Can't modify Favorites, please watch the portlet definition");
        }
    }

    @Override
    public void addToUserFavorites(@NotNull HttpServletRequest request, @NotNull String favorite) {
        try {
            if (!favorite.isEmpty()) {
                preferenceResource.addToUserFavorites(request, favorite);
            } else {
                log.warn("Tried to add an empty string passed as favorite !");
            }
        } catch (ReadOnlyException e) {
            log.error("Can't modify Favorites, please watch the portlet definition");
        }
    }

    @Override
    public void removeToUserFavorites(@NotNull HttpServletRequest request, @NotNull String favorite) {
        try {
            if (!favorite.isEmpty()) {
                preferenceResource.removeToUserFavorites(request, favorite);
            } else {
                log.warn("Tried to remove an empty string passed as favorite !");
            }
        } catch (ReadOnlyException e) {
            log.error("Can't modify Favorites, please watch the portlet definition");
        }
    }
    
    /**
	 * @return the mediaUrlBuilder
	 */
	/**public IMediaUrlBuilder getMediaUrlBuilder() {
		return mediaUrlBuilder;
	}*/

	/**
	 * @param mediaUrlBuilder the mediaUrlBuilder to set
	 */
	/**public void setMediaUrlBuilder(IMediaUrlBuilder mediaUrlBuilder) {
		this.mediaUrlBuilder = mediaUrlBuilder;
	}*/

    /**
     * @return the media Pref Url
     */
	public static String getPrefMediaUrl() {
    	return PREF_MEDIA_URL;
    }

    @Override
    public List<Ressource> retrieveListRessource(final HttpServletRequest request) {
        
        if (log.isDebugEnabled()) {
            log.debug("Preference mediacentre url is {}", urlRessources);
        }

        if (urlRessources == null || urlRessources.trim().isEmpty() ) {
            return Lists.newArrayList();
        }

        // case of url is relative
        /**String rewroteUrl = mediaUrlBuilder.transform(request, urlRessources);
        if (rewroteUrl == null || rewroteUrl.trim().isEmpty()) {
            return Lists.newArrayList();
        }

        if (log.isDebugEnabled()) {
            log.debug("After url completion mediacentreUrl is {}", urlRessources);
        }*/
        List<Ressource> listRessources = mediaCentreResource.retrieveListRessource(urlRessources, request, getUserInfos(request));
        List<String> listeFavoris = this.getUserFavorites(request);
        
        
        // on alimente l'attribut des favoris
        int id=1;
        for(Ressource ressource : listRessources) {
        	ressource.setIdInterne(id++);
        	String idRessource = ressource.getIdRessource();
        	if(StringUtils.isNotBlank(idRessource)){
        		if(listeFavoris.contains(idRessource)){
        			ressource.setFavorite(true);
        		}
        		else{
        			ressource.setFavorite(false);
        		}
        	}
        }
        return listRessources;
    }
}
