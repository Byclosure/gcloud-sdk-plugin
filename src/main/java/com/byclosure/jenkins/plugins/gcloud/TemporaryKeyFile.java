package com.byclosure.jenkins.plugins.gcloud;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.util.Secret;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class TemporaryKeyFile {
    private AbstractBuild build;
    private Launcher launcher;
    private File keyFile;
    private FilePath tmpDir;
    private FilePath tmpKeyFile;

    public TemporaryKeyFile(AbstractBuild build, Launcher launcher, File keyFile) {
        this.build = build;
        this.launcher = launcher;
        this.keyFile = keyFile;
    }

    public FilePath getDir() {
        return tmpDir;
    }

    public FilePath getKeyFile() {
        return tmpKeyFile;
    }

    public TemporaryKeyFile copyToTmpDir() throws IOException, InterruptedException {
        tmpDir = build.getWorkspace().createTempDir("gcloud", null);
        tmpKeyFile = new FilePath(launcher.getChannel(),
                new File(tmpDir.getRemote(), keyFile.getName()).getPath());
        String tmp = IOUtils.toString(new FileInputStream(keyFile));
        tmpKeyFile.write(Secret.decrypt(tmp).getPlainText(), "UTF-8");
        return this;
    }

    void remove() throws IOException, InterruptedException {
        tmpDir.deleteRecursive();
    }
}
