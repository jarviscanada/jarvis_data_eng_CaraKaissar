package ca.jrvs.apps.practice;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStreamExcImp implements LambdaStreamExc {
    @Override
    public Stream<String> createStrStream(String... strings) {
        return Stream.of(strings);
    }

    @Override
    public Stream<String> toUpperCase(String... strings) {
        return createStrStream(strings).map(String::toUpperCase);
    }

    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {
        return stringStream.filter(string -> string.contains(pattern));
    }

    @Override
    public IntStream createIntStream(int[] arr) {
        return IntStream.of(arr);
    }

    @Override
    public <E> List<E> toList(Stream<E> stream) {
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<Integer> toList(IntStream intStream) {
        return intStream.boxed().collect(Collectors.toList());
    }

    @Override
    public IntStream createIntStream(int start, int end) {
        return IntStream.rangeClosed(start, end);
    }

    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {
        return intStream.mapToDouble(Math :: sqrt);
    }

    @Override
    public IntStream getOdd(IntStream intStream) {
        return intStream.filter(num -> num % 2 == 1);
    }

    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {
        return m -> System.out.println(prefix + m +suffix);
    }

    @Override
    public void printMessages(String[] messages, Consumer<String> printer) {
    createStrStream(messages).forEach(printer);
    }

    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {
        getOdd(intStream).forEach(num -> printer.accept(Integer.toString(num)));

    }

    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
        return ints.flatMap(List::stream).map(num -> num * num);
    }

    public static void main(String[] args) {
        LambdaStreamExcImp lse = new LambdaStreamExcImp();
        // Test createStrStream
        System.out.println("Testing createStrStream:");
        Stream<String> strStream = lse.createStrStream("hello", "world");
        strStream.forEach(System.out::println);

        // Test toUpperCase
        System.out.println("\nTesting toUpperCase:");
        Stream<String> upperStrStream = lse.toUpperCase("hello", "world");
        upperStrStream.forEach(System.out::println);

        // Test filter
        System.out.println("\nTesting filter:");
        lse.filter(lse.createStrStream("hello", "world", "java"), "o").forEach(System.out::println);

        // Test createIntStream (array)
        System.out.println("\nTesting createIntStream (array):");
        lse.createIntStream(new int[]{1, 2, 3, 4, 5}).forEach(System.out::println);

        // Test toList (Stream)
        System.out.println("\nTesting toList (Stream):");
        List<String> stringList = lse.toList(lse.createStrStream("hello", "world", "java"));
        stringList.forEach(System.out::println);

        // Test toList (IntStream)
        System.out.println("\nTesting toList (IntStream):");
        List<Integer> intList = lse.toList(lse.createIntStream(new int[]{1, 2, 3, 4, 5}));
        intList.forEach(System.out::println);

        // Test createIntStream (range)
        System.out.println("\nTesting createIntStream (range):");
        lse.createIntStream(1, 10).forEach(System.out::println);

        // Test squareRootIntStream
        System.out.println("\nTesting squareRootIntStream:");
        lse.squareRootIntStream(lse.createIntStream(1, 5)).forEach(System.out::println);

        // Test getOdd
        System.out.println("\nTesting getOdd:");
        lse.getOdd(lse.createIntStream(1, 10)).forEach(System.out::println);

        // Test getLambdaPrinter and printMessages
        System.out.println("\nTesting getLambdaPrinter and printMessages:");
        Consumer<String> printer = lse.getLambdaPrinter("start>", "<end");
        lse.printMessages(new String[]{"Message1", "Message2"}, printer);

        // Test printOdd
        System.out.println("\nTesting printOdd:");
        lse.printOdd(lse.createIntStream(1, 10), printer);

        // Test flatNestedInt
        System.out.println("\nTesting flatNestedInt:");
        Stream<List<Integer>> nestedIntStream = Stream.of(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8, 9)
        );
        lse.flatNestedInt(nestedIntStream).forEach(System.out::println);
    }


}
