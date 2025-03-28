package utd.ti.esb_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${CLIENTES_API_URL:http://localhost:7000}")
    private String clientesApiUrl;

    @Value("${USERS_API_URL:http://localhost:5000}")
    private String usersApiUrl;

    @Value("${EMAIL_API_URL:http://localhost:8000}")
    private String emailApiUrl;
    
    @Bean
    public WebClient usersWebClient() {
        return WebClient.builder()
                .baseUrl(usersApiUrl)
                .build();
    }
    
    @Bean
    public WebClient clientesWebClient() {
        return WebClient.builder()
                .baseUrl(clientesApiUrl)
                .build();
    }
    
    @Bean
    public WebClient emailWebClient() {
        return WebClient.builder()
                .baseUrl(emailApiUrl)
                .build();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}