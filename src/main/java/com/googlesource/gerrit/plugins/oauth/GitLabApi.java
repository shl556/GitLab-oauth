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

import static com.google.gerrit.server.OutputFormat.JSON;
import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.scribe.model.OAuthConstants.ACCESS_TOKEN;
import static org.scribe.model.OAuthConstants.CODE;

public class GitLabApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL =
            "http://172.27.12.85/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s";
    private static final String ACCESS_TOKEN_ENDPOINT =
            "http://172.27.12.85/oauth/token";

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

}
