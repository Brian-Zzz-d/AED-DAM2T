package com.mycompany.proyectoaed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
// Dirección de la base de datos

    private static final String URL
            = "jdbc:mariadb://localhost:3306/empresa_logistica";
// Usuario y contraseña de MariaDB
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    public static Connection conectar() throws SQLException {
// Abre la conexión y la devuelve
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
