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
package fr.recia.mediacentre.api.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.mediacentre.api.interceptor.bean.SoffitHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Profile("!test")
public class SoffitInterceptor implements HandlerInterceptor {

  private final SoffitHolder soffitHolder;

  public SoffitInterceptor(SoffitHolder soffitHolder) {
    this.soffitHolder = soffitHolder;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String path = request.getRequestURI().substring(request.getContextPath().length());
    String token = request.getHeader("Authorization");

    if (token == null) {
      log.debug("No Authorization header found");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return false;
    }

    Base64.Decoder decoder = Base64.getUrlDecoder();
    String payload = new String(decoder.decode(token.replace("Bearer ", "").split("\\.")[1]));

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String,Object> soffit = new HashMap<>();
    try {
      soffit = objectMapper.readValue(payload,Map.class);

      boolean isGuest = Pattern.matches("^guest.*", (CharSequence) soffit.get(("sub")));
      if(isGuest){
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
      }
      soffitHolder.setSub(soffit.get("sub").toString());


      soffitHolder.setEtabIds(Collections.singletonList(soffit.get("ESCOSIREN").toString()));
      soffitHolder.setCurrentEtabId(Collections.singletonList((soffit.get("ESCOSIRENCourant").toString())));
      soffitHolder.setUid(Collections.singletonList((soffit.get("ENTPersonGARIdentifiant").toString())));
      soffitHolder.setProfil((soffit.get("profile").toString()));

    } catch (IOException ignored) {
      log.error("Unable to read soffit" + soffit);
    }
    catch (NullPointerException e) {
      return false;
    }
    return true;
  }
}
