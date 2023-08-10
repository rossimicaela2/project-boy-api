package backendspring.com.backendspring.bbdd.entity;

public class Client {
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
}

