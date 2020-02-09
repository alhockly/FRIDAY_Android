package com.example.friday_android;

import androidx.core.content.FileProvider;

import java.io.File;

public class GenericFileProvider extends FileProvider implements IUpdateApp {
    @Override
    public void installApp(File apkFile) {

    }
}
