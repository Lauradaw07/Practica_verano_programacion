package controlador;

import DAO.*;
import modelos.*;
import vista.VistaAPP;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;

public class GestionAPP {

    //Atributos
    private VistaAPP vista;
    private DAOUsuarioSQL daoUsuarioSQL;
    private DAOAdminSQL daoAdminSQL;
    private DAOTecnicoSQL daoTecnicoSQL;
    private DAOIncidenciaSQL daoIncidenciaSQL;


    //Getters y setters
    public VistaAPP getVista() {
        return vista;
    }

    public void setVista(VistaAPP vista) {
        this.vista = vista;
    }

    public DAOUsuarioSQL getDaoUsuarioSQL() {
        return daoUsuarioSQL;
    }

    public void setDaoUsuarioSQL(DAOUsuarioSQL daoUsuarioSQL) {
        this.daoUsuarioSQL = daoUsuarioSQL;
    }

    public DAOAdminSQL getDaoAdminSQL() {
        return daoAdminSQL;
    }

    public void setDaoAdminSQL(DAOAdminSQL daoAdminSQL) {
        this.daoAdminSQL = daoAdminSQL;
    }

    public DAOTecnicoSQL getDaoTecnicoSQL() {
        return daoTecnicoSQL;
    }

    public void setDaoTecnicoSQL(DAOTecnicoSQL daoTecnicoSQL) {
        this.daoTecnicoSQL = daoTecnicoSQL;
    }

    public DAOIncidenciaSQL getDaoIncidenciaSQL() {
        return daoIncidenciaSQL;
    }

    public void setDaoIncidenciaSQL(DAOIncidenciaSQL daoIncidenciaSQL) {
        this.daoIncidenciaSQL = daoIncidenciaSQL;
    }

    //Constructor
    public GestionAPP() {
        this.vista = new VistaAPP();
        this.daoUsuarioSQL = new DAOUsuarioSQL();
        this.daoAdminSQL = new DAOAdminSQL();
        this.daoTecnicoSQL = new DAOTecnicoSQL();
        this.daoIncidenciaSQL = new DAOIncidenciaSQL();
    }

    //Métodos
    //Iniciar programa
    public void inicia() {
        DAOManager dao = DAOManager.getSinglentonInstance();
        if (dao == null) System.out.println("El singlenton funciona");
        try {
            dao.open();
            System.out.println("Conexión establecida");
        } catch (Exception e) {
            System.out.println("Error de conexión en la BBDD");
        }

        boolean salida = false;
        do {
            //MENÚ INICIO
            int respuestaMenu = muestraMenuPrincipal();

            switch (respuestaMenu) {
                //INICIAR SESIÓN
                case 1 -> {
                    String correo = vista.pideDatosString("correo");
                    String password = vista.pideDatosString("contraseña");

                    //LOG IN
                    //Comprobamos que el usuario y el correo estén en la tabla usuarios, técnicos o administradores de la BD
                    if (logIn(correo, password, dao)) {
                        //Comprobamos si el correo registrado pertenece a un usuario
                        if (buscaUsuarioPorCorreo(correo, dao) != null) {
                            //TODO USUARIOS
                            //Obtenemos el usuario de la BD para realizar todas las tareas
                            Usuario usuarioInicioSesion = buscaUsuarioPorCorreo(correo, dao);
                            boolean cerrarSesionUsuario = false;

                            do {
                                //Obtenemos los datos necesarios de la BD para la barra de estado
                                ArrayList<Incidencia> incidenciasAbiertasUsuarioBarraEstado = buscaIncidenciasAbiertasUsuario(usuarioInicioSesion.getId(), dao);
                                ArrayList<Incidencia> incidenciasCerradasUsuarioBarraEstado = buscaIncidenciasCerradasUsuario(usuarioInicioSesion.getId(), dao);

                                //MENÚ USUARIO
                                int respuestaMenuUsuario = muestraMenuUsuario(usuarioInicioSesion, incidenciasAbiertasUsuarioBarraEstado, incidenciasCerradasUsuarioBarraEstado);

                                switch (respuestaMenuUsuario) {
                                    //REGISTRAR INCIDENCIA
                                    case 1:
                                        //Creamos la incidencia que se va a insertar en la BD
                                        Incidencia nuevaIncidencia = creaIncidencia(usuarioInicioSesion.getId());

                                        //Comprobamos que la incidencia se inserta en la BD
                                        if (insertaIncidencia(nuevaIncidencia, dao)) {
                                            vista.mensajeIncidenciaRegistrada();
                                            enviaMensajeTelegramIncidenciaRegistrada();
                                        } else {
                                            vista.errorIncidenciaCreada();
                                        }
                                        break;
                                    //BORRAR INCIDENCIA
                                    case 2:
                                        //Obtenemos un Arraylist de la BD con las incidencias del usuario
                                        ArrayList<Incidencia> incidenciasDelUsuario = buscaTodasIncidenciasUsuario(usuarioInicioSesion.getId(), dao);

                                        //Si el Arraylist no está vacío (el usuario tiene incidencias), mostramos las incidencias
                                        if (!incidenciasDelUsuario.isEmpty()) {
                                            muestraIncidencias(incidenciasDelUsuario);

                                            //Pedimos por consola que se seleccione una incidencia de la lista
                                            int numeroIncidencia;
                                            do {
                                                numeroIncidencia = 0;
                                                try {
                                                    numeroIncidencia = vista.pideDatosEnteros("el número de la incidencia que quiere borrar");
                                                } catch (NumberFormatException e) {
                                                    vista.errorDatoOpcionMenu();
                                                }
                                            } while (numeroIncidencia < 0 || numeroIncidencia > incidenciasDelUsuario.size());

                                            if (retroceso("la incidencia")){
                                                //Una vez seleccionada la incidencia, la borramos de la BD
                                                if (borraIncidencia(incidenciasDelUsuario, numeroIncidencia, dao)) {
                                                    vista.mensajeIncidenciaBorrada();
                                                } else {
                                                    vista.errorIncidenciaBorrada();
                                                }
                                            }
                                        } else {
                                            vista.errorIncidenciaRegistrada();
                                        }
                                        break;
                                    //CONSULTAR INCIDENCIAS ABIERTAS
                                    case 3:
                                        //Obtenemos un Arraylist de la BD con las incidencias abiertas del usuario
                                        ArrayList incidenciasAbiertas = buscaIncidenciasAbiertasUsuario(usuarioInicioSesion.getId(), dao);

                                        //Si el Arraylist no está vacío (el usuario tiene incidencias abiertas), mostramos las incidencias
                                        if (!incidenciasAbiertas.isEmpty()) {
                                            muestraIncidencias(incidenciasAbiertas);
                                        } else {
                                            vista.errorIncidenciaRegistrada();
                                        }
                                        break;
                                    //CONSULTAR INCIDENCIAS CERRADAS
                                    case 4:
                                        //Obtenemos un Arraylist de la BD con las incidencias ceradas del usuario
                                        ArrayList<Incidencia> incidenciasCerradas = buscaIncidenciasCerradasUsuario(usuarioInicioSesion.getId(), dao);

                                        //Si el Arraylist no está vacío (el usuario tiene incidencias cerradas), mostramos las incidencias
                                        if (!incidenciasCerradas.isEmpty()) {
                                            muestraIncidencias(incidenciasCerradas);
                                        } else {
                                            vista.errorIncidenciaRegistrada();
                                        }
                                        break;
                                    //MOSTRAR PERFIL
                                    case 5:
                                        //Mostramos el perfil del usuario (To String)
                                        System.out.println(usuarioInicioSesion);
                                        break;
                                    //CAMBIAR CONTRASEÑA
                                    case 6:
                                        //Comprobamos que la contraseña que el usuario tenía registrada, es igual a la que se pasa por consola
                                        if (compruebaClave(usuarioInicioSesion.getClave())) {

                                            //Si es igual, modificamos la contraseña en la BD
                                            if (cambiarClaveUsuario(usuarioInicioSesion.getId(), dao)) {
                                                vista.mensajeClaveCambiada();
                                                usuarioInicioSesion = buscaUsuarioPorCorreo(correo, dao);
                                            } else {
                                                vista.errorClaveCambiada();
                                            }
                                        }
                                        break;
                                    //CERRAR SESIÓN
                                    case 7:
                                        cerrarSesionUsuario = true;
                                        break;
                                    default:
                                        vista.errorOpcionMenu();
                                }
                            } while (!cerrarSesionUsuario);
                            //Comprobamos si el correo registrado pertenece a un técnico
                        } else if (buscaTecnicoPorCorreo(correo, dao) != null) {
                            //Obtenemos el técnico de la BD para realizar todas las tareas
                            Tecnico tecnicoInicioSesion = buscaTecnicoPorCorreo(correo, dao);
                            boolean cerrarSesionTecnico = false;
                            //TODO TÉCNICOS

                            do {
                                //Obtenemos los datos necesarios para la barra de estado
                                ArrayList<Incidencia> incidenciasAsignadasTecnicoBarraEstado = buscaIncidenciasAsignadasTecnico(tecnicoInicioSesion.getId(), dao);
                                ArrayList<Incidencia> incidenciasCerradasTecnicoBarraEstado = buscaIncidenciasCerradasTecnico(tecnicoInicioSesion.getId(), dao);

                                //MENU TECNICO
                                int respuestaMenuTecnico = muestraMenuTecnico(tecnicoInicioSesion, incidenciasAsignadasTecnicoBarraEstado, incidenciasCerradasTecnicoBarraEstado);


                                switch (respuestaMenuTecnico) {
                                    //CONSULTAR INCIDENCIAS ASIGNADAS
                                    case 1:
                                        //Obtenemos el Arraylist de incidencias asignadas al técnico de la BD
                                        ArrayList<Incidencia> incidenciasAsignadas = buscaIncidenciasAsignadasTecnico(tecnicoInicioSesion.getId(), dao);

                                        //Si el Arraylist no está vacio (el técnico tiene incidencias asignadas), mostramos las incidencias
                                        if (!incidenciasAsignadas.isEmpty()) {
                                            muestraIncidencias(incidenciasAsignadas);
                                        } else {
                                            vista.errorIncidenciaAsignada();
                                        }
                                        break;
                                    //MARCAR INCIDENCIA COMO RESUELTA
                                    case 2:
                                        //Obtenemos un Arraylist de la BD con las incidencias asignadas al técnico
                                        ArrayList<Incidencia> incidenciasAsignadasTecnico = buscaIncidenciasAsignadasTecnico(tecnicoInicioSesion.getId(), dao);

                                        //Si el Arraylist no está vacío (el técnico tiene incidencias asignadas), mostramos las incidencias
                                        if (!incidenciasAsignadasTecnico.isEmpty()) {
                                            muestraIncidencias(incidenciasAsignadasTecnico);

                                            //Pedimos por consola que se seleccione una incidencia de la lista
                                            int numeroIncidencia;
                                            do {
                                                numeroIncidencia = 0;
                                                try {
                                                    numeroIncidencia = vista.pideDatosEnteros("el número de la incidencia que quiere seleccionar");
                                                } catch (NumberFormatException e) {
                                                    vista.errorDatoOpcionMenu();
                                                }
                                            } while (numeroIncidencia < 0 || numeroIncidencia > incidenciasAsignadasTecnico.size());

                                            /*Una vez seleccionada la incidencia que se va a cerrar, se pide al técnico que indique una solución
                                              y se procede a cerrarla*/
                                            if (marcaIncidenciaResuelta(incidenciasAsignadasTecnico, numeroIncidencia, dao)) {
                                                vista.mensajeIncidenciaCerrada();
                                            } else {
                                                vista.errorIncidenciaCerrada();
                                            }
                                        } else {
                                            vista.errorIncidenciaAsignada();
                                        }
                                        break;
                                    //CONSULTAR INCIDENCIAS CERRADAS
                                    case 3:
                                        //Obtenemos un Arraylis de la BD con las incidencias cerradas por el técnico
                                        ArrayList<Incidencia> incidenciasCerradasTecnico = buscaIncidenciasCerradasTecnico(tecnicoInicioSesion.getId(), dao);

                                        //Si el Arraylist no está vacio (el técnico tiene incidencias cerradas), se muestran las incidencias
                                        if (!incidenciasCerradasTecnico.isEmpty()) {
                                            muestraIncidencias(incidenciasCerradasTecnico);
                                        } else {
                                            vista.errorExistenciaIncidenciasCerradas();
                                        }
                                        break;
                                    //MOSTRAR PERFIL
                                    case 4:
                                        //Mostramos el perfil del técnico (To String)
                                        System.out.println(tecnicoInicioSesion);
                                        break;
                                    //CAMBIAR CONTRASEÑA
                                    case 5:
                                        //Comprobamos que la contraseña que el técnico tenía registrada, es igual a la que se pasa por consola
                                        if (compruebaClave(tecnicoInicioSesion.getClave())) {

                                            //Si es igual, modificamos la contraseña en la BD
                                            if (cambiarClaveTecnico(tecnicoInicioSesion.getId(), dao)) {
                                                vista.mensajeClaveCambiada();
                                                tecnicoInicioSesion = buscaTecnicoPorCorreo(correo, dao);
                                            } else {
                                                vista.errorClaveCambiada();
                                            }
                                        }
                                        break;
                                    //CERRAR SESIÓN
                                    case 6:
                                        cerrarSesionTecnico = true;
                                        break;
                                    default:
                                        vista.errorOpcionMenu();
                                        break;
                                }
                            } while (!cerrarSesionTecnico);
                        } else {
                            //Si el correo y la contraseña no pertenecen a un usuario o técnico, pertenecen a la fuerza a un administrador
                            //Obtenemos el administrador de la BD para realizar todas las tareas
                            Admin adminInicioSesion = buscaAdminPorCorreo(correo, dao);
                            if (adminInicioSesion.isEsSuperAdmin()) {
                                boolean cerrarSesionSuperAdmin = false;
                                //TODO SUPERADMINS

                                do {
                                    //Obtenemos los datos necesarios para la barra de estado
                                    ArrayList<Incidencia> incidenciasAbiertasSistemaBarraEstado = buscaIncidenciasAbiertasSistema(dao);
                                    ArrayList<Incidencia> incidenciasSinAsignarSistemaBarraEstado = buscaIncidenciasSinAsignarSistema(dao);

                                    //MENÚ SUPERADMIN
                                    int respuestaMenuSuperAdmin = muestraMenuSuperAdmin(adminInicioSesion, incidenciasAbiertasSistemaBarraEstado, incidenciasSinAsignarSistemaBarraEstado);


                                    switch (respuestaMenuSuperAdmin) {
                                        //CONSULTAR INCIDENCIAS ABIERTAS (SISTEMA COMPLETO)
                                        case 1:
                                            //Obtenemos un Arraylist de la BD con todas las incidencias abiertas del sistema
                                            ArrayList<Incidencia> incidenciasAbiertasSistema = buscaIncidenciasAbiertasSistema(dao);

                                            //Si el Arraylist no está vacio (hay incidencias abiertas en el sistema), se muestran las incidencias
                                            if (!incidenciasAbiertasSistema.isEmpty()) {
                                                muestraIncidencias(incidenciasAbiertasSistema);
                                            } else {
                                                vista.errorIncidenciaRegistrada();
                                            }
                                            break;
                                        //CONSULTAR INCIDENCIAS CERRADAS (POR TÉCNICO)
                                        case 2:
                                            //Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema
                                            ArrayList<Tecnico> tecnicosSistema = buscaTecnicosSistema(dao);

                                            //Si el Arraylist no está vacio (hay técnicos registrados en el sistema), se muestran los técnicos
                                            if (!tecnicosSistema.isEmpty()) {
                                                muestraTecnicos(tecnicosSistema);

                                                //Pedimos por consola que se seleccione un técnico de la lista
                                                int numeroTecnico = 0;
                                                do {
                                                    try {
                                                        numeroTecnico = vista.pideDatosEnteros("el número del técnico que quiere seleccionar");
                                                    } catch (NumberFormatException e) {
                                                        vista.errorDatoOpcionMenu();
                                                    }
                                                } while (numeroTecnico < 0 || numeroTecnico > tecnicosSistema.size());

                                                //Una vez seleccionado el técnico, obtenemos su id
                                                int idTecnico = buscaIdTecnico(tecnicosSistema, numeroTecnico);

                                                //Con el id del técnico, obtenemos un Arraylist de la BD con las incidencias cerradas por ese técnico
                                                ArrayList<Incidencia> incidenciasCerradasPorTecnico = buscaIncidenciasCerradasTecnico(idTecnico, dao);

                                                //Si el Arraylist no está vacio (el técnico seleccionado tiene incidencias cerradas), se muestran las incidencias
                                                if (!incidenciasCerradasPorTecnico.isEmpty()) {
                                                    muestraIncidencias(incidenciasCerradasPorTecnico);
                                                } else {
                                                    vista.errorExistenciaIncidenciasCerradas();
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //CONSULTAR INCIDENCIAS POR TÉRMINO
                                        case 3:
                                            /*Pedimos por consola que se introduzca una palabra y obtenemos un Arraylist de la BD con las incidencias
                                            que contengan esa palabra en la descripción*/
                                            ArrayList<Incidencia> incidenciasEncontradasPorTermino = buscaIncidenciaPorTermino(dao);

                                            //Si el Arraylist no está vacio (hay incidencias que contienen la palabra en la descripción), se muestran las incidencias
                                            if (!incidenciasEncontradasPorTermino.isEmpty()) {
                                                muestraIncidencias(incidenciasEncontradasPorTermino);
                                            } else {
                                                vista.errorIncidenciaPorTermino();
                                            }
                                            break;
                                        //MOSTRAR USUARIOS
                                        case 4:
                                            //Obtenemos un Arraylist de la BD con los usuarios registrados del sistema
                                            ArrayList<Usuario> usuariosEncontrados = buscaUsuariosSistema(dao);

                                            //Si el Arraylist no está vacio (hay usuarios registrados en el sistema), se muestran los usuarios
                                            if (!usuariosEncontrados.isEmpty()) {
                                                muestraUsuarios(usuariosEncontrados);
                                            } else {
                                                vista.errorUsuariosRegistrados();
                                            }
                                            break;
                                        //BORRAR USUARIO
                                        case 5:
                                            //Obtenemos un Arraylist de la BD con los usuarios registrados en el sistema
                                            ArrayList<Usuario> usuarios = buscaUsuariosSistema(dao);

                                            //Si el Arraylist no está vacio (hay usuarios registrados en el sistema), se muestran los usuarios
                                            if (!usuarios.isEmpty()) {
                                                muestraUsuarios(usuarios);

                                                //Pedimos por consola que se seleccione un usuario de la lista
                                                int numeroUsuario = 0;
                                                do {
                                                    try {
                                                        numeroUsuario = vista.pideDatosEnteros("el número del usuario que quiere seleccionar");
                                                    } catch (NumberFormatException e) {
                                                        vista.errorDatoOpcionMenu();
                                                    }
                                                } while (numeroUsuario < 0 || numeroUsuario > usuarios.size());

                                                if (retroceso("al usuario")) {
                                                    //Una vez seleccionado el usuario, lo borramos de la BD
                                                    if (borraUsuario(usuarios, numeroUsuario, dao)) {
                                                        vista.mensajeUsuarioBorrado();
                                                    } else {
                                                        vista.errorUsuarioBorrado();
                                                    }
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //MOSTRAR TÉCNICOS
                                        case 6:
                                            //Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema
                                            ArrayList<Tecnico> tecnicosEncontrados = buscaTecnicosSistema(dao);

                                            //Si el Arraylist no está vacio (hay técnicos registrados en el sistema), se muestran los técnicos
                                            if (!tecnicosEncontrados.isEmpty()) {
                                                muestraTecnicos(tecnicosEncontrados);
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //ASIGNAR INCIDENCIA
                                        case 7:
                                            /*Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema, ya que si no hay,
                                            no se puede asignar ninguna incidencia*/
                                            ArrayList<Tecnico> tecnicosDisponibles = buscaTecnicosSistema(dao);

                                            /*Si el Arraylist no está vacio (hay técnicos registrados en el sistema), obtenemos
                                             un Arraylist con todas las incidencias que están sin asignar del sistema*/
                                            if (!tecnicosDisponibles.isEmpty()) {
                                                //Obtenemos el Arraylist de las incidencias sin asignar
                                                ArrayList<Incidencia> incidenciasSinAsignar = buscaIncidenciasSinAsignarSistema(dao);

                                                //Si el Arraylist no está vacio (hay incidencias sin asignar), se muestran las incidencias
                                                if (!incidenciasSinAsignar.isEmpty()) {
                                                    muestraIncidencias(incidenciasSinAsignar);

                                                    //Pedimos por consola que se seleccione una incidencia de la lista
                                                    int numeroIncidencia = 0;
                                                    do {
                                                        try {
                                                            numeroIncidencia = vista.pideDatosEnteros("el número de la incidencia que quiere seleccionar");
                                                        } catch (NumberFormatException e) {
                                                            vista.errorDatoOpcionMenu();
                                                        }
                                                    } while (numeroIncidencia < 0 || numeroIncidencia > incidenciasSinAsignar.size());

                                                    //Mostramos los técnicos disponibles del sistema (obtenidos al principio del Case)
                                                    muestraTecnicos(tecnicosDisponibles);

                                                    //Pedimos por consola que se seleccione un técnico de la lista
                                                    int numeroTecnico = 0;
                                                    do {
                                                        try {
                                                            numeroTecnico = vista.pideDatosEnteros("el número del técnico que quiere seleccionar");
                                                        } catch (NumberFormatException e) {
                                                            vista.errorDatoOpcionMenu();
                                                        }
                                                    } while (numeroTecnico < 0 || numeroTecnico > tecnicosDisponibles.size());

                                                /*Asignamos la incidencia seleccionada al técnico seleccionado, pasándole a la incidencia la id
                                                 del técnico en la BD*/
                                                    if (asignaIncidencia(incidenciasSinAsignar, numeroIncidencia, tecnicosDisponibles, numeroTecnico, dao)) {
                                                        vista.mensajeIncidenciaAsignada();
                                                        //TODO PETA
                                                        //Buscamos el técnico en el Arraylist de técnicos disponibles, para obtener su correo
                                                        String correoTecnico = buscaCorreoTecnico(tecnicosDisponibles, numeroTecnico);
                                                        /*Una vez encontrado el correo, mandamos un email con el que avisaremos al técnico de
                                                        que se le ha asignado una nueva incidencia*/
                                                        enviaEmailIncidenciaAsignada(correoTecnico);
                                                    } else {
                                                        vista.errorNuevaIncidenciaAsignada();
                                                    }
                                                } else {
                                                    vista.errorIncidenciaRegistrada();
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //CREAR TÉCNICO
                                        case 8:
                                            //Pedimos por consola los datos del técnico que se va a registrar en la BD
                                            Tecnico tecnicoDeRegistro = creaTecnico();

                                            /*Comprobamos que el correo del técnico no esté registrado en la BD, obteniendo un técnico que tenga
                                            ese correo registrado. Si el técnico obtenido es null, podremos registrar al nuevo técnico*/
                                            if (buscaTecnicoPorCorreo(tecnicoDeRegistro.getEmail(), dao) != null) {
                                                vista.errorCrearTecnico();
                                            } else {
                                                //Registramos al nuevo técnico en la BD
                                                if (insertaTecnico(tecnicoDeRegistro, dao)) {
                                                    vista.mensajeTecnicoRegistrado();
                                                } else {
                                                    vista.errorTecnicoCreado();
                                                }
                                            }
                                            break;
                                        //BORRAR TÉCNICO
                                        case 9:
                                            //Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema
                                            ArrayList<Tecnico> tecnicos = buscaTecnicosSistema(dao);

                                            //Si el Arraylist no está vacio (hay técnicos registrados en el sistema), se muestran los técnicos
                                            if (!tecnicos.isEmpty()) {
                                                muestraTecnicos(tecnicos);

                                                //Pedimos por consola que se seleccione un técnico de la lista
                                                int numeroTecnico = 0;
                                                do {
                                                    try {
                                                        numeroTecnico = vista.pideDatosEnteros("el número del técnico que quiere seleccionar");
                                                    } catch (NumberFormatException e) {
                                                        vista.errorDatoOpcionMenu();
                                                    }
                                                } while (numeroTecnico < 0 || numeroTecnico > tecnicos.size());

                                                if (retroceso("al técnico")) {
                                                    //Una vez seleccionado el técnico, lo borramos de la BD
                                                    if (borraTecnico(tecnicos, numeroTecnico, dao)) {
                                                        vista.mensajeTecnicoBorrado();
                                                    } else {
                                                        vista.errorBorrarTecnico();
                                                    }
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //CREAR ADMINISTRADOR
                                        case 10:
                                            //TODO PROBAR
                                            //Pedimos por consola los datos del administrador que se va a registrar en la BD
                                            Admin adminDeRegistro = creaAdmin();

                                            /*Comprobamos que el correo del administrador no esté registrado en la BD, obteniendo un administrador
                                             que tenga ese correo registrado. Si el administrador obtenido es null, podremos registrar al
                                             nuevo administrador*/
                                            if (buscaAdminPorCorreo(adminDeRegistro.getEmail(), dao) != null) {
                                                vista.errorCrearAdmin();
                                            } else {
                                                //Registramos al nuevo técnico en la BD
                                                if (insertaAdmin(adminDeRegistro, dao)) {
                                                    vista.mensajeAdminRegistrado();
                                                } else {
                                                    vista.errorAdminRegistrado();
                                                }
                                            }
                                            break;
                                        //BORRAR ADMINISTRADOR
                                        case 11:
                                            //Obtenemos un Arraylist de la BD con los administradores registrados en el sistema
                                            ArrayList<Admin> admins = buscaAdminsSistema(dao);

                                            //Si el Arraylist no está vacio (hay administradores registrados en el sistema), se muestran los administradores
                                            if (!admins.isEmpty()) {
                                                //TODO CAMBIAR
                                                muestraAdmins(admins);

                                                //Pedimos por consola que se seleccione un administrador de la lista
                                                int numeroAdmin = 0;
                                                do {
                                                    try {
                                                        numeroAdmin = vista.pideDatosEnteros("el número del administrador que quiere seleccionar");
                                                    } catch (NumberFormatException e) {
                                                        vista.errorDatoOpcionMenu();
                                                    }
                                                } while (numeroAdmin < 0 || numeroAdmin > admins.size());

                                                if (retroceso("al administrador")) {
                                                    //Una vez seleccionado el administrador, lo borramos de la BD
                                                    if (borraAdmin(admins, numeroAdmin, dao)) {
                                                        vista.mensajeAdminBorrado();
                                                    } else {
                                                        vista.errorAdminBorrado();
                                                    }
                                                }
                                            } else {
                                                vista.errorExistenciaAdmin();
                                            }
                                            break;
                                        //MOSTRAR ESTADÍSTICAS DE LA APLICACIÓN
                                        case 12:
                                            //Obtenemos los Arraylist de la BD necesarios para las estadísticas del sistema
                                            muestraEstadisticasSistema(dao);
                                            break;
                                        //CERRAR SESIÓN
                                        case 13:
                                            cerrarSesionSuperAdmin = true;
                                            break;
                                    }
                                } while (!cerrarSesionSuperAdmin);
                            } else {
                                boolean cerrarSesionAdmin = false;
                                //TODO ADMINS
                                //MENÚ ADMIN
                                do {
                                    //Obtenemos los datos necesarios para la barra de estado
                                    ArrayList<Incidencia> incidenciasAbiertasSistemaBarraEstado = buscaIncidenciasAbiertasSistema(dao);
                                    ArrayList<Incidencia> incidenciasSinAsignarSistemaBarraEstado = buscaIncidenciasSinAsignarSistema(dao);

                                    //MENÚ ADMIN
                                    int respuestaMenuAdmin = muestraMenuAdmin(adminInicioSesion, incidenciasAbiertasSistemaBarraEstado, incidenciasSinAsignarSistemaBarraEstado);


                                    switch (respuestaMenuAdmin) {
                                        //CONSULTAR INCIDENCIAS ABIERTAS (SISTEMA COMPLETO)
                                        case 1:
                                            //Obtenemos un Arraylist de la BD con todas las incidencias abiertas del sistema
                                            ArrayList<Incidencia> incidenciasAbiertasSistema = buscaIncidenciasAbiertasSistema(dao);

                                            //Si el Arraylist no está vacio (hay incidencias abiertas en el sistema), se muestran las incidencias
                                            if (!incidenciasAbiertasSistema.isEmpty()) {
                                                muestraIncidencias(incidenciasAbiertasSistema);
                                            } else {
                                                vista.errorIncidenciaRegistrada();
                                            }
                                            break;
                                        //CONSULTAR INCIDENCIAS CERRADAS (POR TÉCNICO)
                                        case 2:
                                            //Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema
                                            ArrayList<Tecnico> tecnicosSistema = buscaTecnicosSistema(dao);

                                            //Si el Arraylist no está vacio (hay técnicos registrados en el sistema), se muestran los técnicos
                                            if (!tecnicosSistema.isEmpty()) {
                                                muestraTecnicos(tecnicosSistema);

                                                //Pedimos por consola que se seleccione un técnico de la lista
                                                int numeroTecnico = 0;
                                                do {
                                                    try {
                                                        numeroTecnico = vista.pideDatosEnteros("el número del técnico que quiere seleccionar");
                                                    } catch (NumberFormatException e) {
                                                        vista.errorDatoOpcionMenu();
                                                    }
                                                } while (numeroTecnico < 0 || numeroTecnico > tecnicosSistema.size());

                                                //Una vez seleccionado el técnico, obtenemos su id
                                                int idTecnico = buscaIdTecnico(tecnicosSistema, numeroTecnico);

                                                //Con el id del técnico, obtenemos un Arraylist de la BD con las incidencias cerradas por ese técnico
                                                ArrayList<Incidencia> incidenciasCerradasPorTecnico = buscaIncidenciasCerradasTecnico(idTecnico, dao);

                                                //Si el Arraylist no está vacio (el técnico seleccionado tiene incidencias cerradas), se muestran las incidencias
                                                if (!incidenciasCerradasPorTecnico.isEmpty()) {
                                                    muestraIncidencias(incidenciasCerradasPorTecnico);
                                                } else {
                                                    vista.errorExistenciaIncidenciasCerradas();
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //CONSULTAR INCIDENCIAS POR TÉRMINO
                                        case 3:
                                            /*Pedimos por consola que se introduzca una palabra y obtenemos un Arraylist de la BD con las incidencias
                                            que contengan esa palabra en la descripción*/
                                            ArrayList<Incidencia> incidenciasEncontradasPorTermino = buscaIncidenciaPorTermino(dao);

                                            //Si el Arraylist no está vacio (hay incidencias que contienen la palabra en la descripción), se muestran las incidencias
                                            if (!incidenciasEncontradasPorTermino.isEmpty()) {
                                                muestraIncidencias(incidenciasEncontradasPorTermino);
                                            } else {
                                                vista.errorIncidenciaPorTermino();
                                            }
                                            break;
                                        //MOSTRAR USUARIOS
                                        case 4:
                                            //Obtenemos un Arraylist de la BD con los usuarios registrados del sistema
                                            ArrayList<Usuario> usuariosEncontrados = buscaUsuariosSistema(dao);

                                            //Si el Arraylist no está vacio (hay usuarios registrados en el sistema), se muestran los usuarios
                                            if (!usuariosEncontrados.isEmpty()) {
                                                muestraUsuarios(usuariosEncontrados);
                                            } else {
                                                vista.errorUsuariosRegistrados();
                                            }
                                            break;
                                        //BORRAR USUARIO
                                        case 5:
                                            //Obtenemos un Arraylist de la BD con los usuarios registrados en el sistema
                                            ArrayList<Usuario> usuarios = buscaUsuariosSistema(dao);

                                            //Si el Arraylist no está vacio (hay usuarios registrados en el sistema), se muestran los usuarios
                                            if (!usuarios.isEmpty()) {
                                                muestraUsuarios(usuarios);

                                                //Pedimos por consola que se seleccione un usuario de la lista
                                                int numeroUsuario = 0;
                                                do {
                                                    try {
                                                        numeroUsuario = vista.pideDatosEnteros("el número del usuario que quiere seleccionar");
                                                    } catch (NumberFormatException e) {
                                                        vista.errorDatoOpcionMenu();
                                                    }
                                                } while (numeroUsuario < 0 || numeroUsuario > usuarios.size());

                                                if (retroceso("al usuario")) {
                                                    //Una vez seleccionado el usuario, lo borramos de la BD
                                                    if (borraUsuario(usuarios, numeroUsuario, dao)) {
                                                        vista.mensajeUsuarioBorrado();
                                                    } else {
                                                        vista.errorUsuarioBorrado();
                                                    }
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //MOSTRAR TÉCNICOS
                                        case 6:
                                            //Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema
                                            ArrayList<Tecnico> tecnicosEncontrados = buscaTecnicosSistema(dao);

                                            //Si el Arraylist no está vacio (hay técnicos registrados en el sistema), se muestran los técnicos
                                            if (!tecnicosEncontrados.isEmpty()) {
                                                muestraTecnicos(tecnicosEncontrados);
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //ASIGNAR INCIDENCIA
                                        case 7:
                                            /*Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema, ya que si no hay,
                                            no se puede asignar ninguna incidencia*/
                                            ArrayList<Tecnico> tecnicosDisponibles = buscaTecnicosSistema(dao);

                                            /*Si el Arraylist no está vacio (hay técnicos registrados en el sistema), obtenemos
                                             un Arraylist con todas las incidencias que están sin asignar del sistema*/
                                            if (!tecnicosDisponibles.isEmpty()) {
                                                //Obtenemos el Arraylist de las incidencias sin asignar
                                                ArrayList<Incidencia> incidenciasSinAsignar = buscaIncidenciasSinAsignarSistema(dao);

                                                //Si el Arraylist no está vacio (hay incidencias sin asignar), se muestran las incidencias
                                                if (!incidenciasSinAsignar.isEmpty()) {
                                                    muestraIncidencias(incidenciasSinAsignar);

                                                    //Pedimos por consola que se seleccione una incidencia de la lista
                                                    int numeroIncidencia = 0;
                                                    do {
                                                        try {
                                                            numeroIncidencia = vista.pideDatosEnteros("el número de la incidencia que quiere seleccionar");
                                                        } catch (NumberFormatException e) {
                                                            vista.errorDatoOpcionMenu();
                                                        }
                                                    } while (numeroIncidencia < 0 || numeroIncidencia > incidenciasSinAsignar.size());

                                                    //Mostramos los técnicos disponibles del sistema (obtenidos al principio del Case)
                                                    muestraTecnicos(tecnicosDisponibles);

                                                    //Pedimos por consola que se seleccione un técnico de la lista
                                                    int numeroTecnico = 0;
                                                    do {
                                                        try {
                                                            numeroTecnico = vista.pideDatosEnteros("el número del técnico que quiere seleccionar");
                                                        } catch (NumberFormatException e) {
                                                            vista.errorDatoOpcionMenu();
                                                        }
                                                    } while (numeroTecnico < 0 || numeroTecnico > tecnicosDisponibles.size());

                                                /*Asignamos la incidencia seleccionada al técnico seleccionado, pasándole a la incidencia la id
                                                 del técnico en la BD*/
                                                    if (asignaIncidencia(incidenciasSinAsignar, numeroIncidencia, tecnicosDisponibles, numeroTecnico, dao)) {
                                                        vista.mensajeIncidenciaAsignada();
                                                    } else {
                                                        vista.errorNuevaIncidenciaAsignada();
                                                    }
                                                } else {
                                                    vista.errorIncidenciaRegistrada();
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //CREAR TÉCNICO
                                        case 8:
                                            //Pedimos por consola los datos del técnico que se va a registrar en la BD
                                            Tecnico tecnicoDeRegistro = creaTecnico();

                                            /*Comprobamos que el correo del técnico no esté registrado en la BD, obteniendo un técnico que tenga
                                            ese correo registrado. Si el técnico obtenido es null, podremos registrar al nuevo técnico*/
                                            if (buscaTecnicoPorCorreo(tecnicoDeRegistro.getEmail(), dao) != null) {
                                                vista.errorCrearTecnico();
                                            } else {
                                                //Registramos al nuevo técnico en la BD
                                                if (insertaTecnico(tecnicoDeRegistro, dao)) {
                                                    vista.mensajeTecnicoRegistrado();
                                                } else {
                                                    vista.errorTecnicoCreado();
                                                }
                                            }
                                            break;
                                        //BORRAR TÉCNICO
                                        case 9:
                                            //Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema
                                            ArrayList<Tecnico> tecnicos = buscaTecnicosSistema(dao);

                                            //Si el Arraylist no está vacio (hay técnicos registrados en el sistema), se muestran los técnicos
                                            if (!tecnicos.isEmpty()) {
                                                muestraTecnicos(tecnicos);

                                                //Pedimos por consola que se seleccione un técnico de la lista
                                                int numeroTecnico = 0;
                                                do {
                                                    try {
                                                        numeroTecnico = vista.pideDatosEnteros("el número del técnico que quiere seleccionar");
                                                    } catch (NumberFormatException e) {
                                                        vista.errorDatoOpcionMenu();
                                                    }
                                                } while (numeroTecnico < 0 || numeroTecnico > tecnicos.size());

                                                if (retroceso("al técnico")) {
                                                    //Una vez seleccionado el técnico, lo borramos de la BD
                                                    if (borraTecnico(tecnicos, numeroTecnico, dao)) {
                                                        vista.mensajeTecnicoBorrado();
                                                    } else {
                                                        vista.errorBorrarTecnico();
                                                    }
                                                }
                                            } else {
                                                vista.errorTecnicoRegistrado();
                                            }
                                            break;
                                        //MOSTRAR ESTADÍSTICAS DE LA APLICACIÓN
                                        case 10:
                                            //Obtenemos los Arraylist de la BD necesarios para las estadísticas del sistema
                                            muestraEstadisticasSistema(dao);
                                            break;
                                        case 11:
                                            cerrarSesionAdmin = true;
                                            break;
                                    }
                                } while (!cerrarSesionAdmin);
                            }
                        }
                    } else {
                        vista.errorInicioSesion();
                    }
                }
                //REGISTRARSE
                case 2 -> {
                    //Creamos el usuario, pidiendo los datos
                    Usuario usuarioDeRegistro = creaUsuario();
                    //Comprobamos si el usuario está registrado en la BD, buscando por el correo
                    if (buscaUsuarioPorCorreo(usuarioDeRegistro.getEmail(), dao) != null) {
                        //Si nos devuelve un usuario con datos, el correo está registrado en la BD
                        vista.errorCrearUsuario();
                    } else {
                        //Si nos devuelve un usuario vacío, el correo no está registrado en la BD
                        //Insertamos el usuario en la BD
                        if (insertaUsuario(usuarioDeRegistro, dao)) {
                            vista.mensajeUsuarioRegistrado();
                        } else {
                            vista.errorUsuarioRegistrado();
                        }
                    }
                }
                case 3 -> salida = true;
                default -> vista.errorOpcionMenu();
            }
        } while (!salida);
    }

    //MÉTODOS

    //GENERALES-----------------------------------------------------------------------------------------
//todo muestra menús
    //MOSTRAR MENÚ PRINCIPAL
    public int muestraMenuPrincipal() {
        int respuestaMenu = 0;
        do {
            vista.muestraMenuInicio();
            try {
                respuestaMenu = vista.pideDatosEnteros("opción");
            } catch (NumberFormatException e) {
                vista.errorDatoOpcionMenu();
            }
        } while (respuestaMenu <= 0 || respuestaMenu > 3);

        return respuestaMenu;
    }

    //MOSTRAR MENÚ USUARIO
    public int muestraMenuUsuario(Usuario usuarioInicioSesion, ArrayList<Incidencia> incidenciasAbiertasUsuarioBarraEstado, ArrayList<Incidencia> incidenciasCerradasUsuarioBarraEstado) {
        int respuestaMenuUsuario = 0;
        do {
            vista.muestraMenuUsuario(usuarioInicioSesion.getNombre(), incidenciasAbiertasUsuarioBarraEstado.size(), incidenciasCerradasUsuarioBarraEstado.size());
            try {
                respuestaMenuUsuario = vista.pideDatosEnteros("opción");
            } catch (NumberFormatException e) {
                vista.errorDatoOpcionMenu();
            }
        } while (respuestaMenuUsuario <= 0 || respuestaMenuUsuario > 7);
        return respuestaMenuUsuario;
    }

    //MOSTRAR MENÚ TÉCNICO
    public int muestraMenuTecnico(Tecnico tecnicoInicioSesion, ArrayList<Incidencia> incidenciasAsignadasTecnicoBarraEstado, ArrayList<Incidencia> incidenciasCerradasTecnicoBarraEstado) {
        int respuestaMenuTecnico = 0;
        do {
            vista.muestraMenuTecnico(tecnicoInicioSesion.getNombre(), incidenciasAsignadasTecnicoBarraEstado.size(), incidenciasCerradasTecnicoBarraEstado.size());
            try {
                respuestaMenuTecnico = vista.pideDatosEnteros("opción");
            } catch (NumberFormatException e) {
                vista.errorDatoOpcionMenu();
            }
        } while (respuestaMenuTecnico <= 0 || respuestaMenuTecnico > 6);
        return respuestaMenuTecnico;
    }

    //MOSTRAR MENÚ SUPERADMIN
    public int muestraMenuSuperAdmin(Admin adminInicioSesion, ArrayList<Incidencia> incidenciasAbiertasSistemaBarraEstado, ArrayList<Incidencia> incidenciasSinAsignarSistemaBarraEstado) {
        int respuestaMenuSuperAdmin = 0;
        do {
            vista.muestraMenuSuperAdmin(adminInicioSesion.getNombre(), incidenciasAbiertasSistemaBarraEstado.size(), incidenciasSinAsignarSistemaBarraEstado.size());
            try {
                respuestaMenuSuperAdmin = vista.pideDatosEnteros("opción");
            } catch (NumberFormatException e) {
                vista.errorDatoOpcionMenu();
            }
        } while (respuestaMenuSuperAdmin <= 0 || respuestaMenuSuperAdmin > 13);
        return respuestaMenuSuperAdmin;
    }

    //MOSTRAR MENÚ ADMIN
    public int muestraMenuAdmin(Admin adminInicioSesion, ArrayList<Incidencia> incidenciasAbiertasSistemaBarraEstado, ArrayList<Incidencia> incidenciasSinAsignarSistemaBarraEstado) {
        int respuestaMenuAdmin = 0;
        do {
            vista.muestraMenuAdmin(adminInicioSesion.getNombre(), incidenciasAbiertasSistemaBarraEstado.size(), incidenciasSinAsignarSistemaBarraEstado.size());
            try {
                respuestaMenuAdmin = vista.pideDatosEnteros("opción");
            } catch (NumberFormatException e) {
                vista.errorDatoOpcionMenu();
            }
        } while (respuestaMenuAdmin <= 0 || respuestaMenuAdmin > 11);
        return respuestaMenuAdmin;
    }

    //LOG IN
    public boolean logIn(String correo, String password, DAOManager dao) {
        //Buscamos en la BD que el correo y la contraseña se encuentren en las misma tabla
        boolean registrado = false;
        if (buscaUsuarioPorCorreoYClave(correo, password, dao) != null) {
            registrado = true;
        }

        if (buscaTecnicoPorCorreoYClave(correo, password, dao) != null) {
            registrado = true;
        }

        if (buscaAdminPorCorreoYClave(correo, password, dao) != null) {
            registrado = true;
        }

        //Si se encuentra en alguna de las tablas, devuelve true. Si no lo encuentra, devuelve false
        return registrado;
    }

    //Comprobar clave
    public boolean compruebaClave(String claveOriginal) {
        //Se compara la contraseña original del usuario, técnico o administrador con la pasada por consola
        String clave = vista.pideDatosString("su contraseña actual");
        //Si es igual devuelve true. Si no, devuelve false
        if (claveOriginal.equals(clave)) {
            return true;
        } else {
            return false;
        }
    }

    //Mostrar estadísticas del sistema
    public void muestraEstadisticasSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con los usuarios registrados en el sistema
        ArrayList<Usuario> todosUsuariosSistema = buscaUsuariosSistema(dao);

        //Obtenemos un Arraylist de la BD con los técnicos registrados en el sistema
        ArrayList<Tecnico> todosTecnicosSistema = buscaTecnicosSistema(dao);

        //Obtenemos un Arraylist de la BD con los administradores registrados en el sistema
        ArrayList<Admin> todosAdminSistema = buscaAdminsSistema(dao);

        //Obtenemos un Arraylist de la BD con las incidencias abiertas del sistema
        ArrayList<Incidencia> todasIncidenciasAbiertasSistema = buscaIncidenciasAbiertasSistema(dao);

        //Obtenemos un Arraylist de la BD con las incidencias cerradas del sistema
        ArrayList<Incidencia> todasIncidenciasCerradasSistema = buscaIncidenciasCerradasSistema(dao);

        //Obtenemos un Arraylist de la BD con las incidencias asignadas del sistema
        ArrayList<Incidencia> todasIncidenciasAsignadasSistema = buscaIncidenciasAsignadasSistema(dao);

        //Obtenemos un Arraylist de la BD con las incidencias sin asignar del sistema
        ArrayList<Incidencia> todasIncidenciasSinAsignarSistema = buscaIncidenciasSinAsignarSistema(dao);

        //Pasamos el tamaño de todos los Arraylist al método de la vista que los mostrará como estadísticas
        vista.muestraEstadisticas(todosUsuariosSistema.size(), todosTecnicosSistema.size(), todosAdminSistema.size(), todasIncidenciasAbiertasSistema.size(), todasIncidenciasCerradasSistema.size(), todasIncidenciasAsignadasSistema.size(), todasIncidenciasSinAsignarSistema.size());
    }

    public boolean retroceso(String objetoABorrar) {
        int respuestaMenuRetroceso = 0;
        boolean retrocede = false;
        do {
            try {
                vista.muestraMenuRetroceso(objetoABorrar);
                respuestaMenuRetroceso = vista.pideDatosEnteros("una opción");
            } catch (NumberFormatException e) {
                vista.errorDatoOpcionMenu();
            }
        }while (respuestaMenuRetroceso < 0 || respuestaMenuRetroceso > 2 );
        if (respuestaMenuRetroceso == 1) {
            retrocede = true;
        }
        return retrocede;
    }

    //Generar token
    public String generaToken() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }

    //TELEGRAM-----------------------------------------------------------------------------------------
    public static boolean enviaMensajeTelegram (String mensaje) {
        String direccion;
        String fijo = "https://api.telegram.org/bot5202479427:AAEo2tSiarYI1hf6jjMFs4wlTOu67WA6R48/sendMessage?chat_id=1954372519&text=";
        direccion = fijo + mensaje;
        URL url;
        boolean dev;
        dev = false;

        try {
            url = new URL (direccion);
            URLConnection con = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            dev = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return dev;
    }

    //Enviar mensaje Telegram incidenciaRegistrada
    public void enviaMensajeTelegramIncidenciaRegistrada() {
        String mensaje = "¡Nueva incidencia registrada en el sistema!";
        if (enviaMensajeTelegram(mensaje)) {
            vista.mensajeNotificacionEnviada();
        } else {
            vista.errorNotificacionEnviada();
        }
    }

    //EMAILS-----------------------------------------------------------------------------------------
    //Mandar email
    public static void enviaEmail(String destinatario, String asunto, String cuerpo) {
        String remitente = "admiproyectoincidencias@gmail.com";
        String clave = "Admin20175258";
        // Propiedades de la conexión que se va a establecer con el servidor de correo SMTP
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Servidor SMTP de Google
        props.put("mail.smtp.user", remitente);
        props.put("mail.smtp.clave", clave);
        props.put("mail.smtp.auth", "true"); // Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); // Conectar de manera segura
        props.put("mail.smtp.port", "587"); // Puerto SMTP seguro de Google
        // Se obtiene la sesión en el servidor de correo
        Session session = Session.getDefaultInstance(props);
        try {
            // Creación del mensaje a enviar
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destinatario));

            message.setSubject(asunto);
            //message.setText(cuerpo); // Para enviar texto plano
            message.setContent(cuerpo, "text/html; charset=utf-8"); // Para enviar html
            // Definición de los parámetros del protocolo de transporte
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", remitente, clave);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }

    //Mandar email incidencia asignada
    public void enviaEmailIncidenciaAsignada(String correo) {
        String asunto = "Nueva incidencia asignada";
        String cuerpo = "<p>Le informamos de que se le ha asignado una nueva incidencia. Acceda a la aplicación para gestionarla lo antes posible.</p>";
        enviaEmail(correo, asunto, cuerpo);
    }


    //USUARIOS-----------------------------------------------------------------------------------------
    //todo MÉTODOS USUARIOS
    //Crear usuario
    public Usuario creaUsuario() {
        //Pedimos los datos necesarios para registrar un nuevo usuario
        String nombre = vista.pideDatosString("su nombre");
        String apellido = vista.pideDatosString("su apellido");
        String email = vista.pideDatosString("su email");
        String clave = vista.pideDatosString("su contraseña");

        //Pasamos los datos a un objeto de tipo Usuario, que se registrará en la BD
        Usuario usuario = new Usuario(nombre, apellido, email, clave);
        return usuario;
    }

    //Borrar usuario
    public boolean borraUsuario(ArrayList<Usuario> usuarios, int numeroUsuario, DAOManager dao) {
        //Recorremos el Arraylist de usuarios hasta encontrar al usuario que se corresponde con el seleccionado para ser borrado
        int id = 0;
        for (int i = 0; i < usuarios.size(); i++) {
            if ((numeroUsuario - 1) == i) {
                //Obtenemos la id del usuario que será borrado
                id = usuarios.get(i).getId();
                break;
            }
        }

        //Utilizamos la id del usuario para buscarlo en la BD y borrar todos sus datos
        if (daoUsuarioSQL.deleteUsuario(id, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Buscar usuario por correo para saber si está registrado
    public Usuario buscaUsuarioPorCorreo(String email, DAOManager dao) {
        /*Buscamos un usuario cuyo correo coincida con el introducido y guardamos sus datos en un nuevo usuario.
          Si el nuevo usuario contiene datos, está registrado en el sistema. Si sus datos están vacios, no se encuentra en la BD*/
        Usuario usuario = daoUsuarioSQL.readUsuarioPorCorreo(email, dao);
        return usuario;
    }

    //Buscar usuario por correo y contraseña para el log in
    public Usuario buscaUsuarioPorCorreoYClave(String email, String clave, DAOManager dao) {
        /*Buscamos un usuario cuyo correo y contraseña coincidan con el email y la clave introducidos, y guardamos sus datos en un nuevo usuario.
          Si el nuevo usuario contiene datos, está registrado en el sistema. Si sus datos están vacios, no se encuentra en la BD*/
        Usuario usuario = daoUsuarioSQL.readUsuarioPorCorreoYClave(email, clave, dao);
        return usuario;
    }

    //Insertar usuario en la BD
    public boolean insertaUsuario(Usuario usuario, DAOManager dao) {
        //Insertamos en la BD los datos del usuario obtenido
        if (daoUsuarioSQL.insert(usuario, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Cambiar contraseña del usuario
    public boolean cambiarClaveUsuario(int id, DAOManager dao) {
        //Pedimos por consola la nueva contraseña
        String clave = vista.pideDatosString("su nueva contraseña");
        //Actualizamos la contraseña del usuario que coincida con la id en la BD
        if (daoUsuarioSQL.update(clave, id, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Buscar todos los usuarios del sistema
    public ArrayList<Usuario> buscaUsuariosSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con todos los usuarios registrados en el sistema
        ArrayList<Usuario> usuariosEncontrados = daoUsuarioSQL.readALLUsuarios(dao);
        return usuariosEncontrados;
    }

    //Mostrar usuarios
    public void muestraUsuarios(ArrayList<Usuario> usuarios) {
        //Mostramos los usuarios contenidos en el Arraylist obtenido
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println("\n <- Usuario nº: " + (i + 1) + " ->");
            System.out.println(usuarios.get(i));
        }
    }


    //TÉCNICOS-----------------------------------------------------------------------------------------
    //todo MÉTODOS TÉCNICOS
    //Crear técnico
    public Tecnico creaTecnico() {
        //Pedimos por consola los datos necesarios para crear un nuevo técnico
        String nombre = vista.pideDatosString("su nombre");
        String apellido = vista.pideDatosString("su apellido");
        String email = vista.pideDatosString("su email");
        String clave = vista.pideDatosString("su contraseña");

        //Pasamos los datos a un objeto de tipo Técnico, que se registrará en la BD
        Tecnico tecnico = new Tecnico(nombre, apellido, email, clave);
        return tecnico;
    }

    //Buscar tecnico por correo para saber si está registrado
    public Tecnico buscaTecnicoPorCorreo(String email, DAOManager dao) {
        /*Buscamos un técnico cuyo correo coincida con el introducido y guardamos sus datos en un nuevo técnico.
          Si el nuevo técnico contiene datos, está registrado en el sistema. Si sus datos están vacios, no se encuentra en la BD*/
        Tecnico tecnico = daoTecnicoSQL.readTecnicoPorCorreo(email, dao);
        return tecnico;
    }

    //Buscar tecnico por correo y contraseña para el log in
    public Tecnico buscaTecnicoPorCorreoYClave(String email, String clave, DAOManager dao) {
        /*Buscamos un técnico cuyo correo y contraseña coincidan con el email y la clave introducidos, y guardamos sus datos en un nuevo técnico.
          Si el nuevo técnico contiene datos, está registrado en el sistema. Si sus datos están vacios, no se encuentra en la BD*/
        Tecnico tecnico = daoTecnicoSQL.readTecnicoPorCorreoYClave(email, clave, dao);
        return tecnico;
    }

    //Insertar tecnicos en la BD
    public boolean insertaTecnico(Tecnico tecnico, DAOManager dao) {
        //Insertamos en la BD los datos del técnico obtenido
        if (daoTecnicoSQL.insert(tecnico, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Cambiar contraseña del técnico
    public boolean cambiarClaveTecnico(int id, DAOManager dao) {
        //Pedimos por consola la nueva contraseña
        String clave = vista.pideDatosString("su nueva contraseña");
        //Actualizamos la contraseña del técnico que coincida con la id en la BD
        if (daoTecnicoSQL.update(clave, id, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Mostrar técnicos
    public void muestraTecnicos(ArrayList<Tecnico> tecnicos) {
        //Mostramos los técnicos contenidos en el Arraylist obtenido
        for (int i = 0; i < tecnicos.size(); i++) {
            System.out.println("\n <- Técnico nº: " + (i + 1) + " ->");
            System.out.println(tecnicos.get(i));
        }
    }

    //Buscar todos los técnicos del sistema
    public ArrayList<Tecnico> buscaTecnicosSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con todos los técnicos registrados en el sistema
        ArrayList<Tecnico> tecnicosEncontrados = daoTecnicoSQL.readALLTecnicos(dao);
        return tecnicosEncontrados;
    }

    //Obtener la id del técnico
    public int buscaIdTecnico(ArrayList<Tecnico> tecnicos, int numeroTecnico) {
        //Recorremos el Arraylis de técnicos hasta encontrar al técnico que se corresponde con el seleccionado
        int id = 0;
        for (int i = 0; i < tecnicos.size(); i++) {
            if ((numeroTecnico - 1) == i) {
                //Obtenemos la id del técnico
                id = tecnicos.get(i).getId();
                break;
            }
        }
        return id;
    }

    public String buscaCorreoTecnico(ArrayList<Tecnico> tecnicos, int numeroTecnico) {
        String correo = "";
        for (int i = 0; i < tecnicos.size(); i++) {
            if ((numeroTecnico -1) == i) {
                correo = tecnicos.get(i).getEmail();
            }
        }
        return correo;
    }

    //Borrar técnico
    public boolean borraTecnico(ArrayList<Tecnico> tecnicos, int numeroTecnico, DAOManager dao) {
        //Recorremos el Arraylist de técnicos hasta encontrar al técnico que se corresponde con el seleccionado para ser borrado
        int id = 0;
        for (int i = 0; i < tecnicos.size(); i++) {
            if ((numeroTecnico - 1) == i) {
                //Obtenemos la id del técnico que será borrado
                id = tecnicos.get(i).getId();
                break;
            }
        }

        //Utilizamos la id del técnico para buscarlo en la BD y borrar todos sus datos
        if (daoTecnicoSQL.delete(id, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //ADMINS-----------------------------------------------------------------------------------------
    //todo MÉTODOS ADMINS
    //Crear admin
    public Admin creaAdmin() {
        //Pedimos por consola los datos necesarios para crear un nuevo administrador
        String nombre = vista.pideDatosString("su nombre");
        String apellido = vista.pideDatosString("su apellido");
        String email = vista.pideDatosString("su email");
        String clave = vista.pideDatosString("su contraseña");

        //Pasamos los datos a un objeto de tipo Admin, que se registrará en la BD
        Admin admin = new Admin(nombre, apellido, email, clave, false);
        return admin;
    }

    //Buscar tecnico por correo para saber si está registrado
    public Admin buscaAdminPorCorreo(String email, DAOManager dao) {
        /*Buscamos un administrador cuyo correo coincida con el introducido y guardamos sus datos en un nuevo Admin.
          Si el nuevo administrador contiene datos, está registrado en el sistema. Si sus datos están vacios, no se encuentra en la BD*/
        Admin admin = daoAdminSQL.readAdminPorCorreo(email, dao);
        return admin;
    }

    //Buscar admin por correo y contraseña para el log in
    public Admin buscaAdminPorCorreoYClave(String email, String clave, DAOManager dao) {
        /*Buscamos un administrador cuyo correo y contraseña coincidan con el email y la clave introducidos, y guardamos sus datos en un nuevo Admin.
          Si el nuevo administrador contiene datos, está registrado en el sistema. Si sus datos están vacios, no se encuentra en la BD*/
        Admin admin = daoAdminSQL.readAdminPorCorreoYClave(email, clave, dao);
        return admin;
    }

    //Insertar admin en la BD
    public boolean insertaAdmin(Admin admin, DAOManager dao) {
        //Insertamos en la BD los datos del administrador obtenido
        if (daoAdminSQL.insert(admin, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Buscar todos los administradores del sistema
    public ArrayList<Admin> buscaAdminsSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con los administradores registrados en el sistema
        ArrayList<Admin> adminsEncontrados = daoAdminSQL.readALLAdmins(dao);
        return adminsEncontrados;
    }

    //Mostrar administradores
    public void muestraAdmins(ArrayList<Admin> admins) {
        //Mostramos los administradores contenidos en el Arraylist obtenido
        for (int i = 0; i < admins.size(); i++) {
            System.out.println("\n <- Administrador nº: " + (i + 1) + " ->");
            System.out.println(admins.get(i));
        }
    }

    //Borrar administrador
    public boolean borraAdmin(ArrayList<Admin> admins, int numeroAdmin, DAOManager dao) {
        //Recorremos el Arraylist de adminstradores hasta encontrar al administrador que se corresponde con el seleccionado para ser borrado
        int id = 0;
        for (int i = 0; i < admins.size(); i++) {
            if ((numeroAdmin - 1) == i) {
                //Obtenemos la id del administrador que será borrado
                id = admins.get(i).getId();
                break;
            }
        }

        //Utilizamos la id del administrador para buscarlo en la BD y borrar todos sus datos
        if (daoAdminSQL.delete(id, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //INCIDENCIAS-----------------------------------------------------------------------------------------
    //todo MÉTODOS INCIDENCIAS
    //Crear incidencia
    public Incidencia creaIncidencia(int idUsuario) {
        //Pedimos la descripción de la incidencia
        String descripcion = vista.pideDatosString("una descripción");

        //Pedimos la prioridad de la incidencia
        int prioridad = 0;
        do {
            vista.muestraMenuPrioridad();
            try {
                prioridad = vista.pideDatosEnteros("la prioridad");
            } catch (NumberFormatException e) {
                vista.errorDatoOpcionMenu();
            }

            if (prioridad <= 0 || prioridad > 5) {
                vista.errorOpcionMenu();
            }
        } while (prioridad < 0 || prioridad > 5);

        //Obtenemos la fecha actual (fecha en la que se crea la incidencia)
        Calendar fechaInicio = Calendar.getInstance();

        String fechaInicioFormateada = new SimpleDateFormat("dd/MM/yyyy").format(fechaInicio.getTime());

        //Pasamos todos los datos a un objeto de tipo Incidencia, que se registrará en la BD
        Incidencia incidencia = new Incidencia(descripcion, prioridad, fechaInicioFormateada, idUsuario);
        return incidencia;
    }

    //Insertar incidencia en la BD
    public boolean insertaIncidencia(Incidencia incidencia, DAOManager dao) {
        //Registramos la incidencia obtenida en la BD
        if (daoIncidenciaSQL.insert(incidencia, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Buscar las incidencias del usuario
    public ArrayList<Incidencia> buscaTodasIncidenciasUsuario(int idUsuario, DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias del usuario cuya id coincida con la id obtenida
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLPorIdUsuario(idUsuario, dao);
        return incidenciasEncontradas;
    }

    //Buscar las incidencias abiertas del usuario
    public ArrayList<Incidencia> buscaIncidenciasAbiertasUsuario(int idUsuario, DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias abiertas del usuario cuya id coincida con la id obtenida
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLPorIdUsuarioYAbiertas(idUsuario, dao);
        return incidenciasEncontradas;
    }

    //Buscar las incidencias cerradas del usuario
    public ArrayList<Incidencia> buscaIncidenciasCerradasUsuario(int idUsuario, DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias cerradas del usuario cuya id coincida con la id obtenida
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLPorIdUsuarioYResueltas(idUsuario, dao);
        return incidenciasEncontradas;
    }

    //Buscar todas las incidencias asignadas al técnico
    public ArrayList<Incidencia> buscaIncidenciasAsignadasTecnico(int idTecnico, DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias asignadas al técnico cuya id coincida con la id obtenida
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLPorIdTecnicoYAsignadas(idTecnico, dao);
        return incidenciasEncontradas;
    }

    //Buscar todas las incidencias cerradas por el técnico
    public ArrayList<Incidencia> buscaIncidenciasCerradasTecnico(int idTecnico, DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias cerradas del técnico cuya id coincida con la id obtenida
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLPorIdTecnicoYCerradas(idTecnico, dao);
        return incidenciasEncontradas;
    }

    //Buscar todas las incidencias abiertas del sistema
    public ArrayList<Incidencia> buscaIncidenciasAbiertasSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias abiertas del sistema
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLTodasAbiertas(dao);
        return incidenciasEncontradas;
    }

    //Buscar todas las incidencias cerradas del sistema
    public ArrayList<Incidencia> buscaIncidenciasCerradasSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias cerradas del sistema
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLTodasCerradas(dao);
        return incidenciasEncontradas;
    }

    //Buscar todas las incidencias asignadas del sistema
    public ArrayList<Incidencia> buscaIncidenciasAsignadasSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias asignadas del sistema
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLTodasAsignadas(dao);
        return incidenciasEncontradas;
    }

    //Buscar todas las incidencias sin asignar del sistema
    public ArrayList<Incidencia> buscaIncidenciasSinAsignarSistema(DAOManager dao) {
        //Obtenemos un Arraylist de la BD con las incidencias sin asignar del sistema
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLTodasSinAsignar(dao);
        return incidenciasEncontradas;
    }

    //Buscar incidencias por término
    public ArrayList<Incidencia> buscaIncidenciaPorTermino(DAOManager dao) {
        //Pedimos por consola que se introduza una palabra
        String termino = vista.pideDatosString("una palabra");

        //Obtenemos un Arraylist de la BD con las incidencias que contengan la palabra intoducida en su decripción
        ArrayList<Incidencia> incidenciasEncontradas = daoIncidenciaSQL.readALLPorTermino(termino, dao);
        return incidenciasEncontradas;
    }

    //Borrar incidencia
    public boolean borraIncidencia(ArrayList<Incidencia> incidencias, int numeroIncidencia, DAOManager dao) {
        //Recorremos el Arraylist de incidencias hasta encontrar la incidencia que se corresponde con la seleccionada para ser borrada
        int id = 0;
        for (int i = 0; i < incidencias.size(); i++) {
            if ((numeroIncidencia - 1) == i) {
                //Obtenemos la id de la incidencia
                id = incidencias.get(i).getId();
                break;
            }
        }

        //Borramos todos los datos de la incidencia que coincida con la id en la BD
        if (daoIncidenciaSQL.delete(id, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Marcar incidencia como resuelta
    public boolean marcaIncidenciaResuelta(ArrayList<Incidencia> incidencias, int numeroIncidencia, DAOManager dao) {
        //Recorremos el Arraylist de incidencias hasta encontrar la incidencia que se corresponde con la seleccionada para ser resuelta
        int id = 0;
        for (int i = 0; i < incidencias.size(); i++) {
            if ((numeroIncidencia - 1) == i) {
                //Obtenemos la id de la incidencia
                id = incidencias.get(i).getId();
                break;
            }
        }

        //Pedimos por consola que el técnico introduzca la solución
        String solucion = vista.pideDatosString("una solución");

        //Obtenemos la fecha actual (fecha en la que se cierra la incidencia)
        Calendar fechaFin = Calendar.getInstance();

        String fechaFinFormateada = new SimpleDateFormat("dd/MM/yyyy").format(fechaFin.getTime());

        //Pasamos todos los datos que serán modificados en la incidencia que coincida con la id en la BD
        //Se modifica el boolean asignada a false, el boolean resuelta a true, la solución y la fecha fin
        if (daoIncidenciaSQL.updateResuelta(id, solucion, fechaFinFormateada, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Asignar incidencia
    public boolean asignaIncidencia(ArrayList<Incidencia> incidencias, int numeroIncidencia, ArrayList<Tecnico> tecnicos, int numeroTecnico, DAOManager dao) {
        //Recorremos el Arraylist de incidencias hasta encontrar la incidencia que se corresponde con la seleccionada para ser asignada
        int idIncidencia = 0;
        for (int i = 0; i < incidencias.size(); i++) {
            if ((numeroIncidencia - 1) == i) {
                //Obtenemos la id de la incidencia
                idIncidencia = incidencias.get(i).getId();
                break;
            }
        }

        //Recorremos el Arraylist de técnicos hasta encontrar el técnico que se corresponde con el seleccionado
        int idTecnico = 0;
        for (int i = 0; i < tecnicos.size(); i++) {
            if ((numeroTecnico - 1) == i) {
                //Obtenemos la id del técnico
                idTecnico = tecnicos.get(i).getId();
                break;
            }
        }

        //Modificamos la incidencia que coincida con la id en la base de datos
        //Se modifica el boolean asignada a true y la id del técnico
        if (daoIncidenciaSQL.updateAsignada(idIncidencia, idTecnico, dao)) {
            return true;
        } else {
            return false;
        }
    }

    //Mostrar incidencias
    public void muestraIncidencias(ArrayList<Incidencia> incidencias) {
        //Mostramos las incidencias contenidas en el Arraylist obtenido
        for (int i = 0; i < incidencias.size(); i++) {
            System.out.println("\n <- Incidencia nº: " + (i + 1) + " ->");
            System.out.println(incidencias.get(i));
        }
    }
}