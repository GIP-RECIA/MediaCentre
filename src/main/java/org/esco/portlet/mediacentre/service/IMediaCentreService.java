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
package org.esco.portlet.mediacentre.service;

import lombok.NonNull;
import org.esco.portlet.mediacentre.model.ressource.Ressource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by jgribonvald on 06/06/17.
 */
public interface IMediaCentreService {

	List<String> getUserLinkedEtablissements(@NonNull final HttpServletRequest request);

	List<String> getUserCurrentEtablissement(@NonNull final HttpServletRequest request);

    List<String> getUserGroups(@NonNull final HttpServletRequest request);

    String getCurrentUserId (@NonNull final HttpServletRequest request);

    List<String> getUserFavorites(@NonNull final HttpServletRequest request);

    /* Best method call to obtain a user attribute value. */
    List<String> getUserInfoOnAttribute(@NonNull final HttpServletRequest request, @NonNull final String attributeKey);

    /* Should be used to share user info. */
    Map<String, List<String>> getUserInfos(@NonNull final HttpServletRequest request);

    void setAndSaveUserFavorites(@NonNull final HttpServletRequest request, @NonNull final List<String> favorites);

    void addToUserFavorites(@NonNull final HttpServletRequest request, @NonNull final String favorite);

    void removeToUserFavorites(@NonNull final HttpServletRequest request, @NonNull final String favorite);

    List<Ressource> retrieveListRessource(final HttpServletRequest request);
}
