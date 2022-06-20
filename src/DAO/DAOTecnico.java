package DAO;

import modelos.Tecnico;

import java.util.ArrayList;

public interface DAOTecnico {
    public boolean insert(Tecnico tecnico, DAOManager dao);

    public boolean update(String clave, int id, DAOManager dao);

    public boolean delete(int id, DAOManager dao);

    public Tecnico readTecnicoPorCorreo(String email, DAOManager dao);

    public Tecnico readTecnicoPorCorreoYClave(String email, String calve, DAOManager dao);

    public ArrayList<Tecnico> readALLTecnicos(DAOManager dao);
}
