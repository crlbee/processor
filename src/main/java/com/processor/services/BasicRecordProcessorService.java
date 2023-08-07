package com.processor.services;

import com.processor.pojo.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

@Service
public class BasicRecordProcessorService implements RecordProcessorService {

    private final String inputFilePath;
    private final String outputFolder;

    @Autowired
    public BasicRecordProcessorService(@Value("${input.folder}") String inputFolderPath,
                                       @Value("${output.folder}") String outputFolder) {
        this.inputFilePath = inputFolderPath;
        this.outputFolder = outputFolder;
    }

    public void processAllFiles() throws IOException {
        File inputFolder = new File(inputFilePath);
        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            throw new RuntimeException("не найдена папка");
        }

        File[] inputFiles = inputFolder.listFiles();
        if (inputFiles != null) {
            Arrays.sort(inputFiles, Comparator.comparingLong(File::lastModified));
            for (File inputFile : inputFiles) {
                String filename = inputFile.getName().toString();
                if (inputFile.isFile() && filename.charAt(filename.indexOf('.') - 3) == '-') {
                    processRecords(inputFile.toString());
                }
            }
        }
    }

    private void processRecords(String inputFilePath) throws IOException {
        int recordCount = 0;
        int currentFileNumber = 1;
        BufferedWriter outputWriter = null;
        String currentFileName = null;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (recordCount % 100 == 0) {
                    if (outputWriter != null) {
                        outputWriter.close();
                    }
                    FileInfo fileInfo = getFileInfoFromName(inputFilePath);
                    currentFileName = String.format("%s/%s-%s-%04d.log", outputFolder, fileInfo.name(), fileInfo.date(), currentFileNumber++);
                    outputWriter = new BufferedWriter(new FileWriter(currentFileName));
                }
                outputWriter.write(line + "\n");
                recordCount++;
            }
        } finally {
            if (outputWriter != null) {
                outputWriter.close();
            }
        }
    }

    private FileInfo getFileInfoFromName(String fileName) {
        Path path = Paths.get(fileName);
        String nameWithoutExtension = path.getFileName().toString();
        int dotIndex = nameWithoutExtension.lastIndexOf('.');
        String name = nameWithoutExtension.substring(0, dotIndex);
        String date = path.getParent().getFileName().toString();
        return new FileInfo(name, date);
    }
}

