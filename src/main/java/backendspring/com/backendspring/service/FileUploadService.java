package backendspring.com.backendspring.service;

import java.io.InputStream;

public interface FileUploadService {
  void uploadFile(InputStream inputStream, String extension);
}
