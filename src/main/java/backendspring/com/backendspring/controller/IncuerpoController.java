package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.bbdd.dto.IncuerpoDTO;
import backendspring.com.backendspring.bbdd.entity.Incuerpo;
import backendspring.com.backendspring.bbdd.service.IncuerpoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(value = "/incuerpo")
@CrossOrigin("*")
public class IncuerpoController {

  @Autowired
  private IncuerpoService incuerpoService;

  public ResponseEntity<String> save(@RequestBody Incuerpo producto) throws Exception {
    IncuerpoDTO productoDTO = incuerpoService.getProductoByName(producto.getDenominacion());

    // Parsear los valores de stock a BigDecimal
    BigDecimal stockToInsert = parseStockValue(producto.getStock());
    BigDecimal defaultStock = new BigDecimal("1000000");

    BigDecimal resultado;
    if (productoDTO == null) {
      // Si el producto no existe, restar al stock por defecto
      resultado = defaultStock.subtract(stockToInsert);
    } else {
      // Si el producto existe, restar al stock actual
      BigDecimal currentStock = parseStockValue(productoDTO.getStock());
      resultado = currentStock.subtract(stockToInsert);

      if (resultado.compareTo(BigDecimal.ZERO) < 0) {
        resultado = BigDecimal.ZERO; // No permitir valores negativos
      }
    }

    // Formatear el resultado nuevamente al formato original (con coma para decimales y punto para miles)
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
    symbols.setDecimalSeparator(',');
    symbols.setGroupingSeparator('.');
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.000", symbols);
    String resultadoFormateado = decimalFormat.format(resultado);

    if (productoDTO == null) {
      producto.setStock(resultadoFormateado);
      incuerpoService.save(producto);
    } else { // actualizar stock
      incuerpoService.updateSubStock(resultadoFormateado, productoDTO.getId());
    }

    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  // Función para parsear los valores de stock
  private BigDecimal parseStockValue(String stockValue) {
    String cleanedValue = stockValue.replace(".", "").replace(",", ".");
    return new BigDecimal(cleanedValue);
  }

  public void saveAll(List<Incuerpo> productos) throws Exception {
    Map<String, String> stockUpdates = new HashMap<>();

    for (Incuerpo producto : productos) {
      IncuerpoDTO productoDTO = incuerpoService.getProductoByName(producto.getDenominacion());

      if (productoDTO == null) {
        incuerpoService.save(producto);
      } else {
        // Aquí solo se recopilan los productos que necesitan actualizar el stock
        if (!productoDTO.getStock().equals(producto.getStock())) {
          stockUpdates.put(productoDTO.getId(), producto.getStock());
        }
      }
    }

    // Realizar el batch update al final del bucle
    if (!stockUpdates.isEmpty()) {
      incuerpoService.updateStockInBatch(stockUpdates);
    }
  }
}
