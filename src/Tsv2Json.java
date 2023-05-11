import javax.json.*;
import java.util.ArrayList;

public class Tsv2Json extends tsvBaseVisitor<String> {
    public enum STATE {
        KEY_PAIR, TABLE_HEADER, TABLE_VALUE
    }
    static int index;
    static STATE state=STATE.KEY_PAIR;
    static String lastField;

    JsonObjectBuilder headBuilder = Json.createObjectBuilder();
    ArrayList<String> tableHeader = new ArrayList<String>();
    JsonArrayBuilder warning = Json.createArrayBuilder();

    JsonObjectBuilder currRow = Json.createObjectBuilder();
    JsonArrayBuilder tableBuilder = Json.createArrayBuilder();

    JsonObject build() {
        headBuilder.add("data", tableBuilder.build()).add("warning", warning);
        return headBuilder.build();
        /*
        JsonObject personObject = Json.createObjectBuilder().add("name", "Jack").add("age", 13)
            .add("isMarried", false)
            .add("address",
                    Json.createObjectBuilder().add("street", "Main Street").add("city", "New York")
                            .add("zipCode", "11111").build())
            .add("phoneNumber",
                    Json.createArrayBuilder().add("00-000-0000").add("11-111-1111").add("11-111-1112").build())
            .build();
       */
    }

    @Override public String visitRenlab(tsvParser.RenlabContext ctx) {
        headBuilder.add("Source", "renlab");
        return visitChildren(ctx);
    }

    @Override public String visitIgm(tsvParser.IgmContext ctx) {
        headBuilder.add("Source", "igm");
        return visitChildren(ctx);
    }

    @Override public String visitHdr(tsvParser.HdrContext ctx) {
        // System.out.println("hdr:");
        index=0;
        return visitChildren(ctx);
    }

    @Override public String visitSid(tsvParser.SidContext ctx) {
        // System.out.println("Sample ID:");
        index=0; state=STATE.TABLE_HEADER;
        tableHeader.add("Sample ID");
        return visitChildren(ctx);
    }

    @Override public String visitSname(tsvParser.SnameContext ctx) {
        // System.out.println("Sample Name:");
        index=0; state=STATE.TABLE_HEADER;
        tableHeader.add("Sample Name");
        return visitChildren(ctx);
    }

    @Override public String visitRow(tsvParser.RowContext ctx) {
        // System.out.println("row:");
        if (state==STATE.TABLE_HEADER)
            state=STATE.TABLE_VALUE;
        index=0;
        String child=visitChildren(ctx);
        return child;
    }
    @Override public String visitField(tsvParser.FieldContext ctx) {
        String child=visitChildren(ctx);
        // if (ctx.txt != null) System.out.println("\tfield["+index+"]: '"+ctx.txt.getText()+"'");
        String value=null;
        if (ctx.txt!=null)
            value=ctx.txt.getText();

        if (state==STATE.KEY_PAIR) {
            if (index==1) {
                if (lastField!=null && value!=null) {
                    // System.out.println(lastField + " => '" + value + "'");
                    headBuilder.add(lastField, value);
                }
            } else if (index==0) {
                if (value==null)
                    warning.add("A key field is null at line "+ ctx.getStart().getLine());
            } else {
                if (value!=null)
                    warning.add("Skip '"+value+"' at line " + ctx.getStart().getLine());
            }
            // if (value!=null) System.err.println("\tFind field["+index+"]: '"+value+"'");
            lastField=value;
        } else if (state==STATE.TABLE_HEADER) {
            //System.out.println("\theader["+index+"] = '"+value+"'");
            tableHeader.add(value);
        } else { // STATE.TABLE_VALUE
            //if (value!=null) System.out.println("\ttable["+index+"] = '"+value+"'");
            if (index==0) {
                currRow=Json.createObjectBuilder();
                currRow.add(tableHeader.get(index), value);
                tableBuilder.add(currRow);
            } else {
                if (index>=tableHeader.size()) {
                    String msg="Out of scope? ["+index+"]>"+tableHeader.size();
                    warning.add(msg);
                    System.err.println(msg);
                }
                if (value!=null)
                    currRow.add(tableHeader.get(index), value);
                else
                    currRow.add(tableHeader.get(index), "");
            }
        }
        index++;
        return child;
    }
}
