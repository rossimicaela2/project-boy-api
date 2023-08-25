package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceAPI;
import backendspring.com.backendspring.bbdd.dto.AuditoriaDTO;
import backendspring.com.backendspring.bbdd.entity.Auditoria;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface AuditoriaService extends GenericServiceAPI<Auditoria, AuditoriaDTO> {
  List<Auditoria> searchAuditorias(String query) throws ExecutionException, InterruptedException;
  List<Auditoria> getAllAuditorias() throws ExecutionException, InterruptedException;
  AuditoriaDTO getByName(String name) throws Exception;
}
