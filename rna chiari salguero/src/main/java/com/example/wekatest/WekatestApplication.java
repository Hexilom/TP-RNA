package com.example.wekatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class WekatestApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WekatestApplication.class, args);

        //inicializa el scanner para ingresos por teclado
        Scanner scanner = new Scanner(System.in);
        //inicializa la interfaz de usuario
        UserInterface ui = new UserInterface(scanner);
        ui.start();
    }
}
