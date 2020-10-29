package controller;

import DTO.LogDTO;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import model.Log;
import utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogController {
    public void writeOnLogFile(LogDTO logDTO, String viewId) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Log log = new Log();
            log.setMemberId(viewId);
            log.setOperation(logDTO.getOperationType().toString() + " - " + logDTO.getOperation());
            ArrayList<Log> logs = findAll();
            if (logs.isEmpty()) {
                log.setId(1L);
            } else {
                log.setId(logs.get(logs.size() - 1).getId() + 1);
            }
            logs.add(log);
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(Paths.get(Constants.LOG_PATH_FILE).toFile(), logs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compareLogs(List<Log> logs1, List<Log> logs2) {
        if (!logs1.isEmpty() && !logs2.isEmpty()) {
            Log lastLog1 = logs1.get(logs1.size() - 1);
            Log lastLog2 = logs2.get(logs1.size() - 1);
            if (lastLog1.getId() > lastLog2.getId()) {
                logs2 = logs1.subList(0, logs1.size() - 1);
            } else {
                logs1 = logs2.subList(0, logs2.size() - 1);
            }
        }
    }

    public ArrayList<Log> findAll() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(Paths.get(Constants.LOG_PATH_FILE))) {
            try {
                return new ArrayList<>(Arrays.asList(mapper.readValue(Paths.get(Constants.LOG_PATH_FILE).toFile(), Log[].class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}
