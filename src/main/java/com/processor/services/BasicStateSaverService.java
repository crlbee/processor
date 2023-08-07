package com.processor.services;

import com.processor.pojo.State;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class BasicStateSaverService implements StateSaverService{

    private final String configFileName;

    public BasicStateSaverService(@Value("${config.path}") String configFileName) {
        this.configFileName = configFileName;
    }

    public void saveState(String lastProcessedMessage, int lastFileNumber) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFileName))) {
            writer.write(lastProcessedMessage + "\n" + lastFileNumber);
        }
    }

    public State loadState() throws IOException {
        File configFile = new File(configFileName);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                String lastProcessedMessage = reader.readLine();
                if (lastProcessedMessage != null && !lastProcessedMessage.isBlank()) {
                    int lastFileNumber = Integer.parseInt(reader.readLine());
                    return new State(lastProcessedMessage, lastFileNumber);
                }
            }
        }
        return new State("", 1);
    }

}
