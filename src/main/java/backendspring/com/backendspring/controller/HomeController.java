package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.bbdd.dto.AuditoriaDTO;
import backendspring.com.backendspring.bbdd.dto.UserDTO;
import backendspring.com.backendspring.bbdd.entity.Auditoria;
import backendspring.com.backendspring.bbdd.entity.Incabpie;
import backendspring.com.backendspring.bbdd.entity.Incuerpo;
import backendspring.com.backendspring.bbdd.entity.User;
import backendspring.com.backendspring.entity.Remito;
import backendspring.com.backendspring.service.EmailService;
import backendspring.com.backendspring.service.FileUploadService;
import backendspring.com.backendspring.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@CrossOrigin
public class HomeController {

  private final static Logger log = LoggerFactory.getLogger(HomeController.class);

  @Value("${reset.password.url}")
  private String resetPasswordUrl;

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
  @Autowired
  private AuditoriaController auditoriaController;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  /******************** LOGIN / REGISTER **********************************/


  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
    try {
      UserDTO userfind = userController.findByName(user.getName());
      if (userfind != null && userfind.getName().equalsIgnoreCase(user.getName()) && userfind.getPassword().equalsIgnoreCase(user.getPassword())) {
        // Genera el token JWT
        String token = jwtTokenUtil.generateToken(user.getName());
        userController.updateUser("token", token, userfind);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("avatar", userfind.getAvatar());
        response.put("roles", userfind.getRoles());
        log.info("Login Success " + userfind.getName());
        return ResponseEntity.ok(response);
      } else{
        log.error("Login error ");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    } catch (Exception e) {
      log.error("Login error " + e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
    try {
      UserDTO userfind = userController.findByName(user.getName());
      if (userfind != null ) { // lo va a actualizar
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      } else{
        // Genera el token JWT
        String token = jwtTokenUtil.generateToken(user.getName());
        user.setToken(token);
        userController.save(user, null);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        log.info("Register Success " + user.getName());
        return ResponseEntity.ok(response);
      }
    } catch (Exception e) {
      log.error("Register error " + e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/update")
  public ResponseEntity<?> update(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("avatar") MultipartFile avatar) {
    try {
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
        log.info("Update Success " + user.getName());
        return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
      } else{
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    } catch (Exception e) {
      log.error("Register error " + e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/recovery")
  public ResponseEntity<?> forgotPassword(@RequestBody User user) {
    try {
        UserDTO userfind = userController.findByEmail(user.getEmail());

        if (userfind != null) {
          String token = jwtTokenUtil.generateToken(userfind.getName());
          Boolean update = userController.updateUser("resetToken", token, userfind);
          if (update) {
            String resetUrl = resetPasswordUrl + "?token=" + token;
            emailService.sendEmail(userfind.getEmail(), resetUrl);
          } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

          }

        }
      return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
    } catch (Exception e) {
      log.error("Recovery error " + e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @PostMapping("/reset")
  public ResponseEntity<?> resetPassword(@RequestBody User user) {
    try {
      // Verificar y decodificar el token JWT
      UserDTO userfind = decodeToken(user.getResetToken());
      if (userfind != null) {
        Boolean update = userController.updateUser("password", user.getPassword(), userfind);
        if (update) {
          return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
      } else {
        log.error("Reset error ");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

    } catch (Exception e) {
      log.error("Reset error " + e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }


  private UserDTO decodeToken(String token) {
    try {
      if (jwtTokenUtil.isTokenExpired(token)) {
        return null;
      } else {
        String username = jwtTokenUtil.extractUsername(token);
        UserDTO userdto = userController.findByName(username);
        return userdto;
      }
    } catch (Exception e) {
      log.error("Token error " + e);
      return null;
    }
  }

  /******************** FUNCIONALIDADES **********************************/

  /**
   * EDITAR DATOS DEL USUARIO
   * @param token
   * @return
   */
  @GetMapping("/user")
  public ResponseEntity<UserDTO> getUserFromToken(@RequestHeader("Authorization") String token) {
    try {
      // Verificar y decodificar el token JWT
      UserDTO user = decodeToken(token);
      if (user != null) {
        return ResponseEntity.ok(user);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    } catch (Exception e) {
      log.error("Error " + e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * SUBIR EL ARCHIVO EXCEL - PROCESARLO COMO DATOS PARA MOSTRARLO EN UNA TABLA
   * @param file
   * @param sheetname
   * @return
   */
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

      log.info("Upload Success ");
      return ResponseEntity.ok(jsonData);
    } catch (IOException e) {
      log.error("Error " + e);
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

  /**
   * PROCESAR LO SELECCIONADO EN LA TABLA DE FRONT
   * Guardar en BBDD y generar auditoria
   *
   * @param payload
   * @param token
   * @return
   */
  @PostMapping("/selection")
  public ResponseEntity<String> handleSelection(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String token) {
    try {
      List<Object> selectedItems = (List<Object>) payload.get("selectedItems");
      List<String> jsonDatas = remitoController.transformData(selectedItems);
      List<Incabpie> newProductos = new ArrayList<>();
      List<Incuerpo> subProductos = new ArrayList<>();
      List<String> auditoriaData = new ArrayList<>();

      ObjectMapper objectMapper = new ObjectMapper();

      for (String jsonData : jsonDatas) {
        JsonNode jsonNode = objectMapper.readTree(jsonData);

        try {
          String productoDenominacion = jsonNode.get("producto").asText();
          Double totalDosificado = jsonNode.get("totalDosificado").asDouble();
          newProductos.add(createIncabpie(productoDenominacion, totalDosificado));

          ArrayNode subproductosArray = (ArrayNode) jsonNode.get("subproductos");
          subProductos.addAll(createSubProductos(subproductosArray));

          auditoriaData.add(jsonData);

        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
      }

      //incabpieController.saveAll(newProductos);
      //incuerpoController.saveAll(subProductos);
      UserDTO user = decodeToken(token);
      auditoriaController.createMultipleAuditorias(auditoriaData, user);

      return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
    } catch (Exception e) {
      log.error("Error " + e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private Incabpie createIncabpie(String denominacion, Double totalDosificado) {
    Incabpie newProducto = new Incabpie();
    newProducto.setDenominacion(denominacion);
    newProducto.setStock(totalDosificado.toString());
    return newProducto;
  }

  private List<Incuerpo> createSubProductos(ArrayNode subproductosArray) {
    List<Incuerpo> subProductos = new ArrayList<>();
    for (JsonNode subproductoNode : subproductosArray) {
      String subProductoDenominacion = subproductoNode.get("nombre").asText();
      Double cant = subproductoNode.get("cantidad").asDouble();
      Incuerpo subProducto = new Incuerpo();
      subProducto.setDenominacion(subProductoDenominacion);
      subProducto.setStock(cant.toString());
      subProductos.add(subProducto);
    }
    return subProductos;
  }

  /**
   * Listado de auditorias creadas
   * @param ruta
   * @param extension
   * @return
   */
  @RequestMapping("/listado")
  public List<Auditoria> listarAuditorias(@RequestParam(required = false) String ruta, @RequestParam(required = false) String extension) {
    return auditoriaController.getAllAuditorias();
  }

  @RequestMapping("/download")
  public ResponseEntity<?> descargarAuditoria(@RequestParam(required = false) String nameAuditoria) {

    try {
      AuditoriaDTO audit = auditoriaController.findByName(nameAuditoria);
      // Creo el remito que se va a descargar
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(audit.getData());

      Remito remito = objectMapper.readValue(audit.getData(), Remito.class);
      ResponseEntity<byte[]> responseEntity = RemitoController.generarRemito(remito);
      byte[] response = responseEntity.getBody();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", "Auditoria.pdf");

      return new ResponseEntity<>(response, headers, HttpStatus.OK);

    } catch (Exception e) {
      log.error("Error " + e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}