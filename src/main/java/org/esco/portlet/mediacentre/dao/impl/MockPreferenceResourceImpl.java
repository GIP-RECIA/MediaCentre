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
import org.esco.portlet.mediacentre.dao.IPreferenceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgribonvald on 06/06/17.
 */
@Service
@Profile("mock")
public class MockPreferenceResourceImpl implements IPreferenceResource {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private List<String> favoris = new ArrayList<String>(); 
	
	@NonNull
    @Value("${userInfo.mediacentre.favorites}")
    private String mediacentreFavorites;

	public MockPreferenceResourceImpl() {
		favoris.add("http://n2t.net/ark:/99999/RSF000006");
	}
	
	@Override
    public List<String> getUserFavorites(@NonNull final HttpServletRequest request) {
		Assert.hasText(this.mediacentreFavorites, "No Favorite Codes user info key configured !");
		
		//final String[] favorite = System.getProperty("mediacentre.userFavorite", "http://n2t.net/ark:/99999/RSF000001").split(SPLIT_SEP);
		
		return favoris;
    }

    @Override
    public void setUserFavorites(@NonNull final HttpServletRequest request, @NonNull final List<String> favorites) {
    }


	@Override
    public void addToUserFavorites(@NonNull final HttpServletRequest request, @NonNull final String favorite) {
    	favoris.add(new String(favorite));
    	log.debug("addToUserFavorites" + favoris);
    }

    @Override
    public void removeToUserFavorites(@NonNull final HttpServletRequest request, @NonNull final String favorite) {
    	int i = favoris.indexOf(favorite);
    	if (i>=0) {
    		favoris.remove(i);
    	}
    	log.debug("removeToUserFavorites" + favoris);
    }
}
