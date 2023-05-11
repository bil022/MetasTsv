import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.json.*;

public class Main {
    public static void main(String [] args) throws Exception
    {
        // ANTLRInputStream antlrInputStream = new ANTLRInputStream("hello world");
        System.out.println("Path: " + Paths.get("").toAbsolutePath().toString());

        CharStream antlrInputStream = CharStreams.fromFileName("src/igm.tsv");
        String input=antlrInputStream.toString()+"\n";
        tsvLexer lexer = new tsvLexer(CharStreams.fromString(input));

        CommonTokenStream tokens = new CommonTokenStream( lexer );
        tsvParser parser = new tsvParser( tokens );
        ParseTree tree = parser.tsvFile();

        Tsv2Json visitor = new Tsv2Json();
        visitor.visit(tree);

        JsonObject json = ((Tsv2Json)visitor).build();
        System.out.println(json.toString());
        /*
        JsonObject personObject = Json.createObjectBuilder().add("name", "Jack").add("age", 13)
                .add("isMarried", false)
                .add("address",
                        Json.createObjectBuilder().add("street", "Main Street").add("city", "New York")
                                .add("zipCode", "11111").build())
                .add("phoneNumber",
                        Json.createArrayBuilder().add("00-000-0000").add("11-111-1111").add("11-111-1112").build())
                .build();

        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = Json.createWriter(stringWriter);
        writer.writeObject(personObject);
        writer.close();
        System.out.println(stringWriter.getBuffer().toString());
        */
    }
}