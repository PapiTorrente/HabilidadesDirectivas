package com.yael.burbujas;

//Importar conexión a base de datos
import com.yael.burbujas.conexionMySQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Burbujas extends JFrame implements ActionListener {

    private static final int TamanioMaximoArreglo = 20;
    private int TamanioActualArreglo;
    private static final int ValorMaximoRandom = 20;

    private JTextField campoTextoArregloOriginal;
    private JTextField campoTextoArregloOrdenado;
    private JTextArea consolaProgreso;
    private JButton botonGenerarNumero;
    private JButton botonOrdenarArreglo;
    private JButton botonGuardarEnBD;
    private int[] arregloOriginal;
    private int[] arregloOrdenado;

    public Burbujas() {
        setTitle("Ordenamiento Burbuja");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Márgenes para los componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Expande horizontalmente
        gbc.weightx = 1.0; // Distribuye espacio horizontal extra

        // Inicializar componentes
        campoTextoArregloOriginal = new JTextField(30);
        campoTextoArregloOriginal.setEditable(true);
        campoTextoArregloOrdenado = new JTextField(30);
        campoTextoArregloOrdenado.setEditable(false);
        consolaProgreso = new JTextArea(8, 60);
        consolaProgreso.setEditable(false);
        JScrollPane panelDesplazamientoConsola = new JScrollPane(
                consolaProgreso,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        botonGenerarNumero = new JButton("Generar Arreglo");
        botonOrdenarArreglo = new JButton("Ordenar Arreglo");
        botonOrdenarArreglo.setEnabled(true);
        botonGuardarEnBD = new JButton("Guardar en DB");
        botonGuardarEnBD.setEnabled(false); // Deshabilitar hasta que se ordene el arreglo
        

        // Agregar listeners a los botones
        botonGenerarNumero.addActionListener(this);
        botonOrdenarArreglo.addActionListener(this);
        botonGuardarEnBD.addActionListener(this);

        // Añadir componentes al frame
        //Texto 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Arreglo Aleatorio:"), gbc);
        //Campo de texto 1
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(campoTextoArregloOriginal, gbc);
        //Botón 1
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(botonGenerarNumero, gbc);
        //Texto 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Arreglo Ordenado:"), gbc);
        //Campo de texto 2
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(campoTextoArregloOrdenado, gbc);
        //Botón 2
        gbc.gridx = 2;
        gbc.gridy = 2;
        add(botonOrdenarArreglo, gbc);

        //Campo de texto para la consola
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        add(panelDesplazamientoConsola, gbc);
        
        //Botón generar BD
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(botonGuardarEnBD, gbc);

        pack();
        setLocationRelativeTo(null); // Centrar la ventana
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Asegurar que la GUI se cree en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(Burbujas::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonGenerarNumero) {
            generarArreglo();
            botonGuardarEnBD.setEnabled(false); // Deshabilitar el botón de guardar al generar un nuevo arreglo
            consolaProgreso.setText("");
        } else if (e.getSource() == botonOrdenarArreglo) {
            if(campoTextoArregloOriginal.getText() != null && verificarTamanio(campoTextoArregloOriginal.getText())){
                if(arregloOriginal != null) { //En caso de que se haya generado automáticamente
                    ordenarArreglo();
                } else {
                    guardarArregloEnArreglo(campoTextoArregloOriginal.getText());
                    ordenarArreglo();
                }
                
                botonGuardarEnBD.setEnabled(true); // Habilitar el botón de guardar después de ordenar
            } else {
                JOptionPane.showMessageDialog(this, "Ingresa una secuencia de números válida de máximo 20, entre espacios.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } else if (e.getSource() == botonGuardarEnBD) {
            try {
                saveDataToDatabase(Arrays.toString(arregloOriginal).replaceAll("[\\[\\],]", ""),
                        Arrays.toString(arregloOrdenado).replaceAll("[\\[\\],]", ""));
            } catch (SQLException ex) {
                Logger.getLogger(Burbujas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void generarArreglo() {
        SecureRandom random = new SecureRandom();
        arregloOriginal = new int[TamanioMaximoArreglo];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TamanioMaximoArreglo; i++) {
            arregloOriginal[i] = random.nextInt(ValorMaximoRandom) + 1;
            sb.append(arregloOriginal[i]).append(" ");
        }
        campoTextoArregloOriginal.setText(sb.toString().trim());
        campoTextoArregloOrdenado.setText(""); // Limpiar el resultado anterior
    }
    
    private boolean verificarTamanio(String texto){
        int espacios = 0;
        for(int num = 0; num < texto.length(); num++){
            if(texto.charAt(num) == ' '){
                espacios++;
            }
        }
        if(espacios < 20){
            TamanioActualArreglo = espacios + 1;
            return true;
        }
        return false;
    }
    
    private void guardarArregloEnArreglo(String texto){
        String numero = "";
        int espacio = 0;
        arregloOriginal = new int[TamanioActualArreglo];
        for(int indice = 0; indice < texto.length(); indice++){
            if (texto.charAt(indice) == ' '){
                espacio++;
                arregloOriginal[espacio - 1] = Integer.parseInt(numero);
                numero = "";
            } else {
                numero += texto.charAt(indice);
            }
        }
        if(numero != null){
            arregloOriginal[espacio] = Integer.parseInt(numero);
        }
        System.out.println("Finalizado");
    }

    private void ordenarArreglo() {
        arregloOrdenado = Arrays.copyOf(arregloOriginal, arregloOriginal.length);
        if (arregloOrdenado == null || arregloOrdenado.length == 0) {
            return;
        }
        int n = arregloOrdenado.length;
        int temp;
        
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arregloOrdenado[j] > arregloOrdenado[j + 1]) {
                    temp = arregloOrdenado[j];
                    arregloOrdenado[j] = arregloOrdenado[j + 1];
                    arregloOrdenado[j + 1] = temp;
                    consolaProgreso.append("> " + Arrays.toString(arregloOrdenado).replaceAll("[\\[\\],]", "") + "\n");
                }
            }
        }
        campoTextoArregloOrdenado.setText(Arrays.toString(arregloOrdenado).replaceAll("[\\[\\],]", ""));
    }
    
    private void saveDataToDatabase(String arregloOriginal, String arregloOrdenado) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/burbuja";
        String usuario = "root";
        String contraseña = "12345678";

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
            PreparedStatement statement = conexion.prepareStatement("INSERT INTO cambios_burbuja (caracter, caracter_acomo) VALUES (?, ?)")) {

            statement.setString(1, arregloOriginal);
            statement.setString(2, arregloOrdenado);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Datos guardados en la base de datos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudieron guardar los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            }    
    }
}