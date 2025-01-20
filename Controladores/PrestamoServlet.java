package BibliotecaN.Controladores;

import BibliotecaN.Modelos.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/prestamo")
public class PrestamoServlet extends HttpServlet {

    private PrestamoDAO prestamoDAO;
    private UsuarioDAO usuarioDAO;
    private EjemplarDAO ejemplarDAO;
    private EntityManager entityManager;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        entityManager = emf.createEntityManager();
        prestamoDAO = new PrestamoDAO(entityManager);
        usuarioDAO = new UsuarioDAO(entityManager);
        ejemplarDAO = new EjemplarDAO(entityManager);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String id = request.getParameter("id");

        if ("delete".equals(action)) {
            prestamoDAO.registrarDevolucion(Integer.parseInt(id), LocalDate.now());
            response.sendRedirect("prestamo");
        } else {
            List<Prestamo> prestamos = entityManager.createQuery("SELECT p FROM Prestamo p", Prestamo.class).getResultList();
            request.setAttribute("prestamos", prestamos);
            request.getRequestDispatcher("/prestamos.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuarioId = request.getParameter("usuarioId");
        String ejemplarId = request.getParameter("ejemplarId");

        try {
            Usuario usuario = usuarioDAO.buscarPorId(Integer.parseInt(usuarioId));
            Ejemplar ejemplar = ejemplarDAO.buscarPorId(Integer.parseInt(ejemplarId));

            Prestamo prestamo = new Prestamo(usuario, ejemplar);
            prestamoDAO.registrarPrestamo(prestamo);
            response.sendRedirect("prestamo");
        } catch (Exception e) {
            response.getWriter().write("Error al registrar el prestamo: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
