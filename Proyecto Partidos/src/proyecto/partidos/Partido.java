package proyecto.partidos;

import java.util.Scanner;

public class Partido {

    String temporada;
    String categoria;
    String fecha;
    int jornada;
    String local;
    String visitante;
    int goles_local;
    int goles_visitante;
    String resultado;

    public Partido(String temporada, String categoria, String fecha, int jornada, String local, String visitante, int goles_local, int goles_visitante, String resultado) {
        this.temporada = temporada;
        this.categoria = categoria;
        this.fecha = fecha;
        this.jornada = jornada;
        this.local = local;
        this.visitante = visitante;
        this.goles_local = goles_local;
        this.goles_visitante = goles_visitante;
        this.resultado = resultado;
    }

    public Partido() {
    }

    //GETTERS
    public String getTemporada() {
        return temporada;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFecha() {
        return fecha;
    }

    public int getJornada() {
        return jornada;
    }

    public String getLocal() {
        return local;
    }

    public String getVisitante() {
        return visitante;
    }

    public int getGoles_local() {
        return goles_local;
    }

    public int getGoles_visitante() {
        return goles_visitante;
    }

    public String getResultado() {
        return resultado;
    }

    //SETTERS
    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setJornada(int jornada) {
        this.jornada = jornada;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setVisitante(String visitante) {
        this.visitante = visitante;
    }

    public void setGoles_local(int goles_local) {
        this.goles_local = goles_local;
    }

    public void setGoles_visitante(int goles_visitante) {
        this.goles_visitante = goles_visitante;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    //METODOS
    public void introducir_datos_manual() {
        Scanner entrada = new Scanner(System.in);
        System.out.println("=================================");
        System.out.println("Introduce los datos del Partido");
        System.out.println("=================================");
        System.out.print("Temporada: ");
        this.temporada = entrada.nextLine();
        System.out.print("Categoria: ");
        this.categoria = entrada.nextLine();
        System.out.print("Fecha: ");
        this.fecha = entrada.nextLine();
        System.out.print("Jornada: ");
        String jor = entrada.nextLine();
        int jorna = Integer.parseInt(jor);
        while (jorna < 1) {
            System.out.print("No puede ser que la jornada sea menor que 1: ");
            jor = entrada.nextLine();
            jorna = Integer.parseInt(jor);
        }
        this.jornada = jorna;
        System.out.print("Local: ");
        this.local = entrada.nextLine();
        System.out.print("Visitante: ");
        this.visitante = entrada.nextLine();
        System.out.print("Goles Locales: ");
        int golesLoc = entrada.nextInt();
        while (golesLoc < 0) {
            System.out.print("No puede ser que hayan marcado menos de 0 goles: ");
            golesLoc = entrada.nextInt();
        }
        this.goles_local = golesLoc;
        System.out.print("Goles Visitante: ");
        int golesVis = entrada.nextInt();
        while (golesVis < 0) {
            System.out.print("No puede ser que hayan marcado menos de 0 goles: ");
            golesVis = entrada.nextInt();
        }
        this.goles_visitante = golesVis;
        String Loc = Integer.toString(this.getGoles_local());
        String Vis = Integer.toString(this.getGoles_visitante());
        this.resultado = Loc + "-" + Vis;
    }

    public void mostrar_datos_partido() {
        System.out.println("");
        System.out.println("DATOS PARTIDO:");
        System.out.println("");
        System.out.println("Temporada : " + this.getTemporada());
        System.out.println("Categoria : " + this.getCategoria());
        System.out.println("Fecha : " + this.getFecha());
        System.out.println("Jornada : " + this.getJornada());
        System.out.println("Local : " + this.getLocal());
        System.out.println("Visitante : " + this.getVisitante());
        System.out.println("Resultado : " + this.getResultado());
        System.out.println("");
    }
}
