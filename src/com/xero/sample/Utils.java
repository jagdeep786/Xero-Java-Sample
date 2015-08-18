/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.xero.sample;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.GenericUrl;
import javax.servlet.http.HttpServletRequest;

class Utils {

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  static final String MAIN_SERVLET_PATH = "/plussampleservlet";
  static final String AUTH_CALLBACK_SERVLET_PATH = "/oauth2callback";
  static final UrlFetchTransport HTTP_TRANSPORT = new UrlFetchTransport();
  private static final String CONSUMER_KEY = "EORT56K6NSY9QEW8VMXVBN6VHGH60E";
  private static final String CONSUMER_SECRET = "EIQJBZLXOB72AOREGAIPOBTBO23S6I";
  private static final String API_ENDPOINT_URL = "https://api.xero.com/api.xro/2.0/";
  private static final String TOKEN_SERVER_URL = "https://api.xero.com/oauth/RequestToken";
  private static final String AUTHENTICATE_URL = "https://api.xero.com/oauth/Authorize";
  private static final String ACCESS_TOKEN_URL = "https://api.xero.com/oauth/AccessToken";

  static String getConsumerKey() {
	    return CONSUMER_KEY;
  }
  
  static String getConsumerSecret() {
	    return CONSUMER_SECRET;
  }
  
  static String getApiUrl() {
	    return API_ENDPOINT_URL;
  }
  
  static String getRequestTokenUrl() {
	    return TOKEN_SERVER_URL;
  }
  
  static String getAuthorizeUrl() {
	    return AUTHENTICATE_URL;
  }
  
  static String getAccessTokenUrl() {
	    return ACCESS_TOKEN_URL;
  }
  
  static String getRedirectUri(HttpServletRequest req) {
    GenericUrl requestUrl = new GenericUrl(req.getRequestURL().toString());
    requestUrl.setRawPath(AUTH_CALLBACK_SERVLET_PATH);
    return requestUrl.build();
  }
}
