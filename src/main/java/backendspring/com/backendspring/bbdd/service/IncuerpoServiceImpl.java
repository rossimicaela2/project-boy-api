package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceImpl;
import backendspring.com.backendspring.bbdd.dto.IncuerpoDTO;
import backendspring.com.backendspring.bbdd.entity.Incuerpo;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncuerpoServiceImpl  extends GenericServiceImpl<Incuerpo, IncuerpoDTO> implements IncuerpoService {

  @Autowired
  private Firestore firestore;

  @Override
  public CollectionReference getCollection() {
    return firestore.collection("InCuerpo");
  }

  @Override
  public Boolean updateSubStock(String stock, String incuerpo) throws Exception {
    getCollection().document(incuerpo)
        .update("stock", stock)
        .get();

    return true;
  }

  @Override
  public IncuerpoDTO getProductoByName(String name) throws Exception {

    CollectionReference collection = getCollection();

    Query query = collection.whereEqualTo("denominacion", name).limit(1);
    ApiFuture<QuerySnapshot> future = query.get();
    QuerySnapshot querySnapshot = future.get();

    if (!querySnapshot.isEmpty()) {
      List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

      if (!documents.isEmpty()) {
        DocumentSnapshot document = documents.get(0);
        IncuerpoDTO product = document.toObject(IncuerpoDTO.class);
        PropertyUtils.setProperty(product, "id", document.getId());
        return product;
      }
    }

    return null;
  }
}