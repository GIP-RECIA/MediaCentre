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
package fr.recia.mediacentre.api.service.impl;

import fr.recia.mediacentre.api.configuration.bean.CategoriesByProfilesProperties;
import fr.recia.mediacentre.api.dao.impl.MediaCentreResourceJacksonImpl;
import fr.recia.mediacentre.api.web.rest.exception.MediacentreWSException;
import fr.recia.mediacentre.api.web.rest.exception.YmlPropertyNotFoundException;
import fr.recia.mediacentre.api.interceptor.bean.SoffitHolder;
import fr.recia.mediacentre.api.model.filter.FilterEnum;
import fr.recia.mediacentre.api.model.resource.Ressource;
import fr.recia.mediacentre.api.service.MediaCentreService;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jgribonvald on 06/06/17.
 */
@Slf4j
@Service
@NoArgsConstructor
public class MediaCentreServiceImpl implements MediaCentreService {

  @NonNull
  @Value("${url.ressources.mediacentre}")
  @Setter
  private String urlRessources;

  @Autowired
  private MediaCentreResourceJacksonImpl mediaCentreResource;

  @Autowired
  private SoffitHolder soffit;

  @Autowired
  private CategoriesByProfilesProperties categoriesByFilters;

  @Override
  public List<Ressource> retrieveListRessource(List<String> isMemberOf) throws YmlPropertyNotFoundException, MediacentreWSException {

    if (log.isDebugEnabled()) {
      log.debug("Preference mediacentre url is {}", urlRessources);
    }

    if (Objects.isNull(urlRessources) || urlRessources.trim().isEmpty()) {
      throw new YmlPropertyNotFoundException();
    }

    Map<String, List<String>> userInfos = new HashMap<>();

    userInfos.put("etabIds", soffit.getEtabIds());
    userInfos.put("currentEtabId", soffit.getCurrentEtabId());
    userInfos.put("uid", soffit.getUid());
    userInfos.put("profils", Collections.singletonList(soffit.getProfil()));
    userInfos.put("isMemberOf", isMemberOf);
    List<Ressource> listRessources = mediaCentreResource.retrieveListRessource(urlRessources, userInfos);
    return listRessources;
  }

  @Override
  public List<FilterEnum> retrieveFiltersList() throws YmlPropertyNotFoundException {
    String userProfile = soffit.getProfil();
    return getFiltersByProfile(userProfile);
  }

  private List<FilterEnum> getFiltersByProfile(String profile) throws YmlPropertyNotFoundException {
    List<CategoriesByProfilesProperties.ProfilesMap> profilesMapList = categoriesByFilters.getCategoriesByProfiles();
    if (profilesMapList.isEmpty()) {
      throw new YmlPropertyNotFoundException();
    }
    for (CategoriesByProfilesProperties.ProfilesMap item : profilesMapList) {
      if (item.getProfiles().contains(profile)) {
        return item.getFilters();
      }
    }
    return new ArrayList<>();
  }

}
