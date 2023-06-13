package com.example.wekatest;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import weka.attributeSelection.CorrelationAttributeEval;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.LinearNNSearch;


public class MultiLayerPerceptron {
    private final String basepath;
    private final Scanner scanner;

    public MultiLayerPerceptron(String basepath, Scanner scanner) {
        this.basepath = basepath;
        this.scanner = scanner;
    }

    /***
     *
     * @param filename Nombre del archivo a leer
     * @param configNeuronas número de capas y neuronas por capa
     * @param learningRate tasa de aprendizaje
     * @param trainingTime tiempo de aprendizaje/epoch
     * @param tipoEvaluacion 1-Split 80/20    |    2-Validación cruzada con 10 pliegues
     * @throws Exception
     */
    public void crear(String filename, String configNeuronas, Double learningRate, int trainingTime, int tipoEvaluacion) throws Exception {
        // Cargar el archivo ARFF del conjunto de datos
        DataSource source = new DataSource(this.basepath + filename + ".arff");
        Instances data = source.getDataSet();

        //Randomiza los datos
        Random rand = new Random(123);   // crea una seed
        Instances dataRandomizada = new Instances(data);   // crea una copia de los datos
        dataRandomizada.randomize(rand); // randomiza el orden de las instancias

        // Establece el atributo objetivo (class) como la última columna del archivo arff
        if (dataRandomizada.classIndex() == -1) {
            dataRandomizada.setClassIndex(dataRandomizada.numAttributes() - 1);
        }

        //Crea y configura el MLP
        System.out.println("\n--Configurando el MLP, espere un momento...");
        MultilayerPerceptron mlp = new MultilayerPerceptron();
        mlp.setHiddenLayers(configNeuronas);
        mlp.setLearningRate(learningRate);
        mlp.setTrainingTime(trainingTime);

        //realiza la evaluación según el tipo elegido
        if (tipoEvaluacion == 1){
            validacionSplit(dataRandomizada, mlp);
        }
        if (tipoEvaluacion == 2){
            validacionCruzada(dataRandomizada, mlp);
        }

        //Guarda el modelo para visualizar los nodos mediante el software WEKA
        System.out.println("Ingrese nombre del archivo para guardar el modelo: (NO para no guardar)");
        String nombreModelo = scanner.nextLine();
        if (!nombreModelo.equals("no")){
            SerializationHelper.write(this.basepath +nombreModelo + ".model", mlp);
        }
    }

    /***
     * Entrena el MLP y realiza sola validación split de 80/20
     * @param data datos preferentemente randomizados
     * @param mlp objeto MultiLayerPerceptron ya configurado
     */
    private void validacionSplit(Instances data, MultilayerPerceptron mlp) throws Exception {
        // Dividir el conjunto de datos en entrenamiento y prueba
        int trainSize = (int) Math.round(data.numInstances() * 0.8);   // 80% entrenamiento
        int testSize = data.numInstances() - trainSize;                // 20% prueba

        Instances trainData = new Instances(data, 0, trainSize);  //(dataset, primer instancia, cantidad)
        Instances testData = new Instances(data, trainSize, testSize);

        //Entrenar con datos de entrenamiento
        mlp.buildClassifier(trainData);

        // Evaluar el modelo con los datos de prueba
        Evaluation eval = new Evaluation(trainData);
        eval.evaluateModel(mlp, testData);

        // Mostrar las métricas y la matriz de confusión
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toMatrixString());
    }

    /***
     * Entrena el MLP y realiza una valización cruzada de X pliegues
     * Corresponde al promedio de X validaciones split de 90/10
     * X es un número ingresado por teclado
     * @param data datos preferentemente randomizados
     * @param mlp objeto MultiLayerPerceptron ya configurado
     */
    private void validacionCruzada(Instances data, MultilayerPerceptron mlp) throws Exception{
        System.out.println("Cuantas folds para la validacion cruzada?");
        int cantFolds = Integer.parseInt(scanner.nextLine());

        //Realiza el entrenamiento y la evaluación cruzada con 10 pliegues
        mlp.buildClassifier(data);
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(mlp, data, cantFolds, new Random(1));

        //Muestra las métricas y la matriz de confusión
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toMatrixString());
    }
}
