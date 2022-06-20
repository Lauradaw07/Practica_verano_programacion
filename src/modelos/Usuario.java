package modelos;

import java.util.ArrayList;

public class Usuario extends UsuarioBase{

    //Constructor
    public Usuario(String nombre, String apellido, String email, String clave) {
        super(nombre, apellido, email, clave);
    }

    @Override
    public String toString() {
        return "\n╔═════════════════════════════════════════════════════════════════════╗" + "\n" +
                " ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   USUARIO   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡  " + "\n" +
                "    Usuario con id: " + getId() + "\n" +
                "    Nombre: " + getNombre() + "\n" +
                "    Apellido: " + getApellido() + "\n" +
                "    Email: " + getEmail() + "\n" +
                "╚═════════════════════════════════════════════════════════════════════╝";
    }
}
