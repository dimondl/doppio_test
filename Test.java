import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class Test {

    static String code = "public class Sample {\n" +
            "\n" +
            "    public static void main(String args[]) {\n" +
            "        System.out.println(\"Hello world\");\n" +
            "    }\n" +
            "\n" +
            "    public void Func() {\n" +
            "        int d = 5;\n" +
            "    }\n" +
            "}";
    public static void main(String args[]) throws IOException{

        StringBuilder sb = new StringBuilder();
        sb.append(code);
        /*for (String content : Files.readAllLines(FileSystems.getDefault().getPath("Sample.java"))) {
            sb.append(content);
            sb.append(System.lineSeparator());
        }*/

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(sb.toString().toCharArray());
        //parser.setSource("/*abc*/".toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        //ASTNode node = parser.createAST(null);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        String result = cu.toString();

        System.out.println("Original code " + result);


        cu.accept(new ASTVisitor() {

            Set names = new HashSet();

            public boolean visit(VariableDeclarationFragment node) {
                SimpleName name = node.getName();
                this.names.add(name.getIdentifier());
                System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(name.getStartPosition()));
                return false; // do not continue to avoid usage info
            }

            public boolean visit(SimpleName node) {
                if (this.names.contains(node.getIdentifier())) {
                    System.out.println("Usage of '" + node + "' at line " +	cu.getLineNumber(node.getStartPosition()));
                }
                return true;
            }

        });
    }
}