package com.agnelle.backend.entity;

import java.util.*;

public class CodeGenerator{
    private static Set<Integer> sortedNumbers = new HashSet<>();
    private static final int NUMBERS_LENGTH = 6;
    private static final int MINIMUM = 0;
    private static final int MAXIMUM = 9;

    public static List<Integer> generateCode() {
        Random random = new Random();
        Set<Integer> newNumbers = new HashSet<>();
        while (newNumbers.size() < NUMBERS_LENGTH) {
            int number = random.nextInt(MAXIMUM - MINIMUM + 1) + MINIMUM;
            newNumbers.add(number);
        }
        sortedNumbers.addAll(newNumbers);

        List<Integer> codeNumbers = new ArrayList<>(newNumbers);
        Collections.sort(codeNumbers);

        return codeNumbers;
    }
}
