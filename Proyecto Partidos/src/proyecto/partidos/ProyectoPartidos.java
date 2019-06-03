package proyecto.partidos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * AUTOR: MIGUEL ANGEL MENA ALCALDE Y CARLOS PALENCIA 
 * VERSION: 2.0 ULTIMA
 * MODIFICACION: 02/06/0219
 *
 */
public class ProyectoPartidos {

    public static void main(String[] args) {
        Connection con = null;
        try {
            con = obtenerConexion();//OBTENEMOS CONEXION CON LA BASE DE DATOS
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONEXION(" + e.getMessage() + ")");
        }
        Scanner menu = new Scanner(System.in);
        boolean salir = false;
        while (!salir) {
            System.out.println("========================================");
            System.out.println("=                 MENU                 =");
            System.out.println("========================================");
            System.out.println("=   1-Introducir Partidos              =");
            System.out.println("=   2-Introducir Partidos por Archivo  =");
            System.out.println("=   3-Buscar Partido                   =");
            System.out.println("=   4-Imprimir Partidos                =");
            System.out.println("=   5-Salir                            =");
            System.out.println("========================================");
            System.out.print("Que quieres hacer: ");
            int opcion = menu.nextInt();
            while (opcion > 5 || opcion < 1) {
                System.out.print("Esa no es una opcion posible: ");
                opcion = menu.nextInt();
            }
            System.out.println("");
            switch (opcion) {
                case 1:
                    introducirPartido(con);
                    break;
                case 2:
                    try {
                        buscarPartidoArchivo(con);
                    } catch (IOException e) {
                        System.out.println("ERROR EN LA LECTURA DEL ARCHIVO("
                                + e.getMessage() + ")");
                    }
                    break;
                case 3:
                    buscarPartido(con);
                    break;
                case 4:
                    guardarPartido(con);
                    break;
                case 5:
                    salir = true;
                    break;
                default:

            }

        }
        cerrarConexion(con);
    }

    //BUSCAR PARTIDO
    /**
     * BUSCA PARTIDOS EN LA BASE DE DATOS FILTRANDO PRIMERO POR LA TEMPORADA Y
     * LUEGO POR LA FECHA O LA CATEGORIA
     *
     * @param con
     * @return
     */
    public static boolean buscarPartido(Connection con) {
        Scanner bc = new Scanner(System.in);
        try {
            consultarTemporada(con);//CONSULTAMOS LAS TEMPORADAS DISPONIBLES
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ")");
            return true;
        }
        //SELECCIONAMOS UNA DE LA TEMPORADAS
        System.out.print("¿De que temporada es el partido que deseas buscar? :");
        String temporada = bc.nextLine();
        boolean salir2 = false;
        while (!salir2) {
            System.out.println("");
            System.out.println("Por que deseas buscar :");
            System.out.println("   1-Categoria");
            System.out.println("   2-Fecha");
            System.out.println("");
            System.out.print("Que quieres hacer: ");
            int opcionTemp = bc.nextInt();
            while (opcionTemp > 2 || opcionTemp < 1) {
                System.out.print("Esa no es una opcion posible: ");
                opcionTemp = bc.nextInt();
            }
            System.out.println("");
            switch (opcionTemp) {
                case 1:
                    Scanner cat = new Scanner(System.in);
                    try {
                        //CONSULTAMOS LAS CATEGORIAS DISPONIBLES
                        consultarCategoria(con, temporada);
                    } catch (SQLException e) {
                        System.out.println("ERROR EN LA CONSULTA("
                                + e.getMessage() + ")");
                        salir2 = true;
                        break;
                    }
                    //SELECCIONAMOS UNA CATEGORIA
                    System.out.print("¿Que categoria? :");
                    String categoria = cat.nextLine();
                    System.out.println("");
                    try {
                        consultarCategoriaDef(con, temporada, categoria);
                    } catch (SQLException e) {
                        System.out.println("ERROR EN LA CONSULTA("
                                + e.getMessage() + ")");
                        salir2 = true;
                        break;
                    }
                    salir2 = true;
                    break;
                case 2:
                    Scanner fec = new Scanner(System.in);
                    try {
                        //CONSULTAMOS LAS CATEGORIAS DISPONIBLES
                        consultarFecha(con, temporada);
                    } catch (SQLException e) {
                        System.out.println("ERROR EN LA CONSULTA("
                                + e.getMessage() + ")");
                        salir2 = true;
                        break;
                    }
                    System.out.print("¿Que Fecha? :");//SELECCIONAMOS UNA FECHA
                    String fecha = fec.nextLine();
                    System.out.println("");
                    try {
                        consultarFechaDef(con, temporada, fecha);
                    } catch (SQLException e) {
                        System.out.println("ERROR EN LA CONSULTA("
                                + e.getMessage() + ")");
                        salir2 = true;
                        break;
                    }
                    salir2 = true;
                    break;
            }
        }
        return false;
    }

    //INCTRODUCIR PARTIDOS
    /**
     * INTRODUCE DE MANERA MANUAL UN PARTIDO EN LA BASE DE DATOS
     * @param con
     */
    public static void introducirPartido(Connection con) {//INTRODUCIR PARTIDO
        Scanner partnue = new Scanner(System.in);
        Partido partidoNuevo = new Partido();//CREAMOS EL PARTIDO
        partidoNuevo.introducir_datos_manual();//INTRODUCIMOS LOS DATOS
        partidoNuevo.mostrar_datos_partido();//MOSTRAMOS LOS DATOS INTRODUCIDOS
        System.out.print("¿ESTAN LOS DATOS CORRECTOS?(SI/NO): ");
        String respuesta = partnue.nextLine();
        while (!"NO".equals(respuesta) && !"SI".equals(respuesta)
                && !"no".equals(respuesta) && !"si".equals(respuesta)) {
            System.out.print("Solo puede ser SI o NO: ");
            respuesta = partnue.nextLine();
        }
        if ("SI".equals(respuesta) || "si".equals(respuesta)) {
            insertarPartido(con, partidoNuevo);
        } else {
            System.out.println("El partido no se ha añadido a la base de datos.");
        }
    }

    //CONSULTAR LA TEMPORADA
    /**
     * REALIZA UNA CONSULTA A LA BASE DE DATOS PARA OBTENER LAS TEMPORADAS
     * DISPOINBLES
     *
     * @param con
     * @throws SQLException
     */
    public static void consultarTemporada(Connection con) throws SQLException {
        ResultSet sr = null;
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            sr = stmt.executeQuery("SELECT DISTINCT Temporada FROM partidos;");
            System.out.println("=============");
            System.out.println("= TEMPORADA =");
            System.out.println("=============");
            while (sr.next()) {
                System.out.println("= " + sr.getString(1) + " =");
            }
            System.out.println("=============");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ").");
        } finally {
            sr.close();
        }
    }

    //CONSULTAR LA CATEGORIA
    /**
     * REALIZA UNA CONSULTA A LA BASE DE DATOS PARA OBTENER LAS CATEGORIAS
     * DISPONIBLES
     *
     * @param con
     * @param temporada
     * @throws SQLException
     */
    public static void consultarCategoria(Connection con, String temporada)
            throws SQLException {
        ResultSet sr = null;
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            sr = stmt.executeQuery("SELECT DISTINCT categoria FROM partidos "
                    + "WHERE temporada='" + "" + temporada + "';");
            System.out.println("===================");
            System.out.println("=    CATEGORIA    =");
            System.out.println("===================");
            while (sr.next()) {
                System.out.println(" " + sr.getString(1));
            }
            System.out.println("===================");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ").");
        } finally {
            sr.close();
        }
    }

    //CONSULTA CATEGORIA
    /**
     * REALIZA UNA CONSULTA A LA BASE DE DATOS SEGUN LA CATEGORIA Y LA TEMPORADA
     * QUE LE HEMOS INTRODUCIDO ANTERIORMENTE
     *
     * @param con
     * @param temporada
     * @param categoria
     * @throws SQLException
     */
    public static void consultarCategoriaDef(Connection con, String temporada,
            String categoria) throws SQLException {
        ResultSet sr = null;
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            sr = stmt.executeQuery("SELECT Temporada ,Categoria ,Fecha, "
                    + "Jornada, Local,Visitante, Resultado  FROM "
                    + "partidos WHERE temporada='" + temporada
                    + "' AND categoria='" + categoria + "';");
            System.out.println("============================================="
                    + "=================================");
            System.out.println("TEMPORADA || CATEGORIA || FECHA || JORNADA || "
                    + "LOCAL || VISITANTE || RESULTADO");
            System.out.println("==============================================="
                    + "===============================");
            while (sr.next()) {
                System.out.println(sr.getString(1) + " | " + sr.getString(2)
                        + " | " + sr.getString(3) + " | "
                        + sr.getString(4) + " | " + sr.getString(5) + " | "
                        + sr.getString(6) + " | " + sr.getString(7));
            }
            System.out.println("==============================================="
                    + "===============================");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ").");
        } finally {
            sr.close();
        }
    }

    //CONSULTAR FECHA
    /**
     * REALIZA UNA CONSULTA A LA BASE DE DATOS PARA OBTENER LAS FECHAS
     * DISPONIBLES
     *
     * @param con
     * @param temporada
     * @throws SQLException
     */
    public static void consultarFecha(Connection con, String temporada)
            throws SQLException {
        ResultSet sr = null;
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            sr = stmt.executeQuery("SELECT DISTINCT fecha FROM partidos WHERE "
                    + "temporada='" + "" + temporada + "';");
            System.out.println("=================");
            System.out.println("=     FECHA     =");
            System.out.println("=================");
            while (sr.next()) {
                System.out.println(" " + sr.getString(1));
            }
            System.out.println("=================");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ").");
        } finally {
            sr.close();
        }
    }

    //CONSULTAR FECHA
    /**
     * REALIZA UNA CONSULTA A LA BASE DE DATOS SEGUN LA FECHA Y LA TEMPORADA QUE
     * LE HEMOS INTRODUCIDO ANTERIORMENTE
     *
     * @param con
     * @param temporada
     * @param fecha
     * @throws SQLException
     */
    public static void consultarFechaDef(Connection con, String temporada,
            String fecha) throws SQLException {
        ResultSet sr = null;
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            sr = stmt.executeQuery("SELECT Temporada ,Categoria ,Fecha, "
                    + "Jornada, Local,Visitante, Resultado  FROM "
                    + "partidos WHERE temporada='" + temporada + "' AND fecha='"
                    + fecha + "';");
            System.out.println("==============================================="
                    + "===============================");
            System.out.println("TEMPORADA || CATEGORIA || FECHA || JORNADA || "
                    + "LOCAL || VISITANTE || RESULTADO");
            System.out.println("==============================================="
                    + "===============================");
            while (sr.next()) {
                System.out.println(sr.getString(1) + " | " + sr.getString(2)
                        + " | " + sr.getString(3) + " | "
                        + sr.getString(4) + " | " + sr.getString(5) + " | "
                        + sr.getString(6) + " | " + sr.getString(7));
            }
            System.out.println("==============================================="
                    + "===============================");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ").");
        } finally {
            sr.close();
        }
    }

    //INSERTAR PARTIDO
    /**
     * INTRODUCE UN PARTIDO EN LA BASE DE DATOS CON LOS DATOS QUE LE HEMOS
     * INTRODUCIDO ANTERIORMENTE
     *
     * @param con
     * @param partidoNuevo
     */
    public static void insertarPartido(Connection con, Partido partidoNuevo) {
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            String insert = "INSERT INTO partidos (Temporada,Categoria,Fecha,"
                    + "Jornada,Local,Visitante,"
                    + "golesLocal,golesVisitante,Resultado) VALUES('"
                    + partidoNuevo.getTemporada()
                    + "','" + partidoNuevo.getCategoria() + "','"
                    + partidoNuevo.getFecha() + "',"
                    + partidoNuevo.getJornada() + ",'"
                    + partidoNuevo.getLocal() + "','"
                    + partidoNuevo.getVisitante() + "',"
                    + partidoNuevo.getGoles_local() + ","
                    + partidoNuevo.getGoles_visitante() + ",'"
                    + partidoNuevo.getResultado() + "');";
            stmt.executeUpdate(insert);
            System.out.println("El partido se ha añadido a la base de datos.");
        } catch (SQLException e) {
            System.out.println("ERROR AL INSERTAR DATOS(" + e.getMessage()
                    + ").");
        }
    }

    //INSERTAR PARTIDOS EN EL ARCHIVO
    /**
     * INTRODUCE UN PARTIDO EN LA BASE DE DATOS CON LOS DATOS QUE HEMOS OBTENIDO
     * ANTERIORMENTE DE UN FICHERO
     *
     * @param con
     * @param partidoNuevo2
     */
    public static void insertarPartidoArchivo(Connection con,
            Partido partidoNuevo2) {
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            String insert = "INSERT INTO partidos (Temporada,Categoria,Fecha,"
                    + "Jornada,Local,Visitante,"
                    + "golesLocal,golesVisitante,Resultado) VALUES('"
                    + partidoNuevo2.getTemporada()
                    + "','" + partidoNuevo2.getCategoria() + "','"
                    + partidoNuevo2.getFecha() + "',"
                    + partidoNuevo2.getJornada() + ",'"
                    + partidoNuevo2.getLocal() + "','"
                    + partidoNuevo2.getVisitante() + "',"
                    + partidoNuevo2.getGoles_local() + ","
                    + partidoNuevo2.getGoles_visitante() + ",'"
                    + partidoNuevo2.getResultado() + "');";
            stmt.executeUpdate(insert);
        } catch (SQLException e) {
            System.out.println("ERROR AL INSERTAR DATOS(" + e.getMessage()
                    + ").");
        }
    }

    //BUSCAR FICHERO
    /**
     * BUSCA LOS FICHEROS DISPONIBLES EN LA CARPETA
     *
     * @param con
     */
    private static void buscarPartidoArchivo(Connection con) throws
            FileNotFoundException, IOException {
        Scanner guardado = new Scanner(System.in);
        System.out.println("Archivos disponibles:");
        buscarArchivos();
        System.out.print("¿Que archivo quieres cargar?(escribelo): ");
        String partidaSeleccionada = guardado.nextLine() + ".txt";
        //LEER ARCHIVO
        FileReader fr = null;
        File archivo = new File("C:\\Users\\mange\\Dropbox\\DAW\\DAW-1.2\\"
                + "Pruebas\\Partidos\\" + partidaSeleccionada);
        fr = new FileReader(archivo);
        BufferedReader br = new BufferedReader(fr, 10);
        leerFichero(br, con);
    }

    //LISTAR ARCHIVOS 
    /**
     * LISTA LOS FICHEROS DE LA CARPETA
     */
    public static void buscarArchivos() {
        System.out.println("");
        String path = "C:/Users/mange/Dropbox/DAW/DAW-1.2/Pruebas/Partidos/";
        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        int num = 0;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                num = num + 1;
            }
            {
                files = listOfFile.getName();
                if (files.endsWith(".txt") || files.endsWith(".TXT")) {
                    System.out.println(num + " - " + files);
                }
            }
        }
        System.out.println("");
    }

    //LEER EL FICHERO
    /**
     * LEE EL FICHERO SELECCIONADA CON ANTERIORDAD
     *
     * @param br
     * @param con
     * @throws IOException
     */
    public static void leerFichero(BufferedReader br, Connection con)
            throws IOException {//LEEMOS EL ARCHIVO 
        String linea;
        String temporada = "";
        String categoria = "";
        Partido partidoNuevo2 = new Partido();
        System.out.println("===== PARTIDOS AÑADIDOS =====");
        System.out.println("");
        while ((linea = br.readLine()) != null) {
            String vector[] = linea.split("=");
            partidoNuevo2.setTemporada(vector[0]);
            partidoNuevo2.setCategoria(vector[1]);
            partidoNuevo2.setFecha(vector[2]);
            int jorna = Integer.parseInt(vector[3]);
            partidoNuevo2.setJornada(jorna);
            partidoNuevo2.setLocal(vector[4]);
            partidoNuevo2.setVisitante(vector[5]);
            int Loc = Integer.parseInt(vector[6]);
            partidoNuevo2.setGoles_local(Loc);
            int Vis = Integer.parseInt(vector[7]);
            partidoNuevo2.setGoles_visitante(Vis);
            partidoNuevo2.setResultado(vector[8]);
            insertarPartidoArchivo(con, partidoNuevo2);
            partidoNuevo2.mostrar_datos_partido();
        }
    }

    //GUARDAR PARTIDA
    /**
     * GUARDA PARTIDOS EN UN FICHERO NUEVO
     *
     * @param con
     * @return
     */
    public static boolean guardarPartido(Connection con) {
        Scanner bc = new Scanner(System.in);
        try {
            consultarTemporada(con);
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ")");
            return true;
        }
        System.out.print("¿De que temporada es el partido que deseas buscar? :");
        String temporada = bc.nextLine();
        try {
            consultarCategoria(con, temporada);
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ")");
        }
        System.out.print("¿Que categoria? :");
        String categoria = bc.nextLine();
        System.out.println("");
        try {
            guardarPartidaArchivo(con, temporada, categoria);
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ")");
        } catch (IOException i) {
            System.out.println("ERROR AL GUARDAR LA PARTIDA("
                    + i.getMessage() + ")");
        }
        return false;
    }

    //GUARDAR PARTIDA EN EL ARCHIVO
    /**
     * CREA EL FICHERO E INTRODUCE EN EL FICHERO LOS DATOS DE LOS PARTIDOS
     *
     * @param con
     * @param temporada
     * @param categoria
     * @throws SQLException
     * @throws IOException
     */
    public static void guardarPartidaArchivo(Connection con, String temporada,
            String categoria) throws SQLException, IOException {
        ResultSet sr = null;
        FileWriter fichero = new FileWriter("C:\\Users\\mange\\Dropbox\\DAW\\"
                + "DAW-1.2\\Pruebas\\Partidos\\Partidos Temporada " + temporada
                + " Categoria " + categoria + ".txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw = new PrintWriter(fichero);
        try {
            java.sql.Statement stmt = (java.sql.Statement) con.createStatement();
            sr = stmt.executeQuery("SELECT Temporada ,Categoria ,Fecha, Jornada,"
                    + " Local,Visitante, Resultado  FROM "
                    + "partidos WHERE temporada='" + temporada
                    + "' AND categoria='" + categoria + "';");
            pw.println("TEMPORADA || CATEGORIA || FECHA || JORNADA || LOCAL || "
                    + "VISITANTE || RESULTADO");
            while (sr.next()) {
                pw.println(sr.getString(1) + " | " + sr.getString(2) + " | "
                        + sr.getString(3) + " | " + sr.getString(4) + " | "
                        + sr.getString(5) + " | " + sr.getString(6) + " | "
                        + sr.getString(7));
            }
            cerrarFicheroEscritura(pw);
        } catch (SQLException e) {
            System.out.println("ERROR EN LA CONSULTA(" + e.getMessage() + ").");
        } finally {
            sr.close();
        }
    }

    //OBTENER CONEXION
    /**
     * NOS PERMITE OBETENET CONEXION CON LA BASE DE DATOS DE LOS PARTIDOS
     *
     * @return
     * @throws SQLException
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Partidos"
                + "?verifyServerCertificate=false"
                + "&useSSL=true", "root", "Juancarlos-5");
    }

    //CERRAR LA CONEXION
    /**
     * CIERRA LA CONEXION CON LA BASE DE DATOS
     *
     * @param con
     */
    public static void cerrarConexion(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //CERRAR FICHERO

    /**
     * CIERRA EL FICHERO
     *
     * @param pw
     */
    public static void cerrarFicheroEscritura(PrintWriter pw) {
        try {
            if (null != pw) {
                pw.close();
            }
        } catch (Exception e) {
            System.out.println("ERROR AL CERRAR EL FICHERO(" + e.getMessage()
                    + ").");
        }
    }

}
