// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.oauth;


import com.google.common.io.BaseEncoding;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.gerrit.server.OutputFormat.JSON;
import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.scribe.model.OAuthConstants.ACCESS_TOKEN;
import static org.scribe.model.OAuthConstants.CODE;

public class GitLabApi extends DefaultApi20 {


    private static final String AUTHORIZE_URL =
            "http://172.27.12.64:8081/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s";
    private static final String ACCESS_TOKEN_ENDPOINT =
            "http://172.27.12.64:8081/oauth/token";

    public GitLabApi() {
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return format(AUTHORIZE_URL, config.getApiKey(), config.getCallback());
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_ENDPOINT;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public OAuthService createService(OAuthConfig config) {
        return new GitLabOAuthService(this, config);
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new GitLabTokenExtractor();
    }

    private static final class GitLabOAuthService implements OAuthService {

        private static final Logger log =
                LoggerFactory.getLogger(GitLabOAuthService.class);
        private static final String VERSION = "2.0";

        private static final String GRANT_TYPE = "grant_type";
        private static final String GRANT_TYPE_VALUE = "authorization_code";

        private final DefaultApi20 api;
        private final OAuthConfig config;

        private GitLabOAuthService(DefaultApi20 api, OAuthConfig config) {
            this.config = config;
            this.api = api;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Token getAccessToken(Token requestToken, Verifier verifier) {
            OAuthRequest request =
                    new OAuthRequest(api.getAccessTokenVerb(),
                            api.getAccessTokenEndpoint());
            request.addBodyParameter("client_id", config.getApiKey());
            request.addBodyParameter("client_secret", config.getApiSecret());
            request.addBodyParameter("code", verifier.getValue());
            request.addBodyParameter("redirect_uri", config.getCallback());
            request.addBodyParameter("grant_type", "authorization_code");
            Response response = request.send();
            log.debug(response.getBody());
            if (response.getCode() == 200) {
                Token t = api.getAccessTokenExtractor().extract(response.getBody());
                return new Token(t.getToken(), config.getApiSecret());
            } else {
                throw new OAuthException(
                        String.format("Error response received: %s, HTTP status: %s",
                                response.getBody(), response.getCode()));
            }
        }

        private String prepareAuthorizationHeaderValue() {
            String value = String.format("%s:%s", config.getApiKey(), config.getApiSecret());
            String valueBase64 = BaseEncoding.base64().encode(value.getBytes());
            return String.format("Basic %s", valueBase64);
        }

        @Override
        public Token getRequestToken() {
            throw new UnsupportedOperationException(
                    "Unsupported operation, please use 'getAuthorizationUrl' and redirect your users there");
        }

        @Override
        public String getVersion() {
            return VERSION;
        }

        @Override
        public void signRequest(Token token, OAuthRequest request) {
            request.addQuerystringParameter(ACCESS_TOKEN, token.getToken());
        }

        @Override
        public String getAuthorizationUrl(Token token) {
            return api.getAuthorizationUrl(config);
        }
    }

    private static final class GitLabTokenExtractor
            implements AccessTokenExtractor {

        @Override
        public Token extract(String response) {
            JsonElement json = JSON.newGson().fromJson(response, JsonElement.class);
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                JsonElement id = jsonObject.get(ACCESS_TOKEN);
                if (id == null || id.isJsonNull()) {
                    throw new OAuthException(
                            "Response doesn't contain 'access_token' field");
                }
                JsonElement accessToken = jsonObject.get(ACCESS_TOKEN);
                return new Token(accessToken.getAsString(), "");
            } else {
                throw new OAuthException(
                        String.format("Invalid JSON '%s': not a JSON Object", json));
            }
        }
    }
}
