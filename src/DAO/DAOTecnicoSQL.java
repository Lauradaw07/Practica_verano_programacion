package DAO;

import modelos.Tecnico;
import modelos.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOTecnicoSQL implements DAOTecnico {

    //INSERT-----------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean insert(Tecnico tecnico, DAOManager dao) {
        String sentencia;
        sentencia = "INSERT INTO tecnicos VALUES(" + tecnico.getId() + ", '" + tecnico.getNombre() + "', '" + tecnico.getApellido() + "', '" + tecnico.getEmail() + "', '" + tecnico.getClave() + "');";
        try (Statement stmt = dao.getConn().createStatement()) {
            // enviar el commando insert
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }


    @Override
    public boolean update(String clave, int id, DAOManager dao) {
        String sentencia;
        sentencia = "UPDATE tecnicos SET clave = '" + clave + "' WHERE id = " + id + ";";
        try (Statement stmt = dao.getConn().createStatement()) {
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //DELETE-----------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean delete(int id, DAOManager dao) {
        String sentencia;
        sentencia = "DELETE FROM tecnicos WHERE id = " + id + ";";
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
    public Tecnico readTecnicoPorCorreo(String email, DAOManager dao) {
        Tecnico tecnico = null;
        String sentencia;
        sentencia = "SELECT * FROM tecnicos WHERE email = ?";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tecnico = new Tecnico(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave")
                    );
                    tecnico.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tecnico;
    }

    @Override
    public Tecnico readTecnicoPorCorreoYClave(String email, String clave, DAOManager dao) {
        Tecnico tecnico = null;
        String sentencia;
        sentencia = "SELECT * FROM tecnicos WHERE email = ? AND clave = ?";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            ps.setString(1, email);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tecnico = new Tecnico(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave")
                    );
                    tecnico.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tecnico;
    }

    //READ ALL-----------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Tecnico> readALLTecnicos(DAOManager dao) {
        ArrayList<Tecnico> tecnicos = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM tecnicos;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // obtener cada una de la columnas y mapearlas a la clase Alumno
                    Tecnico tecnico = new Tecnico(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave")
                    );
                    tecnico.setId(rs.getInt("id"));
                    tecnicos.add(tecnico);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tecnicos;
    }
}
