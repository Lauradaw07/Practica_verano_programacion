package DAO;

import modelos.Admin;

import java.util.ArrayList;

public interface DAOAdmin {
    public boolean insert(Admin admin, DAOManager dao);

    public boolean delete(int id, DAOManager dao);

    public Admin readAdminPorCorreo(String email, DAOManager dao);

    public Admin readAdminPorCorreoYClave(String email, String clave, DAOManager dao);

    public ArrayList<Admin> readALLAdmins(DAOManager dao);
}
