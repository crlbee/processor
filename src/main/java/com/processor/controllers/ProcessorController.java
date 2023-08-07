package com.processor.controllers;

import com.processor.pojo.State;
import com.processor.services.RecordProcessorService;
import com.processor.services.StateSaverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ProcessorController {

    private final StateSaverService stateSaver;
    private final RecordProcessorService recordProcessor;

    public ProcessorController(StateSaverService stateSaverService,
                               RecordProcessorService recordProcessor1) {
        this.stateSaver = stateSaverService;
        this.recordProcessor = recordProcessor1;
    }

    @GetMapping("/start")
    public ResponseEntity<String> start() throws IOException {
        State state = stateSaver.loadState();
        recordProcessor.processAllFiles();

        if (state != null) {
            stateSaver.saveState(state.lastProcessedMessage(), state.lastFileNumber());
        }
        return ResponseEntity.ok("ok");
    }


}
