package backendspring.com.backendspring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class StatusController {

  @GetMapping("/status")
  public ResponseEntity<?>  checkStatus() {
    return ResponseEntity.ok().body("{\"status\": \"SUCCESS\"}");  }
}
