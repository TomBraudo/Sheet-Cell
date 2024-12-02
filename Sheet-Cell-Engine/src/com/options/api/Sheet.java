package com.options.api;
import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.*;

class Sheet {
    Cell[][] sheet;
    String sheetName;
    int rows;
    int columns;
    int columnsWidthUnits;
    int rowsHeightUnits;
    public Sheet(String filePath) {

    }

    public Cell[][] getSheet() {
        return sheet;
    }

    public void setCell(int row, int col, Expression value) {
        sheet[row-1][col-1].setValue(value);
    }

    private void createSheet(String filePath) {
        try{
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(file);

            Element root = doc.getDocumentElement();
            sheetName = root.getAttribute("name");

            NodeList layoutList = doc.getElementsByTagName("STL-Layout");
            if (layoutList.getLength() > 0) {
                Element layout = (Element) layoutList.item(0);
                rows = Integer.parseInt(layout.getAttribute("rows"));
                columns = Integer.parseInt(layout.getAttribute("columns"));
                Element size = (Element) layout.getElementsByTagName("STL-Size").item(0);
                rowsHeightUnits = Integer.parseInt(size.getAttribute("column-width-units"));
                columnsWidthUnits = Integer.parseInt(size.getAttribute("rows-height-units"));
            }

            sheet = new Cell[rows][columns];

            NodeList cellList = doc.getElementsByTagName("STL-Cell");
            for (int i = 0; i < cellList.getLength(); i++) {
                Element cell = (Element) cellList.item(i);
                int row = Integer.parseInt(cell.getAttribute("row"));
                int col = Integer.parseInt(cell.getAttribute("col"));

            }
        }


    }

}
