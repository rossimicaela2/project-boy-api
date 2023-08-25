package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.bbdd.dto.AuditoriaDTO;
import backendspring.com.backendspring.bbdd.dto.UserDTO;
import backendspring.com.backendspring.bbdd.entity.Auditoria;
import backendspring.com.backendspring.bbdd.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

  private final AuditoriaService auditoriaService;

  public AuditoriaController(AuditoriaService auditoriaService) {
    this.auditoriaService = auditoriaService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createAuditoria(@RequestBody String jsonData) {

    ZoneId zonaArgentina = ZoneId.of("America/Argentina/Buenos_Aires");
    LocalDateTime fechaHoraActual = LocalDateTime.now(zonaArgentina);
    DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    String fechaHoraFormateada = fechaHoraActual.format(formatoFechaHora);

    Auditoria audit = new Auditoria();
    audit.setData(jsonData);
    audit.setAuditName("Auditoria_" + fechaHoraFormateada);

    try {
      auditoriaService.save(audit);
      return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  public void createMultipleAuditorias(List<String> auditoriaData, UserDTO user) throws Exception {
    for (String jsonData : auditoriaData) {
      ZoneId zonaArgentina = ZoneId.of("America/Argentina/Buenos_Aires");
      LocalDateTime fechaHoraActual = LocalDateTime.now(zonaArgentina);
      DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
      String fechaHoraFormateada = fechaHoraActual.format(formatoFechaHora);

      DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
      String fechaFormateada = fechaHoraActual.format(formatoFecha);

      Auditoria audit = new Auditoria();
      audit.setData(jsonData);
      audit.setAuditName("Auditoria_" + fechaHoraFormateada);
      audit.setAuditDate(fechaFormateada);
      audit.setUserName(user.getName());
      audit.setUserId(user.getId());
      auditoriaService.save(audit);
    }
  }

  public List<Auditoria> getAllAuditorias() {
    try {
      return auditoriaService.getAllAuditorias();
    } catch (ExecutionException e) {
      e.printStackTrace();
      return null;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

  public AuditoriaDTO findByName(@PathVariable String name) throws Exception {
    return auditoriaService.getByName(name);
  }
}
