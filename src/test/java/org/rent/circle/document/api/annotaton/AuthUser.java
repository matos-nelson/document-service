package org.rent.circle.document.api.annotaton;

import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@TestSecurity(user = "test_user")
@JwtSecurity(claims = {
    @Claim(key = "user_id", value = "abc123")
})
public @interface AuthUser {

}