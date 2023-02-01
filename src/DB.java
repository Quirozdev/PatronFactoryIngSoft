import java.sql.*;
import java.util.Properties;

public abstract class DB {
    private Connection connection;
    private String servidor;
    private Integer puerto;
    private String nombreBaseDeDatos;
    private Usuario usuario;

    public DB(String servidor, Integer puerto, String nombreBaseDeDatos, Usuario usuario) {
        this.servidor = servidor;
        this.puerto = puerto;
        this.nombreBaseDeDatos = nombreBaseDeDatos;
        this.usuario = usuario;
        this.connection = conectarBD(crearURLDeConexion());
    }

    public DB(String servidor, String nombreBaseDeDatos, Usuario usuario) {
        this.servidor = servidor;
        this.nombreBaseDeDatos = nombreBaseDeDatos;
        this.usuario = usuario;
        this.connection = conectarBD(crearURLDeConexion());
    }

    public String getServidor() {
        return servidor;
    }

    public Integer getPuerto() {
        return puerto;
    }

    public String getNombreBaseDeDatos() {
        return nombreBaseDeDatos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Connection getConnection() {
        return connection;
    }

    public abstract String crearURLDeConexion();

    public abstract void renombrarTabla(String nombreTabla, String nuevoNombre);


    // conecta a la base de datos y regresa un objeto de tipo Connection
    private Connection conectarBD(String url) {
        Connection connection = null;
        try {
            Properties propiedades = new Properties();
            propiedades.put("user", this.getUsuario().getNombre());
            propiedades.put("password", this.getUsuario().getContrasenia());
            connection = DriverManager.getConnection(crearURLDeConexion(), propiedades);
            if (connection != null) {
                System.out.println("Conexion exitosa a la base de datos");
            }
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        return connection;
    }

    // lee los datos de una tabla dada y se pueden especificar los nombres de columnas a filtrar,
    // si el arreglo nombreColumnasAMostrar esta vacio, entonces se regresan todas las columnas (*)
    public ResultSet leerTabla(String nombreTabla, String[] nombreColumnasAMostrar) {
        ResultSet datosTabla = null;
        try {
            // con el objeto statement se pueden realizar las querys
            Statement statement = this.getConnection().createStatement();
            // vamos a ir formando la query
            StringBuilder query = new StringBuilder("SELECT ");
            // se valida si el arreglo nombreColumnasAMostrar no esta vacio
            if (nombreColumnasAMostrar.length != 0) {
                // se recorre el arreglo de columnas a mostrar hasta el penultimo elemento
                for (int i = 0; i < nombreColumnasAMostrar.length - 1; i++) {
                    query.append(nombreColumnasAMostrar[i] + ", ");
                    // SELECT estado, fecha, id FROM asistencias;
                }
                // el ultimo elemento se agrega sin un ", " para que la query funcione
                query.append(nombreColumnasAMostrar[nombreColumnasAMostrar.length - 1]);
            } else {
                // si el arreglo nombreColumnasAMostrar esta vacio, entonces la query quedara como:
                // SELECT * FROM (nombreTabla);
                query.append("*");
            }
            query.append(" FROM " + nombreTabla + ";");
            // se ejecuta la query
            datosTabla = statement.executeQuery(query.toString());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return datosTabla;
    }

    // sirve para mostrar los datos obtenidos de la funcion leerTabla()
    public void mostrarDatosTabla(ResultSet datosTabla) {
        try {
            // con esto se obtienen la cantidad de columnas en una tabla
            int cantidadColumnas = datosTabla.getMetaData().getColumnCount();
            // para imprimir los nombres de las columnas / encabezado
            for (int i = 1; i <= cantidadColumnas; i++) {
                System.out.print(datosTabla.getMetaData().getColumnName(i) + " ");
            }
            System.out.println();
            while (datosTabla.next()) {
                // el indice de columnas empieza desde 1 y no 0
                for (int i = 1; i <= cantidadColumnas; i++) {
                    System.out.print(datosTabla.getString(i) + " ");
                }
                System.out.println();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    // sirve para insertar datos en una tabla especifica, tambien recibe como argumentos los datos de cada columna
    // en un arreglo de Strings
    public void insertarDatos(String nombreTabla, String[] datos) {
        // se lee la tabla para obtener sus metadatos
        ResultSet datosTabla = leerTabla(nombreTabla, new String[]{});
        try {
            ResultSetMetaData metaDatosTabla = datosTabla.getMetaData();
            int numDeColumnas = metaDatosTabla.getColumnCount();
            // esto es para checar que el usuario ingrese los datos para todas las columnas
            if (datos.length != numDeColumnas) {
                System.out.println("Datos insuficientes");
                return;
            }
            // vamos a ir formando la query de insercion
            StringBuilder query = new StringBuilder(String.format("INSERT INTO %s (", nombreTabla));
            String nombreDeColumna = "";
            // primero se agregan los nombres de las columnas
            for (int i = 1; i < numDeColumnas; i++) {
                nombreDeColumna = metaDatosTabla.getColumnName(i);
                query.append(nombreDeColumna + ", ");
            }
            query.append(metaDatosTabla.getColumnName(numDeColumnas));
            query.append(") VALUES (");
            // aqui se cicla sobre los valores
            for (int i = 0; i < datos.length - 1; i++) {
                // se agregan las comillas simples entre los valores, las comillas dobles no se puede porque
                // postgres solo acepta las comillas simples
                query.append("\'" + datos[i] + "\'" + ", ");
            }
            query.append("\'" + datos[datos.length - 1] + "\'");
            query.append(");");
            Statement statement = this.getConnection().createStatement();
            System.out.println(query);
            statement.execute(query.toString());
            System.out.println("Escritura de datos exitosa");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }
}
