package modelos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Tecnico extends UsuarioBase {

    //Constructor
    public Tecnico(String nombre, String apellido, String email, String clave) {
        super(nombre, apellido, email, clave);
    }

    //To String
    @Override
    public String toString() {
        return "\n╔═════════════════════════════════════════════════════════════════════╗" + "\n" +
                " ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   TECNICO   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡  " + "\n" +
                "    Técnico con id: " + getId() + "\n" +
                "    Nombre: " + getNombre() + "\n" +
                "    Apellido: " + getApellido()+ "\n" +
                "    Email: " + getEmail() + "\n" +
                "╚═════════════════════════════════════════════════════════════════════╝";
    }

}
