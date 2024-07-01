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
package fr.recia.mediacentre.api.configuration.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(
  prefix = "app",
  ignoreUnknownFields = false
)
@Data
@Validated
@Slf4j
public class MediaCentreProperties {

  private CorsProperties cors = new CorsProperties();
  private SecurityProperties security = new SecurityProperties();
  private SoffitProperties soffit = new SoffitProperties();

  @PostConstruct
  private void init() throws JsonProcessingException {
    log.info("Loaded properties: {}", this);
  }

  @Override
  public String toString() {
    return "{\n"
      + cors + ",\n"
            + security + ",\n"
            + soffit + "\n"
      + "\n}";
  }
}

