package client.CompileAndRun;

public class CompileAndRunTest {
    static String programCode = "#include<iostream>\n" +
            "using namespace std;\n" +
            "int main() {\n" +
            "string str;\n" +
            "cin>>str;\n" +
            "cout<<str<<\" Hello World\"; }";
    static String programInput = "nothing";
    public static void main(String args[]) {
        CompileAndRunCPP compileAndRunCPP = new CompileAndRunCPP(programCode,programInput);
        compileAndRunCPP.run();
        System.out.println(compileAndRunCPP.getCompileOutput());
//        System.out.println(compileAndRunCPP.getCompileError());
//        System.out.println(compileAndRunCPP.getRunError());
        System.out.println(compileAndRunCPP.getRunOutput());
    }
}
