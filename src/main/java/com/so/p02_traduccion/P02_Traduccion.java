package com.so.p02_traduccion;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class P02_Traduccion {
    
    // Mapas para almacenar los tokens del Excel
    static Map<String, Integer> tablaSimbolos = new HashMap<>();
    static Map<String, String> tipoToken = new HashMap<>();
    
    // Método principal
    public static void main(String[] args) {
        System.out.println("=== ANALIZADOR LÉXICO ===");
        
        // PASO 1: Cargar tokens desde Excel
        String archivoExcel = "C:\\Users\\Deyvids\\Documents\\Token.xlsx"; // Cambia el nombre de tu archivo aquí
        String architTexto ="C:\\Users\\Deyvids\\Documents\\Analizar.txt";
        
        if (cargarTablaDesdeExcel(archivoExcel)) {
//            System.out.println("✓ Tokens cargados exitosamente");
//            // PASO 2: Mostrar tokens cargados para verificar
//            mostrarTokensCargados();
//            
//            // PASO 3: Probar con algunas palabras
//            probarAnalisisBasico();
//
//            // PASO 4: Ejecutar tests completos
//            ejecutarTestsCompletos();    
            analizarArchivoTexto(architTexto);
        } else {
            System.out.println("✗ Error al cargar tokens");
        }
    }
    
    // PASO 1: Método para cargar tokens desde Excel
    
    // MÉTODO CORREGIDO - Solo reemplaza el método cargarTablaDesdeExcel
    public static boolean cargarTablaDesdeExcel(String rutaArchivo) {
        try {
            FileInputStream archivoEntrada = new FileInputStream(new File(rutaArchivo));
            Workbook libroExcel = new XSSFWorkbook(archivoEntrada);

            // Asume que la hoja se llama "Hoja1" o usa la primera hoja
            Sheet hojaTokens = libroExcel.getSheetAt(0);

            System.out.println("Leyendo archivo: " + rutaArchivo);

            for (Row filaActual : hojaTokens) {
                // Saltar la primera fila (encabezados)
                if (filaActual.getRowNum() == 0) {
                    System.out.println("Saltando encabezados...");
                    continue;
                }

                // ORDEN CORREGIDO: CODIGO | COMPONENTE | DESCRIPCION
                Cell celdaCodigoNum = filaActual.getCell(0); // Columna A - CODIGO
                Cell celdaLexema = filaActual.getCell(1);    // Columna B - COMPONENTE  
                Cell celdaTipo = filaActual.getCell(2);      // Columna C - DESCRIPCION

                // Verificar que las celdas no estén vacías
                if (celdaCodigoNum != null && celdaLexema != null && celdaTipo != null) {
                    // Manejar diferentes tipos de celdas para el código
                    int codigoNumerico;
                    if (celdaCodigoNum.getCellType() == CellType.NUMERIC) {
                        codigoNumerico = (int) celdaCodigoNum.getNumericCellValue();
                    } else {
                        // Si está como texto, convertir a número
                        codigoNumerico = Integer.parseInt(celdaCodigoNum.getStringCellValue());
                    }

                    String lexema = celdaLexema.getStringCellValue().trim();
                    String descripcion = celdaTipo.getStringCellValue().trim();

                    // Almacenar en los mapas
                    tablaSimbolos.put(lexema, codigoNumerico);
                    tipoToken.put(lexema, descripcion);

                    System.out.println("Cargado: " + lexema + " -> " + codigoNumerico + " (" + descripcion + ")");
                }
            }

            libroExcel.close();
            archivoEntrada.close();
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("Error: No se encontró el archivo " + rutaArchivo);
            System.out.println("Verifica que el archivo existe en: " + rutaArchivo);
            return false;
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.out.println("Error: El código no es un número válido - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // PASO 2: Método para mostrar todos los tokens cargados
    public static void mostrarTokensCargados() {
        System.out.println("\n=== TABLA DE SÍMBOLOS CARGADA ===");
        System.out.println("CÓDIGO\t\tCOMPONENTE\tTIPO");
        System.out.println("----------------------------------------");
        
        for (Map.Entry<String, Integer> entrada : tablaSimbolos.entrySet()) {
            String simbolo = entrada.getKey();
            Integer codigo = entrada.getValue();
            String tipo = tipoToken.get(simbolo);
            
           System.out.printf("%d\t\t%-15s\t%s%n", codigo, simbolo, tipo);
        }
        
        System.out.println("\nTotal de tokens cargados: " + tablaSimbolos.size());
    }
    
    // PASO 3: Método básico para probar el reconocimiento de tokens
    public static void probarAnalisisBasico() {
        System.out.println("\n=== PRUEBA BÁSICA DE ANÁLISIS ===");
        
        // Palabras de prueba
        String[] palabrasPrueba = {"Si", "SiNo", "+", "(", "123", "miVariable"};
        
        for (String palabra : palabrasPrueba) {
            analizarPalabraIndividual(palabra);
        }
    }
    
    // PASO 4: Método para analizar una palabra individual
    public static void analizarPalabraIndividual(String palabra) {
        if (tablaSimbolos.containsKey(palabra)) {
            int codigo = tablaSimbolos.get(palabra);
            String tipo = tipoToken.get(palabra);
            System.out.println("'" + palabra + "' -> Código: " + codigo + " | Tipo: " + tipo);
        } else {
            // Aquí puedes agregar lógica para números, identificadores, etc.
            if (esNumeroValido(palabra)) {
                System.out.println("'" + palabra + "' -> Código: 400 | Tipo: Número");
            } else if (esIdentificadorValido(palabra)) {
                System.out.println("'" + palabra + "' -> Código: [IDENTIFICADOR] | Tipo: Variable");
            } else {
                System.out.println("'" + palabra + "' -> ERROR: Token no reconocido");
            }
        }
    }
    
    // Métodos auxiliares para validación
    public static boolean esNumeroValido(String texto) {
        try {
            Integer.parseInt(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean esIdentificadorValido(String texto) {
        if (texto == null || texto.isEmpty()) return false;
        
        // Debe empezar con letra o _
        if (!Character.isLetter(texto.charAt(0)) && texto.charAt(0) != '_') {
            return false;
        }
        
        // El resto pueden ser letras, números o _
        for (int i = 1; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            if (!Character.isLetterOrDigit(caracter) && caracter != '_') {
                return false;
            }
        }
        
        return true;
    }

    // PASO 5: Test completo con casos de error
    public static void ejecutarTestsCompletos() {
        System.out.println("\n=== TESTS COMPLETOS DEL ANALIZADOR ===");

        // Test 1: Tokens válidos
        System.out.println("\n--- TEST 1: TOKENS VÁLIDOS ---");
        String[] tokensValidos = {"Si", "SiNo", "FinSi", "+", "-", "(", ")", "123", "variable1"};
        for (String token : tokensValidos) {
            analizarTokenConValidacion(token);
        }

        // Test 2: Errores léxicos por mayúsculas/minúsculas
        System.out.println("\n--- TEST 2: ERRORES POR MAYÚSCULAS ---");
        String[] erroresMayusculas = {"SI", "sino", "finsi", "MIENTRAS", "para"};
        for (String token : erroresMayusculas) {
            analizarTokenConValidacion(token);
        }

        // Test 3: Cadenas de texto
        System.out.println("\n--- TEST 3: CADENAS DE TEXTO ---");
        String[] cadenas = {"\"Hola mundo\"", "\"Esta es una cadena\"", "\"123\"", "\"\""};
        for (String token : cadenas) {
            analizarTokenConValidacion(token);
        }

        // Test 4: Tokens inválidos
        System.out.println("\n--- TEST 4: TOKENS INVÁLIDOS ---");
        String[] tokensInvalidos = {"@", "#", "%", "123abc", "variable-invalida"};
        for (String token : tokensInvalidos) {
            analizarTokenConValidacion(token);
        }
    }

    // Método mejorado para analizar tokens individuales con validación completa
    public static void analizarTokenConValidacion(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            System.out.println("ERROR LÉXICO: Token vacío o nulo");
            return;
        }

        palabra = palabra.trim();

        // 1. Verificar si es una cadena de texto (entre comillas dobles)
        if (esCadenaTexto(palabra)) {
            System.out.println("'" + palabra + "' -> Código: 600 | Tipo: Cadena de texto");
            return;
        }

        // 2. Verificar si es token conocido (exacto)
        if (tablaSimbolos.containsKey(palabra)) {
            int codigo = tablaSimbolos.get(palabra);
            String tipo = tipoToken.get(palabra);
            System.out.println("'" + palabra + "' -> Código: " + codigo + " | Tipo: " + tipo);
            return;
        }

        // 3. Verificar errores por mayúsculas/minúsculas
        String errorMayusculas = verificarErrorMayusculas(palabra);
        if (errorMayusculas != null) {
            System.out.println("ERROR LÉXICO: \"" + palabra + "\" está mal escrito. " + errorMayusculas);
            return;
        }

        // 4. Verificar si es número
        if (esNumeroValido(palabra)) {
            System.out.println("'" + palabra + "' -> Código: 400 | Tipo: Número");
            return;
        }

        // 5. Verificar si es identificador válido
        if (esIdentificadorValido(palabra)) {
            System.out.println("'" + palabra + "' -> Código: 500 | Tipo: Identificador");
            return;
        }

        // 6. Token no reconocido
        System.out.println("ERROR LÉXICO: \"" + palabra + "\" no es un token válido.");
    }

    // Verificar si es una cadena de texto válida
    public static boolean esCadenaTexto(String token) {
        return token.length() >= 2 && token.startsWith("\"") && token.endsWith("\"");
    }

    // Verificar errores por mayúsculas/minúsculas
    public static String verificarErrorMayusculas(String palabra) {
        for (String tokenValido : tablaSimbolos.keySet()) {
            if (tokenValido.equalsIgnoreCase(palabra) && !tokenValido.equals(palabra)) {
                return "Debería escribirse como: \"" + tokenValido + "\"";
            }
        }
        return null;
    }
    
    
    // Analizador lexico .txt
    // MÉTODO CON FORMATO ESPECÍFICO SOLICITADO
    // Reemplaza el método analizarArchivoTexto con este:

    public static void analizarArchivoTexto(String rutaArchivo) {
        System.out.println("--------------------RESULTADO DEL ANALIZADOR LEXICO--------------------");
        System.out.println();

        try {
            BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo));
            String lineaActual;
            int numeroLinea = 1;

            while ((lineaActual = lector.readLine()) != null) {
                if (!lineaActual.trim().isEmpty()) {
                    System.out.println("LINEA " + numeroLinea + ": " + lineaActual);

                    List<String> tokens = extraerTokensDeLinea(lineaActual);

                    for (String token : tokens) {
                        if (!token.trim().isEmpty()) {
                            analizarTokenConFormatoEspecifico(token, numeroLinea);
                        }
                    }
                    System.out.println(); // Línea en blanco después de cada línea
                }
                numeroLinea++;
            }

            lector.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: No se encontró el archivo " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("ERROR al leer el archivo: " + e.getMessage());
        }
    }

    // Método para analizar token con el formato específico solicitado
    public static void analizarTokenConFormatoEspecifico(String palabra, int numeroLinea) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return;
        }

        palabra = palabra.trim();

        // 1. Verificar si es una cadena de texto
        if (esCadenaTexto(palabra)) {
            System.out.println("TOKEN: " + palabra + "      CODIGO: 600      DESCRIPCION: Cadena de texto");
            return;
        }

        // 2. Verificar si es token conocido (exacto)
        if (tablaSimbolos.containsKey(palabra)) {
            int codigo = tablaSimbolos.get(palabra);
            String descripcion = tipoToken.get(palabra);
            System.out.println("TOKEN: " + palabra + "      CODIGO: " + codigo + "      DESCRIPCION: " + descripcion);
            return;
        }

        // 3. Verificar errores por mayúsculas/minúsculas
        String errorMayusculas = verificarErrorMayusculas(palabra);
        if (errorMayusculas != null) {
            System.out.println("\u001B[31mERROR LEXICO en la linea " + numeroLinea + ": \"" + palabra + "\" " + errorMayusculas + "\u001B[0m");
            return;
        }

        // 4. Verificar si es número
        if (esNumeroValido(palabra)) {
            System.out.println("TOKEN: " + palabra + "      CODIGO: 400      DESCRIPCION: Numero");
            return;
        }

        // 5. Verificar si es identificador válido
        if (esIdentificadorValido(palabra)) {
            System.out.println("TOKEN: " + palabra + "      CODIGO: 500      DESCRIPCION: Identificador");
            return;
        }

        // 6. Token no reconocido
        System.out.println("\u001B[31mERROR LEXICO en la linea " + numeroLinea + ": \"" + palabra + "\" no es un token valido\u001B[0m");
    }

    // VERSIÓN SIN COLORES (si los colores no funcionan en tu consola)
    public static void analizarTokenConFormatoEspecificoSinColores(String palabra, int numeroLinea) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return;
        }

        palabra = palabra.trim();

        // 1. Verificar si es una cadena de texto
        if (esCadenaTexto(palabra)) {
            System.out.println("TOKEN: " + palabra + "      CODIGO: 600      DESCRIPCION: Cadena de texto");
            return;
        }

        // 2. Verificar si es token conocido (exacto)
        if (tablaSimbolos.containsKey(palabra)) {
            int codigo = tablaSimbolos.get(palabra);
            String descripcion = tipoToken.get(palabra);
            System.out.println("TOKEN: " + palabra + "      CODIGO: " + codigo + "      DESCRIPCION: " + descripcion);
            return;
        }

        // 3. Verificar errores por mayúsculas/minúsculas
        String errorMayusculas = verificarErrorMayusculas(palabra);
        if (errorMayusculas != null) {
            System.out.println("ERROR LEXICO en la linea " + numeroLinea + ": \"" + palabra + "\" " + errorMayusculas);
            return;
        }

        // 4. Verificar si es número
        if (esNumeroValido(palabra)) {
            System.out.println("TOKEN: " + palabra + "      CODIGO: 400      DESCRIPCION: Numero");
            return;
        }

        // 5. Verificar si es identificador válido
        if (esIdentificadorValido(palabra)) {
            System.out.println("TOKEN: " + palabra + "      CODIGO: 500      DESCRIPCION: Identificador");
            return;
        }

        // 6. Token no reconocido
        System.out.println("ERROR LEXICO en la linea " + numeroLinea + ": \"" + palabra + "\" no es un token valido");
    }
    
    // MÉTODO SIMPLE PARA EXTRAER TOKENS DE UNA LÍNEA
    // Agrega este método a tu clase:

    public static List<String> extraerTokensDeLinea(String linea) {
        List<String> tokens = new ArrayList<>();
        StringBuilder tokenActual = new StringBuilder();
        boolean dentroComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            // Manejar comillas dobles
            if (c == '"') {
                if (dentroComillas) {
                    // Cerrar cadena
                    tokenActual.append(c);
                    tokens.add(tokenActual.toString());
                    tokenActual = new StringBuilder();
                    dentroComillas = false;
                } else {
                    // Abrir cadena
                    if (tokenActual.length() > 0) {
                        tokens.add(tokenActual.toString());
                        tokenActual = new StringBuilder();
                    }
                    tokenActual.append(c);
                    dentroComillas = true;
                }
            }
            // Si estamos dentro de comillas, agregar todo
            else if (dentroComillas) {
                tokenActual.append(c);
            }
            // Si es espacio en blanco fuera de comillas
            else if (Character.isWhitespace(c)) {
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual = new StringBuilder();
                }
            }
            // Si es un delimitador/operador
            else if (esDelimitador(c)) {
                // Guardar token anterior si existe
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual = new StringBuilder();
                }

                // Verificar operadores de dos caracteres
                if (i + 1 < linea.length()) {
                    String dobleChar = "" + c + linea.charAt(i + 1);
                    if (tablaSimbolos.containsKey(dobleChar)) {
                        tokens.add(dobleChar);
                        i++; // Saltar siguiente carácter
                        continue;
                    }
                }

                // Agregar como carácter individual
                tokens.add(String.valueOf(c));
            }
            // Carácter normal
            else {
                tokenActual.append(c);
            }
        }

        // Agregar último token si existe
        if (tokenActual.length() > 0) {
            tokens.add(tokenActual.toString());
        }

        return tokens;
    }

    // Método auxiliar para verificar delimitadores
    public static boolean esDelimitador(char c) {
        // Basado en tu tabla de tokens
        String delimitadores = "(){}[];.,'\"+-*/<>=!?";
        return delimitadores.indexOf(c) >= 0;
    }

    // VERSIÓN AÚN MÁS SIMPLE SI LA DE ARRIBA DA PROBLEMAS:
    public static List<String> extraerTokensSimple(String linea) {
        List<String> tokens = new ArrayList<>();

        // Dividir por espacios primero
        String[] palabras = linea.split("\\s+");

        for (String palabra : palabras) {
            if (!palabra.trim().isEmpty()) {
                // Si contiene delimitadores, separarlos
                tokens.addAll(separarDelimitadores(palabra));
            }
        }

        return tokens;
    }

    // Método para separar delimitadores de palabras
    public static List<String> separarDelimitadores(String palabra) {
        List<String> resultado = new ArrayList<>();
        StringBuilder tokenActual = new StringBuilder();
        boolean dentroComillas = false;

        for (int i = 0; i < palabra.length(); i++) {
            char c = palabra.charAt(i);

            if (c == '"') {
                if (dentroComillas) {
                    tokenActual.append(c);
                    resultado.add(tokenActual.toString());
                    tokenActual = new StringBuilder();
                    dentroComillas = false;
                } else {
                    if (tokenActual.length() > 0) {
                        resultado.add(tokenActual.toString());
                        tokenActual = new StringBuilder();
                    }
                    tokenActual.append(c);
                    dentroComillas = true;
                }
            } else if (dentroComillas) {
                tokenActual.append(c);
            } else if (esDelimitador(c)) {
                if (tokenActual.length() > 0) {
                    resultado.add(tokenActual.toString());
                    tokenActual = new StringBuilder();
                }
                resultado.add(String.valueOf(c));
            } else {
                tokenActual.append(c);
            }
        }

        if (tokenActual.length() > 0) {
            resultado.add(tokenActual.toString());
        }

        return resultado;
    }
}