package backendspring.com.backendspring.bbdd.dto;

public class AuditoriaDTO {
  private String id;
  private String data;
  private String audit_id;
  private String audit_name;
  private String audit_date;
  private String user_id;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getAudit_id() {
    return audit_id;
  }

  public void setAudit_id(String audit_id) {
    this.audit_id = audit_id;
  }

  public String getAudit_name() {
    return audit_name;
  }

  public void setAudit_name(String audit_name) {
    this.audit_name = audit_name;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getAudit_date() {
    return audit_date;
  }

  public void setAudit_date(String audit_date) {
    this.audit_date = audit_date;
  }

}
