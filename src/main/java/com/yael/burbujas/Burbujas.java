package com.yael.burbujas;

//imposrts db
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

    private static final int ARRAY_SIZE = 20;
    private static final int MAX_RANDOM = 10;

    private JTextField originalArrayTextField;
    private JTextField sortedArrayTextField;
    private JButton generateButton;
    private JButton sortButton;
    private JButton saveToDBButton;
    private int[] arrayToSort;
    private int[] sortedArray;

    public Burbujas() {
        setTitle("Ordenamiento Burbuja");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Inicializar componentes
        originalArrayTextField = new JTextField(30); // Aumentar el ancho para mostrar más números
        originalArrayTextField.setEditable(false);
        sortedArrayTextField = new JTextField(30); // Aumentar el ancho
        sortedArrayTextField.setEditable(false);
        generateButton = new JButton("Generar Arreglo");
        sortButton = new JButton("Ordenar Arreglo");
        sortButton.setEnabled(false); // Deshabilitar hasta que se genere un arreglo
        saveToDBButton = new JButton("Guardar en DB"); // Inicializar el nuevo botón
        saveToDBButton.setEnabled(false); // Deshabilitar hasta que se ordene el arreglo
        

        // Agregar listeners a los botones
        generateButton.addActionListener(this);
        sortButton.addActionListener(this);
        saveToDBButton.addActionListener(this);

        // Añadir componentes al frame
        add(new JLabel("Arreglo Aleatorio:"));
        add(originalArrayTextField);
        add(generateButton);
        add(new JLabel("Arreglo Ordenado:"));
        add(sortedArrayTextField);
        add(sortButton);
        add(saveToDBButton);

        pack();
        setLocationRelativeTo(null); // Centrar la ventana
        setVisible(true);
    }

    public static void main(String[] args) {
        // Asegurar que la GUI se cree en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(Burbujas::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            generateRandomArray();
            sortButton.setEnabled(true);
            saveToDBButton.setEnabled(false); // Deshabilitar el botón de guardar al generar un nuevo arreglo
        } else if (e.getSource() == sortButton) {
            sortArray();
            saveToDBButton.setEnabled(true); // Habilitar el botón de guardar después de ordenar
        } else if (e.getSource() == saveToDBButton) {
            try {
                saveDataToDatabase(Arrays.toString(arrayToSort).replaceAll("[\\[\\],]", ""),
                        Arrays.toString(sortedArray).replaceAll("[\\[\\],]", ""));
            } catch (SQLException ex) {
                Logger.getLogger(Burbujas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void generateRandomArray() {
        SecureRandom random = new SecureRandom();
        arrayToSort = new int[ARRAY_SIZE];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            arrayToSort[i] = random.nextInt(MAX_RANDOM) + 1;
            sb.append(arrayToSort[i]).append(" ");
        }
        originalArrayTextField.setText(sb.toString().trim());
        sortedArrayTextField.setText(""); // Limpiar el resultado anterior
    }

    private void sortArray() {
        sortedArray = Arrays.copyOf(arrayToSort, arrayToSort.length);
        if (sortedArray == null || sortedArray.length == 0) {
            return;
        }
        int n = sortedArray.length;
        int temp;
        
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedArray[j] > sortedArray[j + 1]) {
                    temp = sortedArray[j];
                    sortedArray[j] = sortedArray[j + 1];
                    sortedArray[j + 1] = temp;
                }
            }
        }
        sortedArrayTextField.setText(Arrays.toString(sortedArray).replaceAll("[\\[\\],]", ""));
    }
    
    private void saveDataToDatabase(String arrayToSort, String sortedArray) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/burbuja";
        String usuario = "root";
        String contraseña = "12345678";

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
             PreparedStatement statement = conexion.prepareStatement("INSERT INTO cambios_burbuja (caracter, caracter_acomo) VALUES (?, ?)")) {

            statement.setString(1, arrayToSort);
            statement.setString(2, sortedArray);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Datos guardados en la base de datos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudieron guardar los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            }    
    }
}

 

