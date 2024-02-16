package com.p3.content.export;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.p3.content.export.bean.CoortinateType;
import com.p3.content.export.bean.StructureBean;
import com.p3.content.export.bean.TemplateConfigBean;
import com.p3.content.export.handler.PdfExportHandler;
import com.p3.content.utils.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author : Suri Aravind @Creation Date : 16/02/24
 */
public class ExportProcessor {

  public void startExport(
      String outputPath, String filePath, String overlayPath, String configFile) {
    Type fooType = new TypeToken<TemplateConfigBean>() {}.getType();
    TemplateConfigBean templateConfigBean = Utility.mapUtils(Utility.readAll(configFile), fooType);

    fooType = new TypeToken<List<Map<String, Object>>>() {}.getType();
    List<Map<String, Object>> inputMap = Utility.mapUtils(Utility.readAll(filePath), fooType);
    PdfDocument pdfFoot = null;
    try (PdfReader pdfReader = new PdfReader(overlayPath)) {
      String outputFilePath = outputPath + File.separator + UUID.randomUUID().toString() + ".pdf";
      PdfWriter pdfWriter = new PdfWriter(outputFilePath);
      pdfFoot = new PdfDocument(pdfWriter);
      IEventHandler handler = new PdfExportHandler(pdfFoot, pdfReader);
      pdfFoot.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
      Document output = new Document(pdfFoot, PageSize.A4);
      Map<String, Object> map = inputMap.get(0);
      setNormal(templateConfigBean.getFields(), output, map, 1);
      Map<String, StructureBean> columnFields =
          templateConfigBean.getFields().stream()
              .filter(structureBean -> structureBean.getType().equals(CoortinateType.TABLE_COLUMN))
              .collect(Collectors.toMap(StructureBean::getName, value -> value));
      int pageNumber = 1;
      int overlay_count = 1;
      int y = 0;
      int pageLastCoordinates = templateConfigBean.getTable_coordinates().getY();
      for (int i = 0; i < inputMap.size(); i++) {
        Map<String, Object> objectMap = inputMap.get(i);
        for (Map.Entry<String, StructureBean> beanEntry : columnFields.entrySet()) {
          String columnValue = objectMap.getOrDefault(beanEntry.getKey(),"").toString();
          Text author = new Text(columnValue);
          Paragraph p = new Paragraph().setFontSize(8).add(author);
          y = beanEntry.getValue().getCoordinates().getY() - (overlay_count * 20);
          if ((y > pageLastCoordinates)) {
            p.setFixedPosition(pageNumber, beanEntry.getValue().getCoordinates().getX(), y, 200);
            output.add(p);
          } else {
            pageNumber++;
            addExtraPage(overlayPath, pdfFoot, templateConfigBean, output, map, pageNumber);
            overlay_count = 0;
            y = beanEntry.getValue().getCoordinates().getY() - (overlay_count * 20);
            p.setFixedPosition(pageNumber, beanEntry.getValue().getCoordinates().getX(), y, 200);
            output.add(p);
          }
        }
        if((i+1) % templateConfigBean.getTotalLines() ==0 && (inputMap.size() <i+1)){
          pageNumber++;
          addExtraPage(overlayPath, pdfFoot, templateConfigBean, output, map, pageNumber);
          overlay_count = 0;
        }
        overlay_count++;
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (Objects.nonNull(pdfFoot)) {
        pdfFoot.close();
      }
    }
  }

  private static void addExtraPage(String overlayPath, PdfDocument pdfFoot, TemplateConfigBean templateConfigBean, Document output, Map<String, Object> map, int pageNumber) throws IOException {
    PdfDocument template = new PdfDocument(new PdfReader(overlayPath));
    PdfPage page = template.getFirstPage();
    page.copyTo(pdfFoot);
    setNormal(templateConfigBean.getFields(), output, map, pageNumber);
    template.close();
  }

  private static void setNormal(
      List<StructureBean> structureBeanList,
      Document output,
      Map<String, Object> map,
      int pageNumber) {
    structureBeanList.stream()
        .filter(
            t -> {
              if (t.getType().equals(CoortinateType.NORMAL)) return true;
              else return false;
            })
        .forEach(
            bean -> {
              Text author = new Text(map.get(bean.getName()).toString());
              Paragraph p = new Paragraph().setFontSize(8).add(author);
              p.setFixedPosition(
                  pageNumber, bean.getCoordinates().getX(), bean.getCoordinates().getY(), 200);
              output.add(p);
            });
  }

  /*private static void setTable(
      TemplateConfigBean bean,
      List<StructureBean> structureBeanList,
      Document output,
      Map<String, Object> map,
      PdfDocument pdfFoot) {
    Gson gson = new Gson();

    List<StructureBean> structureBeanListTable = structureBeanList;
    int pageLastCoordinates = bean.getTable_coordinates().getY();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    String jsonArray = map.get(bean.getName()).toString();
    JsonArray jsonArrayList = gson.fromJson(jsonArray, JsonArray.class);
    int count = 0;
    int pageNumber = 1;
    for (int i = 0; i < jsonArrayList.size(); i++) {
      JsonElement objects = jsonArrayList.get(i);
      Map<String, String> stringMap = gson.fromJson(objects.getAsJsonObject(), type);
      for (Map.Entry<String, String> maps : stringMap.entrySet()) {
        List<StructureBean> list =
            structureBeanListTable.stream()
                .filter(
                    structureBean -> {
                      if (structureBean.getName().equalsIgnoreCase(maps.getKey())) {
                        return true;
                      }
                      return false;
                    })
                .collect(Collectors.toList());
        Text author = new Text(maps.getValue());
        Paragraph p = new Paragraph().setFontSize(8).add(author);
        int y = list.get(0).getCoordinates().getY() - (count * 20);
        if (y > pageLastCoordinates) {
          p.setFixedPosition(pageNumber, list.get(0).getCoordinates().getX(), y, 200);
          output.add(p);
        } else {
          pageNumber++;
          PdfDocument template = null;
          try {
            template = new PdfDocument(new PdfReader("Loan Reconciliation Template.pdf"));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          PdfPage page = template.getFirstPage();
          page.copyTo(pdfFoot);
          setNormal(structureBeanList, output, map, pageNumber);
          template.close();
          count = 0;
          y = list.get(0).getCoordinates().getY() - (count * 20);
          p.setFixedPosition(pageNumber, list.get(0).getCoordinates().getX(), y, 200);
          output.add(p);
        }
      }
      count++;
    }
  }*/
}
