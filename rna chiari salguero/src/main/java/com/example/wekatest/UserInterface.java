package com.example.wekatest;
import java.util.Scanner;

/**
 * @author Salguero Benjamín | Chiari Guillermo | UNNE 2023 | Inteligencia Artificial
 */
public class UserInterface {
    private final Scanner scanner;
    private final Resampler resampler;
    private final MultiLayerPerceptron mlp;

    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
        String basepath = "C:\\Users\\Usuario\\Desktop\\rna\\"; //Directorio donde se leerán y escribirán los archivos
        this.resampler = new Resampler(basepath);
        this.mlp = new MultiLayerPerceptron(basepath, scanner);
    }

    public void start() throws Exception {
        //Shows the menu
        System.out.println("\n\nComandos: ");
        System.out.println("resample");
        System.out.println("mlp");
        System.out.println("stop");

        while(true){
            System.out.println("");
            System.out.println("Ingresa comando: ");
            String command = scanner.nextLine();

            if (command.equals("stop")){
                break;
            }
            if (command.equals("resample")){
                this.empezarResample();
            }
            if (command.equals("mlp")){
                this.empezarMLP();
            }
        }
    }

    /***
     * Realiza un balanceo de datos según la técnica elegida
     */
    private void empezarResample() throws Exception {
        System.out.println("Ingrese nombre del archivo: ");
        String filename = scanner.nextLine();
        System.out.println("Que tipo de sampleo desea utilizar? 1-Undersampling | 2-Combinacion | 3-Oversampling");
        int tipoSampleo = Integer.parseInt(scanner.nextLine());
        System.out.println("Ingrese nombre del archivo a crear: ");
        String newFilename = scanner.nextLine();

        //Llama al método resample() del objeto Resampler
        resampler.resample(filename, tipoSampleo, newFilename);
    }

    /***
     * Crea un MLP, entrena, valida y muestra los resultados
     */
    private void empezarMLP() throws Exception {
        System.out.println("Ingrese nombre del archivo: ");
        String filename = scanner.nextLine();
        System.out.println("Ingrese número de capas y neuronas ocultas. Ej '2,4,3'");
        String configNeuronas = scanner.nextLine();
        System.out.println("Ingrese tasa de aprendizaje. Ej '0.2'");
        Double learningRate = Double.valueOf(scanner.nextLine());
        System.out.println("Ingrese tiempo de aprendizaje. Ej '200'");
        int trainingTime = Integer.parseInt(scanner.nextLine());
        System.out.println("Ingrese tipo de evaluación. 1-Split 80/20 | 2-Cruzada con 10 pliegues");
        int tipoEvaluacion = Integer.parseInt(scanner.nextLine());

        //Llama al método crear del objeto MLP
        mlp.crear(filename, configNeuronas, learningRate, trainingTime, tipoEvaluacion);
    }

}