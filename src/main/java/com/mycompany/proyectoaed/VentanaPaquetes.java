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
public class VentanaPaquetes extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPaquetes.class.getName());

    /**
     * Creates new form VentanaPaquetes
     */
    private JTextField txtCodPaquete;
    private JTextField txtDescripcion;
    private JTextField txtDestinatario;
    private JTextField txtDireccion;
    private JTextField txtDniConductor;
    private JTextField txtCodMunicipio;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaPaquetes() {
        setTitle("Gestión de Paquetes");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        iniciarComponentes();
        cargarTabla();
    }

    private void iniciarComponentes() {
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Paquete"));

        form.add(new JLabel("Código Paquete (Auto):"));
        txtCodPaquete = new JTextField();
        txtCodPaquete.setEditable(false);
        form.add(txtCodPaquete);

        form.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        form.add(txtDescripcion);

        form.add(new JLabel("Destinatario:"));
        txtDestinatario = new JTextField();
        form.add(txtDestinatario);

        form.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        form.add(txtDireccion);

        form.add(new JLabel("DNI Conductor:"));
        txtDniConductor = new JTextField();
        form.add(txtDniConductor);

        form.add(new JLabel("Cód. Municipio:"));
        txtCodMunicipio = new JTextField();
        form.add(txtCodMunicipio);

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

        modeloTabla = new DefaultTableModel(new String[]{"Código", "Descripción", "Destinatario", "Dirección", "DNI Conductor", "Municipio"}, 0) {
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
                txtCodPaquete.setText(modeloTabla.getValueAt(fila, 0).toString());
                Object desc = modeloTabla.getValueAt(fila, 1);
                txtDescripcion.setText(desc != null ? desc.toString() : "");
                txtDestinatario.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtDireccion.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtDniConductor.setText(modeloTabla.getValueAt(fila, 4).toString());
                txtCodMunicipio.setText(modeloTabla.getValueAt(fila, 5).toString());
            }
        });

        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT cod_paquete, descripcion, destinatario, direccion, dni_conductor, cod_municipio FROM Paquete";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("cod_paquete"),
                    rs.getString("descripcion"),
                    rs.getString("destinatario"),
                    rs.getString("direccion"),
                    rs.getString("dni_conductor"),
                    rs.getInt("cod_municipio")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar paquetes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertar() {
        String desc = txtDescripcion.getText().trim();
        String dest = txtDestinatario.getText().trim();
        String dir = txtDireccion.getText().trim();
        String dni = txtDniConductor.getText().trim();
        String codMunStr = txtCodMunicipio.getText().trim();

        if (dest.isEmpty() || dir.isEmpty() || dni.isEmpty() || codMunStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Destinatario, dirección, DNI y municipio son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Paquete (descripcion, destinatario, direccion, dni_conductor, cod_municipio) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, desc.isEmpty() ? null : desc);
            ps.setString(2, dest);
            ps.setString(3, dir);
            ps.setString(4, dni);
            ps.setInt(5, Integer.parseInt(codMunStr));
            ps.executeUpdate();
            limpiar();
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Paquete registrado.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al insertar (revisa que el DNI y código de municipio existan): " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código de municipio debe ser un número entero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizar() {
        String idStr = txtCodPaquete.getText().trim();
        String desc = txtDescripcion.getText().trim();
        String dest = txtDestinatario.getText().trim();
        String dir = txtDireccion.getText().trim();
        String dni = txtDniConductor.getText().trim();
        String codMunStr = txtCodMunicipio.getText().trim();

        if (idStr.isEmpty() || dest.isEmpty() || dir.isEmpty() || dni.isEmpty() || codMunStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un paquete de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Paquete SET descripcion = ?, destinatario = ?, direccion = ?, dni_conductor = ?, cod_municipio = ? WHERE cod_paquete = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, desc.isEmpty() ? null : desc);
            ps.setString(2, dest);
            ps.setString(3, dir);
            ps.setString(4, dni);
            ps.setInt(5, Integer.parseInt(codMunStr));
            ps.setInt(6, Integer.parseInt(idStr));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                limpiar();
                cargarTabla();
                JOptionPane.showMessageDialog(this, "Paquete actualizado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código de municipio debe ser numérico.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminar() {
        String idStr = txtCodPaquete.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un paquete de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar paquete " + idStr + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Paquete WHERE cod_paquete = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(idStr));
            ps.executeUpdate();
            limpiar();
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Paquete eliminado.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtCodPaquete.setText("");
        txtDescripcion.setText("");
        txtDestinatario.setText("");
        txtDireccion.setText("");
        txtDniConductor.setText("");
        txtCodMunicipio.setText("");
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
        java.awt.EventQueue.invokeLater(() -> new VentanaPaquetes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
