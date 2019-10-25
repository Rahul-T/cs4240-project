import java.io.IOException;

public class Testing {
    public static void main(String[] args) throws IOException {
        final String testDir = "Testing/";
        String fName;
        int testCount = 0;
        String[] testFiles = {
        "factorial.tiger",
        "testfor.tiger",
        "testfunction.tiger",
        "testif.tiger",
        "testopassoc.tiger",
        "testoppres.tiger",
        "testwhile.tiger",
        // "testtypechecking.tiger",
        "functionInFunction.tiger",
        "nestedForWhile.tiger",
        "typeDefAmbiguity.tiger",
        "testBreakCorrect.tiger",
        // "testBreakError.tiger",
        "testArrayOps.tiger",
        // "testLetScoping.tiger"
        };

        for (String testFile : testFiles) {
            fName = testDir + testFile;
            System.out.println("===========TESTING " + fName + "============");
            String[] argsCompiler = {fName, "-no-ir-file"};
            TigerCompiler.main(argsCompiler);
            testCount++;
        }

        System.out.println("\nTesting Finished! " + testCount + " tests run.");
    }
}
