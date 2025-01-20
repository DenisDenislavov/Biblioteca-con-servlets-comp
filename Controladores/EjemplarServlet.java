package BibliotecaN.Controladores;

import BibliotecaN.Modelos.Ejemplar;
import BibliotecaN.Modelos.EjemplarDAO;
import BibliotecaN.Modelos.Libro;
import BibliotecaN.Modelos.LibroDAO;
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

@WebServlet("/ejemplar")
public class EjemplarServlet extends HttpServlet {

    private EjemplarDAO ejemplarDAO;
    private LibroDAO libroDAO;
    private EntityManager entityManager;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        entityManager = emf.createEntityManager();
        ejemplarDAO = new EjemplarDAO(entityManager);
        libroDAO = new LibroDAO(entityManager);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String id = request.getParameter("id");

        if ("edit".equals(action)) {
            Ejemplar ejemplar = ejemplarDAO.buscarPorId(Integer.parseInt(id));
            if (ejemplar != null) {
                request.setAttribute("ejemplar", ejemplar);
                request.setAttribute("ejemplar", ejemplarDAO.listarEjemplares());
                request.getRequestDispatcher("/editarEjemplar.jsp").forward(request, response);
            } else {
                response.getWriter().write("Ejemplar no encontrado.");
            }
        } else if ("delete".equals(action)) {
            ejemplarDAO.eliminarEjemplar(Integer.parseInt(id));
            response.sendRedirect("ejemplar");
        } else {
            List<Ejemplar> ejemplares = ejemplarDAO.listarEjemplares();
            request.setAttribute("ejemplares", ejemplares);
            request.getRequestDispatcher("/ejemplares.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String isbn = request.getParameter("isbn");
        String estadoStr = request.getParameter("estado");
        Ejemplar.EstadoEjemplar estado = Ejemplar.EstadoEjemplar.valueOf(estadoStr);

        try {
            Libro libro = libroDAO.buscarPorIsbn(isbn);
            if (libro != null) {
                Ejemplar ejemplar = new Ejemplar(libro, estado);
                ejemplarDAO.crearEjemplar(ejemplar);
                response.sendRedirect("ejemplar");
            } else {
                response.getWriter().write("Libro con ISBN no encontrado.");
            }
        } catch (Exception e) {
            response.getWriter().write("Error al procesar el ejemplar: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
