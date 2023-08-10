package backendspring.com.backendspring.entity;

import backendspring.com.backendspring.bbdd.entity.Client;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class Remito {

  private Number lote;
  private Number codigo;
  private String producto;
  private Double totalDosificado;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private String fecha;
  private Client cliente;
  private List<SubProducto> subproductos;

  // constructor, getters y setters

  public Number getLote() {
    return lote;
  }

  public void setLote(Number lote) {
    this.lote = lote;
  }

  public String getFecha() {
    return fecha;
  }

  public void setFecha(String fecha) {
    this.fecha = fecha;
  }

  public Client getCliente() {
    return cliente;
  }

  public void setCliente(Client cliente) {
    this.cliente = cliente;
  }

  public List<SubProducto> getSubproductos() {
    return subproductos;
  }

  public void setSubproductos(List<SubProducto> subproductos) {
    this.subproductos = subproductos;
  }

  public Number getCodigo() {
    return codigo;
  }

  public void setCodigo(Number codigo) {
    this.codigo = codigo;
  }

  public String getProducto() {
    return producto;
  }

  public void setProducto(String producto) {
    this.producto = producto;
  }


  public Double getTotalDosificado() {
    return totalDosificado;
  }

  public void setTotalDosificado(Double totalDosificado) {
    this.totalDosificado = totalDosificado;
  }

}

