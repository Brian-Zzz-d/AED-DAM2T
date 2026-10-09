/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.proyectoaed;

/**
 *
 * @author aleri
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VentanaConductores extends javax.swing.JFrame {

    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtSalario;
    private JTextField txtCodMunicipio;

    private JTable tablaConductores;
    private DefaultTableModel modeloTabla;

    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    

    public VentanaConductores() {
        setTitle("Gestión de Conductores");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        iniciarComponentes();
        cargarDatosTabla();
    }

    private void iniciarComponentes() {
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Conductor"));

        panelFormulario.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panelFormulario.add(txtDireccion);

        panelFormulario.add(new JLabel("Salario:"));
        txtSalario = new JTextField();
        panelFormulario.add(txtSalario);

        panelFormulario.add(new JLabel("Cód. Municipio:"));
        txtCodMunicipio = new JTextField();
        panelFormulario.add(txtCodMunicipio);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar Campos");

        panelBotones.add(btnInsertar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        String[] columnas = {"DNI", "Nombre", "Dirección", "Salario", "Cód. Municipio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaConductores = new JTable(modeloTabla);
        tablaConductores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabla = new JScrollPane(tablaConductores);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Listado de Conductores"));
        add(scrollTabla, BorderLayout.CENTER);

        tablaConductores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaConductores.getSelectedRow() != -1) {
                int fila = tablaConductores.getSelectedRow();
                txtDni.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                Object dir = modeloTabla.getValueAt(fila, 2);
                txtDireccion.setText(dir != null ? dir.toString() : "");
                Object sal = modeloTabla.getValueAt(fila, 3);
                txtSalario.setText(sal != null ? sal.toString() : "");
                txtCodMunicipio.setText(modeloTabla.getValueAt(fila, 4).toString());
                txtDni.setEditable(false);
            }
        });

        btnInsertar.addActionListener(e -> insertarConductor());
        btnActualizar.addActionListener(e -> actualizarConductor());
        btnEliminar.addActionListener(e -> eliminarConductor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT dni, nombre, direccion, salario, cod_municipio FROM Conductor";

        try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql); ResultSet rs = sentencia.executeQuery()) {

            while (rs.next()) {
                Object[] fila = new Object[]{
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("direccion"),
                    rs.getBigDecimal("salario"),
                    rs.getInt("cod_municipio")
                };
                modeloTabla.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertarConductor() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String salarioStr = txtSalario.getText().trim();
        String codMunStr = txtCodMunicipio.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || salarioStr.isEmpty() || codMunStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena los campos obligatorios (DNI, Nombre, Salario y Municipio).", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal salario = new BigDecimal(salarioStr);
            int codMunicipio = Integer.parseInt(codMunStr);

            String sql = "INSERT INTO Conductor (dni, nombre, direccion, salario, cod_municipio) VALUES (?, ?, ?, ?, ?)";

            try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql)) {

                sentencia.setString(1, dni);
                sentencia.setString(2, nombre);
                sentencia.setString(3, direccion.isEmpty() ? null : direccion);
                sentencia.setBigDecimal(4, salario);
                sentencia.setInt(5, codMunicipio);

                sentencia.executeUpdate();
                JOptionPane.showMessageDialog(this, "Conductor insertado correctamente.");
                limpiarFormulario();
                cargarDatosTabla();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al insertar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El salario o el código de municipio tienen un formato numérico incorrecto.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarConductor() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String salarioStr = txtSalario.getText().trim();
        String codMunStr = txtCodMunicipio.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || salarioStr.isEmpty() || codMunStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un registro de la tabla o indica el DNI.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal salario = new BigDecimal(salarioStr);
            int codMunicipio = Integer.parseInt(codMunStr);

            String sql = "UPDATE Conductor SET nombre = ?, direccion = ?, salario = ?, cod_municipio = ? WHERE dni = ?";

            try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql)) {

                sentencia.setString(1, nombre);
                sentencia.setString(2, direccion.isEmpty() ? null : direccion);
                sentencia.setBigDecimal(3, salario);
                sentencia.setInt(4, codMunicipio);
                sentencia.setString(5, dni);

                int filas = sentencia.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(this, "Conductor actualizado correctamente.");
                    limpiarFormulario();
                    cargarDatosTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el conductor con DNI " + dni, "Aviso", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El salario o el código de municipio tienen un formato numérico incorrecto.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarConductor() {
        String dni = txtDni.getText().trim();

        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce el DNI del conductor o selecciónalo en la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar al conductor con DNI: " + dni + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Conductor WHERE dni = ?";

        try (Connection conexion = ConexionDB.conectar(); PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, dni);
            int filas = sentencia.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Conductor eliminado correctamente.");
                limpiarFormulario();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún conductor con ese DNI.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtDni.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtSalario.setText("");
        txtCodMunicipio.setText("");
        txtDni.setEditable(true);
        tablaConductores.clearSelection();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new VentanaConductores().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
