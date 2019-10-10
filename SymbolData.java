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

    // If it is an array, this represents the size of it. -1 if not an array.
    private int arraySize;

    public SymbolData(String classification, String type, boolean isArray, int arraySize) {
        this.classification = classification;
        this.type = type;
        this.isArray = isArray;
        this.arraySize = arraySize;
    }

    /**
     * Constructor for non arrays. Sets isArray to false and arraySize to -1.
     */
    public SymbolData(String classification, String type) {
        this(classification, type, false, -1);
    }

    public String getClassification() {
        return this.classification;
    }

    public String getType() {
        return this.type;
    }

    public boolean isArray() {
        return this.isArray;
    }

    public int getArraySize() {
        return this.arraySize;
    }

    @Override
    public String toString() {
        if (isArray) {
            return String.format("<classification: %s, type: %s, size: %d>",
                classification, type, arraySize);
        } else {
            return String.format("<classification: %s, type: %s", classification, 
                type);
        }
    }
}
