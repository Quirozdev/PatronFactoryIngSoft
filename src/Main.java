public class Main {
    public static void main(String[] args) {

        MySQL mySQL = new MySQL("localhost", 3306, "prueba", new Usuario("root", "luis1234"));
        mySQL.mostrarDatosTabla(mySQL.leerTabla("asistencias", new String[] {"estado", "fecha", "id"}));
        mySQL.insertarDatos("asistencias", new String[]{"877", "2023-01-29", "Falta", "367", "gruposustentabilidad"});
        // mySQL.renombrarTabla("asistencias", "nuevasasistencias");

        PostgreSQL postgreSQL = new PostgreSQL("localhost", 5432, "prueba", new Usuario("postgres", "luis1234"));
        postgreSQL.mostrarDatosTabla(postgreSQL.leerTabla("usuarios", new String[]{}));
        postgreSQL.insertarDatos("usuarios", new String[]{"295", "si", "no"});
        // postgreSQL.renombrarTabla("nuevosusuarios", "usuarios");
    }
}