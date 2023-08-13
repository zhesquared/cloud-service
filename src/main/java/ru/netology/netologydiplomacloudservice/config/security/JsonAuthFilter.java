package ru.netology.netologydiplomacloudservice.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.netology.netologydiplomacloudservice.exception.IncorrectFileDataException;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class JsonAuthFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        String username;
        String password;

        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);

            username = requestMap.get("login");
            if (username == null) {
                log.warn("Не заполнено поле 'username'");
                throw new IncorrectFileDataException("Username is mandatory");
            }

            password = requestMap.get("password");
            if (password == null) {
                log.warn("Не заполнено поле 'password'");
                throw new IncorrectFileDataException("Password is mandatory");
            }
        } catch (IOException | RuntimeException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password);
        this.setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
