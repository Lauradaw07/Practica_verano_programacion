package DAO;

import modelos.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAOUsuarioSQL implements DAOUsuario {

    //INSERT-----------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean insert(Usuario usuario, DAOManager dao) {
        String sentencia;
        sentencia = "INSERT INTO usuarios VALUES(" + usuario.getId() + ", '" + usuario.getNombre() + "', '" + usuario.getApellido() + "', '" + usuario.getEmail() + "', '" + usuario.getClave() + "');";
        try (Statement stmt = dao.getConn().createStatement()) {
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    //UPDATE-----------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean update(String clave, int id, DAOManager dao) {
        String sentencia;
        sentencia = "UPDATE usuarios SET clave = '" + clave + "' WHERE id = " + id + ";";
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
    public boolean deleteUsuario(int id, DAOManager dao) {
        String sentencia;
        sentencia = "DELETE FROM usuarios WHERE id = " + id + ";";
        try (Statement stmt = dao.getConn().createStatement()) {
            // enviar el commando insert
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //READ-----------------------------------------------------------------------------------------------------------------------
    @Override
    public Usuario readUsuarioPorCorreo(String email, DAOManager dao) {
        Usuario usuario = null;
        String sentencia;
        sentencia = "SELECT * FROM usuarios WHERE email = ?";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // obtener cada una de la columnas y mapearlas a la clase Alumno
                    usuario = new Usuario(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave")
                    );
                    usuario.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    @Override
    public Usuario readUsuarioPorCorreoYClave(String email, String password, DAOManager dao) {
        Usuario usuario = null;
        String sentencia;
        sentencia = "SELECT * FROM usuarios WHERE email = ? AND clave = ?";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            ps.setString(1, email);
            ps.setString(2,password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // obtener cada una de la columnas y mapearlas a la clase Alumno
                    usuario = new Usuario(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave")
                    );
                    usuario.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    //READ ALL-----------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Usuario> readALLUsuarios(DAOManager dao) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM usuarios;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("clave")
                    );
                    usuario.setId(rs.getInt("id"));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
}