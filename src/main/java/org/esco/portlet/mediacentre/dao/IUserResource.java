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

import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface IUserResource {

	List<String> getUserInfo(@NonNull final HttpServletRequest request, @NonNull final String attributeName);

	Map<String, List<String>> getUserInfoMap(@NonNull final HttpServletRequest request);
}
