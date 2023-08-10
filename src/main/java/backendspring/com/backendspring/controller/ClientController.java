package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.bbdd.dto.ClientDTO;
import backendspring.com.backendspring.bbdd.entity.Client;
import backendspring.com.backendspring.bbdd.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {

  private final ClientService clientService;

  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createClients(@RequestBody List<Map<String, String>> jsonData) {
    // Crea una lista de clientes a partir del JSON recibido
    List<Client> clients = jsonData.stream()
        .map(data -> {
          Client client = new Client();
          client.setNombre(data.get("NOMBRE"));
          client.setCuit(data.get("CUIT"));
          return client;
        })
        .collect(Collectors.toList());

    boolean create = clientService.createClients(clients);

    if (create) {
      return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping
  public List<Client> searchClients(@RequestParam String query) {
    try {
      return clientService.searchClients(query);
    } catch (ExecutionException e) {
      e.printStackTrace();
      return null;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

  public ClientDTO findByNombre(@PathVariable String nombre) throws Exception {
    return clientService.getClientByNombre(nombre);
  }
}
