package org.cloudfoundry.identity.uaa.oauth.jwt;

import org.cloudfoundry.identity.uaa.oauth.KeyInfo;
import org.cloudfoundry.identity.uaa.oauth.KeyInfoBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.cloudfoundry.identity.uaa.test.ModelTestUtils.getResourceAsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JwtHelperTest {
    private KeyInfo keyInfo;

    private static final String certificate = getResourceAsString(JwtHelperTest.class, "certificate.pem");
    private static final String privatekey = getResourceAsString(JwtHelperTest.class, "privatekey.pem");

    @Before
    public void setUp() {
        keyInfo = KeyInfoBuilder.build("testKid", "symmetricKey", "http://localhost/uaa");
    }

    @Test
    public void testKidInHeader() {
        Jwt jwt = JwtHelper.encode("testJwtContent", keyInfo);
        assertEquals("testKid", jwt.getHeader().getKid());

        jwt = JwtHelper.decode(jwt.getEncoded());
        assertEquals("testKid", jwt.getHeader().getKid());
    }

    @Test
    public void jwtHeaderShouldContainJkuInTheHeader() {
        Jwt jwt = JwtHelper.encode("testJwtContent", keyInfo);
        assertEquals(jwt.getHeader().getJku(), "https://localhost/uaa/token_keys");
    }

    @Test
    public void jwtHeaderShouldNotContainJkuInTheHeaderIfCertificateDefined() {
        KeyInfo rsaKeyInfo = KeyInfoBuilder.build("key-id-1", privatekey, "http://localhost/uaa", "RS256", certificate);
        Jwt jwt = JwtHelper.encode("testJwtContent", rsaKeyInfo, true);
        assertNull(jwt.getHeader().getJku());
        assertEquals("8aFXmG4WA6wAiViW2DF2E6uigBU", jwt.getHeader().getX5t());
    }
}
