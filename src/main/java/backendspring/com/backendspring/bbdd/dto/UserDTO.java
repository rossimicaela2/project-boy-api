package backendspring.com.backendspring.bbdd.dto;

import java.util.List;

public class UserDTO {

  private String id;
  private String name;
  private String email;
  private String password;
  private String resetToken;
  private String activeToken;
  private List<String> roles;
  private String avatar;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() { return email; }

  public String getPassword() { return password; }

  public String getName() {
    return name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getResetToken() {
    return resetToken;
  }

  public void setResetToken(String resetToken) {
    this.resetToken = resetToken;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getActiveToken() {
    return activeToken;
  }

  public void setActiveToken(String activeToken) {
    this.activeToken = activeToken;
  }
  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}
