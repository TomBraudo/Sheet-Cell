package com.options.api;

//Test main for engine options
public class Main {
    public static void main(String[] args) {
        Cell cell = new Cell("A1", "{MINUS,2,3}");
        Cell cell2 = new Cell("A2", "nignog");
        System.out.println(cell2.getValue());
    }
}
