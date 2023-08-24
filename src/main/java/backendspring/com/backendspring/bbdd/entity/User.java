package backendspring.com.backendspring.bbdd.entity;


import java.util.List;

public class User {

  private String name;
  private String email;
  private String password;
  private String resetToken;
  private String token;
  private List<String> roles; // Roles del usuario como una lista de strings
  private String avatar;

  public String getToken() { return token; }

  public void setToken(String token) {
    this.token = token;
  }

  public String getResetToken() { return resetToken; }

  public void setResetToken(String resetToken) {
    this.resetToken = resetToken;
  }

  public String getEmail() { return email; }

  public String getPassword() { return password; }

  public String getName() {
    return name;
  }

  public void setEmail(String email) { this.email = email; }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}


