package utd.ti.esb_service.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        Instant start = Instant.now();
        String requestUri = request.getRequestURI();
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            Instant end = Instant.now();
            long timeElapsed = ChronoUnit.MILLIS.between(start, end);
            int status = response.getStatus();
            
            logger.info(String.format(
                "Request completed: [%s] %s - %d (%d ms)",
                request.getMethod(), requestUri, status, timeElapsed
            ));
        }
    }
}