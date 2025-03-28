package utd.ti.esb_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${CLIENTES_API_URL}")
    private String clientesApiUrl;

    @Value("${USERS_API_URL}")
    private String usersApiUrl;

    @Value("${EMAIL_API_URL}")
    private String emailApiUrl;
    
    @Bean
    public WebClient usersWebClient() {
        System.out.println("Configurando usersWebClient con URL: " + usersApiUrl);
        return WebClient.builder()
                .baseUrl(usersApiUrl)
                .build();
    }
    
    @Bean
    public WebClient clientesWebClient() {
        System.out.println("Configurando clientesWebClient con URL: " + clientesApiUrl);
        return WebClient.builder()
                .baseUrl(clientesApiUrl)
                .build();
    }
    
    @Bean
    public WebClient emailWebClient() {
        System.out.println("Configurando emailWebClient con URL: " + emailApiUrl);
        return WebClient.builder()
                .baseUrl(emailApiUrl)
                .build();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}