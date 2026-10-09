/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.proyectoaed;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author aleri
 */
public class VentanaVehiculos extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaVehiculos.class.getName());

    /**
     * Creates new form VentanaVehiculos
     */
    private JTextField txtMatricula;
    private JTextField txtModelo;
    private JTextField txtPotencia;
    private JComboBox<String> cbTipo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaVehiculos() {
        setTitle("Gestión de Vehículos");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        iniciarComponentes();
        cargarTabla();
    }

    private void iniciarComponentes() {
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Vehículo"));

        form.add(new JLabel("Matrícula:"));
        txtMatricula = new JTextField();
        form.add(txtMatricula);

        form.add(new JLabel("Modelo:"));
        txtModelo = new JTextField();
        form.add(txtModelo);

        form.add(new JLabel("Potencia (CV):"));
        txtPotencia = new JTextField();
        form.add(txtPotencia);

        form.add(new JLabel("Tipo:"));
        cbTipo = new JComboBox<>(new String[]{"coche", "moto"});
        form.add(cbTipo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnInsertar = new JButton("Insertar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        botones.add(btnInsertar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(form, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"Matrícula", "Modelo", "Potencia", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                txtMatricula.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtModelo.setText(modeloTabla.getValueAt(fila, 1).toString());
                Object pot = modeloTabla.getValueAt(fila, 2);
                txtPotencia.setText(pot != null ? pot.toString() : "");
                cbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
                txtMatricula.setEditable(false);
            }
        });

        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT matricula, modelo, potencia, tipo FROM Vehiculo";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getString("matricula"),
                    rs.getString("modelo"),
                    rs.getInt("potencia"),
                    rs.getString("tipo")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertar() {
        String matricula = txtMatricula.getText().trim();
        String modelo = txtModelo.getText().trim();
        String potStr = txtPotencia.getText().trim();
        String tipo = cbTipo.getSelectedItem().toString();

        if (matricula.isEmpty() || modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Matrícula y modelo son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Vehiculo (matricula, modelo, potencia, tipo) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            ps.setString(2, modelo);
            if (potStr.isEmpty()) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, Integer.parseInt(potStr));
            }
            ps.setString(4, tipo);
            ps.executeUpdate();
            limpiar();
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Vehículo guardado correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al insertar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La potencia debe ser numérica.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizar() {
        String matricula = txtMatricula.getText().trim();
        String modelo = txtModelo.getText().trim();
        String potStr = txtPotencia.getText().trim();
        String tipo = cbTipo.getSelectedItem().toString();

        if (matricula.isEmpty() || modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Vehiculo SET modelo = ?, potencia = ?, tipo = ? WHERE matricula = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, modelo);
            if (potStr.isEmpty()) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, Integer.parseInt(potStr));
            }
            ps.setString(3, tipo);
            ps.setString(4, matricula);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                limpiar();
                cargarTabla();
                JOptionPane.showMessageDialog(this, "Vehículo actualizado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La potencia debe ser numérica.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminar() {
        String matricula = txtMatricula.getText().trim();
        if (matricula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar vehículo " + matricula + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Vehiculo WHERE matricula = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            ps.executeUpdate();
            limpiar();
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Vehículo eliminado.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: tiene registros asociados (FK).", "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtMatricula.setText("");
        txtModelo.setText("");
        txtPotencia.setText("");
        cbTipo.setSelectedIndex(0);
        txtMatricula.setEditable(true);
        tabla.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VentanaVehiculos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
