/**
 * Copyright © 2025 GIP-RECIA (https://www.recia.fr/)
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
package fr.recia.widget.api.apiRestMail.config.custom.impl;

import fr.recia.widget.api.apiRestMail.config.bean.AppConfProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CustomCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    public CustomCasAuthenticationEntryPoint(AppConfProperties appConfProperties) {
        this.appConfProperties = appConfProperties;
    }

    private final AppConfProperties appConfProperties;

    @Override
    protected String createServiceUrl(HttpServletRequest request, HttpServletResponse response) {
        final String url = request.getRequestURL().toString();
        final String uri = request.getRequestURI();
        return url.substring(0, url.length() - uri.length()) + appConfProperties.getCasServiceId();
    }

}