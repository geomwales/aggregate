/*
 * Copyright (C) 2009 Google Inc. 
 * Copyright (C) 2010 University of Washington.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.aggregate.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opendatakit.aggregate.CallingContext;
import org.opendatakit.aggregate.constants.ErrorConsts;
import org.opendatakit.aggregate.constants.HtmlUtil;
import org.opendatakit.aggregate.constants.ServletConsts;
import org.opendatakit.aggregate.exception.ODKExternalServiceAuthenticationError;
import org.opendatakit.aggregate.exception.ODKExternalServiceNotAuthenticated;
import org.opendatakit.aggregate.externalservice.OAuthToken;
import org.opendatakit.common.constants.BasicConsts;
import org.opendatakit.common.constants.HtmlConsts;
import org.opendatakit.common.web.servlet.CommonServletBase;

import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;

/**
 * Base class for Servlets that contain useful utilities
 * 
 */
@SuppressWarnings("serial")
public class ServletUtilBase extends CommonServletBase {
	
  protected static final String APPLET_SIGNING_CERTIFICATE_SECTION = "\n<h3><a name=\"Cert\">Import the signing certificate</a></h3>" +
  "\n<p>This applet is signed with a self-signed certificate created by the OpenDataKit team.</p><p>If you are " +
  "\naccessing this page frequently, you may want to import the certificate into your browser's certificate " +
  "\nstore to suppress the security warnings.</p><p>Note that whenever you import a certificate, you are trusting " +
  "\nthe owner of that certificate with your system security, as it will allow any software that the owner " +
  "\nsigns to be transparently executed on your system. Note further that anyone can create a self-signed " +
  "\ncertificate and claim to be the named organization.</p><h4>Download the Certificate</h4><p>Click <a href=\"res/OpenDataKit.cer\">here</a>" +
  " to download the certificate.  <b>NOTE:</b> On Firefox, you must right-click and select `Save Link As...` in order to download the certificate file." +
  "\n</p><h4>Import on Windows</h4><ol><li>Open <code>Control Panel/Java</code>.</li><li>Go to " +
  "\nthe <code>Security</code> tab,</li><li>Click <code>Certificates...</code> tab.</li><li>Select <code>Import</code> to import " +
  "\nthe certificate.</li></ol>";

  protected ServletUtilBase() {
    super(ServletConsts.APPLICATION_NAME);
  }

  @Override
  protected void emitPageHeader(PrintWriter out, boolean displayLinks, CallingContext cc) {
    if (displayLinks) {
        out.write(generateNavigationInfo(cc));
    }
  }
  
  @Override
  protected String getVersionString(CallingContext cc) {
	  return HtmlConsts.TAB + "<FONT SIZE=1>" + ServletConsts.VERSION + "</FONT>";
  }

  /**
   * Generate error response for ODK ID not found
   * 
   * @param resp
   *          The HTTP response to be sent to client
   * @throws IOException
   *           caused by problems writing error information to response
   */
  protected void odkIdNotFoundError(HttpServletResponse resp) throws IOException {
    resp.sendError(HttpServletResponse.SC_NOT_FOUND, ErrorConsts.ODKID_NOT_FOUND);
  }

  /**
   * Generate error response for missing the Key parameter
   * 
   * @param resp
   *          The HTTP response to be sent to client
   * @throws IOException
   *           caused by problems writing error information to response
   */
  protected void errorMissingKeyParam(HttpServletResponse resp) throws IOException {
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.ODK_KEY_PROBLEM);
  }

  protected void errorMissingParam(HttpServletResponse resp) throws IOException {
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.MISSING_PARAMS);
  }
  /**
   * Generate error response for invalid parameters
   * 
   * @param resp
   *          The HTTP response to be sent to client
   * @throws IOException
   *           caused by problems writing error information to response
   */
  protected void errorBadParam(HttpServletResponse resp) throws IOException {
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.INVALID_PARAMS);
  }

  /**
   * Generate error response for missing the Key parameter
   * 
   * @param resp
   *          The HTTP response to be sent to client
   * @throws IOException
   *           caused by problems writing error information to response
   */
  protected void errorRetreivingData(HttpServletResponse resp) throws IOException {
    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorConsts.INCOMPLETE_DATA);
  }

  /**
   * Generate common navigation links
 * @param req 
   * 
   * @return a string with href links
   */
  public final String generateNavigationInfo(CallingContext cc) {
	  
	  final String listFormsHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(FormsServlet.ADDR), 
			  ServletConsts.FORMS_LINK_TEXT);
	  final String configuredServicesHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(ExternalServicesListServlet.ADDR), 
			  ServletConsts.EXTERNAL_SERVICES_LINK_TEXT);
	  final String uploadSubmissionsHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(UploadSubmissionsAppletServlet.ADDR),
			  ServletConsts.UPLOAD_SUBMISSIONS_APPLET_LINK_TEXT);
	  final String changePasswordHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(UserPasswordServlet.ADDR), 
			  UserPasswordServlet.TITLE_INFO);
	  
	  final String resultsHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(ResultServlet.ADDR), 
			  ServletConsts.RESULT_FILES_LINK_TEXT);
	  final String uploadFormHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(UploadXFormAppletServlet.ADDR), 
			  ServletConsts.UPLOAD_XFORM_APPLET_LINK_TEXT);
	  final String accessManagementHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(AccessManagementServlet.ADDR), 
			  AccessManagementServlet.TITLE_INFO);
	  
	  final String briefcaseHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(BriefcaseServlet.ADDR), 
			  ServletConsts.BRIEFCASE_LINK_TEXT);
	  final String deleteHref = HtmlUtil.createHref(
			  cc.getWebApplicationURL(FormDeleteServlet.ADDR), 
			  ServletConsts.DELETE_FORM_LINK_TEXT);
	  
	  StringBuilder html = new StringBuilder();
	html.append(HtmlUtil.createBeginTag(HtmlConsts.CENTERING_DIV));
    html.append(HtmlConsts.HEADING_TABLE_OPEN);
    String[] headers = new String[] { "Access", "Publish", "Upload", "Manage" }; 
	for (String header : headers) {
		html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_HEADER, header));
    }
	html.append(HtmlConsts.TABLE_ROW_OPEN);
	// access
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,listFormsHref));
	// publish
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,configuredServicesHref));
	// upload
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,uploadSubmissionsHref));
	// manage
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,changePasswordHref));
	html.append(HtmlConsts.TABLE_ROW_CLOSE);
	
	html.append(HtmlConsts.TABLE_ROW_OPEN);
	// access
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,resultsHref));
	// publish
	html.append(HtmlUtil.createSelfClosingTag(HtmlConsts.HEADING_TABLE_DATA));
	// upload
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,uploadFormHref));
	// manage
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,deleteHref));
	html.append(HtmlConsts.TABLE_ROW_CLOSE);
	
	html.append(HtmlConsts.TABLE_ROW_OPEN);
	// access
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,briefcaseHref));
	// publish
	html.append(HtmlUtil.createSelfClosingTag(HtmlConsts.HEADING_TABLE_DATA));
	// upload
	html.append(HtmlUtil.createSelfClosingTag(HtmlConsts.HEADING_TABLE_DATA)); // TODO: Debrief
	// manage
	html.append(HtmlUtil.wrapWithHtmlTags(HtmlConsts.HEADING_TABLE_DATA,accessManagementHref));
	html.append(HtmlConsts.TABLE_ROW_CLOSE);

	html.append(HtmlConsts.TABLE_CLOSE);
	html.append(HtmlUtil.createEndTag(HtmlConsts.DIV));
    return html.toString();
  }

  protected OAuthToken verifyGDataAuthorization(HttpServletRequest req, HttpServletResponse resp) 
  		throws IOException, ODKExternalServiceAuthenticationError, ODKExternalServiceNotAuthenticated {
	  
		boolean receivingToken = getParameter(req, ServletConsts.OAUTH_TOKEN_PARAMETER) != null;
		if (receivingToken)
		{
		  	GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
			oauthParameters.setOAuthConsumerKey(ServletConsts.OAUTH_CONSUMER_KEY);
			oauthParameters.setOAuthConsumerSecret(ServletConsts.OAUTH_CONSUMER_SECRET);
			GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(new OAuthHmacSha1Signer());
			oauthHelper.getOAuthParametersFromCallback(req.getQueryString(), oauthParameters);
			try {
				oauthHelper.getAccessToken(oauthParameters);
			} catch (OAuthException e) {
		        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		        		ErrorConsts.OAUTH_SECURITY_ERROR_WHILE_RETRIEVING_SESSION_TOKEN);
				throw new ODKExternalServiceAuthenticationError();
			}
			
			return new OAuthToken(oauthParameters.getOAuthToken(), oauthParameters.getOAuthTokenSecret());
		}
		else
		{
			return null;
		}
  }

  protected String generateAuthButton(String buttonText, Map<String, String> params,
      HttpServletRequest req, HttpServletResponse resp, String... scopes) throws IOException {

	GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
	oauthParameters.setOAuthConsumerKey(ServletConsts.OAUTH_CONSUMER_KEY);
	oauthParameters.setOAuthConsumerSecret(ServletConsts.OAUTH_CONSUMER_SECRET);
	String scope = BasicConsts.EMPTY_STRING;
	for (String singleScope : scopes)
	{
		scope += singleScope + BasicConsts.SPACE;
	}
	oauthParameters.setScope(scope);
	
	GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(new OAuthHmacSha1Signer());
	try 
	{
		oauthHelper.getUnauthorizedRequestToken(oauthParameters);
	} 
	catch (OAuthException e) 
	{
        e.printStackTrace();
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            ErrorConsts.OAUTH_SERVER_REJECTED_ONE_TIME_USE_TOKEN);
	}

	params.put(ServletConsts.OAUTH_TOKEN_SECRET_PARAMETER, oauthParameters.getOAuthTokenSecret());
	String callbackUrl = ServletConsts.HTTP
		+ HtmlUtil.createLinkWithProperties(getServerURL(req) + req.getServletPath(), params);
	oauthParameters.setOAuthCallback(callbackUrl);
	String requestUrl = oauthHelper.createUserAuthorizationUrl(oauthParameters);

    StringBuilder form = new StringBuilder();
    form.append(HtmlConsts.LINE_BREAK);
    form.append(HtmlUtil.createFormBeginTag(requestUrl, null, HtmlConsts.POST));
    form.append(HtmlUtil.createInput(HtmlConsts.INPUT_TYPE_SUBMIT, null, buttonText));
    form.append(HtmlConsts.FORM_CLOSE);

    return form.toString();
  }
}