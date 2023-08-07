package com.processor.services;

import com.processor.pojo.State;

import java.io.IOException;

public interface StateSaverService {
    State loadState() throws IOException;
    void saveState(String lastProcessedMessage, int lastFileNumber) throws IOException;
}
