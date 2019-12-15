package client.CompileAndRun;

import java.io.FileWriter;

public class CompileAndRunJava implements CompileAndRun {
    private String programCode=null,programInput = null,compileOutput= null,runOutput=null,compileError=null,runError=null;
    public CompileAndRunJava(String programCode, String programInput) {
        setProgramCode(programCode);
        setInput(programInput);
    }
    @Override
    public void setInput(String input) {
        this.programInput = input;
    }
    @Override
    public String getCompileOutput() {
        return compileOutput;
    }
    @Override
    public String getRunOutput() {
        return runOutput;
    }
//    @Override
//    public String getCompileError() {
//        return compileError;
//    }
//    @Override
//    public String getRunError() {
//        return runError;
//    }

    @Override
    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    @Override
    public void run() {

        try {
            FileWriter fw = new FileWriter("Main.java");
            fw.write(programCode);
            fw.close();
            fw = new FileWriter("temp.inp");
            fw.write(programInput);
            fw.close();
        }
        catch (Exception e) {
            System.out.println("Exception in writing to temp.java" + e);
        }

        StringBuilder compileCommand = new StringBuilder();
        StringBuilder runCommand = new StringBuilder();
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String currDir = System.getProperty("user.dir");
        if(isWindows) {
            compileCommand.append("cmd.exe /c ");
            runCommand.append("cmd /c java Main < temp.inp");
        }
        else {
            compileCommand.append("sh -c ");
            runCommand.append("sh -c java Main < temp.inp");
        }

        compileCommand.append("javac " + currDir + "/Main.java");

        Executor executor = new Executor(compileCommand.toString(),runCommand.toString());
        executor.compileAndRun();
        compileOutput=executor.getCompileOutput();
//        compileError=executor.getCompileError();
        runOutput=executor.getRunOutput();
//        runError=executor.getRunError();

    }
}
