package application.pooja.com.vedioplayer.application;

import android.app.Application;

import java.util.ArrayList;

import application.pooja.com.vedioplayer.model.Example;

public class myapplication extends Application {

    public static ArrayList<Example> examples;

    public static ArrayList<Example> getExamples() {
        return examples;
    }

    public static void setExamples(ArrayList<Example> examples) {
        myapplication.examples = examples;
    }
}
