import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class quiz {
    public static void main(String[] args) {
        String directorioDatos = "src/data/";
        boolean salirJuego = false;

        do {
            String nombreUsuario = "";
            boolean nombreGuardado = false;
            int cantidadPreguntas;
            int categoria = menuCategoria();
            String categoriaNombre;


            if (categoria == 1) {
                categoriaNombre = "Gato";
            } else {
                categoriaNombre = "Perro";
            }

            while (!nombreGuardado) {
                System.out.println("\nIngrese su nombre de usuario para el quiz: ");
                nombreUsuario = sc.nextLine();
                System.out.println("\nSu nombre de usuario guardado para el quiz es: " + nombreUsuario);
                System.out.println("Es correcto?");
                System.out.println("1. Si");
                System.out.println("2. No");
                int opcion = validarOpcion(1, 2);
                if (opcion == 1) {
                    nombreGuardado = true;
                }
            }

            System.out.println("\nCuantas pregunats desea responder? (minimo 5 y maximo 20)");
            cantidadPreguntas = validarOpcion(5, 20);

            int[] preguntas = preguntasAleatorias(cantidadPreguntas);

            String[][] myMatrix = preguntasJuego(categoria, directorioDatos);

            int correctas = juego(nombreUsuario, myMatrix, preguntas);

            String fechaHoraFin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String nombreBaseArchivo = categoriaNombre + ".txt";
            Path pathArchivo = Paths.get(directorioDatos, nombreBaseArchivo);
            String nombreArchivo = pathArchivo.toString();

            String lineaEstadistica = nombreUsuario + " | " + fechaHoraFin + " | Correctas: " + correctas + " | Incorrectas: " + (cantidadPreguntas - correctas) + "\n";

            try (FileWriter escritor = new FileWriter(nombreArchivo, true)) {
                escritor.write(lineaEstadistica);
                System.out.println("\n✅ Estadísticas guardadas en el archivo: " + nombreArchivo);
            } catch (IOException e) {
                System.err.println("\n❌ Error al escribir en el archivo de estadísticas: " + e.getMessage());
            }

            System.out.println("\nQuieres seguir jugando?\n1.Si\n2.No");
            int opcion = validarOpcion(1, 2);
            if  (opcion == 2) {
                salirJuego = true;
            }
        } while (!salirJuego);
    }

    private static final Scanner sc = new Scanner(System.in);

    private static int validarOpcion(int min, int max) {
        int opcion = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print("\nElige una opción (" + min + "-" + max + "): ");

            if (sc.hasNextInt()) {
                opcion = sc.nextInt();
                sc.nextLine();

                if (opcion >= min && opcion <= max) {
                    entradaValida = true;
                } else {
                    System.out.println("Opción no válida. Por favor, introduce un número entre (" + min + "-" + max + ")");
                }
            } else {
                System.out.println("¡Error! Debes introducir un número entero. Intenta de nuevo.");
                sc.nextLine();
            }
        }
        return opcion;
    }

    private static int menuCategoria() {
        System.out.println("\n------------------------------------------------");
        System.out.println("Hola bienvenido al QUIZ de la protectora animal\n");
        System.out.println("1. Las necesidades diarias de un gato");
        System.out.println("2. Como entrenar a tu perro");

        return validarOpcion(1,2);
    }

    private static String[][] preguntasJuego(int categoria, String directorioDatos) {
        String misPreguntas = directorioDatos + "preguntasRespuestas.txt";
        Path path  = Paths.get(misPreguntas);

        String[][] myMatrix = new String[20][5];
        int indiceArchivo = 2 + (22 * (categoria - 1));
        int finalArchivo = indiceArchivo + myMatrix.length;

        try {
            List<String> lineas = Files.readAllLines(path);
            int indiceMatriz = 0;

            for (int i = indiceArchivo; i < finalArchivo && i < lineas.size(); i++) {
                String linea = lineas.get(i);
                String[] partesPregunta = linea.split(";");

                if (indiceMatriz < myMatrix.length && partesPregunta.length > 0) {
                    for (int j = 0; j < partesPregunta.length && j < myMatrix[indiceMatriz].length; j++) {
                        myMatrix[indiceMatriz][j] = partesPregunta[j];
                    }
                    indiceMatriz++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + misPreguntas + ": " + e.getMessage());
        }

        return myMatrix;
    }

    private static int[] preguntasAleatorias (int cantidadPreguntas) {
        Random random = new Random();
        int[] numerosAleatorios = new int[cantidadPreguntas];

        for (int i = 0; i < cantidadPreguntas; i++) {
            int numeroRandom;
            boolean guardadoExitoso = false;

            do {
                numeroRandom = random.nextInt(20);
                boolean esDuplicado = false;

                for (int j = 0; j < i; j++) {
                    if (numeroRandom == numerosAleatorios[j]) {
                        esDuplicado = true;
                    }
                }
                if (!esDuplicado) {
                    numerosAleatorios[i] = numeroRandom;
                    guardadoExitoso = true;
                }
            } while (!guardadoExitoso);
        }
        return numerosAleatorios;
    }

    private static int juego(String nombreUsuario, String[][] myMatrix, int[] preguntas) {
        int puntuacion = 0;
        String respuesta;

        System.out.println("------------------------------------------------");
        System.out.println("\nPreguntas juego:\n");

        for (int pregunta : preguntas) {
            boolean entradaValida = false;

            System.out.println("------------------------------------------------");
            System.out.println(nombreUsuario + "                                puntuación: " + puntuacion + "\n");

            for (int j = 0; j < 4; j++) {
                System.out.println(myMatrix[pregunta][j] + " ");
            }
            do {
                System.out.println("\nIntroduce tu respuesta (A, B o C):");
                respuesta = sc.nextLine();

                if (respuesta.equalsIgnoreCase("a") || respuesta.equalsIgnoreCase("b") || respuesta.equalsIgnoreCase("c")) {
                    entradaValida = true;
                } else {
                    System.out.println("ERROR: Respuesta no válida. Por favor, introduce A, B o C.");
                }
            } while (!entradaValida);

            if (respuesta.equalsIgnoreCase(myMatrix[pregunta][4])) {
                System.out.println("La respuesta es correcta");
                puntuacion++;
            } else {
                System.out.println("La respuesta es incorrecta");
                System.out.println("La respuesta correcta es: " + myMatrix[pregunta][4]);
            }
        }

        double porcentajePuntuacion  = (double) (puntuacion * 100) /preguntas.length;

        System.out.println("\n------------------------------------------------");
        System.out.printf("Tu puntuación es: %.2f%%%n", porcentajePuntuacion);
        if (porcentajePuntuacion <= 33) {
            System.out.println("¡NO MERECES TENER MASCOTA!");
        } else if (porcentajePuntuacion <= 66) {
            System.out.println("Cuidate a ti mismo, luego a otro ser vivo");
        } else if (porcentajePuntuacion <= 99) {
            System.out.println("Debes tener un poco más de cuidado pero puedes tener mascota");
        } else {
            System.out.println("¿Acaso eres de PETA?");
        }
        return puntuacion;
    }
}