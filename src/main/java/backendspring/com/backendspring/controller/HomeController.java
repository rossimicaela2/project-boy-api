package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.bbdd.dto.UserDTO;
import backendspring.com.backendspring.bbdd.entity.Incabpie;
import backendspring.com.backendspring.bbdd.entity.Incuerpo;
import backendspring.com.backendspring.bbdd.entity.User;
import backendspring.com.backendspring.entity.Archivo;
import backendspring.com.backendspring.entity.Remito;
import backendspring.com.backendspring.service.EmailService;
import backendspring.com.backendspring.service.FileUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
public class HomeController {

  @Autowired
  private UserController userController;
  @Autowired
  private EmailService emailService;

  @Autowired
  private FileUploadService fileUploadService;
  @Autowired
  private ClientController clientController;
  @Autowired
  private RemitoController remitoController;
  @Autowired
  private IncabpieController incabpieController;
  @Autowired
  private IncuerpoController incuerpoController;

  private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  /******************** LOGIN / REGISTER **********************************/


  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
    try {
      UserDTO userfind = userController.findByName(user.getName());
      if (userfind != null && userfind.getName().equalsIgnoreCase(user.getName()) && userfind.getPassword().equalsIgnoreCase(user.getPassword())) {
        // Genera el token JWT
        String token = generateToken(userfind);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("avatar", userfind.getAvatar());
        return ResponseEntity.ok(response);
      } else{
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  private String generateToken(UserDTO user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRoles());

    Date expirationDate = new Date(System.currentTimeMillis() + 86400000); // 24 horas de duración del token
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getName())
        .setExpiration(expirationDate)
        .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  @GetMapping("/user")
  public ResponseEntity<UserDTO> getUserFromToken(@RequestHeader("Authorization") String token) {
    try {
      // Verificar y decodificar el token JWT
      UserDTO user = decodeToken(token);
      if (user != null) {
        return ResponseEntity.ok(user);
      } else {
        System.out.println("NO ENCONTRADO");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private UserDTO decodeToken(String token) {
    try {
      System.out.println("TOKEN " + token);
      Jws<Claims> claimsJws = Jwts.parserBuilder()
          .setSigningKey(SECRET_KEY)
          .build()
          .parseClaimsJws(token);

      Claims claims = claimsJws.getBody();

      // Extraer los datos del UserDTO del token decodificado
      String name = claims.getSubject();
      UserDTO userdto = userController.findByName(name);
      return userdto;
    } catch (MalformedJwtException e) {
      // Manejar la excepción de token JWT malformado
      e.printStackTrace();
      return null;
    } catch (Exception e) {
      // Manejar otras excepciones
      e.printStackTrace();
      return null;
    }
  }


  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
    try {
      UserDTO userfind = userController.findByName(user.getName());
      if (userfind != null ) { // lo va a actualizar
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      } else{
        userController.save(user, null);
        // Genera el token JWT
        String token = generateToken(userfind);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/update")
  public ResponseEntity<?> update(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("avatar") MultipartFile avatar) {
    try {
      System.out.println("UPDATE USER");
      UserDTO userfind = userController.findByName(name);
      if (userfind != null ) { // lo va a actualizar BUSCAR CLONAR OBJETOS
        System.out.println("UPDATE USER 2");
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(userfind.getPassword());
        user.setRoles(userfind.getRoles());
        user.setAvatar(userfind.getAvatar());
        if (!avatar.isEmpty()) {
          byte[] avatarBytes = avatar.getBytes();
          String avatarBase64 = Base64.getEncoder().encodeToString(avatarBytes);
          // Guarda los datos binarios de la imagen en el campo avatar del usuario
          user.setAvatar(avatarBase64);
        } else {
          // Mantén el valor actual de avatar si no se proporcionó una nueva imagen
          user.setAvatar(userfind.getAvatar());
        }
        userController.save(user, userfind.getId());

        return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
      } else{
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/recovery")
  public ResponseEntity<?> forgotPassword(@RequestBody User user) {
    try {
        UserDTO userfind = userController.findByEmail(user.getEmail());

        if (userfind != null) {
          String token = generateToken(userfind);
          Boolean update = userController.updateUser("resetToken", token, userfind);
          if (update) {
            String resetUrl = "http://localhost:4200/reset-password?token=" + token;
            emailService.sendEmail(userfind.getEmail(), resetUrl);
          } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

          }

        }
      return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/validate")
  public ResponseEntity<?> validateToken(@RequestBody User user) {
    try {
      UserDTO userfind = userController.findByToken(user.getResetToken());

      if (userfind != null) {
        return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/reset")
  public ResponseEntity<?> resetPassword(@RequestBody User user) {
    try {
      UserDTO userfind = userController.findByToken(user.getResetToken());

      if (userfind != null) {
        Boolean update = userController.updateUser("password", user.getPassword(), userfind);
        if (update) {
          return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  /******************** FUNCIONALIDADES **********************************/

  @PostMapping("/upload-socios")
  public ResponseEntity<?> uploadSocios(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String sheetname) {
    ResponseEntity<?> endPoint = this.uploadExcel(file,sheetname);
    if ((List<Map<String, String>>) this.uploadExcel(file,sheetname).getBody() != null ) {
      return clientController.createClients((List<Map<String, String>>) this.uploadExcel(file,sheetname).getBody());
    } else {
      return endPoint;
    }
  }

    @PostMapping("/upload-excel")
  public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String sheetname) {
    try {

      InputStream inputStream = file.getInputStream();
      Workbook workbook = WorkbookFactory.create(inputStream);

      Sheet sheet = workbook.getSheetAt(0);
      if (sheetname != null) {
        System.out.println("NOMBRE ARCHIVO" + sheetname);
        sheet = workbook.getSheet(sheetname);
      }

      List<Map<String, String>> jsonData = new ArrayList<>();
      Row headerRow = sheet.getRow(0);
      int rowCount = sheet.getLastRowNum();
      int columnCount = headerRow.getLastCellNum();

      for (int i = 1; i <= rowCount; i++) {
        Row row = sheet.getRow(i);
        Map<String, String> rowData = new LinkedHashMap<>();

        for (int j = 0; j < columnCount; j++) {
          Cell cell = row.getCell(j);
          Cell headerCell = headerRow.getCell(j);
          String key = headerCell.getStringCellValue();
          String value = "";

          if (cell != null) {
            value = getCellValueAsString(cell);
          }
          rowData.put(key, value);
        }
        jsonData.add(rowData);
      }

      System.out.println(jsonData);
      return ResponseEntity.ok(jsonData);
    } catch (IOException e) {
      System.out.println("ERROR" + e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  public static String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return "";
    }

    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        DataFormatter formatterNumeric = new DataFormatter();
        String strValueNumeric = formatterNumeric.formatCellValue(cell);
        return strValueNumeric;
      case BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case FORMULA:
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        DataFormatter formatter = new DataFormatter();
        String strValue = formatter.formatCellValue(cell, evaluator);
        return strValue;
      default:
        return "";
    }
  }

  @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
          // Obtener la extensión del archivo original
          String originalFilename = file.getOriginalFilename();
          String extension = ".xls";
          int extensionIndex = originalFilename.lastIndexOf('.');
          if (extensionIndex >= 0) {
            extension = originalFilename.substring(extensionIndex);
          }
          fileUploadService.uploadFile(file.getInputStream(), extension);
          return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
        } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
      }

  @RequestMapping("/listado")
  public List<Archivo> listarArchivos(@RequestParam(required = false) String ruta, @RequestParam(required = false) String extension) {
    List<Archivo> archivos = new ArrayList<>();
    String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/upload/auditoria";
    File directorio = new File(ruta != null ? ruta : UPLOAD_DIR); // Si no se especifica una ruta, se utiliza el directorio actual

    File[] listaArchivos = directorio.listFiles();

    if (listaArchivos != null) {
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

      for (File archivo : listaArchivos) {
        if (archivo.isFile() && !archivo.isHidden()) {
          if (extension != null && !archivo.getName().endsWith(extension)) {
            continue; // Si se especifica una extensión y no coincide, se salta al siguiente archivo
          }

          Archivo archivoInfo = new Archivo();
          archivoInfo.setNombre(archivo.getName());
          archivoInfo.setFecha(formatter.format(archivo.lastModified()));
          archivoInfo.setUrl(archivo.getPath());
          archivos.add(archivoInfo);
        }
      }
    } else {
      System.out.println("No se encontraron archivos en la ubicación especificada: " + ruta);
    }

    return archivos;
  }

  @GetMapping("/files/{filename}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {
    String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/upload/auditoria";
    String filePathString = UPLOAD_DIR + "/" + filename;
    Path filePath = Paths.get(filePathString);

    try {
      Resource fileResource = new UrlResource(filePath.toUri());

      if (fileResource.exists()) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(fileResource);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (MalformedURLException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/selection")
  public ResponseEntity<String> handleSelection(@RequestBody Map<String, Object> payload) {
    try {
      List<Object> selectedItems = (List<Object>) payload.get("selectedItems");
      System.out.println("LLEGA JSON: " + selectedItems);
      List<String> jsonDatas = remitoController.transformData(selectedItems);
      for (String jsonData : jsonDatas) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
          Remito remito = objectMapper.readValue(jsonData, Remito.class);
          // actualizar stock de productos y materias primas
          Incabpie newProducto = new Incabpie();
          newProducto.setDenominacion(remito.getProducto());
          newProducto.setStock(remito.getTotalDosificado().toString());
          incabpieController.save(newProducto);

          // actualiza stock materia prima
          for (int i = 0; i < remito.getSubproductos().size() ; i++) {
            Incuerpo subProducto = new Incuerpo();
            subProducto.setDenominacion(remito.getSubproductos().get(i).getNombre());
            subProducto.setStock(remito.getSubproductos().get(i).getCantidad().toString());
            incuerpoController.save(subProducto);
          }

          ResponseEntity<byte[]> responseEntity = RemitoController.generarRemito(remito);
          byte[] response = responseEntity.getBody();

          try (InputStream inputStream = new ByteArrayInputStream(response)) {
            fileUploadService.uploadFile(inputStream, ".pdf");
          } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }

        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
      }
      return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}