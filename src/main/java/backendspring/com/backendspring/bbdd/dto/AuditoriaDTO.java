package backendspring.com.backendspring.bbdd.dto;

public class AuditoriaDTO {
  private String id;
  private String data;
  private String auditId;
  private String auditName;
  private String auditDate;
  private String userName;
  private String userId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAuditId() {
    return auditId;
  }

  public void setAuditId(String auditId) {
    this.auditId = auditId;
  }

  public String getAuditName() {
    return auditName;
  }

  public void setAuditName(String auditName) {
    this.auditName = auditName;
  }

  public String getAuditDate() {
    return auditDate;
  }

  public void setAuditDate(String auditDate) {
    this.auditDate = auditDate;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }


}
