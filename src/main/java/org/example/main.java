package org.example;

import org.example.Conexion.ConnectionSQL;

import java.sql.*;

import static org.example.Conexion.ConnectionSQL.getConnection;

public class main {
    public static void main(String[] args) throws SQLException {
        ConnectionSQL connectionSQL = ConnectionSQL.getInstance();
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM Usuario");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int codigo = rs.getInt("codigo");
            String nombre = rs.getString("nombre");
            String apellido_Paterno = rs.getString("apellido_Paterno");
            String apellido_Materno = rs.getString("apellido_Materno");
            Date fecha_nac = rs.getDate("fecha_nac");
            String direccion = rs.getString("direccion");
            int telefono = rs.getInt("telefono");
            String DNI = rs.getString("DNI");
            String sexo = rs.getString("sexo");
            String correo = rs.getString("correo");

            System.out.println("codigo" + codigo + "nombre" + nombre + "apellido_Paterno" + apellido_Paterno +
                    "apellido_Materno" + apellido_Materno + "fecha_nac" + fecha_nac + "direccion" + direccion +
                    "telefono" + telefono + "DNI" + DNI + "sexo" + sexo + "correo" + correo);
        }
    }
}
