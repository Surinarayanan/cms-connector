package com.p3.content.processor;

import com.p3.content.bean.CMODTableType;
import com.p3.content.bean.ColumnInfo;
import com.p3.content.bean.ConfigBean;
import com.p3.content.bean.WriterBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.p3.content.constants.CmodConstants.STAR_CONSTANTS;

/**
 * @Author : Suri Aravind @Creation Date : 16/02/24
 */
public class Processor {
  public static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
  int MAX_BRANCH_COUNT = 368;
  private String filePath;
  private String outputPath;

  public Processor(String filePath, String outputPath) {
    this.filePath = filePath;
    this.outputPath = outputPath;
  }

  public void start() throws FileNotFoundException {
    Map<CMODTableType, WriterBean> writerBeansMap = new LinkedHashMap<>();
    Map<CMODTableType, ConfigBean> tableMappingDetails = Arrays.stream(CMODTableType.values())
            .collect(Collectors.toMap(key->key, value->value.getConfigBean()));
    createWriterBeanList(tableMappingDetails, writerBeansMap);
    Map<String, List<String>> branchDetailsMap = new LinkedHashMap<>();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
      List<String> branchDetails = new ArrayList<>();
      String line;
      int count = 0;
      int skipLine = 0;
      while ((line = bufferedReader.readLine()) != null) {
        if (skipLine > 0) {
          skipLine--;
          continue;
        }
        if (line.contains(STAR_CONSTANTS)) {
          skipLine = 14;
          branchDetails.clear();
          continue;
        }
        branchDetails.add(line);
        count++;
        if (count % MAX_BRANCH_COUNT == 0) {
          processBankDetailsIntoTables(branchDetails, tableMappingDetails, writerBeansMap);
          branchDetailsMap.put(UUID.randomUUID().toString(), branchDetails);
          branchDetails.clear();
          count = 0;
        }
      }
      if (!branchDetails.isEmpty()) {
        processBankDetailsIntoTables(branchDetails, tableMappingDetails, writerBeansMap);
        branchDetailsMap.put(UUID.randomUUID().toString(), branchDetails);
        branchDetails.clear();
        count = 0;
      }
      branchDetailsMap.put(UUID.randomUUID().toString(), branchDetails);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    closeWriterBeanList(writerBeansMap.values());
  }

  private void closeWriterBeanList(Collection<WriterBean> writerBeans) {
    writerBeans.forEach(
        writer -> {
          writer.getPrintWriter().flush();
          writer.getPrintWriter().close();
        });
  }

  private void createWriterBeanList(
      Map<CMODTableType, ConfigBean> tableMappingDetails,
      Map<CMODTableType, WriterBean> writerBeansMap)
      throws FileNotFoundException {
    for (Map.Entry<CMODTableType, ConfigBean> cmodTableTypeConfigBeanEntry :
        tableMappingDetails.entrySet()) {
      String path =
          outputPath + File.separator + cmodTableTypeConfigBeanEntry.getKey().toString() + ".csv";
      PrintWriter printWriter = new PrintWriter(path);
      List<String> tableColumnList = new ArrayList<>();
      if (!cmodTableTypeConfigBeanEntry.getKey().equals(CMODTableType.MASTER_INFO)) {
        tableColumnList.addAll(
            CMODTableType.MASTER_INFO.getColumnList().stream()
                .filter(ColumnInfo::isPrimary)
                .map(ColumnInfo::getName)
                .toList());
      }
      tableColumnList.addAll(
          cmodTableTypeConfigBeanEntry.getKey().getColumnList().stream()
              .map(ColumnInfo::getName)
              .toList());
      printWriter.append(String.join(",", tableColumnList));
      printWriter.append("\n");
      WriterBean writerBean =
          WriterBean.builder()
              .name(cmodTableTypeConfigBeanEntry.getKey())
              .path(path)
              .printWriter(printWriter)
              .build();
      writerBeansMap.put(cmodTableTypeConfigBeanEntry.getKey(), writerBean);
    }
  }

  private void processBankDetailsIntoTables(
      List<String> branchDetails,
      Map<CMODTableType, ConfigBean> tableMappingDetails,
      Map<CMODTableType, WriterBean> writerBeansMap) {
    if (!branchDetails.get(0).contains("DEMAND DEPOSIT ACCOUNTING")) {
      return;
    }
    String masterKeyRecord = "";
    // System.out.println(branchDetails.get(0));
    // System.out.println(branchDetails.get(1));
    // System.out.println(branchDetails.get(2));
    for (Map.Entry<CMODTableType, ConfigBean> beanEntry : tableMappingDetails.entrySet()) {
      ConfigBean value = beanEntry.getValue();
      List<String> tableData = branchDetails.subList(value.getStartPoint(), value.getEndPoint());
      CMODTableType entryKey = beanEntry.getKey();
      switch (entryKey) {
        case MASTER_INFO -> {
          masterKeyRecord = processMASTER_INFO(tableData, entryKey, writerBeansMap);
        }
        case LOAN_RECONCILIATION -> {
          processLOAN_RECONCILIATION(tableData, writerBeansMap, masterKeyRecord, entryKey);
        }
        case SAVINGS_RECONCILIATION -> {
          processSAVINGS_RECONCILIATION(tableData, writerBeansMap, masterKeyRecord, entryKey);
        }
      }
    }
  }

  private String processMASTER_INFO(
      List<String> tableData,
      CMODTableType cmodTableType,
      Map<CMODTableType, WriterBean> writerBeansMap) {
    List<String> mainTableRecord = new ArrayList<>();
    List<String> mainTableKeyRecord = new ArrayList<>();
    for (ColumnInfo columnInfo : cmodTableType.getColumnList()) {
      String substringData =
          tableData
              .get(columnInfo.getLineNo() - 1)
              .substring(
                  columnInfo.getPosition(), columnInfo.getPosition() + columnInfo.getLength());
      mainTableRecord.add(substringData);
      if (columnInfo.isPrimary()) {
        mainTableKeyRecord.add(substringData);
      }
    }
    String recordList = String.join(",", mainTableRecord);
    writerBeansMap.get(cmodTableType).getPrintWriter().append(recordList).append("\n");
    return String.join(",", mainTableKeyRecord);
  }

  private void processLOAN_RECONCILIATION(
      List<String> tableData,
      Map<CMODTableType, WriterBean> writerBeansMap,
      String masterKeyRecord,
      CMODTableType cmodTableType) {
    if (!tableData.get(0).contains("ENTERED")) {
      return;
    }
    for (int i = 0; i < tableData.size(); i++) {
      List<String> arrayList = new ArrayList<>();
      for (ColumnInfo columnInfo : cmodTableType.getColumnList()) {
        arrayList.add(
            tableData
                .get(i)
                .substring(
                    columnInfo.getPosition(), columnInfo.getPosition() + columnInfo.getLength()));
      }
      String recordList = String.join(",", arrayList);
      writerBeansMap
          .get(cmodTableType)
          .getPrintWriter()
          .append(masterKeyRecord)
          .append(",")
          .append(recordList)
          .append("\n");
    }
  }

  private void processSAVINGS_RECONCILIATION(
      List<String> tableData,
      Map<CMODTableType, WriterBean> writerBeansMap,
      String masterKeyRecord,
      CMODTableType cmodTableType) {
    if (!tableData.get(0).contains("ENTERED")) {
      return;
    }
    for (int i = 0; i < tableData.size(); i++) {
      List<String> arrayList = new ArrayList<>();
      for (ColumnInfo columnInfo : cmodTableType.getColumnList()) {
        arrayList.add(
            tableData
                .get(i)
                .substring(
                    columnInfo.getPosition(), columnInfo.getPosition() + columnInfo.getLength()));
      }
      String recordList = String.join(",", arrayList);
      writerBeansMap
          .get(cmodTableType)
          .getPrintWriter()
          .append(masterKeyRecord)
          .append(",")
          .append(recordList)
          .append("\n");
    }
  }
}
