package BibliotecaN.Controladores;

import BibliotecaN.Modelos.Usuario;
import BibliotecaN.Modelos.UsuarioDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/usuario")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;
    private EntityManager entityManager;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        entityManager = emf.createEntityManager();
        usuarioDAO = new UsuarioDAO(entityManager);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String id = request.getParameter("id");

        if ("edit".equals(action)) {
            Usuario usuario = usuarioDAO.buscarPorId(Integer.parseInt(id));

            if (usuario != null) {
                request.setAttribute("usuario", usuario);
                request.getRequestDispatcher("/editarUsuario.jsp").forward(request, response);
            } else {
                response.getWriter().write("Usuario no encontrado.");
            }
        } else if ("delete".equals(action)) {
            usuarioDAO.eliminarUsuario(Integer.parseInt(id));
            response.sendRedirect("usuario");
        } else {
            List<Usuario> usuarios = usuarioDAO.listarUsuarios();
            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("/usuarios.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Usuario.TipoUsuario tipo = Usuario.TipoUsuario.valueOf(request.getParameter("tipo"));
        String id = request.getParameter("id"); // Agregar el id

        try {
            if (id != null && !id.isEmpty()) {
                Usuario usuarioExistente = usuarioDAO.buscarPorId(Integer.parseInt(id));

                if (usuarioExistente != null) {
                    usuarioExistente.setNombre(nombre);
                    usuarioExistente.setEmail(email);
                    usuarioExistente.setPassword(password);
                    usuarioExistente.setTipo(tipo);

                    usuarioDAO.actualizarUsuario(usuarioExistente);
                    response.sendRedirect("usuario");
                } else {
                    response.getWriter().write("Usuario no encontrado.");
                }
            } else {
                Usuario usuario = usuarioDAO.buscarPorDni(dni);

                if (usuario == null) {
                    usuario = new Usuario(dni, nombre, email, password, tipo);
                    usuarioDAO.crearUsuario(usuario);
                    response.sendRedirect("usuario");
                } else {
                    response.getWriter().write("Usuario con DNI repetido!");
                }
            }
        } catch (Exception e) {
            response.getWriter().write("Error al procesar el usuario: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}

