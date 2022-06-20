package DAO;

import modelos.Incidencia;

import java.util.ArrayList;

public interface DAOIncidencia {
    public boolean insert(Incidencia incidencia, DAOManager dao);

    public boolean updateResuelta(int id, String comentario, String fechaFin, DAOManager dao);

    public boolean updateAsignada(int idIncidencia, int idTecnico, DAOManager dao);

    public boolean delete(int id, DAOManager dao);

    public Incidencia read(int id, DAOManager dao);

    public ArrayList<Incidencia> readALLPorIdUsuario(int idUsuario, DAOManager dao);

    public ArrayList<Incidencia> readALLPorIdUsuarioYAbiertas(int idUsuario, DAOManager dao);

    public ArrayList<Incidencia> readALLPorIdUsuarioYResueltas(int idUsuario, DAOManager dao);

    public ArrayList<Incidencia> readALLPorIdTecnicoYAsignadas(int idTecnico, DAOManager dao);

    public ArrayList<Incidencia> readALLPorIdTecnicoYCerradas(int idTecnico, DAOManager dao);

    public ArrayList<Incidencia> readALLTodasAbiertas(DAOManager dao);

    public ArrayList<Incidencia> readALLTodasCerradas(DAOManager dao);

    public ArrayList<Incidencia> readALLTodasAsignadas(DAOManager dao);

    public ArrayList<Incidencia> readALLTodasSinAsignar(DAOManager dao);

    public ArrayList<Incidencia> readALLPorTermino(String termino, DAOManager dao);
}
