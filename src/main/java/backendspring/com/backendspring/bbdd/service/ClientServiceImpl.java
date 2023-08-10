package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceImpl;
import backendspring.com.backendspring.bbdd.dto.ClientDTO;
import backendspring.com.backendspring.bbdd.entity.Client;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ClientServiceImpl extends GenericServiceImpl<Client, ClientDTO> implements ClientService {

  @Autowired
  private Firestore firestore;

  @Override
  public CollectionReference getCollection() {
    return firestore.collection("client");
  }

  @Override
  public List<Client> searchClients(String query) throws ExecutionException, InterruptedException {
    List<Client> results = new ArrayList<>();

    // Obtén la referencia a la colección en Firestore
    CollectionReference collection = getCollection();

    // Realiza la consulta para buscar los documentos que coincidan con la consulta
    // Puedes personalizar la consulta según tu estructura de datos y requisitos específicos
    collection.whereGreaterThanOrEqualTo("nombre", query)
        .whereLessThanOrEqualTo("nombre", query + "\uf8ff")
        .limit(10)
        .get()
        .get()
        .getDocuments()
        .forEach(document -> {
          // Obtén el nombre del documento y agrégalo a los resultados
          String clientName = document.getString("nombre");
          String cuijName = document.getString("cuij");
          Client objResult = new Client();
          objResult.setNombre(clientName);
          objResult.setCuit(cuijName);
          results.add(objResult);
        });

    return results;
  }

  public boolean createClients(List<Client> clients) {

    try {
      int batchSize = 200; // Tamaño del lote de escrituras
      int totalClients = clients.size();
      int batches = (int) Math.ceil((double) totalClients / batchSize);

      for (int i = 0; i < batches; i++) {
        int fromIndex = i * batchSize;
        int toIndex = Math.min((i + 1) * batchSize, totalClients);

        // Obtiene el lote actual de clientes
        List<Client> batchClients = clients.subList(fromIndex, toIndex);

        // Inicia una transacción batch
        WriteBatch batch = firestore.batch();

        // Recorre el lote de clientes y agrega las operaciones de escritura a la transacción batch
        for (Client client : batchClients) {
          DocumentReference clientRef = getCollection().document();
          batch.set(clientRef, client);
        }

        // Ejecuta la transacción batch
        batch.commit().get();
      }
      return true;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    } catch (ExecutionException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public ClientDTO getClientByNombre(String nombre) throws Exception {
    CollectionReference collection = getCollection();

    Query query = collection.whereEqualTo("nombre", nombre).limit(1);
    ApiFuture<QuerySnapshot> future = query.get();
    QuerySnapshot querySnapshot = future.get();

    if (!querySnapshot.isEmpty()) {
      DocumentSnapshot document = querySnapshot.getDocuments().get(0);
      ClientDTO client = document.toObject(clazz);
      PropertyUtils.setProperty(client, "id", document.getId());
      return client;
    }

    return null;
  }
}