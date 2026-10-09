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
import java.sql.Date;
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

    private DefaultTableModel modeloConductores;

    private JTable tablaTelefonos;
    private DefaultTableModel modeloTelefonos;
    private JTextField txtNuevoTelefono;

    private JTable tablaAsignaciones;
    private DefaultTableModel modeloAsignaciones;
    private JComboBox<String> cbVehiculos;
    private JTextField txtFechaAsignacion;

    public VentanaConductores() {
        setTitle("Gestión Integral de Conductores");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        iniciarComponentes();
        cargarConductores();
        cargarComboVehiculos();
    }

    private void iniciarComponentes() {
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setPreferredSize(new Dimension(500, 0));

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 6, 6));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Conductor"));

        panelForm.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelForm.add(txtDni);

        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panelForm.add(txtDireccion);

        panelForm.add(new JLabel("Salario:"));
        txtSalario = new JTextField();
        panelForm.add(txtSalario);

        panelForm.add(new JLabel("Cód. Municipio:"));
        txtCodMunicipio = new JTextField();
        panelForm.add(txtCodMunicipio);

        JPanel panelBotonesCond = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnInsertar = new JButton("Insertar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelBotonesCond.add(btnInsertar);
        panelBotonesCond.add(btnActualizar);
        panelBotonesCond.add(btnEliminar);
        panelBotonesCond.add(btnLimpiar);

        JPanel panelArribaIzquierda = new JPanel(new BorderLayout());
        panelArribaIzquierda.add(panelForm, BorderLayout.CENTER);
        panelArribaIzquierda.add(panelBotonesCond, BorderLayout.SOUTH);
        panelIzquierdo.add(panelArribaIzquierda, BorderLayout.NORTH);

        txtNuevoTelefono = new JTextField(10);

        ((javax.swing.text.AbstractDocument) txtNuevoTelefono.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (string == null) {
                    return;
                }
                if (string.matches("\\d+") && (fb.getDocument().getLength() + string.length() <= 9)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if (text == null) {
                    return;
                }
                if (text.matches("\\d*") && (fb.getDocument().getLength() - length + text.length() <= 9)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JButton btnAddTel = new JButton("Añadir");
        JButton btnDelTel = new JButton("Eliminar");

        modeloConductores = new DefaultTableModel(new String[]{"DNI", "Nombre", "Dirección", "Salario", "Municipio"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaConductores = new JTable(modeloConductores);
        tablaConductores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollConductores = new JScrollPane(tablaConductores);
        scrollConductores.setBorder(BorderFactory.createTitledBorder("Conductores"));
        panelIzquierdo.add(scrollConductores, BorderLayout.CENTER);

        add(panelIzquierdo, BorderLayout.WEST);

        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel panelTel = new JPanel(new BorderLayout(5, 5));
        panelTel.setBorder(BorderFactory.createTitledBorder("Teléfonos del Conductor Seleccionado"));

        modeloTelefonos = new DefaultTableModel(new String[]{"Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaTelefonos = new JTable(modeloTelefonos);
        panelTel.add(new JScrollPane(tablaTelefonos), BorderLayout.CENTER);

        JPanel panelControlesTel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        txtNuevoTelefono = new JTextField(10);
        panelControlesTel.add(new JLabel("Tel:"));
        panelControlesTel.add(txtNuevoTelefono);
        panelControlesTel.add(btnAddTel);
        panelControlesTel.add(btnDelTel);
        panelTel.add(panelControlesTel, BorderLayout.SOUTH);

        panelDerecho.add(panelTel);

        JPanel panelAsig = new JPanel(new BorderLayout(5, 5));
        panelAsig.setBorder(BorderFactory.createTitledBorder("Vehículos Asignados"));

        modeloAsignaciones = new DefaultTableModel(new String[]{"Matrícula", "Fecha"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaAsignaciones = new JTable(modeloAsignaciones);
        panelAsig.add(new JScrollPane(tablaAsignaciones), BorderLayout.CENTER);

        JPanel panelControlesAsig = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        cbVehiculos = new JComboBox<>();
        txtFechaAsignacion = new JTextField(8);
        JButton btnAddAsig = new JButton("Asignar");
        JButton btnDelAsig = new JButton("Quitar");

        panelControlesAsig.add(new JLabel("Vehículo:"));
        panelControlesAsig.add(cbVehiculos);
        panelControlesAsig.add(new JLabel("Fecha (YYYY-MM-DD):"));
        panelControlesAsig.add(txtFechaAsignacion);
        panelControlesAsig.add(btnAddAsig);
        panelControlesAsig.add(btnDelAsig);
        panelAsig.add(panelControlesAsig, BorderLayout.SOUTH);

        panelDerecho.add(panelAsig);

        add(panelDerecho, BorderLayout.CENTER);

        tablaConductores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaConductores.getSelectedRow() != -1) {
                int fila = tablaConductores.getSelectedRow();
                String dni = modeloConductores.getValueAt(fila, 0).toString();
                txtDni.setText(dni);
                txtNombre.setText(modeloConductores.getValueAt(fila, 1).toString());
                Object dir = modeloConductores.getValueAt(fila, 2);
                txtDireccion.setText(dir != null ? dir.toString() : "");
                Object sal = modeloConductores.getValueAt(fila, 3);
                txtSalario.setText(sal != null ? sal.toString() : "");
                txtCodMunicipio.setText(modeloConductores.getValueAt(fila, 4).toString());
                txtDni.setEditable(false);

                cargarTelefonos(dni);
                cargarAsignaciones(dni);
            }
        });

        btnInsertar.addActionListener(e -> insertarConductor());
        btnActualizar.addActionListener(e -> actualizarConductor());
        btnEliminar.addActionListener(e -> eliminarConductor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        btnAddTel.addActionListener(e -> insertarTelefono());
        btnDelTel.addActionListener(e -> eliminarTelefono());

        btnAddAsig.addActionListener(e -> insertarAsignacion());
        btnDelAsig.addActionListener(e -> eliminarAsignacion());
    }

    private void cargarConductores() {
        modeloConductores.setRowCount(0);
        String sql = "SELECT dni, nombre, direccion, salario, cod_municipio FROM Conductor";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloConductores.addRow(new Object[]{
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("direccion"),
                    rs.getBigDecimal("salario"),
                    rs.getInt("cod_municipio")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar conductores: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTelefonos(String dni) {
        modeloTelefonos.setRowCount(0);
        String sql = "SELECT telefono FROM Telefono_Conductor WHERE dni_conductor = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloTelefonos.addRow(new Object[]{rs.getString("telefono")});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar teléfonos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarAsignaciones(String dni) {
        modeloAsignaciones.setRowCount(0);
        String sql = "SELECT matricula, fecha FROM Conductor_Vehiculo WHERE dni_conductor = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloAsignaciones.addRow(new Object[]{
                        rs.getString("matricula"),
                        rs.getDate("fecha")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar asignaciones: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboVehiculos() {
        cbVehiculos.removeAllItems();
        String sql = "SELECT matricula FROM Vehiculo";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbVehiculos.addItem(rs.getString("matricula"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar combo vehículos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertarConductor() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String salarioStr = txtSalario.getText().trim();
        String codMunStr = txtCodMunicipio.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || salarioStr.isEmpty() || codMunStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Campos obligatorios: DNI, Nombre, Salario y Municipio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Conductor (dni, nombre, direccion, salario, cod_municipio) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, direccion.isEmpty() ? null : direccion);
            ps.setBigDecimal(4, new BigDecimal(salarioStr));
            ps.setInt(5, Integer.parseInt(codMunStr));
            ps.executeUpdate();

            limpiarFormulario();
            cargarConductores();
            JOptionPane.showMessageDialog(this, "Conductor registrado.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar conductor: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Formato numérico no válido en salario o municipio.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarConductor() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String salarioStr = txtSalario.getText().trim();
        String codMunStr = txtCodMunicipio.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || salarioStr.isEmpty() || codMunStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un conductor primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Conductor SET nombre = ?, direccion = ?, salario = ?, cod_municipio = ? WHERE dni = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion.isEmpty() ? null : direccion);
            ps.setBigDecimal(3, new BigDecimal(salarioStr));
            ps.setInt(4, Integer.parseInt(codMunStr));
            ps.setString(5, dni);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                limpiarFormulario();
                cargarConductores();
                JOptionPane.showMessageDialog(this, "Conductor actualizado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Formato numérico incorrecto.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarConductor() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un conductor de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar conductor " + dni + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Conductor WHERE dni = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                limpiarFormulario();
                cargarConductores();
                JOptionPane.showMessageDialog(this, "Conductor eliminado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: tiene paquetes o vehículos asignados.", "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertarTelefono() {
        String dni = txtDni.getText().trim();
        String tel = txtNuevoTelefono.getText().trim();

        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un conductor de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!tel.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener exactamente 9 dígitos numéricos.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Telefono_Conductor (dni_conductor, telefono) VALUES (?, ?)";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, tel);
            ps.executeUpdate();
            txtNuevoTelefono.setText("");
            cargarTelefonos(dni);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al insertar teléfono: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTelefono() {
        String dni = txtDni.getText().trim();
        int fila = tablaTelefonos.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona el teléfono a eliminar en la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tel = modeloTelefonos.getValueAt(fila, 0).toString();
        String sql = "DELETE FROM Telefono_Conductor WHERE dni_conductor = ? AND telefono = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, tel);
            ps.executeUpdate();
            cargarTelefonos(dni);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar teléfono: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertarAsignacion() {
        String dni = txtDni.getText().trim();
        String mat = cbVehiculos.getSelectedItem() != null ? cbVehiculos.getSelectedItem().toString() : "";
        String fechaStr = txtFechaAsignacion.getText().trim();

        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un conductor de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (mat.isEmpty() || fechaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona vehículo e indica la fecha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Conductor_Vehiculo (dni_conductor, matricula, fecha) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, mat);
            ps.setDate(3, Date.valueOf(fechaStr));
            ps.executeUpdate();
            txtFechaAsignacion.setText("");
            cargarAsignaciones(dni);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Usa el formato de fecha YYYY-MM-DD.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al asignar vehículo: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarAsignacion() {
        String dni = txtDni.getText().trim();
        int fila = tablaAsignaciones.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una asignación en la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mat = modeloAsignaciones.getValueAt(fila, 0).toString();
        String fechaStr = modeloAsignaciones.getValueAt(fila, 1).toString();

        String sql = "DELETE FROM Conductor_Vehiculo WHERE dni_conductor = ? AND matricula = ? AND fecha = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, mat);
            ps.setDate(3, Date.valueOf(fechaStr));
            ps.executeUpdate();
            cargarAsignaciones(dni);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar asignación: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtDni.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtSalario.setText("");
        txtCodMunicipio.setText("");
        txtNuevoTelefono.setText("");
        txtFechaAsignacion.setText("");
        txtDni.setEditable(true);
        tablaConductores.clearSelection();
        modeloTelefonos.setRowCount(0);
        modeloAsignaciones.setRowCount(0);
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
