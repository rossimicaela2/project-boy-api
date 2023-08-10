package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.bbdd.dto.UserDTO;
import backendspring.com.backendspring.bbdd.entity.User;
import backendspring.com.backendspring.bbdd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
@CrossOrigin("*")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping(value = "/all")
  public List<UserDTO> getAll() throws Exception {
    return userService.getAll();
  }

  @GetMapping(value = "/find/{id}")
  public UserDTO find(@PathVariable String id) throws Exception {
    return userService.get(id);
  }

  @GetMapping(value = "/find/name/{name}")
  public UserDTO findByName(@PathVariable String name) throws Exception {
    return userService.getUserByName(name);
  }

  @GetMapping(value = "/find/email/{email}")
  public UserDTO findByEmail(@PathVariable String email) throws Exception {
    return userService.getUserByEmail(email);
  }

  @GetMapping(value = "/find/token/{token}")
  public UserDTO findByToken(@PathVariable String token) throws Exception {
    return userService.getUserByToken(token);
  }


  @PostMapping(value = "/save/{id}")
  public ResponseEntity<String> save(@RequestBody User persona, @PathVariable String id) throws Exception {
    if (id == null || id.length() == 0 || id.equals("null")) {
      id = userService.save(persona);
    } else {
      userService.save(persona, id);
    }
    return new ResponseEntity<String>(id, HttpStatus.OK);
  }

  @GetMapping(value = "/delete/{id}")
  public ResponseEntity<UserDTO> delete(@PathVariable String id) throws Exception {
    UserDTO persona = userService.get(id);
    if (persona != null) {
      userService.delete(id);
    } else {
      return new ResponseEntity<UserDTO>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<UserDTO>(persona, HttpStatus.OK);
  }

  public Boolean updateUser(String field, String fieldValue, UserDTO user) throws Exception {
    return userService.updateField(field, fieldValue, user);
  }
}
