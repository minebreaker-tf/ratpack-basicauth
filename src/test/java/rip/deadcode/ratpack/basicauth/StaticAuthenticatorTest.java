package rip.deadcode.ratpack.basicauth;

import org.junit.jupiter.api.Test;
import ratpack.exec.ExecResult;
import ratpack.test.exec.ExecHarness;

import static com.google.common.truth.Truth.assertThat;

class StaticAuthenticatorTest {

    @Test
    void testSuccess() throws Exception {

        ExecResult result = ExecHarness.yieldSingle( execution -> {
            return new StaticAuthenticator( "user", "password" ).authenticate( "user", "password" );
        } );

        assertThat( result.getValue() ).isEqualTo( Boolean.TRUE );
    }

    @Test
    void testUserContainingColonIsReplaced() throws Exception {

        ExecResult result = ExecHarness.yieldSingle( execution -> {
            return new StaticAuthenticator( "us:er", "password" ).authenticate( "user", "password" );
        } );

        assertThat( result.getValue() ).isEqualTo( Boolean.TRUE );
    }

    @Test
    void testFailure() throws Exception {

        ExecResult result = ExecHarness.yieldSingle( execution -> {
            return new StaticAuthenticator( "user", "password" ).authenticate( "user", "wrong password" );
        } );

        assertThat( result.getValue() ).isEqualTo( Boolean.FALSE );
    }

}
