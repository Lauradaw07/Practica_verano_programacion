package modelos;

import java.util.ArrayList;

public class Admin extends UsuarioBase{
    //Atributo
    private boolean esSuperAdmin = false;

    //Getters y setters
    public boolean isEsSuperAdmin() {
        return esSuperAdmin;
    }

    public void setEsSuperAdmin(boolean esSuperAdmin) {
        this.esSuperAdmin = esSuperAdmin;
    }

    //Constructor
    public Admin(String nombre, String apellido, String email, String clave, boolean esSuperAdmin) {
        super(nombre, apellido, email, clave);
        this.esSuperAdmin = esSuperAdmin;
    }

    //To String

    @Override
    public String toString() {
        return "\n╔═════════════════════════════════════════════════════════════════════╗" + "\n" +
                " ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   ADMINISTRADOR   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡  " + "\n" +
                "    Administrador con id: " + getId() + "\n" +
                "    Nombre: " + getNombre() + "\n" +
                "    Apellido: " + getApellido()+ "\n" +
                "    Email: " + getEmail() + "\n" +
                "╚═════════════════════════════════════════════════════════════════════╝";
    }
}
