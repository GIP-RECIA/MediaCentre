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
package fr.recia.mediacentre.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.mediacentre.api.configuration.bean.CategoriesByProfilesProperties;
import fr.recia.mediacentre.api.dao.impl.MediaCentreResourceJacksonImpl;
import fr.recia.mediacentre.api.web.rest.exception.YmlPropertyNotFoundException;
import fr.recia.mediacentre.api.interceptor.bean.SoffitHolder;
import fr.recia.mediacentre.api.model.filter.FilterEnum;
import fr.recia.mediacentre.api.model.pojo.IsMemberOf;
import fr.recia.mediacentre.api.model.resource.Ressource;
import fr.recia.mediacentre.api.service.impl.MediaCentreServiceImpl;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class MediaCentreServiceImplTest {

    @NonNull
    @Value("${url.ressources.mediacentre}")
    @Setter
    private String urlRessources;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MediaCentreServiceImpl mediaCentreService;

    @MockBean
    private MediaCentreResourceJacksonImpl mediaCentreResource;

    @MockBean
    private SoffitHolder soffit;

    @MockBean
    private CategoriesByProfilesProperties categoriesByFilters;

    private List<Ressource> listeRessourcesMediaCentre;

    @NonNull
    @Value("${path.resources}")
    private String resourcesFilePath;

    @NonNull
    @Value("${path.filters}")
    private String filtersFilePath;

    @NonNull
    @Value("${path.isMemberOf}")
    private String isMemberOfFilePath;

    private IsMemberOf isMemberOf;

    private Map<String,List<String>> userInfos;

    @Before
    public void init() throws IOException {
        listeRessourcesMediaCentre = objectMapper.readValue(new File(resourcesFilePath),new TypeReference<>(){});
        isMemberOf = objectMapper.readValue(new File(isMemberOfFilePath), new TypeReference<>() {
        });
        userInfos = new HashMap<>();
        userInfos.put("etabIds",List.of("id"));
        userInfos.put("currentEtabId",List.of("idCurrent"));
        userInfos.put("uid",List.of("uid"));
        userInfos.put("profils",List.of("profile1"));
        userInfos.put("isMemberOf",isMemberOf.getIsMemberOf());

        when(soffit.getEtabIds()).thenReturn(List.of("id"));
        when(soffit.getCurrentEtabId()).thenReturn(List.of("idCurrent"));
        when(soffit.getUid()).thenReturn(List.of("uid"));
        when(soffit.getProfil()).thenReturn("profile1");

        CategoriesByProfilesProperties.ProfilesMap profilesMap = new CategoriesByProfilesProperties.ProfilesMap();
        profilesMap.setProfiles(List.of("profile1"));
        profilesMap.setFilters(List.of(FilterEnum.TYPE_PRESENTATION_FILTER, FilterEnum.NIVEAU_EDUCATIF_FILTER, FilterEnum.UAI_FILTER));
        when(categoriesByFilters.getCategoriesByProfiles()).thenReturn(List.of(profilesMap));
    }

    // retrieveListRessource() tests :

    @Test
    public void retrieveListRessource_OK() throws YmlPropertyNotFoundException, IOException {
        mediaCentreService.setUrlRessources(urlRessources);
        when(mediaCentreResource.retrieveListRessource(urlRessources,userInfos)).thenReturn(listeRessourcesMediaCentre);
        log.info(isMemberOf.getIsMemberOf().toString());
        List<Ressource> result = mediaCentreService.retrieveListRessource(isMemberOf.getIsMemberOf());

        assertNotNull(result);
        assertEquals(result.size(),listeRessourcesMediaCentre.size());

        for(int i = 0; i < listeRessourcesMediaCentre.size(); i++){
            Ressource expected = listeRessourcesMediaCentre.get(i);
            Ressource actual = result.get(i);
            assertEquals(expected.getIdRessource(),actual.getIdRessource());
        }
    }

    @Test
    public void retrieveListRessource_When_No_Resource_OK() throws YmlPropertyNotFoundException, IOException {
        when(mediaCentreResource.retrieveListRessource(urlRessources,userInfos)).thenReturn(new ArrayList<>());
        List<Ressource> result = mediaCentreService.retrieveListRessource(isMemberOf.getIsMemberOf());

        assertNotNull(result);
        assertEquals(result.size(),0);
    }

    @Test
    public void retrieveListRessource_When_UrlRessources_Is_Missing_In_Yml_Properties_KO() throws YmlPropertyNotFoundException, IOException {
        mediaCentreService.setUrlRessources("");
        assertThrows(YmlPropertyNotFoundException.class, () -> {
            mediaCentreService.retrieveListRessource(isMemberOf.getIsMemberOf());
        });
    }

    // retrieveFiltersList() test :

    @Test
    public void retrieveFiltersList_OK() throws IOException {
        List<FilterEnum> result = mediaCentreService.retrieveFiltersList();
        assertNotNull(result);
        assertEquals(result.size(),3);
        List<FilterEnum> filters = objectMapper.readValue(new File(filtersFilePath), new TypeReference<>() {});
        assertEquals(result,filters);
    }
}
