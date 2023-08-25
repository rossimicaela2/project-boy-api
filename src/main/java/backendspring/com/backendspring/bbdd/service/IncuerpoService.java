package backendspring.com.backendspring.bbdd.service;

import backendspring.com.backendspring.bbdd.commos.GenericServiceAPI;
import backendspring.com.backendspring.bbdd.dto.IncuerpoDTO;
import backendspring.com.backendspring.bbdd.entity.Incuerpo;

import java.util.Map;

public interface IncuerpoService extends GenericServiceAPI<Incuerpo, IncuerpoDTO> {

  Boolean updateSubStock(String fieldValue, String incuerpo) throws Exception;
  IncuerpoDTO getProductoByName(String name) throws Exception;
  void updateStockInBatch(Map<String, String> stockUpdates) throws Exception;

}
