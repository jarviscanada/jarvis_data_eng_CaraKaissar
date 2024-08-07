package ca.jrvs.apps.grep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;
    private Pattern pattern;

    public static void main(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE : JavaGrep regex rootPath outFile");
        }

        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (PatternSyntaxException e) {
            javaGrepImp.logger.error("Invalid regex pattern: {}", args[0], e);
        } catch (Exception ex) {
            javaGrepImp.logger.error("Oops, something went wrong during processing!", ex);
        }
    }

    @Override
    public boolean containsPattern(String line) {
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        logger.debug("Matching line: '{}', Result: {}", line, result);
        return result;
    }

    @Override
    public String getOutFile() {
        logger.debug("Getting output file: {}", outFile);
        return this.outFile;
    }

    @Override
    public String getRegex() {
        logger.debug("Getting regex: {}", regex);
        return this.regex;
    }

    @Override
    public String getRootPath() {
        logger.debug("Getting root path: {}", rootPath);
        return this.rootPath;
    }

    @Override
    public List<File> listFiles(String rootDir) {
        logger.debug("Listing files in directory: {}", rootDir);
        List<File> listFiles = new ArrayList<>();
        File file = new File(rootDir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File x : files) {
                    if (x.isFile()) {
                        listFiles.add(x);
                        logger.debug("Found file: {}", x.getAbsolutePath());
                    } else {
                        logger.debug("Found directory: {}, diving in...", x.getAbsolutePath());
                        listFiles.addAll(listFiles(x.getAbsolutePath()));
                    }
                }
            }
        }
        logger.debug("Total files found: {}", listFiles.size());
        return listFiles;
    }

    @Override
    public void process() throws IOException {
        logger.info("Starting the process with regex: '{}', rootPath: '{}', outFile: '{}'", regex, rootPath, outFile);
        List<File> files = listFiles(rootPath);
        List<String> matchedLines = new ArrayList<>();
        for (File file : files) {
            logger.debug("Processing file: {}", file.getAbsolutePath());
            List<String> lines = readLines(file);
            for (String line : lines) {
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }
        writeToFile(matchedLines);
        logger.info("Finished processing. Total matched lines: {}", matchedLines.size());
    }

    @Override
    public List<String> readLines(File inputFile) {
        logger.debug("Reading lines from file: {}", inputFile.getAbsolutePath());
        List<String> listLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                listLines.add(line);
            }
        } catch (IOException e) {
            logger.error("Couldn't read file: " + inputFile.getAbsolutePath(), e);
        }
        logger.debug("Read {} lines from file: {}", listLines.size(), inputFile.getAbsolutePath());
        return listLines;
    }

    @Override
    public void setOutFile(String outFile) {
        logger.debug("Setting output file to: {}", outFile);
        this.outFile = outFile;
    }

    @Override
    public void setRegex(String regex) {
        logger.debug("Setting regex to: {}", regex);
        this.regex = regex;
        try {
            this.pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            logger.error("Invalid regex pattern: {}", regex, e);
            throw e;
        }
    }

    @Override
    public void setRootPath(String rootPath) {
        logger.debug("Setting root path to: {}", rootPath);
        this.rootPath = rootPath;
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        File outputFile = new File(outFile);
        // Check if the file exists; if not, create it
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        }
        logger.debug("Writing {} lines to output file: {}", lines.size(), outFile);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        logger.info("All lines written to file: {}", outFile);
    }

}
