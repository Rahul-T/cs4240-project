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

    private String[] paramList;

    public SymbolData(String classification, String type, boolean isArray, 
        int arraySize, String[] paramList) {
        this.classification = classification;
        this.type = type;
        this.isArray = isArray;
        this.arraySize = arraySize;
        this.paramList = paramList;
    }

    /**
     * Constructor for array variables. Sets paramList to null and isArray to
     * true.
     */
    public SymbolData(String classification, String type, 
        int arraySize) {
        this.classification = classification;
        this.type = type;
        this.isArray = true;
        this.arraySize = arraySize;
        this.paramList = null;
    }

    /**
     * Constructor for non-array variables. Sets isArray to false and arraySize to -1.
     * Sets paramList to none.
     */
    public SymbolData(String classification, String type) {
        this(classification, type, false, -1, null);
    }

    /**
     * Constructor for function names. Sets isArray to false and arraySize to -1.
     */
    public SymbolData(String classification, String type, String[] paramList) {
        this(classification, type, false, -1, paramList);
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

    public String[] getParamList() {
        return this.paramList;
    }

    @Override
    public String toString() {
        String params = String.join(", ", this.paramList);

        return String.format("<classification: %s | type: %s | size: %d | param_list: %s | isArray = %s>",
              classification, type, arraySize, params, this.isArray);
//        if (isArray) {
//            return String.format("<classification: %s, type: %s, size: %d>",
//                classification, type, arraySize);
//        } else {
//            return String.format("<classification: %s, type: %s", classification,
//                type);
//        }
    }
}
