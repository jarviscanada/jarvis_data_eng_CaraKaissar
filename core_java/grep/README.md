# Introduction
The goal of this project is to create a simple grep application in Java that searches for text patterns within files inside a directory and outputs the results to a specified file. This tool will be useful for efficiently searching large codebases or datasets.

The project is divided into two phases:

1. Initial Implementation: Develop the basic grep functionality using standard Java practices.
2. Lambda and Stream Implementation: Re-implement the application using Java 8 Lambda and Stream APIs for a more functional and modern approach.

# Quick Start
## Usage

The Java Grep App is a command-line tool that searches for text patterns within files in a directory and outputs the results to a specified file.

```
USAGE: java -jar grep.jar regex rootPath outFile
- regex: a special text string for describing a search pattern
- rootPath: root directory path
- outFile: output file name
```
## Using Docker 

```
docker run --rm -v `pwd`/data:/data -v `pwd`/out:/out jrvs/grep ".*Romeo.*Juliet.*" /data /out/${outfile}docker run --rm -v `pwd`/data:/data -v `pwd`/out:/out jrvs/grep ".*Romeo.*Juliet.*" /data /out/${outfile}
```


# Implemenation
## Pseudocode

```
Method: process
  1. Initialize an empty list to store matching lines.
  2. Traverse the directory recursively.
  3. For each file, read it line by line.
  4. If a line matches the regex pattern, add it to the list.
  5. Write all matching lines to the output file.
```

```
matchedLines = []
for file in listFilesRecursively(rootDir)
  for line in readLines(file)
      if containsPattern(line)
        matchedLines.add(line)
writeToFile(matchedLines)
```


## Performance Issue
The initial implementation of the grep app could encounter memory issues, especially when processing large datasets. This is because the app reads entire files into memory, which can lead to an OutOfMemoryError, particularly when handling large files or directories.

To handle the memory exception in the regular grep app, we can increase the heap memory allocation by adjusting the JVM options. For example:
```
java -Xms50m -Xmx200m \
-cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.grep.JavaGrepImp \
.*Romeo.*Juliet.* ./data ./out/grep.txt
```

Benefits of Using Lambda Expressions

Implementing the grep app using Lambda expressions and Streams in Java 8 provides a more memory-efficient approach. Streams process data elements as they are received, which avoids loading the entire dataset into memory at once. This on-the-fly processing is particularly advantageous for handling large files, as it reduces memory consumption and improves performance.

# Test
Manual testing was performed using various input files and patterns. Logging was implemented with slf4j-log4j12 to track the application’s behavior during tests.
# Deployment

To simplify deployment, we containerized the application using Docker. Here are the key commands:

Build the Docker image:
```
docker build -t my-grep-app .
```
Run the Docker container:
```
docker run --rm -v $(pwd)/data:/data -v $(pwd)/out:/out my-grep-app <regex> /data /out/output.txt
```
This setup ensures the app runs consistently across different environments and simplifies distribution and scaling.

# Improvement
1. Memory Efficiency: Implement streaming to handle large files more efficiently, reducing memory usage.
2. Parallel Processing: Introduce multithreading to speed up the search across multiple files.
3. Enhanced Regex Support: Expand the app's functionality to support more complex regex operations and error handling.