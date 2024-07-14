package org.example;

import org.example.Conexion.ConnectionSQL;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static org.example.Conexion.ConnectionSQL.getConnection;

public class Usuarios extends JFrame {
    private JPanel JPrincipal;
    private JTextField jtfnombre;
    private JTextField jtfDNI;
    private JTextField jtftelefono;
    private JTextField jtfapellido;
    private JTextField jtffecha_nac;
    private JTextField jtfdireccion;
    private JButton ingresarButton;
    private JButton consultarButton;
    private JTextField jtfsexo;
    private JTextField jtfcorreo;
    private JTable table;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JButton guardarButton;
    PreparedStatement ps;
    Statement st;
    ResultSet r;
    DefaultTableModel mod;


    public Usuarios() {
        ConnectionSQL connectionSQL = ConnectionSQL.getInstance();
        mod = new DefaultTableModel(new String[]{"Código", "Nombre", "ApellidoPaterno","ApellidoMaterno", "FechaNac"
        , "Direccion", "Teléfono", "DNI", "Sexo", "Correo"}, 0);
        table.setModel(mod);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        eliminarButton.setEnabled(true);
                        actualizarButton.setEnabled(true);
                        guardarButton.setEnabled(true);
                    } else {
                        eliminarButton.setEnabled(false);
                        actualizarButton.setEnabled(false);
                        guardarButton.setEnabled(false);
                    }
                }
            }
        });
        consultarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    insertar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatos();
            }
        });
        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actualizar();
                    JOptionPane.showMessageDialog(null, "Datos guardados");
                    jtfnombre.setText("");
                    jtfapellido.setText("");
                    jtffecha_nac.setText("");
                    jtfdireccion.setText("");
                    jtftelefono.setText("");
                    jtfDNI.setText("");
                    jtfsexo.setText("");
                    jtfcorreo.setText("");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    eliminar();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void listar() throws SQLException{
        Connection connection = getConnection();
        if (connection != null) {
            st = connection.createStatement();
            r = st.executeQuery("SELECT * FROM Usuario");
            mod.setRowCount(0);
            while (r.next()) {
                mod.addRow(new Object[]{
                        r.getInt("codigo"),
                        r.getString("nombre"),
                        r.getString("apellido_Paterno"),
                        r.getString("apellido_Materno"),
                        r.getString("fecha_nac"),
                        r.getString("direccion"),
                        r.getString("telefono"),
                        r.getString("DNI"),
                        r.getString("sexo"),
                        r.getString("correo"),
                });
            }
        } else {
            throw new SQLException("No se pudo establecer la conexión con la base de datos");
        }
    }

    public void insertar() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false); // Iniciar la transacción

            if (connection != null) {
                ps = connection.prepareStatement("INSERT INTO Usuario (nombre, apellido_Paterno, apellido_Materno, fecha_nac, direccion, telefono, DNI, sexo, correo) VALUES (?,?,?,?,?,?,?,?,?)");

                // Obtener valores de los campos de texto
                String nombre = jtfnombre.getText();
                String[] apellidos = jtfapellido.getText().split(" ");
                String fechaNac = jtffecha_nac.getText();
                String direccion = jtfdireccion.getText();
                String telefono = jtftelefono.getText();
                String dni = jtfDNI.getText();
                String sexo = jtfsexo.getText();
                String correo = jtfcorreo.getText();

                // Setear valores en el PreparedStatement
                ps.setString(1, nombre);

                if (apellidos.length > 0) {
                    ps.setString(2, apellidos[0]); // Primer apellido
                } else {
                    ps.setNull(2, Types.VARCHAR); // Campo apellido_Paterno puede ser nulo
                }

                if (apellidos.length > 1) {
                    ps.setString(3, apellidos[1]); // Segundo apellido si existe
                } else {
                    ps.setNull(3, Types.VARCHAR); // Campo apellido_Materno puede ser nulo
                }

                ps.setString(4, fechaNac);
                ps.setString(5, direccion.isEmpty() ? null : direccion); // Campo dirección puede ser nulo
                ps.setString(6, telefono.isEmpty() ? null : telefono); // Campo teléfono puede ser nulo
                ps.setString(7, dni);
                ps.setString(8, sexo.isEmpty() ? null : sexo); // Campo sexo puede ser nulo
                ps.setString(9, correo.isEmpty() ? null : correo); // Campo correo puede ser nulo

                // Ejecutar la inserción
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    // Limpiar los campos después de la inserción exitosa
                    jtfnombre.setText("");
                    jtfapellido.setText("");
                    jtffecha_nac.setText("");
                    jtfdireccion.setText("");
                    jtftelefono.setText("");
                    jtfDNI.setText("");
                    jtfsexo.setText("");
                    jtfcorreo.setText("");

                    // Actualizar la lista si es necesario
                    listar();

                    // Confirmar la transacción
                    connection.commit();
                } else {
                    throw new SQLException("No se insertaron filas en la base de datos.");
                }
            } else {
                throw new SQLException("No se pudo establecer la conexión con la base de datos.");
            }
        } catch (SQLException ex) {
            // Rollback en caso de error
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    // Manejar el error de rollback si es necesario
                    rollbackEx.printStackTrace();
                }
            }
            ex.printStackTrace(); // Imprimir la excepción o manejarla según tus necesidades
        } finally {
            // Cerrar PreparedStatement y Connection
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Restaurar el modo de autocommit
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }


    public void eliminar() throws SQLException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int response = JOptionPane.showConfirmDialog(null, "¿Estás seguro que desea eliminar la columna?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                int codigo = (int) mod.getValueAt(selectedRow, 0);
                Connection connection = getConnection();
                if (connection != null) {
                    ps = connection.prepareStatement("DELETE FROM Usuario WHERE codigo = ?");
                    ps.setInt(1, codigo);
                    if (ps.executeUpdate() > 0) {
                        mod.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(null, "Registro eliminado");
                    }
                } else {
                    throw new SQLException("No se pudo establecer la conexión con la base de datos");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar");
        }
    }

    public void cargarDatos() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            jtfnombre.setText(mod.getValueAt(selectedRow, 1).toString());
            String apellidopaterno = mod.getValueAt(selectedRow,2).toString();
            String apellidomaterno = mod.getValueAt(selectedRow,3).toString();
            jtfapellido.setText(apellidopaterno + " " + apellidomaterno);
            jtffecha_nac.setText(mod.getValueAt(selectedRow, 4).toString());
            jtfdireccion.setText(mod.getValueAt(selectedRow, 5).toString());
            jtftelefono.setText(mod.getValueAt(selectedRow, 6).toString());
            jtfDNI.setText(mod.getValueAt(selectedRow, 7).toString());
            jtfsexo.setText(mod.getValueAt(selectedRow, 8).toString());
            jtfcorreo.setText(mod.getValueAt(selectedRow, 9).toString());
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fila para actualizar");
        }
    }

    public void actualizar() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection();

            if (connection != null) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow >= 0) {
                    int codigo = (int) mod.getValueAt(selectedRow, 0);

                    // Dividir el campo de apellidos correctamente
                    String[] apellidos = jtfapellido.getText().split(" ");
                    String apellidoPaterno = apellidos.length > 0 ? apellidos[0] : "";
                    String apellidoMaterno = apellidos.length > 1 ? apellidos[1] : "";

                    ps = connection.prepareStatement("UPDATE Usuario SET nombre = ?, apellido_Paterno = ?, apellido_Materno = ?, fecha_nac = ?, direccion = ?, telefono = ?, DNI = ?, sexo = ?, correo = ? WHERE codigo = ?");

                    ps.setString(1, jtfnombre.getText());
                    ps.setString(2, apellidoPaterno);
                    ps.setString(3, apellidoMaterno);
                    ps.setString(4, jtffecha_nac.getText());
                    ps.setString(5, jtfdireccion.getText());
                    ps.setString(6, jtftelefono.getText());
                    ps.setString(7, jtfDNI.getText());
                    ps.setString(8, jtfsexo.getText());
                    ps.setString(9, jtfcorreo.getText());
                    ps.setInt(10, codigo);

                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        listar(); // Actualizar la tabla después de la operación exitosa
                    } else {
                        throw new SQLException("No se pudo actualizar el registro.");
                    }
                } else {
                    throw new SQLException("No se ha seleccionado ninguna fila para actualizar.");
                }
            } else {
                throw new SQLException("No se pudo establecer la conexión con la base de datos.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Imprimir la excepción o manejarla según tus necesidades
        } finally {
            // Cerrar PreparedStatement y Connection
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Usuarios u = new Usuarios();
        u.setContentPane(new Usuarios().JPrincipal);
        u.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        u.setSize(1300,820);
        u.setVisible(true);
        u.pack();
        u.setLocationRelativeTo(null);

        UIManager.put("OptionPane.background", new Color(40, 40, 40));
        UIManager.put("Panel.background", new Color(40,40,40));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(184,134,11));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("OptionPane.messageBackground", new Color(40,40,40));
        UIManager.put("OptionPane.foreground", Color.WHITE);
    }
}
