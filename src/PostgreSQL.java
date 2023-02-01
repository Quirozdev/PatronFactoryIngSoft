import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSQL extends DB{
    public PostgreSQL(String servidor, Integer puerto, String nombreBaseDeDatos, Usuario usuario) {
        super(servidor, puerto, nombreBaseDeDatos, usuario);
    }

    @Override
    public String crearURLDeConexion() {
        String nombreSMBD = this.getClass().getSimpleName().toLowerCase();
        String url =String.format("jdbc:%s://%s:%s/%s", nombreSMBD, this.getServidor(), this.getPuerto(), this.getNombreBaseDeDatos());
        return url;
    }

    @Override
    public void renombrarTabla(String nombreTabla, String nuevoNombre) {
        try {
            Statement statement = this.getConnection().createStatement();
            String query = String.format("ALTER TABLE %s RENAME TO %s;", nombreTabla, nuevoNombre);
            statement.execute(query);
            System.out.println("Se cambio el nombre de la tabla " + nombreTabla + " a " + nuevoNombre);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
