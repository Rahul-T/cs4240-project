import java.io.IOException;

public class Testing {
    public static void main(String[] args) throws IOException {
        String[] testFiles = {
        "Phase1/Testing/testfor.tiger",
        "Phase1/Testing/testfunction.tiger",
        "Phase1/Testing/testif.tiger",
        "Phase1/Testing/testopassoc.tiger",
        "Phase1/Testing/testoppres.tiger",
        "Phase1/Testing/testtypechecking.tiger",
        "Phase1/Testing/testwhile.tiger"
        };

        for (String testFile : testFiles) {
            System.out.println("===========TESTING " + testFile + "============");
            String[] argsCompiler = {testFile, "-no-ir-file"};
            TigerCompiler.main(argsCompiler);
        }
    }
}
