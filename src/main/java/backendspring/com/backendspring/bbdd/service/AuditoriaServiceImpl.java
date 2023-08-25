package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceImpl;
import backendspring.com.backendspring.bbdd.dto.AuditoriaDTO;
import backendspring.com.backendspring.bbdd.entity.Auditoria;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AuditoriaServiceImpl extends GenericServiceImpl<Auditoria, AuditoriaDTO> implements AuditoriaService {

  @Autowired
  private Firestore firestore;

  @Override
  public CollectionReference getCollection() {
    return firestore.collection("auditoria");
  }

  @Override
  public List<Auditoria> searchAuditorias(String query) throws ExecutionException, InterruptedException {
    List<Auditoria> results = new ArrayList<>();

    // Obtén la referencia a la colección en Firestore
    CollectionReference collection = getCollection();

    collection.whereGreaterThanOrEqualTo("auditName", query)
        .whereLessThanOrEqualTo("auditName", query + "\uf8ff")
        .limit(10)
        .get()
        .get()
        .getDocuments()
        .forEach(document -> {
          // Obtén el nombre del documento y agrégalo a los resultados
          String auditName = document.getString("auditName");
          String auditData = document.getString("data");
          String auditDate = document.getString("auditDate");
          String auditUser = document.getString("userName");
          Auditoria objResult = new Auditoria();
          objResult.setAuditName(auditName);
          objResult.setData(auditData);
          objResult.setAuditDate(auditDate);
          results.add(objResult);
        });

    return results;
  }

  @Override
  public List<Auditoria> getAllAuditorias() throws ExecutionException, InterruptedException {
    List<Auditoria> results = new ArrayList<>();

    // Obtén la referencia a la colección en Firestore
    CollectionReference collection = getCollection();

    collection.get()
        .get()
        .getDocuments()
        .forEach(document -> {
          // Obtén los datos del documento y agrégalo a los resultados
          String auditName = document.getString("auditName");
          String auditData = document.getString("data");
          String auditDate = document.getString("auditDate");
          String auditUser = document.getString("userName");
          Auditoria objResult = new Auditoria();
          objResult.setAuditName(auditName);
          objResult.setData(auditData);
          objResult.setAuditDate(auditDate);
          objResult.setUserName(auditUser);
          results.add(objResult);
        });

    return results;
  }

  @Override
  public AuditoriaDTO getByName(String name) throws Exception {
    CollectionReference collection = getCollection();

    Query query = collection.whereEqualTo("audit_name", name).limit(1);
    ApiFuture<QuerySnapshot> future = query.get();
    QuerySnapshot querySnapshot = future.get();

    if (!querySnapshot.isEmpty()) {
      DocumentSnapshot document = querySnapshot.getDocuments().get(0);
      AuditoriaDTO audit = document.toObject(clazz);
      System.out.println("ENCONTRO AUDITORIA IMPRIMIR" + audit.getAuditName());
      return audit;
    }

    return null;
  }
}


