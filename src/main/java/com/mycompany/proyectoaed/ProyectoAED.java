package com.mycompany.proyectoaed;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProyectoAED {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conexion = ConexionDB.conectar()) {
            System.out.println("Conexión realizada correctamente.");
        } catch (SQLException e) {
// Muestra el motivo si falla la conexión
            System.out.println("Error de conexión: " + e.getMessage());
        }

        int opcion = -1;

        do {
            System.out.println("\n===== GESTIÓN DE CONDUCTORES =====");
            System.out.println("1. Registrar conductor");
            System.out.println("2. Ver conductores");
            System.out.println("3. Actualizar conductor");
            System.out.println("4. Eliminar conductor");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1:
                        insertarConductor(scanner);
                        break;
                    case 2:
                        mostrarConductores();
                        break;
                    case 3:
                        actualizarConductor(scanner);
                        break;
                    case 4:
                        eliminarConductor(scanner);
                        break;
                    case 0:
                        System.out.println("Saliendo de la aplicación...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un número válido.");
            }

        } while (opcion != 0);

    }

    private static void insertarConductor(Scanner sc) {
        System.out.println("\n--- REGISTRAR CONDUCTOR ---");

        System.out.print("Introduce el DNI: ");
        String dni = sc.nextLine().trim();

        System.out.print("Introduce el nombre: ");
        String nombre = sc.nextLine().trim();

        System.out.print("Introduce la dirección: ");
        String direccion = sc.nextLine().trim();

        System.out.print("Introduce el salario: ");
        BigDecimal salario = new BigDecimal(sc.nextLine().trim());

        System.out.print("Introduce el código de municipio: ");
        int codMunicipio = Integer.parseInt(sc.nextLine().trim());

        String sql = "INSERT INTO Conductor (dni, nombre, direccion, salario, cod_municipio) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, dni);
            sentencia.setString(2, nombre);
            sentencia.setString(3, direccion.isEmpty() ? null : direccion);
            sentencia.setBigDecimal(4, salario);
            sentencia.setInt(5, codMunicipio);

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Conductor insertado correctamente.");
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar conductor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error en el formato de los datos numéricos.");
        }
    }

    private static void mostrarConductores() {
        System.out.println("\n--- LISTADO DE CONDUCTORES ---");
        String sql = "SELECT dni, nombre, direccion, salario, cod_municipio FROM Conductor";

        try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql); ResultSet rs = sentencia.executeQuery()) {

            boolean hayDatos = false;

            while (rs.next()) {
                hayDatos = true;
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String direccion = rs.getString("direccion");
                BigDecimal salario = rs.getBigDecimal("salario");
                int codMunicipio = rs.getInt("cod_municipio");

                System.out.printf("DNI: %s | Nombre: %s | Dirección: %s | Salario: %s | Municipio: %d%n",
                        dni,
                        nombre,
                        (direccion != null ? direccion : "Sin registrar"),
                        (salario != null ? salario.toString() : "0.00"),
                        codMunicipio);
            }

            if (!hayDatos) {
                System.out.println("No hay conductores registrados en la base de datos.");
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar conductores: " + e.getMessage());
        }
    }

    private static void actualizarConductor(Scanner sc) {
        System.out.println("\n--- ACTUALIZAR CONDUCTOR ---");

        System.out.print("Introduce el DNI del conductor a modificar: ");
        String dni = sc.nextLine().trim();

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine().trim();

        System.out.print("Nueva dirección: ");
        String direccion = sc.nextLine().trim();

        System.out.print("Nuevo salario: ");
        BigDecimal salario = new BigDecimal(sc.nextLine().trim());

        System.out.print("Nuevo código de municipio: ");
        int codMunicipio = Integer.parseInt(sc.nextLine().trim());

        String sql = "UPDATE Conductor SET nombre = ?, direccion = ?, salario = ?, cod_municipio = ? WHERE dni = ?";

        try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, direccion.isEmpty() ? null : direccion);
            sentencia.setBigDecimal(3, salario);
            sentencia.setInt(4, codMunicipio);
            sentencia.setString(5, dni);

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Conductor actualizado correctamente.");
            } else {
                System.out.println("No se encontró ningún conductor con el DNI: " + dni);
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar conductor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Formato de número no válido.");
        }
    }

    private static void eliminarConductor(Scanner sc) {
        System.out.println("\n--- ELIMINAR CONDUCTOR ---");

        System.out.print("Introduce el DNI del conductor a eliminar: ");
        String dni = sc.nextLine().trim();

        String sql = "DELETE FROM Conductor WHERE dni = ?";

        try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, dni);

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Conductor eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún conductor con el DNI: " + dni);
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar conductor: " + e.getMessage());
        }
    }
}
