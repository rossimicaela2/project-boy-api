package backendspring.com.backendspring.entity;

public class SubProducto {

  private String nombre;
  private Double cantidad;

  public SubProducto() {
    // Constructor sin argumentos requerido para la deserialización de Jackson
  }

  public SubProducto(String nombre, Double cantidad) {
    this.nombre = nombre;
    this.cantidad = cantidad;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }


}
