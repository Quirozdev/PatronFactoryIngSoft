import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends DB {
    public MySQL(String servidor, int puerto, String nombreBaseDeDatos, Usuario usuario) {
        super(servidor, puerto, nombreBaseDeDatos, usuario);
    }


    @Override
    public String crearURLDeConexion() {
        // con esto se obtiene el nombre de la clase hijo que hereda de esta clase abstracta, por ejemplo si una instancia
        // de MySQL usa este metodo, el nombre que se obtiene es el de esa clase (MySQL).
        // se pasa a minusculas porque asi se suele poner en los parametros de conexion
        String nombreSMBD = this.getClass().getSimpleName().toLowerCase();
        // se forma la url con los atributos de la clase
        String url =String.format("jdbc:%s://%s:%s/%s", nombreSMBD, this.getServidor(), this.getPuerto(), this.getNombreBaseDeDatos());
        // jdbc:mysql://localhost:3306/prueba
        // jdbc:postgresql://localhost:5504/prueba
        return url;
    }

    @Override
    public void renombrarTabla(String nombreTabla, String nuevoNombre) {
        try {
            Statement statement = this.getConnection().createStatement();
            String query = String.format("RENAME TABLE %s TO %s;", nombreTabla, nuevoNombre);
            statement.execute(query);
            System.out.println("Se cambio el nombre de la tabla " + nombreTabla + " a " + nuevoNombre);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
