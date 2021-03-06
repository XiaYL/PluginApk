package com.xyl.plugin.core;

import java.io.File;

public class PluginConfiguration {

    private String outputDir;
    private String libDir;

    private PluginConfiguration(Builder builder) {
        outputDir = builder.outputDir;
        libDir = builder.libDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getLibDir() {
        return libDir;
    }

    public static class Builder {
        private String outputDir;//dex解压后的路径
        private String libDir;//so文件的路径

        public Builder outputDir(String outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        public Builder libDir(String libDir) {
            this.libDir = libDir;
            return this;
        }

        public Builder outputDir(File outputDir) {
            this.outputDir = outputDir.getAbsolutePath();
            return this;
        }

        public Builder libDir(File libDir) {
            this.libDir = libDir.getAbsolutePath();
            return this;
        }

        public PluginConfiguration build() {
            return new PluginConfiguration(this);
        }
    }
}
