package DAO;

import modelos.Incidencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DAOIncidenciaSQL implements DAOIncidencia{

    //INSERT-----------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean insert(Incidencia incidencia, DAOManager dao) {
        String sentencia;
        sentencia = "INSERT INTO incidencias (descripcion, prioridad, fechaInicio, idUsuario) VALUES('" + incidencia.getDescripcion() + "', " + incidencia.getPrioridad() + ", '" + incidencia.getFechaInicio() + "', " + incidencia.getIdUsuario() + ");";
        try (Statement stmt = dao.getConn().createStatement()) {
            // enviar el commando insert
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    //UPDATE-----------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean updateResuelta(int id, String solucion, String fechaFin, DAOManager dao) {
        String sentencia;
        sentencia = "UPDATE incidencias SET resuelta = true, solucion = '" + solucion + "', fechaFin = '" + fechaFin + "', asignada = false WHERE id = " + id + ";";
        try (Statement stmt = dao.getConn().createStatement()) {
            stmt.executeUpdate(sentencia);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateAsignada(int idIncidencia, int idTecnico, DAOManager dao) {
        String sentencia;
        sentencia = "UPDATE incidencias SET asignada = 1,  idTecnico = " + idTecnico + " WHERE id = " + idIncidencia + ";";
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
        sentencia = "DELETE FROM incidencias WHERE id = " + id + ";";
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
    public Incidencia read(int id, DAOManager dao) {
        Incidencia incidencia = null;
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE id = ?";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencia;
    }

    //READ ALL-----------------------------------------------------------------------------------------------------------------------
    @Override
    public ArrayList<Incidencia> readALLPorIdUsuario(int idUsuario, DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE idUsuario = " + idUsuario + ";";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLPorIdUsuarioYAbiertas(int idUsuario, DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE idUsuario = " + idUsuario + " AND resuelta = false;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLPorIdUsuarioYResueltas(int idUsuario, DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE idUsuario = " + idUsuario + " AND resuelta = true;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLPorIdTecnicoYAsignadas(int idTecnico, DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE idTecnico = " + idTecnico + " AND asignada = true;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia  incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLPorIdTecnicoYCerradas(int idTecnico, DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE idTecnico = " + idTecnico + " AND resuelta = true;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLTodasAbiertas(DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE resuelta = false;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLTodasCerradas(DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE resuelta = true;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLTodasAsignadas(DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE asignada = true;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLTodasSinAsignar(DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE asignada = false AND resuelta = false;";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }

    @Override
    public ArrayList<Incidencia> readALLPorTermino(String termino, DAOManager dao) {
        ArrayList<Incidencia> incidencias = new ArrayList<>();
        String sentencia;
        sentencia = "SELECT * FROM incidencias WHERE descripcion LIKE '%" + termino + "%';";
        try {
            PreparedStatement ps = dao.getConn().prepareStatement(sentencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Incidencia incidencia = new Incidencia(
                            rs.getString("descripcion"),
                            rs.getInt("prioridad"),
                            rs.getString("fechaInicio"),
                            rs.getInt("idUsuario")
                    );

                    incidencia.setId(rs.getInt("id"));
                    incidencia.setSolucion(rs.getString("solucion"));
                    incidencia.setEstaResuelta(rs.getBoolean("resuelta"));
                    incidencia.setFechaFin(rs.getString("fechaFin"));

                    incidencia.setEstaAsignada(rs.getBoolean("asignada"));
                    incidencia.setIdTecnico(rs.getInt("idTecnico"));
                    incidencias.add(incidencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidencias;
    }
}
