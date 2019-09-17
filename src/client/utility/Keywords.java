package client.utility;

import java.util.HashMap;

public class Keywords {
    public HashMap< String , Boolean> map_c;

    public Keywords (){
        map_c = new HashMap<>();
        map_c.put("auto" , true);
        map_c.put("double" , true);
        map_c.put("int" , true);
        map_c.put("struct" , true);

        map_c.put("break" , true);
        map_c.put("else" , true);
        map_c.put("long" , true);
        map_c.put("switch" , true);

        map_c.put("case" , true);
        map_c.put("enum" , true);
        map_c.put("register" , true);
        map_c.put("typedef" , true);

        map_c.put("char" , true);
        map_c.put("extern" , true);
        map_c.put("return" , true);
        map_c.put("union" , true);

        map_c.put("continue" , true);
        map_c.put("for" , true);
        map_c.put("signed" , true);
        map_c.put("void" , true);

        map_c.put("do" , true);
        map_c.put("if" , true);
        map_c.put("static" , true);
        map_c.put("while" , true);

        map_c.put("default" , true);
        map_c.put("goto" , true);
        map_c.put("sizeof" , true);
        map_c.put("volatile" , true);

        map_c.put("const" , true);
        map_c.put("float" , true);
        map_c.put("short" , true);
        map_c.put("unsigned" , true);

        map_c.put("include" , true);
        map_c.put("define" , true);

    }


}
