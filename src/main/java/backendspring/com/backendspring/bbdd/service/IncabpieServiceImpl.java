package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceImpl;
import backendspring.com.backendspring.bbdd.dto.IncabpieDTO;
import backendspring.com.backendspring.bbdd.entity.Incabpie;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncabpieServiceImpl  extends GenericServiceImpl<Incabpie, IncabpieDTO> implements IncabpieService {

  @Autowired
  private Firestore firestore;

  @Override
  public CollectionReference getCollection() {
    return firestore.collection("InCabpie");
  }

  @Override
  public Boolean updateStock(String stock, String incabpie) throws Exception {
    getCollection().document(incabpie)
        .update("stock", stock)
        .get();

    return true;
  }

  @Override
  public IncabpieDTO getProductoByName(String name) throws Exception {

    CollectionReference collection = getCollection();

    Query query = collection.whereEqualTo("denominacion", name).limit(1);
    ApiFuture<QuerySnapshot> future = query.get();
    QuerySnapshot querySnapshot = future.get();

    if (!querySnapshot.isEmpty()) {
      List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

      if (!documents.isEmpty()) {
        DocumentSnapshot document = documents.get(0);
        IncabpieDTO product = document.toObject(IncabpieDTO.class);
        PropertyUtils.setProperty(product, "id", document.getId());
        return product;
      }
    }

    return null;
  }

}
