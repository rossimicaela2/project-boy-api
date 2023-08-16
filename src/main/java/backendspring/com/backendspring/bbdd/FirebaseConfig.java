package backendspring.com.backendspring.bbdd;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

  @Bean
  public Firestore firestore() throws Exception {

    Resource resource = new ClassPathResource("firebase-account-info.json");
    InputStream serviceAccountStream = resource.getInputStream();

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
        .setDatabaseUrl("https://bb-boy-default-rtdb.firebaseio.com")
        .build();

    FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);

    return FirestoreClient.getFirestore(firebaseApp);
  }
}
