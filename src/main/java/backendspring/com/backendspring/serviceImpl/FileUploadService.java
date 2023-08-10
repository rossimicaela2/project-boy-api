package backendspring.com.backendspring.serviceImpl;

import backendspring.com.backendspring.service.FileUploadService;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
class FileUploadServiceImpl implements FileUploadService{

  private final String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/upload/auditoria";

  @Override
  public void uploadFile(InputStream inputStream, String extension) {
    try {
      LocalDateTime fechaHoraActual = LocalDateTime.now();
      DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
      String fechaHoraFormateada = fechaHoraActual.format(formatoFechaHora);

      Path uploadDirPath = Paths.get(UPLOAD_DIR);
      if (!Files.exists(uploadDirPath)) {
        Files.createDirectories(uploadDirPath);
        System.out.println("Directorio de carga creado: " + uploadDirPath);
      }

      String fileName = "auditoria_" + fechaHoraFormateada + extension;
      Path filePath = uploadDirPath.resolve(fileName);

      byte[] bytes = IOUtils.toByteArray(inputStream);
      System.out.println("Archivo a guardar: " + filePath);
      Files.write(filePath, bytes);
    } catch (IOException e) {
      // manejar excepción
    }
  }

}
