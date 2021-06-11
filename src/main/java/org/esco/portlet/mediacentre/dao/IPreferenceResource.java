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
package org.esco.portlet.mediacentre.dao;

import javax.portlet.ReadOnlyException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jgribonvald on 06/06/17.
 */
public interface IPreferenceResource {

    List<String> getUserFavorites(@NotNull final HttpServletRequest request);

    void setUserFavorites(@NotNull final HttpServletRequest request, @NotNull final List<String> favorites) throws ReadOnlyException;

    void addToUserFavorites(@NotNull final HttpServletRequest request, @NotNull final String favorite) throws ReadOnlyException;

    void removeToUserFavorites(@NotNull final HttpServletRequest request, @NotNull final String favorite) throws ReadOnlyException ;

}
