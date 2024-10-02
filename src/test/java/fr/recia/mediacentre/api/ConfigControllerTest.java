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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.mediacentre.api.configuration.bean.ConfigProperties;
import fr.recia.mediacentre.api.model.pojo.Config;
import fr.recia.mediacentre.api.web.rest.ConfigController;
import fr.recia.mediacentre.api.web.rest.exception.MediacentreWSException;
import fr.recia.mediacentre.api.web.rest.exception.YmlPropertyNotFoundException;
import fr.recia.mediacentre.api.model.filter.FilterEnum;
import fr.recia.mediacentre.api.model.pojo.IsMemberOf;
import fr.recia.mediacentre.api.model.resource.Ressource;
import fr.recia.mediacentre.api.service.impl.MediaCentreServiceImpl;
import fr.recia.mediacentre.api.web.rest.MediaCentreController;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.resolve;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = ConfigController.class)
@Import(ConfigProperties.class)
@DirtiesContext
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({ "test" })
public class ConfigControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  ConfigProperties configProperties;

    private static String GETCONFIG_URI = "/api/config";

    @Test
    public void getConfig_OK() throws Exception {

      RequestBuilder requestBuilder = MockMvcRequestBuilders.get(GETCONFIG_URI)
        .accept(MediaType.APPLICATION_JSON)
        .characterEncoding(StandardCharsets.UTF_8);

      MvcResult result = mockMvc.perform(requestBuilder).andReturn();

      assertEquals(result.getResponse().getStatus(), HttpStatus.SC_OK);
      List<Config> retrievedConfigs = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

      // get groups regexes from bean for next asserts
      List<String> regexesFromConfigProperties = configProperties.getGroups();

      // check that the number of Configs retrieved match the number of regexes in the bean
      assertEquals(retrievedConfigs.size(), regexesFromConfigProperties.size());

      //no use of stream to not change Java language level
      List<String> retrievedConfigsValues = new ArrayList<>(retrievedConfigs.size());

      for (Config config : retrievedConfigs) {
        // checks if each config has "groups" as key
        assertEquals(config.getKey(), "groups");
        // add the value of each config to the list for next asserts
        retrievedConfigsValues.add(config.getValue());
      }

      for (String regex : regexesFromConfigProperties) {
        //check if each regex is in the response values
        assertTrue(retrievedConfigsValues.contains(regex));
      }
    }
}
