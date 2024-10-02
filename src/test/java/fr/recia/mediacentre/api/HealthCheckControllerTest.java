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
import fr.recia.mediacentre.api.configuration.bean.ConfigProperties;
import fr.recia.mediacentre.api.model.pojo.Config;
import fr.recia.mediacentre.api.web.rest.ConfigController;
import fr.recia.mediacentre.api.web.rest.HealthCheckController;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = HealthCheckController.class)
@Import(ConfigProperties.class)
@DirtiesContext
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({ "test" })
public class HealthCheckControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  ConfigProperties configProperties;

  private static String HEALTHCHECK_URI = "/health-check";

  @Test
  public void healthCheck_OK() throws Exception {

    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(HEALTHCHECK_URI)
      .characterEncoding(StandardCharsets.UTF_8);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();

    assertEquals(result.getResponse().getStatus(), HttpStatus.SC_OK);
  }

}
