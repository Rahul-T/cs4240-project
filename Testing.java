import java.io.IOException;

public class Testing {
    public static void main(String[] args) throws IOException {
        String[] testFiles = {
        "Testing/testfor.tiger",
        "Testing/testfunction.tiger",
        "Testing/testif.tiger",
        "Testing/testopassoc.tiger",
        "Testing/testoppres.tiger",
        "Testing/testtypechecking.tiger",
        "Testing/testwhile.tiger"
        };

        for (String testFile : testFiles) {
            System.out.println("===========TESTING " + testFile + "============");
            String[] argsCompiler = {testFile, "-no-ir-file"};
            TigerCompiler.main(argsCompiler);
        }
    }
}
