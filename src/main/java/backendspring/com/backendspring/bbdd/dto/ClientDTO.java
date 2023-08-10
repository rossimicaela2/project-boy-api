package backendspring.com.backendspring.bbdd.dto;

public class ClientDTO {

  private String id;
  private String nombre;
  private String cuit;

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getCuit() {
    return cuit;
  }

  public void setCuit(String cuit) {
    this.cuit = cuit;
  }

  public String getId() { return id; }

  public void setId(String id) { this.id = id; }
}