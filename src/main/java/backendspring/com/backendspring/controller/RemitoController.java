package backendspring.com.backendspring.controller;

import backendspring.com.backendspring.entity.Remito;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/remitos")
public class RemitoController {

  @Autowired
  private ClientController clientController;

  @PostMapping("/generar")
  public static ResponseEntity<byte[]> generarRemito(@RequestBody Remito remito) {
    // Lógica para generar el remito

    // Generar el archivo PDF del remito utilizando una librería como iText, JasperReports, etc.
    byte[] archivoRemito = new byte[0];
    try {
      archivoRemito = generarArchivoRemito(remito);
    } catch (DocumentException | IOException e) {
      e.printStackTrace();
    }

    // Devolver el archivo PDF como respuesta
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "remito.pdf");

    return ResponseEntity.ok().headers(headers).body(archivoRemito);
  }

  // Método para generar el archivo PDF del remito
  private static byte[] generarArchivoRemito(Remito remito) throws DocumentException, IOException {

    // Retorna el contenido del archivo PDF como un arreglo de bytes
    Document document = new Document();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);

    document.open();


    // Agregar metadatos
    document.addTitle("Auditoria");
    document.addAuthor("Tu Empresa");
    document.addSubject("Generación de remito");
    document.addKeywords("remito, ventas, productos");

    // Establecer márgenes
    document.setMargins(40, 40, 40, 40);

    // Abrir el documento
    document.open();

    // Agregar contenido al documento
    addHeader(document, remito);
    addProductsTable(document, remito);
    addSubProductsTable(document, remito);
    addFooter(document);

    document.close();

    return outputStream.toByteArray();
  }

  private static void addHeader(Document document, Remito remito) throws DocumentException, IOException {
    // Agregar imagen de encabezado
    // Cargar imagen
// Cargar imagen
    Resource imageResource = new ClassPathResource("logo.png");
    InputStream imageStream = imageResource.getInputStream();

// Convertir InputStream a array de bytes
    byte[] imageBytes = IOUtils.toByteArray(imageStream);

// Convertir array de bytes a objeto Image de iText
    Image logo = Image.getInstance(imageBytes);

    logo.setAlignment(Element.ALIGN_CENTER);
    logo.scaleToFit(200, 200);
    document.add(logo);

    // Agregar título
    Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
    Paragraph title = new Paragraph("Auditoria", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(20);
    document.add(title);

    // Crear tabla para los datos de cliente, cuit, lote y fecha
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);

    // Agregar celda de lote y fecha
    Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
    PdfPCell loteCell = new PdfPCell(new Phrase("Lote: " + remito.getLote(), labelFont));
    loteCell.setBorder(Rectangle.NO_BORDER);
    table.addCell(loteCell);

    PdfPCell fechaCell = new PdfPCell(new Phrase("Fecha: " + remito.getFecha()));
    fechaCell.setBorder(Rectangle.NO_BORDER);
    table.addCell(fechaCell);

    // Agregar la tabla al documento
    document.add(table);
  }


  private static void addProductsTable(Document document, Remito remito) throws DocumentException {
    // Crear tabla de productos
    PdfPTable table = new PdfPTable(3);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{5, 15, 10});
    table.setSpacingAfter(20);
    table.setSpacingBefore(20);

    // Establecer encabezados de la tabla
    Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    PdfPCell cell = new PdfPCell();
    cell.setBackgroundColor(BaseColor.GRAY);
    cell.setPadding(5);

    cell.setPhrase(new Phrase("Código", headerFont));
    table.addCell(cell);

    cell.setPhrase(new Phrase("Producto", headerFont));
    table.addCell(cell);

    cell.setPhrase(new Phrase("Total dosificado", headerFont));
    table.addCell(cell);

    // Agregar filas de productos
    Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
      cell = new PdfPCell(new Paragraph("" + remito.getCodigo(), cellFont));
      table.addCell(cell);
      cell = new PdfPCell(new Paragraph(remito.getProducto(), cellFont));
      table.addCell(cell);
      cell = new PdfPCell(new Paragraph(""+remito.getTotalDosificado(), cellFont));
      table.addCell(cell);


    // Agregar tabla al documento
    document.add(table);
  }

  private static void addSubProductsTable(Document document, Remito remito) throws DocumentException {
    // Crear tabla de productos
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{15, 5});
    table.setSpacingAfter(20);

    // Establecer encabezados de la tabla
    Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    PdfPCell cell = new PdfPCell();
    cell.setBackgroundColor(BaseColor.GRAY);
    cell.setPadding(5);

    cell.setPhrase(new Phrase("Subproducto", headerFont));
    table.addCell(cell);

    cell.setPhrase(new Phrase("Cantidad", headerFont));
    table.addCell(cell);


    // Agregar filas de productos
    Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    for (int i = 0; i < remito.getSubproductos().size() ; i++) {
      cell = new PdfPCell(new Paragraph("" + remito.getSubproductos().get(i).getNombre(), cellFont));
      table.addCell(cell);
      cell = new PdfPCell(new Paragraph(""+ remito.getSubproductos().get(i).getCantidad(), cellFont));
      table.addCell(cell);
    }

    // Agregar tabla al documento
    document.add(table);
  }

  private static void addTotalAmount(Document document) throws DocumentException {
    // Agregar total
    Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
    Paragraph totalAmount = new Paragraph("Total: $1000", totalFont);
    totalAmount.setAlignment(Element.ALIGN_RIGHT);
    totalAmount.setSpacingBefore(10);
    document.add(totalAmount);
  }

  private static void addFooter(Document document) throws DocumentException {
    // Agregar pie de página
    Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY);
    Paragraph footer = new Paragraph("------PROCESADO EXITOSAMENTE-----", footerFont);
    footer.setAlignment(Element.ALIGN_CENTER);
    footer.setSpacingBefore(20);
    document.add(footer);
  }

  /**** TRANSFORMAR JSON ******/

  public List<String> transformData(List<Object> dataList) {
    ObjectMapper mapper = new ObjectMapper();
    List<String> transformedJsonList = new ArrayList<>();

    for (Object dataItem : dataList) {
      ObjectNode root = mapper.createObjectNode();
      Map<String, String> data = mapper.convertValue(dataItem, new TypeReference<Map<String, String>>() {});

      // Agregar propiedades al objeto JSON nuevo
      root.put("lote", Integer.parseInt(data.get("LOTE")));
      root.put("fecha", transformFecha(data.get("FECHA")));
      root.put("producto", data.get("PRODUCTO"));
      root.put("codigo", Integer.parseInt(data.get("CODIGO")));
      root.put("totalDosificado", transformTotalDosificado(data.get("TOTAL DOSIFICADO")));
      root.set("subproductos", transformSubproductos(data));

      // Convertir el objeto JSON nuevo a una cadena JSON
      String transformedJson = null;
      try {
        transformedJson = mapper.writeValueAsString(root);
        transformedJsonList.add(transformedJson);
      } catch (JsonProcessingException e) {
        System.out.println("ERROR: " );
        e.printStackTrace();
      }
    }
    return transformedJsonList;
  }

  private static JsonNode transformSubproductos(Map<String, String> data) {
    ArrayNode subproductosNode = JsonNodeFactory.instance.arrayNode();
    for (Map.Entry<String, String> entry : data.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (key.startsWith("4") && !value.isEmpty()) {
        ObjectNode subproductoNode = JsonNodeFactory.instance.objectNode();
        subproductoNode.put("nombre", key);
        String totalDosificadoFormatted = value.replace(",", "");
        try {
          double totalDosificadoValue = Double.parseDouble(totalDosificadoFormatted);
          subproductoNode.put("cantidad", totalDosificadoValue);
          subproductosNode.add(subproductoNode);
        } catch (NumberFormatException e) {
          // Manejar el caso en el que el valor no sea un número válido
          // Puedes ignorar el subproducto o realizar alguna otra acción según tus necesidades
        }
      }
    }
    return subproductosNode;
  }

  private static String transformFecha(String fecha) {
    // Aquí debes implementar la lógica para convertir la fecha en el formato deseado
    return fecha;
  }

  private static double transformTotalDosificado(String totalDosificado) {
    // Aquí debes implementar la lógica para convertir el valor de total dosificado en el formato deseado
    String totalDosificadoFormatted = totalDosificado.replace(",", "");
    double totalDosificadoValue = Double.parseDouble(totalDosificadoFormatted);
    return totalDosificadoValue;
   }

}

