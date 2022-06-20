package DAO;

import modelos.Admin;
import modelos.Tecnico;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOAdminSQL implements DAOAdmin{

    //INSERT-----------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean insert(Admin admin, DAOManager dao) {
        String sentencia;
        sentencia = "INSERT INTO admins VALUES(" + admin.getId() + ", '" + admin.getNombre() + "', '" + admin.getApellido() + "', '" + admin.getEmail() + "', '" + admin.getClave() + "', false);";
        try (Statement stmt = dao.getConn().createStatement()) {
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    //DELETE-----------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean delete(int id, DAOManager dao) {
        String sentencia;
        sentencia = "DELETE FROM admins WHERE id = " + id + ";";
        try (Statement stmt = dao.getConn().createStatement()) {
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //READ-----------------------------------------------------------------------------------------------------------------------
    @Override
    public Admin readAdminPorCorreo(String email, DAOManager dao) {
        Admin admin = null;
        String sentencia;
        sentencia = "SELECT * FROM admins WHERE email = ?";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // obtener cada una de la columnas y mapearlas a la clase Admin
                    admin = new Admin(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave"),
                            rs.getBoolean("esSuperAdmin")
                    );
                    admin.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admin;
    }

    @Override
    public Admin readAdminPorCorreoYClave(String email, String clave, DAOManager dao) {
        Admin admin = null;
        String sentencia;
        sentencia = "SELECT * FROM admins WHERE email = ? AND clave = ?";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            ps.setString(1, email);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Admin(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave"),
                            rs.getBoolean("esSuperAdmin")
                    );
                    admin.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admin;
    }

    //READ ALL-----------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Admin> readALLAdmins(DAOManager dao) {
        ArrayList<Admin> admins = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM admins;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Admin admin = new Admin(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave"),
                            rs.getBoolean("esSuperAdmin")
                    );
                    admin.setId(rs.getInt("id"));
                    admins.add(admin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
}
