package client.CompileAndRun;

import java.io.FileWriter;

public class CompileAndRunCPP implements CompileAndRun {
    private String programCode=null,programInput = null,compileOutput= null,runOutput=null,compileError=null,runError=null;
    public CompileAndRunCPP(String programCode, String programInput) {
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
            FileWriter fw = new FileWriter("temp.cpp");
            fw.write(programCode);
            fw.close();
            fw = new FileWriter("temp.inp");
            fw.write(programInput);
            fw.close();
        }
        catch (Exception e) {
            System.out.println("Exception in writing to temp.code");
        }

        StringBuilder compileCommand = new StringBuilder();
        StringBuilder runCommand = new StringBuilder();
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String currDir = System.getProperty("user.dir");
        if(isWindows) {
            compileCommand.append("cmd.exe /c ");
            runCommand.append("cmd /c out < temp.inp");
        }
        else {
            compileCommand.append("sh -c ");
            runCommand.append("sh -c ./out < temp.inp");
        }

        compileCommand.append("g++ " + currDir + "/temp.cpp " + "-o out");

        Executor executor = new Executor(compileCommand.toString(),runCommand.toString());
        executor.compileAndRun();
        compileOutput=executor.getCompileOutput();
//        compileError=executor.getCompileError();
        runOutput=executor.getRunOutput();
//        runError=executor.getRunError();

    }
}
