package client.CompileAndRun;

public interface CompileAndRun {
    public void setInput(String input);
    public void setProgramCode(String programCode);
    public void run();
    public String getCompileOutput();
    public String getRunOutput();
//    public String getCompileError();
//    public String getRunError();
}
