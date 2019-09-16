package client.CompileAndRun;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor {
    private String compileCommand = null,runCommand = null;
    private StringBuilder compileOutput= new StringBuilder(),runOutput=new StringBuilder(),compileError = new StringBuilder(),runError= new StringBuilder();
    public Executor(String command,String runCommand) {
        this.compileCommand = command;
        this.runCommand = runCommand;
    }

    public void compileAndRun() {
        //compile
        int compileStatus=0;
        if(compileCommand != null)
        {
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.redirectErrorStream(true);
                builder.command(compileCommand.split(" "));
                Process process =builder.start();
                //Process process = Runtime.getRuntime().exec(command);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    compileOutput.append(line + "\n");
                }
                reader.close();
                try {
                    System.out.println("Waiting for Compilation");
                    compileStatus= process.waitFor();
                    System.out.println("Compilation Complete");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                System.out.println("Exception during Compile in Executor");
            }
        }
        //Run
        if(compileStatus == 0)
        {
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.redirectErrorStream(true);
                builder.command(runCommand.split(" "));
                Process process =builder.start();
//                Process process = Runtime.getRuntime().exec("cmd.exe /c ./out < temp.inp");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    runOutput.append(line + "\n");
                }
                reader.close();
                System.out.println("Program ran");
            }
            catch (Exception e) {
                System.out.println("Exception in Running Executor");
            }
        }
        else {
            System.out.println("Not Compiled");
        }

    }
    public String getCompileOutput() {
        return compileOutput.toString();
    }
//    public String getCompileError() {
//        return compileError.toString();
//    }
//    public String getRunError() {
//        return runError.toString();
//    }
    public String getRunOutput() {
        return runOutput.toString();
    }
}
