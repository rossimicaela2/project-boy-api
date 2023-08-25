package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.bbdd.dto.IncabpieDTO;
import backendspring.com.backendspring.bbdd.entity.Incabpie;
import backendspring.com.backendspring.bbdd.service.IncabpieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(value = "/incabpie")
@CrossOrigin("*")
public class IncabpieController {

  @Autowired
  private IncabpieService incabpieService;

  @PostMapping(value = "/save/{id}")
  public ResponseEntity<String> save(@RequestBody Incabpie producto) throws Exception {
    IncabpieDTO productoDTO = incabpieService.getProductoByName(producto.getDenominacion());
    if (productoDTO == null) {
      incabpieService.save(producto);
    } else { // actualizar stock

      // Reemplazar la coma por el punto y quitar los separadores de miles
      String numStr1Formatted = productoDTO.getStock().replace(",", "").replace(".", "");
      String numStr2Formatted = producto.getStock().replace(",", "").replace(".", "");

      // Convertir a BigDecimal
      BigDecimal num1 = new BigDecimal(numStr1Formatted);
      BigDecimal num2 = new BigDecimal(numStr2Formatted);

      BigDecimal suma = num1.add(num2);

      // Formatear el resultado nuevamente al formato original
      DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
      symbols.setDecimalSeparator(',');
      symbols.setGroupingSeparator('.');
      DecimalFormat decimalFormat = new DecimalFormat("#,##0.000", symbols);
      String resultado = decimalFormat.format(suma);

      incabpieService.updateStock(resultado, productoDTO.getId());
    }
    return new ResponseEntity<String>("OK", HttpStatus.OK);
  }


  public void saveAll(List<Incabpie> productos) throws Exception {
    Map<String, String> stockUpdates = new HashMap<>();

    for (Incabpie producto : productos) {
      IncabpieDTO productoDTO = incabpieService.getProductoByName(producto.getDenominacion());

      if (productoDTO == null) {
        incabpieService.save(producto);
      } else {
        // Aquí solo se recopilan los productos que necesitan actualizar el stock
        if (!productoDTO.getStock().equals(producto.getStock())) {
          stockUpdates.put(productoDTO.getId(), producto.getStock());
        }
      }
    }

    // Realizar el batch update al final del bucle
    if (!stockUpdates.isEmpty()) {
      incabpieService.updateStockInBatch(stockUpdates);
    }
  }

}
