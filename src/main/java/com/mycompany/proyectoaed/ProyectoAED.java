package com.mycompany.proyectoaed;

import javax.swing.SwingUtilities;

public class ProyectoAED {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}