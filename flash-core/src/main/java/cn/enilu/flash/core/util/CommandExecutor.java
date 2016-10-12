package cn.enilu.flash.core.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import java.io.*;
import java.util.*;

public class CommandExecutor {
    private File workDir;
    private Map<String, String> envp = new HashMap<String, String>();
    private final String cmd;
    private final List<String> args = new ArrayList<String>();

    private InputStream stdin;

    private List<String> stdOutput = new ArrayList<String>();
    private List<String> errorOutput = new ArrayList<String>();

    private int exitCode;

    public CommandExecutor(String cmd, String... args) {
        this.cmd = cmd;
        this.args.addAll(Arrays.asList(args));
    }

    public void setArgs(List<String> args) {
        this.args.clear();
        this.args.addAll(args);
    }

    public void setArgs(String... args) {
        setArgs(Arrays.asList(args));
    }

    public void addArg(String arg) {
        this.args.add(arg);
    }

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    public void setStdin(InputStream in) {
        this.stdin = in;
    }

    public void setStdin(String in) {
        this.stdin = new ByteArrayInputStream(in.getBytes());
    }

    public void setStdin(byte[] in) {
        this.stdin = new ByteArrayInputStream(in);
    }

    public void setEnv(String name, String value) {
        envp.put(name, value);
    }

    public boolean safeExecute() {
        try {
            return execute();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean execute() throws IOException {
        List<String> command = Lists.newArrayList();
        command.add(cmd);
        command.addAll(args);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(workDir);
        builder.environment().putAll(envp);

        Process process = builder.start();
        if (stdin != null) {
            pipeline(stdin, process.getOutputStream());
        }
        InputStream std = process.getInputStream();
        InputStream error = process.getErrorStream();

        stdOutput = toLines(std);
        errorOutput = toLines(error);

        if(std != null) std.close();
        if(error != null) error.close();

        for (; ; ) {
            try {
                exitCode = process.waitFor();
                break;
            } catch (InterruptedException e) {
                // ignore
            }
        }
        return exitCode == 0;
    }

    private void pipeline(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        for (; ; ) {
            int len = in.read(buf);
            if (len == -1) {
                break;
            }
            out.write(buf, 0, len);
        }
        in.close();
        out.flush();
        out.close();
    }

    private List<String> toLines(InputStream in) throws IOException {
        InputStreamReader inUTF8 = new InputStreamReader(in, Charsets.UTF_8);
        List<String> result = CharStreams.readLines(inUTF8);
        inUTF8.close();
        return result;
    }

    public List<String> getStdOutput() {
        return stdOutput;
    }

    public List<String> getErrorOutput() {
        return errorOutput;
    }

}
