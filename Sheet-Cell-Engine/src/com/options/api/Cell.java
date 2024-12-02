package com.options.api;

class Cell {
    private final String location;
    private Expression value;
    private String content;
    public Cell(String location, String value) {
        this.location = location;
    }

    public String getLocation() {return location;}
    public Object getValue() {return value;}
    private Expression stringToExpression(String value) {
        Expression res;
        if(value == null || value.isEmpty()) {
            content = "";
            return null;
        }
        if(value.startsWith("{") ) {
            if (!value.endsWith("}")) {
                throw new IllegalArgumentException("Illegal expression format");
            }

        }

    }
}
