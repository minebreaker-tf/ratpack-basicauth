package rip.deadcode.ratpack.basicauth;

import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.Test;
import ratpack.exec.Promise;
import ratpack.test.handling.HandlingResult;
import ratpack.test.handling.RequestFixture;

import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasicAuthHandlerTest {

    private static String createToken( String user, String password ) {
        String pair = ( user + ":" + password );
        String encoded = BaseEncoding.base64().encode( pair.getBytes( StandardCharsets.US_ASCII ) );
        return "Basic " + encoded;
    }

    private static void assertWwwAuthHeaderIsAdded( HandlingResult result ) {
        assertWithMessage( "The WWW-Authenticate header should be added" )
                .that( result.getHeaders().get( HttpHeaders.WWW_AUTHENTICATE ) )
                .isEqualTo( "Basic realm=\"WallyWorld\"" );
    }

    @Test
    void testAuthSuccess() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );
        when( auth.authenticate( "user", "password" ) ).thenReturn( Promise.value( Boolean.TRUE ) );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {
                    action.header( HttpHeaders.AUTHORIZATION, createToken( "user", "password" ) );
                } );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.isCalledNext() ).isTrue();
    }

    @Test
    void testAuthSuccessWithPasswordContainsColons() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );
        when( auth.authenticate( "user", "pass:word" ) ).thenReturn( Promise.value( Boolean.TRUE ) );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {
                    action.header( HttpHeaders.AUTHORIZATION, createToken( "user", "pass:word" ) );
                } );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.isCalledNext() ).isTrue();
    }

    @Test
    void testAuthSuccessWithEmptyPassword() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );
        when( auth.authenticate( "user", "" ) ).thenReturn( Promise.value( Boolean.TRUE ) );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {
                    action.header( HttpHeaders.AUTHORIZATION, createToken( "user", "" ) );
                } );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.isCalledNext() ).isTrue();
    }

    @Test
    void testWithoutHeader() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {} );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.getClientError() ).isEqualTo( 401 );
    }

    @Test
    void testInvalidHeader() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {
                    action.header( HttpHeaders.AUTHORIZATION, "INVALID_HEADER" );
                } );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.exception( IllegalStateException.class ) )
                .hasMessageThat().isEqualTo( "Invalid Authorization header value: INVALID_HEADER" );
    }

    @Test
    void testInvalidChallenge() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {
                    action.header( HttpHeaders.AUTHORIZATION, "Digest TOKEN" );
                } );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.exception( IllegalStateException.class ) )
                .hasMessageThat().isEqualTo( "Not Basic authentication." );
    }

    @Test
    void testInvalidToken() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {
                    String token = BaseEncoding.base64().encode(
                            "Not contain colons".getBytes( StandardCharsets.US_ASCII ) );
                    action.header( HttpHeaders.AUTHORIZATION, "Basic " + token );
                } );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.exception( IllegalStateException.class ) )
                .hasMessageThat().isEqualTo( "Invalid token: [Not contain colons]" );
    }

    @Test
    void testAuthWrongPassword() throws Exception {

        BasicAuthAuthenticator auth = mock( BasicAuthAuthenticator.class );
        when( auth.authenticate( anyString(), anyString() ) ).thenReturn( Promise.value( Boolean.FALSE ) );

        HandlingResult result = RequestFixture.handle(
                new BasicAuthHandler( auth ), action -> {
                    action.header( HttpHeaders.AUTHORIZATION, createToken( "user", "password" ) );
                } );

        assertWwwAuthHeaderIsAdded( result );
        assertThat( result.getClientError() ).isEqualTo( 401 );
    }
}
