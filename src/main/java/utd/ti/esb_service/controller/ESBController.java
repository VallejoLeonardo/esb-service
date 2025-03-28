package utd.ti.esb_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import utd.ti.esb_service.model.User;
import utd.ti.esb_service.utils.Auth;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/esb2")
public class ESBController {
    private final Auth auth;
    private final WebClient usersWebClient;
    private final WebClient clientesWebClient;
    private final WebClient emailWebClient;
    
    @Autowired
    public ESBController(Auth auth, WebClient usersWebClient, WebClient clientesWebClient, WebClient emailWebClient) {
        this.auth = auth;
        this.usersWebClient = usersWebClient;
        this.clientesWebClient = clientesWebClient;
        this.emailWebClient = emailWebClient;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body " + user);
        System.out.println("Token recibido: " + token);
        
        //validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.badRequest()
                    .body("Token inválido");
        }

        try {
            System.out.println("Sending user data: " + user);
            String response = usersWebClient.post()
                    .uri("/app/users/create")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Token recibido: " + token);

        //validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.badRequest()
                    .body("Token inválido");
        }

        try {
            String response = usersWebClient.get()
                    .uri("/app/users/all")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            // Validación básica
            if (user.getId() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body("ID y contraseña son requeridos");
            }

            System.out.println("Sending user data for update: " + user);
            String response = usersWebClient.patch()
                    .uri("/app/users/update/" + user.getId())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            // Validar token
            if (!auth.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body("Token inválido");
            }

            System.out.println("Sending delete request for user ID: " + id);
            String response = usersWebClient.patch()
                    .uri("/app/users/delete/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }

    // ENDPOINTS PARA EL SERVICIO DE CLIENTES
    
    @GetMapping("/clientes")
    public ResponseEntity<?> getAllClientes(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Token recibido en getAllClientes: " + token);

        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.badRequest()
                    .body("Token inválido");
        }

        try {
            String response = clientesWebClient.get()
                    .uri("/clientes")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error al obtener clientes: " + e.getMessage());
        }
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> getClienteById(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.badRequest()
                    .body("Token inválido");
        }

        try {
            String response = clientesWebClient.get()
                    .uri("/clientes/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error al obtener cliente: " + e.getMessage());
        }
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> createCliente(
            @RequestBody Object cliente,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.badRequest()
                    .body("Token inválido");
        }

        try {
            System.out.println("Enviando datos de cliente para crear: " + cliente);
            String response = clientesWebClient.post()
                    .uri("/clientes")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(cliente)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error al crear cliente: " + e.getMessage());
        }
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> updateCliente(
            @PathVariable Long id,
            @RequestBody Object cliente,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.badRequest()
                    .body("Token inválido");
        }

        try {
            System.out.println("Enviando datos de cliente para actualizar: " + cliente);
            String response = clientesWebClient.put()
                    .uri("/clientes/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(cliente)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error al actualizar cliente: " + e.getMessage());
        }
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> deleteCliente(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.badRequest()
                    .body("Token inválido");
        }

        try {
            System.out.println("Enviando solicitud para eliminar cliente con ID: " + id);
            String response = clientesWebClient.delete()
                    .uri("/clientes/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error al eliminar cliente: " + e.getMessage());
        }
    }

    @PostMapping("/clientes/{id}/enviar-email")
public ResponseEntity<?> enviarInformacionClienteEmail(
        @PathVariable Long id,
        @RequestBody Map<String, String> body,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
    
    // Validar el token
    if (!auth.validateToken(token)) {
        return ResponseEntity.badRequest()
                .body("Token inválido");
    }

    // Validar que se proporcionó el correo electrónico
    String email = body.get("email");
    if (email == null || email.isEmpty()) {
        return ResponseEntity.badRequest()
                .body("El correo electrónico es requerido");
    }

    try {
        // Obtener información del cliente por ID
        String clienteResponse;
        try {
            clienteResponse = clientesWebClient.get()
                    .uri("/clientes/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cliente no existe");
            }
            throw e;
        }

        // Preparar datos para el servicio de email
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", email);
        emailData.put("subject", "Información del Cliente #" + id);
        emailData.put("message", clienteResponse);
        emailData.put("text", "Detalles del cliente #" + id + ": " + clienteResponse);
        emailData.put("name", "Estimado Usuario");

        // Enviar email con la información del cliente
        String emailResponse = emailWebClient.post()
                .uri("/api/email/send")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(emailData)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(Map.of(
            "message", "Información del cliente enviada por correo electrónico",
            "emailResponse", emailResponse
        ));
    } catch (WebClientResponseException e) {
        System.err.println("Error de servicio externo: " + e.getResponseBodyAsString());
        return ResponseEntity.status(e.getStatusCode())
                .body(e.getResponseBodyAsString());
    } catch (Exception e) {
        System.err.println("Error al procesar la solicitud: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al procesar la solicitud: " + e.getMessage());
    }
}


}




