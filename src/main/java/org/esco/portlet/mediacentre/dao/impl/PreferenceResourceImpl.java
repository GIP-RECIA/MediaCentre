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
import org.apache.commons.lang3.NotImplementedException;
import org.esco.portlet.mediacentre.dao.IPreferenceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by jgribonvald on 06/06/17.
 */
@Service
@Profile("!mock")
public class PreferenceResourceImpl implements IPreferenceResource {

    private static final String FAVORITES_PREF = "mediacentreFavorites";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<String> getUserFavorites(@NonNull final HttpServletRequest request) {
        throw new NotImplementedException("User favorites not yet implemented");

        /*
        final List<String> favorites = Arrays.asList(request.getPreferences().getValues(FAVORITES_PREF, new String[0]));

        if (log.isDebugEnabled()) {
            log.debug("Retrieved Favorites are {}", favorites);
        }

        return favorites;

         */
    }

    @Override
    public void setUserFavorites(@NonNull final HttpServletRequest request, @NonNull final List<String> favorites) {
        throw new NotImplementedException("User favorites not yet implemented");

        /*
        PortletPreferences pp = request.getPreferences();

        if (favorites.isEmpty()) {
            pp.reset(FAVORITES_PREF);
        } else {
            pp.setValues(FAVORITES_PREF, favorites.toArray(new String[favorites.size()]));
        }

        try {
            pp.store();
            if (log.isDebugEnabled()) {
                log.debug("PortletPreferences {} were stored", favorites);
            }
        } catch (ValidatorException | IOException e) {
            log.error("PortletPreferences {} were not store", favorites, e.getMessage());
        }

         */
    }

    @Override
    public void addToUserFavorites(@NonNull final HttpServletRequest request, @NonNull final String favorite) {
        throw new NotImplementedException("User favorites not yet implemented");

        /*
        PortletPreferences pp = request.getPreferences();

        if (!favorite.isEmpty()) {
            List<String> favorites = new ArrayList<String>(Arrays.asList(pp.getValues(FAVORITES_PREF, new String[0])));
            favorites.add(favorite);

            pp.setValues(FAVORITES_PREF, favorites.toArray(new String[favorites.size()]));

            try {
                pp.store();
                if (log.isDebugEnabled()) {
                    log.debug("Favorite {} was added to PortletPreferences {} that were stored", favorite, favorites);
                }
            } catch (ValidatorException | IOException e) {
                log.error("PortletPreferences {} were not store {}", favorites, e.getMessage());
            }
        }

         */
    }

    @Override
    public void removeToUserFavorites(@NonNull final HttpServletRequest request, @NonNull final String favorite) {
        throw new NotImplementedException("User favorites not yet implemented");

        /*
        PortletPreferences pp = request.getPreferences();

        List<String> favorites = new ArrayList<String>(Arrays.asList(pp.getValues(FAVORITES_PREF, new String[0])));
        favorites.remove(favorite);

        if (favorites.isEmpty()) {
            pp.reset(FAVORITES_PREF);
        } else {
            pp.setValues(FAVORITES_PREF, favorites.toArray(new String[favorites.size()]));
        }

        try {
            pp.store();
            if (log.isDebugEnabled()) {
                log.debug("Favorite {} was removed from PortletPreferences {} that were stored", favorite, favorites);
            }
        } catch (ValidatorException | IOException e) {
            log.error("PortletPreferences {} were not store", favorites, e.getMessage());
        }

         */
    }
}
