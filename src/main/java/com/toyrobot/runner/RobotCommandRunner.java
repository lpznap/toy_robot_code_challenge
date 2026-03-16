package com.toyrobot.runner;

import com.toyrobot.service.RobotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class RobotCommandRunner implements CommandLineRunner {

    private final RobotService robotService;

    @Override
    public void run(String... args) throws Exception {
        log.debug("RobotCommandRunner started");

        String filePath = resolveInputFilePath(args);

        if (filePath != null) {
            runFromFile(filePath);
        } else {
            runFromStdin();
        }

        log.debug("RobotCommandRunner finished");
    }

    // -------------------------------------------------------------------------
    // Input routing
    // -------------------------------------------------------------------------
    private String resolveInputFilePath(String[] args) {
        if (args != null && args.length > 0 && !args[0].isBlank()) {
            log.info("Input source: CLI argument -> {}", args[0].trim());
            return args[0].trim();
        }

        String sysProp = System.getProperty("input.file");
        if (sysProp != null && !sysProp.isBlank()) {
            log.info("Input source: system property input.file -> {}", sysProp.trim());
            return sysProp.trim();
        }

        log.info("Input source: standard input (stdin)");
        return null;
    }


    private void runFromFile(String filePath) throws Exception {
        Path path = Path.of(filePath);

        if (!Files.exists(path) || !Files.isReadable(path)) {
            log.error("Cannot read input file: {}", filePath);
            return;
        }

        log.info("Processing commands from file: {}", filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            processLines(reader);
        }

        log.info("Finished processing file: {}", filePath);
    }

    private void runFromStdin() throws Exception {
        log.info("Starting interactive stdin session");
        log.info("Toy Robot Simulator — enter commands (Ctrl+D / Ctrl+Z to quit)");
        log.info("  Commands: PLACE X,Y,F | MOVE | LEFT | RIGHT | REPORT");
        log.info("-------------------------------");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            processLines(reader);
        }

        log.info("Stdin session ended");
    }

    // -------------------------------------------------------------------------
    // Core processing loop
    // -------------------------------------------------------------------------

    private void processLines(BufferedReader reader) throws Exception {
        int lineNumber = 0;
        int commandCount = 0;

        String line;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            log.trace("Line {}: [{}]", lineNumber, line);

            String result = robotService.execute(line);

            if (result != null) {
                commandCount++;
                log.info("Output: {}", result);
            }
        }

        log.info("Processing complete — {} line(s) read, {} REPORT output(s) produced", lineNumber, commandCount);
    }
}
