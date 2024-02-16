package com.p3.content;

import com.p3.content.export.ExportProcessor;
import com.p3.content.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);


    public static void main(String[] args) throws FileNotFoundException {
        args= new String[5];
        args[0] = "FLAT_FILE"; //FLAT_FILE or EXPORT
        args[1] = "INPUT_PATH"; //
        args[2] = "OUTPUT_PATH";
        args[3] = "OVERLAY_PATH";
        args[4] = "CONFIG_FILE";
        String outputPath ="/home/suriaravind/Documents/GameOfCodes/Output";
        //String filePath = "/home/suriaravind/Documents/GameOfCodes/IMPSC310.IM32.txt";
        String filePath = "/home/suriaravind/Documents/GameOfCodes/templateInputData.json";
        String overlayPath = "/home/suriaravind/Documents/GameOfCodes/archon-content-management-connector/data/export/LoanReconciliationTemplate.pdf";
        String configFile = "/home/suriaravind/Documents/GameOfCodes/archon-content-management-connector/data/export/LoanReconciliationtemplateConfig.json";
       // Processor processor = new Processor(filePath,outputPath);
        //processor.start();
        ExportProcessor exportProcessor = new ExportProcessor();
        exportProcessor.startExport(outputPath,filePath,overlayPath,configFile);
    }
}