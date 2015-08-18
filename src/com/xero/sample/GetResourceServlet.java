/*
 * Copyright (c) 2015 Xero Inc.
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


import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.http.HttpResponse;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP servlet to handle callback from Xero and swap temporary 
 * access token for a 30 min AccessToken.
 *
 * @author Sidney Maestre
 */
public class GetResourceServlet
    extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
 
    // Get params from URL
    //String resource = req.getParameter("endpoint");
    String resource = "Organisation";
	  
    //Get Token & Secret from Cookie (or other data storage model)
    String token = null;
    String tokenSecret = null;
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        if (cookies[i].getName().equals("token")) {
        	token = cookies[i].getValue();
        }
        if (cookies[i].getName().equals("tokenSecret")) {
        	tokenSecret = cookies[i].getValue();
        }
      }
    }
    
    // Create HMAC Signer
    OAuthHmacSigner signer = new OAuthHmacSigner();
    signer.clientSharedSecret = Utils.getConsumerSecret();
    signer.tokenSharedSecret = tokenSecret;
    
    // Get Xero API Resource
    OAuthGetResource getResource = new OAuthGetResource(Utils.getApiUrl() + resource);
    getResource.signer = signer;
    getResource.consumerKey = Utils.getConsumerKey();
    getResource.token = token;
    getResource.transport = Utils.HTTP_TRANSPORT;
    HttpResponse response = getResource.execute();
    
    PrintWriter respWriter = resp.getWriter();
    resp.setStatus(200);
    resp.setContentType("text/html"); 
    respWriter.println("<i>Response: " + response.parseAsString() + "<br><br>");
   
  }
  
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }  
}
