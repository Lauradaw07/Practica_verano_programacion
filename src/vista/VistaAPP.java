package vista;

import java.util.Scanner;

public class VistaAPP {

    //Constantes
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    //Atributos
    private Scanner sc = new Scanner(System.in);

    //Métodos
    //Pide datos String
    public String pideDatosString(String dato) {
        System.out.println("Introduzca " + dato + ":");
        return sc.nextLine();
    }

    //Pide datos int
    public int pideDatosEnteros(String dato) {
        System.out.println("Introduzca " + dato + ":");
        return Integer.parseInt(sc.nextLine());
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENU INICIO
    public void muestraMenuInicio() {
        System.out.println("""
                
                ╔═════════════════════════════════════════════════════════════════════════╗
                 ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    MENU DE INICIO    ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
                 
                 Bienvenidx al Sistema de Gestión de Incidencias
                 
                  ◊◊◊ Recuerde que para reportar una incidencia debe estar registrado  ◊◊◊
                  
                      «-------------------------------------------------------------»
                        [1]  Ya estoy registrado
                      «-------------------------------------------------------------»
                        [2]  Registrarme
                      «-------------------------------------------------------------»
                        [3]  Cerrar el programa
                      «-------------------------------------------------------------»
                      
                ╚═════════════════════════════════════════════════════════════════════════╝
                """);
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENU USUARIO

    public void muestraMenuUsuario(String nombre, int incidenciasAbiertas, int incidenciasCerradas) {
        System.out.println("""
                
                ╔═════════════════════════════════════════════════════════════════════════╗
                 ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   MENU DE USUARIO   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡ 
                 
                """);
        System.out.println(" Bienvenidx " + nombre + " tiene usted perfil de usuario normal");
        System.out.println(" Actualmente, tiene " + incidenciasAbiertas + " incidencias abiertas y " + incidenciasCerradas + " incidencias cerradas");
        System.out.println("""
                
                      «-------------------------------------------------------------»
                        [1]  Registrar una incidendia
                      «-------------------------------------------------------------»
                        [2]  Borrar incidencia
                      «-------------------------------------------------------------»
                        [3]  Consultar mis incidencias abiertas
                      «-------------------------------------------------------------»
                        [4]  Consultar mis incidencias cerradas
                      «-------------------------------------------------------------»
                        [5]  Mostrar mi perfil
                      «-------------------------------------------------------------»
                        [6]  Cambiar mi clave de acceso
                      «-------------------------------------------------------------»
                        [7]  Cerrar sesión
                      «-------------------------------------------------------------»
                      
                ╚═════════════════════════════════════════════════════════════════════════╝
                """);
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENÚ TÉCNICO

    public void muestraMenuTecnico(String nombre, int incidenciasAsignadas, int incidenciasCerradas) {
        System.out.println("""
                
                ╔═════════════════════════════════════════════════════════════════════════╗
                 ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   MENU DE TÉCNICO   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡ 
                 
                """);
        System.out.println(" Bienvenidx " + nombre + " tiene usted perfil de técnico");
        System.out.println(" Actualmente, tiene " + incidenciasAsignadas + " incidencias asignadas y " + incidenciasCerradas + " incidencias cerradas");
        System.out.println("""
                
                      «-------------------------------------------------------------»
                        [1]  Consultar las incidencias asignadas no resueltas
                      «-------------------------------------------------------------»  
                        [2]  Marcar una incidencia como resuelta
                      «-------------------------------------------------------------»         
                        [3]  Consultar mis incidencias cerradas
                      «-------------------------------------------------------------»
                        [4]  Mostrar mi perfil
                      «-------------------------------------------------------------»
                        [5]  Cambiar clave de acceso
                      «-------------------------------------------------------------»
                        [6]  Cerrar sesión
                      «-------------------------------------------------------------»
                      
                ╚═════════════════════════════════════════════════════════════════════════╝      
                """);
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENÚ SUPERADMIN
    public void muestraMenuSuperAdmin(String nombre, int incidenciasAbiertas, int incidenciasSinAsignar) {
        System.out.println("""
                
                ╔═════════════════════════════════════════════════════════════════════════╗
                 ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   MENU DE ADMIN   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡ 
                
                """);
        System.out.println(" Bienvenidx " + nombre + " tiene usted perfil de administrador");
        System.out.println(" Actualmente, hay " + incidenciasAbiertas + " incidencias abiertas, de las cuales \n" +
                " " + incidenciasSinAsignar + " no están asignadas a ningún técnico");
        System.out.println("""

                      «-------------------------------------------------------------»
                        [1]  Consultar todas las incidencias abiertas
                      «-------------------------------------------------------------»
                        [2]  Consultar incidencias cerradas
                      «-------------------------------------------------------------»
                        [3]  Consultar incidencias por término
                      «-------------------------------------------------------------»
                        [4]  Consultar los usuarios
                      «-------------------------------------------------------------»
                        [5]  Borrar usuario
                      «-------------------------------------------------------------»
                        [6]  Consultar los técnicos
                      «-------------------------------------------------------------»
                        [7]  Asignar una incidencia a un técnico
                      «-------------------------------------------------------------»
                        [8]  Dar de alta un técnico
                      «-------------------------------------------------------------»
                        [9]  Borrar técnico
                      «-------------------------------------------------------------»
                        [10]  Dar de alta un administrador
                      «-------------------------------------------------------------»
                        [11]  Borrar administrador
                      «-------------------------------------------------------------»
                        [12]  Estadísticas de la aplicación
                      «-------------------------------------------------------------»
                        [13]  Cerrar sesión
                      «-------------------------------------------------------------»
                      
                ╚═════════════════════════════════════════════════════════════════════════╝
                """);
    }

    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENÚ ADMIN
    public void muestraMenuAdmin(String nombre, int incidenciasAbiertas, int incidenciasSinAsignar) {
        System.out.println("""
                
                ╔═════════════════════════════════════════════════════════════════════════╗
                 ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   MENU DE ADMIN   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡ 
                
                """);
        System.out.println(" Bienvenidx " + nombre + " tiene usted perfil de administrador");
        System.out.println(" Actualmente, hay " + incidenciasAbiertas + " incidencias abiertas, de las cuales \n" +
                " " + incidenciasSinAsignar + " no están asignadas a ningún técnico");
        System.out.println("""

                      «-------------------------------------------------------------»
                        [1]  Consultar todas las incidencias abiertas
                      «-------------------------------------------------------------»
                        [2]  Consultar incidencias cerradas
                      «-------------------------------------------------------------»
                        [3]  Consultar incidencias por término
                      «-------------------------------------------------------------»
                        [4]  Consultar los usuarios
                      «-------------------------------------------------------------»
                        [5]  Borrar usuario
                      «-------------------------------------------------------------»
                        [6]  Consultar los técnicos
                      «-------------------------------------------------------------»
                        [7]  Asignar una incidencia a un técnico
                      «-------------------------------------------------------------»
                        [8]  Dar de alta un técnico
                      «-------------------------------------------------------------»
                        [9]  Borrar técnico
                      «-------------------------------------------------------------»
                        [10]  Estadísticas de la aplicación
                      «-------------------------------------------------------------»
                        [11]  Cerrar sesión
                      «-------------------------------------------------------------»
                      
                ╚═════════════════════════════════════════════════════════════════════════╝      
                """);
    }

    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENÚ PRIORIDAD INCIDENCIA

    public void muestraMenuPrioridad() {
        System.out.println("""
                 
                ╔══════════════════════════════════════════════════════╗
                                    ☆ ☆ ☆ ☆ ☆
                  Seleccione la prioridad de su incidencia, del 1 al 5:
                  
                ╚══════════════════════════════════════════════════════╝
                """);
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENÚ PRIORIDAD INCIDENCIA
    public void muestraMenuRetroceso(String objetoABorrar) {
        System.out.println("""
                
                ╔══════════════════════════════════════════════════════╗
                """);
        System.out.println("    ¿Seguro que quiere borrar " + objetoABorrar + " ?");
        System.out.println("""
                    «-----------------------------------------------»
                        [1] Sí
                    «-----------------------------------------------»
                        [2] No
                    «-----------------------------------------------»
                    
                ╚══════════════════════════════════════════════════════╝   
                """);
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //ESTADÍSTICAS DEL SISTEMA

    public void muestraEstadisticas(int usuariosRegistrados, int tecnicosRegistrados, int adminsRegistrados, int incidenciasAbiertas, int incidenciasCerradas, int incidenciasAsignadas, int incidenciasSinAsignar) {
        System.out.println("""
                
                ╔════════════════════════════════════════════════════════════════════╗
                  ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   ESTADÍSTICAS DEL SISTEMA   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
                """);
        System.out.println("    Usuarios registrados: " + usuariosRegistrados);
        System.out.println("    Técnicos registrados: " + tecnicosRegistrados);
        System.out.println("    Administradores registrados: " + adminsRegistrados);
        System.out.println("    Incidencias abiertas: " + incidenciasAbiertas);
        System.out.println("    Incidencias cerradas: " + incidenciasCerradas);
        System.out.println("    Incidencias asignadas a técnicos: " + incidenciasAsignadas);
        System.out.println("    Incidencias sin asignar: " + incidenciasSinAsignar);
        System.out.println("""
                
                ╚════════════════════════════════════════════════════════════════════╝
                """);
    }

    //Barra de estado usuario
    public void muestraBarraEstadoUsuario(int incidenciasAbiertas, int incidenciasAsignadas) {
        System.out.println("Actualmente tiene " + incidenciasAbiertas + " sin asignar y " + incidenciasAsignadas + "incidencias asignadas");
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //MENSAJES

    //Mensajes generales-----------------------------------------------------------------------------------------------

    public void mensajeClaveCambiada() {
        System.out.println(ANSI_GREEN + "\n----- Contraseña modificada con éxito -----\n" + ANSI_RESET);
    }

    public void mensajeNotificacionEnviada() {
        System.out.println(ANSI_GREEN + "\n----- Notificación enviada -----\n" + ANSI_RESET);
    }

    //Mensajes usuarios-----------------------------------------------------------------------------------------------

    public void mensajeUsuarioRegistrado() {
        System.out.println(ANSI_GREEN + "\n------ Usuario registrado con éxito ------\n" + ANSI_RESET);
    }

    public void mensajeUsuarioBorrado() {
        System.out.println(ANSI_GREEN + "\n----- Usuario borrado con éxito -----\n" + ANSI_RESET);
    }


    //Mensajes técnicos-----------------------------------------------------------------------------------------------

    public void mensajeTecnicoRegistrado() {
        System.out.println(ANSI_GREEN + "\n------ Técnico registrado con éxito ------\n" + ANSI_RESET);
    }

    public void mensajeTecnicoBorrado() {
        System.out.println(ANSI_GREEN + "\n------ Técnico borrado con éxito ------\n" + ANSI_RESET);
    }


    //Mensajes admins-----------------------------------------------------------------------------------------------

    public void mensajeAdminRegistrado() {
        System.out.println(ANSI_GREEN + "\n------ Administrador registrado con éxito ------\n" + ANSI_RESET);
    }

    public void mensajeAdminBorrado() {
        System.out.println(ANSI_GREEN + "\n------ Administrador borrado con éxito ------\n" + ANSI_RESET);
    }


    //Mensajes incidencias-----------------------------------------------------------------------------------------------

    public void mensajeIncidenciaRegistrada() {
        System.out.println(ANSI_GREEN + "\n------ Incidencia registrada con éxito ------\n" + ANSI_RESET);
    }

    public void mensajeIncidenciaCerrada() {
        System.out.println(ANSI_GREEN + "\n------ Incidencia cerrada correctamente ------\n" + ANSI_RESET);
    }

    public void mensajeIncidenciaBorrada() {
        System.out.println(ANSI_GREEN + "\n----- Incidencia borrada con éxito -----\n" + ANSI_RESET);
    }

    public void mensajeIncidenciaAsignada() {
        System.out.println(ANSI_GREEN + "\n------ Incidencia asignada con éxito ------\n" + ANSI_RESET);
    }


    //-------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------
    //ERRORES

    //Errores generales-----------------------------------------------------------------------------------------------

    public void errorDatoOpcionMenu() {
        System.out.println(ANSI_RED + "\n------ERROR: La opción seleccionada debe ser un número------\n" + ANSI_RESET);
    }

    public void errorOpcionMenu() {
        System.out.println(ANSI_RED + "\n------ERROR: Introduzca una opción válida------\n" + ANSI_RESET);
    }

    public void errorInicioSesion(){
        System.out.println(ANSI_RED + "\n------ERROR: Usuario o contraseña incorrectas------\n" + ANSI_RESET);
    }

    public void errorClaveCambiada() {
        System.out.println(ANSI_RED + "\n-----ERROR: No se ha podido cambiar la contraseña -----\n" + ANSI_RESET);
    }

    public void errorNotificacionEnviada() {
        System.out.println(ANSI_RED + "\n-----ERROR: No se ha podido enviar la notificación -----\n" + ANSI_RESET);
    }

    //Errores usuarios-----------------------------------------------------------------------------------------------

    public void errorUsuarioRegistrado() {
        System.out.println(ANSI_RED + "\n------ERROR: No se ha podido registrar al usuario------\n" + ANSI_RESET);
    }

    public void errorCrearUsuario() {
        System.out.println(ANSI_RED + "\n------ERROR: Ya existe un técnico con este email en el sistema------\n" + ANSI_RESET);
    }

    public void errorUsuariosRegistrados() {
        System.out.println(ANSI_RED + "\n-----ERROR: No existen usuarios registrados en el sistema-----\n" + ANSI_RESET);
    }

    public void errorUsuarioBorrado() {
        System.out.println(ANSI_RED + "\n-----ERROR: No se ha podido borrar el usuario -----\n" + ANSI_RESET);
    }


    //Errores técnicos-----------------------------------------------------------------------------------------------

    public void errorCrearTecnico() {
        System.out.println(ANSI_RED + "\n------ERROR: Ya existe un técnico con este email en el sistema------\n" + ANSI_RESET);
    }

    public void errorBorrarTecnico() {
        System.out.println(ANSI_RED + "\n------ERROR: No se ha podido borrar al técnico------\n" + ANSI_RESET);
    }

    public void errorTecnicoRegistrado() {
        System.out.println(ANSI_RED + "\n------ERROR: No existen técnicos registrados------\n" + ANSI_RESET);
    }

    public void errorTecnicoCreado() {
        System.out.println(ANSI_RED + "\n-----ERROR: No se ha podido registrar al técnico-----\n" + ANSI_RESET);
    }

    //Errores admins-----------------------------------------------------------------------------------------------

    public void errorCrearAdmin() {
        System.out.println(ANSI_RED + "\n------ERROR: Ya existe un administrador con este email en el sistema------\n" + ANSI_RESET);
    }

    public void errorAdminRegistrado() {
        System.out.println(ANSI_RED + "\n------ERROR: No se ha podido registrar al administrador------\n" + ANSI_RESET);
    }

    public void errorAdminBorrado() {
        System.out.println(ANSI_RED + "\n-----ERROR: No se ha podido borrar al administrador ------\n" + ANSI_RESET);
    }

    public void errorExistenciaAdmin() {
        System.out.println(ANSI_RED + "\n-----ERROR: No existen administradores registrados ------\n" + ANSI_RESET);
    }


    //Errores incidencias-----------------------------------------------------------------------------------------------

    public void errorIncidenciaRegistrada() {
        System.out.println(ANSI_RED + "\n------ERROR: No existen incidencias registradas------\n" + ANSI_RESET);
    }

    public void errorIncidenciaAsignada() {
        System.out.println(ANSI_RED + "\n------ERROR: No existen incidencias asignadas------\n" + ANSI_RESET);
    }

    public void errorIncidenciaCreada() {
        System.out.println(ANSI_RED + "\n-----ERROR: No se ha podido crear la incidencia-----\n" + ANSI_RESET);
    }

    public void errorIncidenciaCerrada() {
        System.out.println(ANSI_RED + "\n------ERROR: No se ha podido cerrar la incidencia------\n" + ANSI_RESET);
    }

    public void errorIncidenciaBorrada() {
        System.out.println(ANSI_RED + "\n-----ERROR: No se ha podido borrar la incidencia-----\n" + ANSI_RESET);
    }

    public void errorExistenciaIncidenciasCerradas() {
        System.out.println(ANSI_RED + "\n------ERROR: No existen incidencias cerradas registradas------\n" + ANSI_RESET);
    }

    public void errorNuevaIncidenciaAsignada() {
        System.out.println(ANSI_RED + "\n------ERROR: No se ha podido asignar la incidencia------\n" + ANSI_RESET);
    }

    public void errorIncidenciaPorTermino() {
        System.out.println(ANSI_RED + "\n------ERROR: No se ha encontrado ninguna incidencia------\n" + ANSI_RESET);
    }
}
