package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceAPI;
import backendspring.com.backendspring.bbdd.dto.ClientDTO;
import backendspring.com.backendspring.bbdd.entity.Client;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ClientService extends GenericServiceAPI<Client, ClientDTO> {

  List<Client> searchClients(String query) throws ExecutionException, InterruptedException;
  boolean createClients(List<Client> clients);
  ClientDTO getClientByNombre(String name) throws Exception;

}

