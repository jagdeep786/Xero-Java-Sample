package com.xero.sample;


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

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Entry sevlet for the Xero Java Sample. Demonstrates how to make an authenticated API call
 * using OAuth 1.0 helper classes.
 *
 * @author Sidney Maestre 
 */
public class OAuthRequestTokenServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
     
    OAuthHmacSigner signer = new OAuthHmacSigner();
    // Get Temporary Token
    OAuthGetTemporaryToken getTemporaryToken = new OAuthGetTemporaryToken(Utils.getRequestTokenUrl());
    signer.clientSharedSecret = Utils.getConsumerSecret();
    
    getTemporaryToken.signer = signer;
    getTemporaryToken.consumerKey = Utils.getConsumerKey();
    getTemporaryToken.callback = getRedirectUri(req);
    getTemporaryToken.transport = Utils.HTTP_TRANSPORT;
    OAuthCredentialsResponse temporaryTokenResponse = getTemporaryToken.execute();
       
    // Store Temporary Token (from getToken request) in Cookie
    String tempToken = temporaryTokenResponse.token;
    Cookie t = new Cookie("temptoken",tempToken);
    resp.addCookie(t);
    
    // Store Temporary Token Secret (from get Token request) in Cookie
    String tempTokenSecret = temporaryTokenResponse.tokenSecret;
    Cookie s = new Cookie("temptokensecret",tempTokenSecret);
    resp.addCookie(s);

    // Build Authoirze URL
    OAuthAuthorizeTemporaryTokenUrl accessTempToken = new OAuthAuthorizeTemporaryTokenUrl(Utils.getAuthorizeUrl());
    accessTempToken.temporaryToken = temporaryTokenResponse.token;
    accessTempToken.set("oauth_callback",getRedirectUri(req));
    String authUrl = accessTempToken.build();

    PrintWriter respWriter = resp.getWriter();
    resp.setStatus(200);
    resp.setContentType("text/html");
    respWriter.println("<i>Temporary Token: " + tempToken + "</i><br><br>");
    respWriter.println("<i>Temporary Token Secret: " + tempTokenSecret + "</i><br><br>");
    respWriter.println("<i>Authorize URL: " + authUrl + "</i><br><br>");
    respWriter.println("<a href='" + authUrl + "'>Continue OAuth Flow</a><br><br>");
    
    // Redirect to Authorize URL in order to get Verifier Code
    //resp.sendRedirect(authUrl);
    
  }

  private String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }
}

