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
package org.esco.portlet.mediacentre.service.bean;

import org.esco.portlet.mediacentre.dao.IUserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 15/09/16.
 */
@Service
public class MediaUrlBuilderImpl implements IMediaUrlBuilder {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Pattern p = Pattern.compile("\\{(.+)\\}");

    @Autowired
    private IUserResource userResource;

    @Override
    public String transform(final HttpServletRequest request, final String url) {
        String rewroteUrl = url;
        if (log.isDebugEnabled()) {
            log.debug("Url in entry is '{}'",rewroteUrl);
        }
        final Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            for (int i=1; i <= matcher.groupCount(); i++) {
                // Using the first value only
                final List<String> values = userResource.getUserInfo(request, matcher.group(i));
                if (values != null && !values.isEmpty()) {
                    rewroteUrl = rewroteUrl.replaceAll("\\{" + matcher.group(i) + "\\}", values.get(0));
                } else {
                    log.warn("No value was retrived from user info of '{}' on attribute '{}'", request.getUserPrincipal(), matcher.group(i));
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Replacement regexp of mediaUrl from {} to {}",url, rewroteUrl );
        }

        try {
            final URI uri = new URI(rewroteUrl);
            if (uri.getScheme() == null && uri.getHost() == null) {
                final int tmpPort = request.getServerPort();
                final String port = ((tmpPort != 80) && (tmpPort != 443) && (tmpPort > 0)) ? ":" + tmpPort : "";
                // if start with // we add only the scheme
                // else if relative url start with / we add to the url only the scheme and domain
                // else if relative url we add sheme + domain + contextPath so in this case only we need to context path
                final String ctx = (!rewroteUrl.startsWith("//") && !rewroteUrl.startsWith("/")) ? request.getContextPath() + "/" : "";
                if (log.isDebugEnabled()) {
                    log.debug("Rewrite mediaUrl from {} to {}, with params scheme {}, host {}, port {}, context path {}",rewroteUrl,
                            request.getScheme() + "://" + request.getServerName() + port + ctx + rewroteUrl, request.getScheme(), request.getServerName(),
                            port, ctx);
                }
                rewroteUrl = request.getScheme() + "://" + request.getServerName() + port + ctx + rewroteUrl;
            } else if (uri.getScheme() == null) {
                final String path = !rewroteUrl.startsWith("//") ? "//" : "";
                if (log.isDebugEnabled()) {
                    log.debug("Rewrite mediaUrl from {} to {}, with params scheme {}",rewroteUrl,
                            request.getScheme() + ":" + path + rewroteUrl, request.getScheme() + path);
                }
                rewroteUrl = request.getScheme() + ":" + path + rewroteUrl;
            }
        } catch (URISyntaxException e) {
            log.error("The url '" + rewroteUrl + "' to get medias is malformed !", e);
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Url at end is '{}'",rewroteUrl);
        }
        return rewroteUrl;
    }

    public IUserResource getUserResource() {
        return userResource;
    }

    public void setUserResource(final IUserResource userResource) {
        this.userResource = userResource;
    }

}
