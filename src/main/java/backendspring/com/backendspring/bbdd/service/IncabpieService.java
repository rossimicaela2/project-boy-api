package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceAPI;
import backendspring.com.backendspring.bbdd.dto.IncabpieDTO;
import backendspring.com.backendspring.bbdd.entity.Incabpie;

import java.util.Map;

public interface IncabpieService extends GenericServiceAPI<Incabpie, IncabpieDTO> {
  Boolean updateStock(String fieldValue, String incabpie) throws Exception;
  IncabpieDTO getProductoByName(String name) throws Exception;
  void updateStockInBatch(Map<String, String> stockUpdates) throws Exception;
}
