package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceAPI;
import backendspring.com.backendspring.bbdd.dto.UserDTO;
import backendspring.com.backendspring.bbdd.entity.User;

public interface UserService extends GenericServiceAPI<User, UserDTO> {

  UserDTO getUserByName(String name) throws Exception;
  UserDTO getUserByEmail(String email) throws Exception;
  Boolean updateField(String field, String fieldValue, UserDTO userfind) throws Exception;
  UserDTO getUserByToken(String token) throws Exception;
}
