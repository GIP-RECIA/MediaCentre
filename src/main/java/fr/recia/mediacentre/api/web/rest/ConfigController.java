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
package fr.recia.mediacentre.api.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.recia.mediacentre.api.configuration.bean.ConfigProperties;
import fr.recia.mediacentre.api.model.pojo.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "api/config")
public class ConfigController {

  @Autowired
  private ConfigProperties configProperties;

  @Autowired
  private Environment environment;

  /**
   * Function that retrieves the child yml properties of the yml config property and creates one Config object per child
   * object with the name of the child yml property for the key attribute and the value of the child yml property for the value attribute.
   * Each object is stored in the config list.
   *
   * @param config - An empty list of Config object
   */
  private void initMap(List<Config> config){
    String prefix = "config.";
    for (String key : ((AbstractEnvironment) environment).getPropertySources().stream()
      .filter(ps -> ps instanceof EnumerablePropertySource)
      .flatMap(ps -> java.util.Arrays.stream(((EnumerablePropertySource) ps).getPropertyNames()))
      .toArray(String[]::new)) {
      if (key.startsWith(prefix)) {
        String configKey = key.substring(prefix.length());
        config.add(new Config(configKey, environment.getProperty(key)));
      }
    }

  }

  @GetMapping
  public ResponseEntity<List<Config>> getConfig() throws JsonProcessingException {
    configProperties.setConfig(new ArrayList<>());
    List<Config> config = configProperties.getConfig();
    this.initMap(config);
    if(config.isEmpty()){
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }else{
      return new ResponseEntity<>(config, HttpStatus.OK);
    }
  }
}
