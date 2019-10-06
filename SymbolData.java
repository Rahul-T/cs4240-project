/*
 * Class to represent the data associated with a symbol
 */
public class SymbolData {
    // func / var.
    private String classification;

    // int / float / null / id / typedef / array.
    private String type;

    // Is this symbol an array?
    private boolean isArray;

    // If it is an array, this represents the size of it.
    private int arraySize;

    public SymbolData(String classification, String type, boolean isArray, int arraySize) {
        this.classification = classification;
        this.type = type;
        this.isArray = isArray;
        this.arraySize = arraySize;
    }
}
