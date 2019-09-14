package client.CompileAndRun;

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
        StringBuilder command = new StringBuilder();
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String currDir = System.getProperty("user.dir");
        if(isWindows) {
            command.append("cmd.exe /c ");
        }
        else {
            command.append("sh -c ");
        }

        command.append("g++ " + currDir + "/temp.cpp " + "-o out");
        Executor executor = new Executor(command.toString(),programCode,programInput);
        executor.compileAndRun();
        compileOutput=executor.getCompileOutput();
//        compileError=executor.getCompileError();
        runOutput=executor.getRunOutput();
//        runError=executor.getRunError();

    }
}
