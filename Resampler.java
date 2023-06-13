package com.example.wekatest;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;

import java.util.HashMap;
import java.util.Map;

public class Resampler {
    private final String basepath;

    public Resampler(String basepath) {
        this.basepath = basepath;
    }

    /***
     * Lee un archivo, lo balancea según la opcion elegida y guarda un nuevo archivo con datos balanceados
     * @param filename nombre del archivo a leer
     * @param tipoSampleo tipo de sampling a realizar. 1-undersampling | 2-combinacion | 3-oversampling
     * @param newFilename nombre del archivo a guardar
     */
    public void resample(String filename, int tipoSampleo, String newFilename) throws Exception {
        // Carga el archivo ARFF del conjunto de datos
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(this.basepath + filename + ".arff");
        Instances data = source.getDataSet();

        // Establece el índice del atributo de clase
        int classIndex = data.numAttributes() - 1;
        data.setClassIndex(classIndex);

        //Realiza el balanceo según la opción elegida
        Instances balancedData = null;
        if (tipoSampleo == 1){
            balancedData = this.undersample(data);
        }
        if (tipoSampleo == 2){
            balancedData = this.combine(data);
        }
        if (tipoSampleo == 3){
            balancedData = this.oversample(data);
        }

        // Guarda el conjunto de datos balanceado en un archivo ARFF
        weka.core.converters.ArffSaver saver = new weka.core.converters.ArffSaver();
        saver.setInstances(balancedData);
        saver.setFile(new java.io.File(this.basepath + newFilename + ".arff"));
        saver.writeBatch();

        System.out.println("\n--Resample realizado con éxito.--\n");
    }

    /***
     * Realiza un undersampling
     * Iguala la cantidad de instancias de todas las clases.
     * El número de instancias de cada clase será igual al de la menormente representada.
     */
    private Instances undersample(Instances data) throws Exception {
        SpreadSubsample filter = new SpreadSubsample();
        filter.setOptions(weka.core.Utils.splitOptions("-M 1.0"));
        filter.setInputFormat(data);
        return Filter.useFilter(data, filter);
    }

    /***
     * Realiza un sampling combinado
     * Iguala la cantidad de instancias de todas las clases.
     * El número de instancias de cada clase será igual a (total de instancias / cantidad de clases)
     */
    private Instances combine(Instances data) throws Exception {
        //Crea el filtro
        Resample filter = new Resample();

        /*
        Configura el filtro
        -B Representa la distribución de clases en los datos de entrada
        0-Distribución original | 1-Ajustar para logar uniformidad

        -Z Establece el tamaño de muestra.
        100.0 Indica que luego del remuestreo habrá la misma cantidad de instancias totales
         */
        filter.setOptions(weka.core.Utils.splitOptions("-B 1.0 -Z 100.0"));

        //Inicia el filtro y lo devuelve
        filter.setInputFormat(data);
        return Filter.useFilter(data, filter);
    }

    /***
     * Realiza un oversampling
     * Iguala la cantidad de instancias de todas las clases.
     * El número de instancias de cada clase será igual al de la mayormente representada.
     */
    private Instances oversample(Instances data) throws Exception{
        //Calcula el ratio necesario para igualar todas las clases
        Double ratio = this.calcularRatioOversample(data) * 100;

        //Crea el filtro
        Resample filter = new Resample();

        /*
        Configura el filtro
        -B Representa la distribución de clases en los datos de entrada
        0-Distribución original | 1-Ajustar para logar uniformidad

        -Z Establece el tamaño de muestra.
        Ratio Indica la cantidad total de instancias resultantes relativo a la cantidad de instancias originales.
        Por ejemplo, un ratio de 150 indica que habrá un 150% de instancias respecto al dataset original
         */
        filter.setOptions(weka.core.Utils.splitOptions("-B 1.0 -Z " +ratio));

        //Inicia el filtro y lo devuelve
        filter.setInputFormat(data);
        return Filter.useFilter(data, filter);
    }

    /***
     * Calcula el ratio necesario para el parámetro Z del filtro Resample.
     */
    private Double calcularRatioOversample(Instances data){
        // Crear un diccionario para almacenar el numero de instancias por clase
        HashMap<String, Integer> map = new HashMap<>();

        // Iterar sobre todas las instancias y contar por clase
        for (int i = 0; i < data.numInstances(); i++) {
            String className = data.instance(i).classAttribute().value((int) data.instance(i).classValue());

            if (map.containsKey(className)) {
                map.put(className, map.get(className) + 1);
            } else {
                map.put(className, 1);
            }
        }

        //Muestra la cantidad de instancias por clase
        System.out.println("\n" +map.toString());

        //Recorre el hashmap generado para obtener el numero de instancias total y el de la más representada
        int valorMayor = 0;
        int cantidadTotal = 0;
        int cantClases = map.size();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            int cantidad = entry.getValue();
            cantidadTotal += cantidad;

            if (cantidad > valorMayor) {
                valorMayor = cantidad;
            }
        }

        System.out.println("Cantidad total de instancias: " +cantidadTotal);
        System.out.println("Cantidad de instancias de la mayor representada: " +valorMayor);

        //Realiza el cálculo del ratio
        Double ratio = (double) ((valorMayor * cantClases) / (double)cantidadTotal);
        //Redondea a dos dígitos
        Double ratioRedondeado = Math.floor(ratio * 100) / 100.0;

        System.out.println("Calculo del ratio: (" +valorMayor +" * " +cantClases +" ) / " +cantidadTotal +") = " +ratioRedondeado +"\n");

        return ratioRedondeado;
    }


}
