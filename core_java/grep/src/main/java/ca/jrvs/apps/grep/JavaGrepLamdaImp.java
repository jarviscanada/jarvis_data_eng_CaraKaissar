package ca.jrvs.apps.grep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaGrepLamdaImp extends JavaGrepImp{
    @Override
    public boolean containsPattern(String line) {
        return super.containsPattern(line);
    }

    @Override
    public List<File> listFiles(String rootDir) {

        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error listing files in directory: {}", rootDir, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void process() throws IOException {
        logger.info("Starting the process with regex: '{}', rootPath: '{}', outFile: '{}'", regex, rootPath, outFile);

        List<File> files = listFiles(rootPath);

        List<String> matchedLines = files.stream()
                .flatMap(file -> {
                    logger.debug("Processing file: {}", file.getAbsolutePath());
                    return readLines(file).stream();
                })
                .filter(this::containsPattern)
                .collect(Collectors.toList());

        writeToFile(matchedLines);

        logger.info("Finished processing. Total matched lines: {}", matchedLines.size());
    }

    @Override
    public List<String> readLines(File inputFile) {
        logger.debug("Reading lines from file: {}", inputFile.getAbsolutePath());
        try (Stream<String> lines = Files.lines(inputFile.toPath())) {
            List<String> listLines = lines.collect(Collectors.toList());
            logger.debug("Read {} lines from file: {}", listLines.size(), inputFile.getAbsolutePath());
            return listLines;
        } catch (IOException e) {
            logger.error("Couldn't read file: " + inputFile.getAbsolutePath(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        File outputFile = new File(getOutFile());
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            lines.stream().forEach(line -> {
                try {
                    bw.write(line);
                    bw.newLine();
                } catch (IOException e) {
                    logger.error("Error writing line to file: " + line, e);
                }
            });
        }
    }
}
