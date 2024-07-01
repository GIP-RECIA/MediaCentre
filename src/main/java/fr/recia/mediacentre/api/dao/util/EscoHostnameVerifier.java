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
package fr.recia.mediacentre.api.dao.util;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.util.Set;

import static javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier;

/**
 * Created by jgribonvald on 27/09/16.
 */

@Slf4j
@NoArgsConstructor
public class EscoHostnameVerifier implements HostnameVerifier, InitializingBean {

    private HostnameVerifier defaultHostnameVerifier;

    @Setter
    private Set<String> trustedDomains;

    public boolean verify(String hostname, SSLSession session) {

        log.debug("EscoHostnameVerifier : checking on hostname [" + hostname + "]");

        if (hostname != null && trustedDomains.contains(hostname)) {
            return true;
        }
        return defaultHostnameVerifier.verify(hostname,session);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notEmpty(trustedDomains, "The list of trusted domains isn't initialized !");

        if (defaultHostnameVerifier == null) {
            defaultHostnameVerifier = getDefaultHostnameVerifier();
        }

        log.debug("Trusted Domain configured are {}", this.trustedDomains);
    }

}
