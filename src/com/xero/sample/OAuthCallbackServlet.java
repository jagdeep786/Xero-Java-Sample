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

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;

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
public class OAuthCallbackServlet
    extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
 
    // Get values returned via Callback URL
    String verifier = req.getParameter("oauth_verifier");
    //String orgShortCode = req.getParameter("org");
    
    //Check is temp token & secret exists
    String tempToken = null;
    String tempTokenSecret = null;
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        if (cookies[i].getName().equals("temptoken")) {
          tempToken = cookies[i].getValue();
        }
        if (cookies[i].getName().equals("temptokensecret")) {
          tempTokenSecret = cookies[i].getValue();
        }
      }
    }
    
    // Create HMAC Signer
    OAuthHmacSigner signer = new OAuthHmacSigner();
    signer.clientSharedSecret = Utils.getConsumerSecret();
    signer.tokenSharedSecret = tempTokenSecret;
    
    // Swap Temp Token for Access Token
    OAuthGetAccessToken getAccessToken = new OAuthGetAccessToken(Utils.getAccessTokenUrl());
    getAccessToken.signer = signer;
    getAccessToken.consumerKey = Utils.getConsumerKey();
    getAccessToken.verifier = verifier;
    getAccessToken.temporaryToken = tempToken;
    getAccessToken.transport = Utils.HTTP_TRANSPORT;
    OAuthCredentialsResponse accessTokenResponse = getAccessToken.execute();
    
    // Store 30 min Access Token (from  accessToken request) in Cookie
    // this is just a demo - you'll want to persist this token along with
    // the user profile in a more secure way.
    String token = accessTokenResponse.token;
    Cookie t = new Cookie("token",token);
    resp.addCookie(t);
    
    // Store 30 min Access Token Secret (from accessToken request) in Cookie
    // this is just a demo - you'll want to persist this secret along with
    // the user profile in a more secure way.
    String tokenSecret = accessTokenResponse.tokenSecret;
    Cookie s = new Cookie("tokenSecret",tokenSecret);
    resp.addCookie(s);
    
    PrintWriter respWriter = resp.getWriter();
    resp.setStatus(200);
    resp.setContentType("text/html"); 
    respWriter.println("<a href='/requestResource'>Get Oraganisation</a><br><br>");
    respWriter.println("<i>Access Token: </li>" + accessTokenResponse.token + "<br><br>");
    respWriter.println("<i>Access Token Secret: </li>" + accessTokenResponse.tokenSecret + "<br><br>");
    
  }
  
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }  
}
