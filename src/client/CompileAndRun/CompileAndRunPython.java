package client.CompileAndRun;

import java.io.FileWriter;

public class CompileAndRunPython implements CompileAndRun {
    private String programCode=null,programInput = null,compileOutput= null,runOutput=null,compileError=null,runError=null;
    public CompileAndRunPython(String programCode, String programInput) {
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

    @Override
    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    @Override
    public void run() {

        try {
            FileWriter fw = new FileWriter("temp.py");
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
            runCommand.append("cmd /c python temp.py < temp.inp");
        }
        else {
            runCommand.append("sh -c python temp.py < temp.inp");
        }

        Executor executor = new Executor(null,runCommand.toString());
        executor.compileAndRun();
        compileOutput=executor.getCompileOutput();
//        compileError=executor.getCompileError();
        runOutput=executor.getRunOutput();
//        runError=executor.getRunError();

    }
}
