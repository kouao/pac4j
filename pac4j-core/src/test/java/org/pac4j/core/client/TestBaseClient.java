/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.client;

import junit.framework.TestCase;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link BaseClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestBaseClient extends TestCase implements TestsConstants {

    public void testDirectClient() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
        final Credentials credentials = client.getCredentials(context);
        assertNull(credentials);
    }

    public void testIndirectClientWithImmediate() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
    }

    public void testNullCredentials() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        final MockWebContext context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertNull(client.getUserProfile(null, context));
    }

    public void testAjaxRequest() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create().addRequestHeader(HttpConstants.AJAX_HEADER_NAME, HttpConstants.AJAX_HEADER_VALUE);
        try {
            client.redirect(context);
            fail("should fail");
        } catch (RequiresHttpAction e) {
            assertEquals(401, e.getCode());
            assertEquals(401, context.getResponseStatus());
        }
    }

    public void testAlreadyTried() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        context.setSessionAttribute(client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        try {
            client.redirect(context);
            fail("should fail");
        } catch (RequiresHttpAction e) {
            assertEquals(403, e.getCode());
            assertEquals(403, context.getResponseStatus());
        }
    }

    public void testSaveAlreadyTried() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.getCredentials(context);
        assertEquals("true",
                (String) context.getSessionAttribute(client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX));
    }

    public void testStateParameter() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        final MockWebContext context = MockWebContext.create();
        try {
            client.getStateParameter(context);
            fail("should fail");
        } catch (UnsupportedOperationException e) {

        }
    }
}
