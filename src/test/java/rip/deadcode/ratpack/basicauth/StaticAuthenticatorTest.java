package rip.deadcode.ratpack.basicauth;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import ratpack.exec.ExecResult;
import ratpack.test.exec.ExecHarness;

import static com.google.common.truth.Truth.assertThat;

class StaticAuthenticatorTest {

    @Test
    void testSuccess() throws Exception {

        ExecResult result = ExecHarness.yieldSingle( execution -> {
            return new StaticAuthenticator( ImmutableMap.of( "user", "password" ) ).authenticate( "user", "password" );
        } );

        assertThat( result.getValue() ).isEqualTo( Boolean.TRUE );
    }

    @Test
    void testWrongUser() throws Exception {

        ExecResult result = ExecHarness.yieldSingle( execution -> {
            return new StaticAuthenticator( ImmutableMap.of( "user", "password" ) )
                    .authenticate( "wrong user", "password" );
        } );

        assertThat( result.getValue() ).isEqualTo( Boolean.FALSE );
    }

    @Test
    void testWrongPassword() throws Exception {

        ExecResult result = ExecHarness.yieldSingle( execution -> {
            return new StaticAuthenticator( ImmutableMap.of( "user", "password" ) )
                    .authenticate( "user", "wrong password" );
        } );

        assertThat( result.getValue() ).isEqualTo( Boolean.FALSE );
    }
}
