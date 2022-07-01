package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import lexer.Lexer;
import parser.MyParser;

/*
Author: Alireza Farzipour
Email:  Alirezafarzipor@gmail.com
 */


public class Main {
    public static void main(String[] args) throws IOException {
        System.setIn(new FileInputStream(new File(args[0])));
        Lexer lexer = new Lexer();
        MyParser parser = new MyParser(lexer);
        parser.start();
    }
    
}
