package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceImpl;
import backendspring.com.backendspring.bbdd.dto.UserDTO;
import backendspring.com.backendspring.bbdd.entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericServiceImpl<User, UserDTO> implements UserService {

  @Autowired
  private Firestore firestore;

  @Override
  public CollectionReference getCollection() {
    return firestore.collection("user");
  }

  @Override
  public UserDTO getUserByName(String name) throws Exception {
    CollectionReference collection = getCollection();

    Query query = collection.whereEqualTo("name", name).limit(1);
    ApiFuture<QuerySnapshot> future = query.get();
    QuerySnapshot querySnapshot = future.get();

    if (!querySnapshot.isEmpty()) {
      DocumentSnapshot document = querySnapshot.getDocuments().get(0);
      UserDTO user = document.toObject(clazz);
      PropertyUtils.setProperty(user, "id", document.getId());
      return user;
    }

    return null;
  }

  @Override
  public UserDTO getUserByEmail(String email) throws Exception {
    CollectionReference collection = getCollection();

    Query query = collection.whereEqualTo("email", email).limit(1);
    ApiFuture<QuerySnapshot> future = query.get();
    QuerySnapshot querySnapshot = future.get();

    if (!querySnapshot.isEmpty()) {
      DocumentSnapshot document = querySnapshot.getDocuments().get(0);
      UserDTO user = document.toObject(clazz);
      PropertyUtils.setProperty(user, "id", document.getId());
      return user;
    }

    return null;
  }

  @Override
  public UserDTO getUserByToken(String token) throws Exception {
    CollectionReference collection = getCollection();

    Query query = collection.whereEqualTo("resetToken", token).limit(1);
    ApiFuture<QuerySnapshot> future = query.get();
    QuerySnapshot querySnapshot = future.get();

    if (!querySnapshot.isEmpty()) {
      DocumentSnapshot document = querySnapshot.getDocuments().get(0);
      UserDTO user = document.toObject(clazz);
      PropertyUtils.setProperty(user, "id", document.getId());
      return user;
    }

    return null;
  }

  @Override
  public Boolean updateField(String field, String fieldValue, UserDTO userfind) throws Exception {

    // Actualiza el campo resetToken en Firestore
    getCollection().document(userfind.getId())
        .update(field, fieldValue)
        .get();

    return true;
  }


 /* @Override
  public Boolean updateField(String field, String fieldValue, UserDTO userfind) throws Exception {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("user").child(userfind.getId());
System.out.println("USAURIO " + userRef.getKey() + " ACTUALIZA " + fieldValue + " " + field);
    // Actualiza el campo resetToken en la base de datos
    Map<String, Object> updates = new HashMap<>();
    updates.put(field, fieldValue);
    final Boolean[] retunResult = {true};
    userRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
          // Se produjo un error al actualizar los datos
          System.out.println(databaseError.getMessage());
          retunResult[0] = false;
        } else {
          // Los datos se actualizaron correctamente
          retunResult[0] = true;
        }
      }
    });
    return retunResult[0];
  }*/
}
