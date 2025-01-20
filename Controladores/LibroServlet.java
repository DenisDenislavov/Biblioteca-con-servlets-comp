package BibliotecaN.Controladores;

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

@WebServlet("/libro")
public class LibroServlet extends HttpServlet {

    private LibroDAO libroDAO;
    private EntityManager entityManager;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        entityManager = emf.createEntityManager();
        libroDAO = new LibroDAO(entityManager);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String isbn = request.getParameter("isbn");

        if ("edit".equals(action)) {
            Libro libro = libroDAO.buscarPorIsbn(isbn);

            if (libro != null) {
                request.setAttribute("libro", libro);
                request.getRequestDispatcher("/editar.jsp").forward(request, response);
            } else {
                response.getWriter().write("No se encontró el libro con ISBN: " + isbn);
            }
        } else if ("delete".equals(action)) {
            if (isbn != null) {
                libroDAO.eliminarLibro(isbn);
            }
            response.sendRedirect("libro");
        } else {
            List<Libro> libros = libroDAO.listarLibros();
            request.setAttribute("libros", libros);
            request.getRequestDispatcher("/libros.jsp").forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");

        try {
            Libro libro = libroDAO.buscarPorIsbn(isbn);

            if (libro != null) {
                libro.setTitulo(titulo);
                libro.setAutor(autor);
                libroDAO.actualizarLibro(libro);
                response.sendRedirect("libro");
            } else {
                libro = new Libro(isbn, titulo, autor);
                libroDAO.crearLibro(libro);
                response.sendRedirect("libro");
            }
        } catch (Exception e) {
            response.getWriter().write("Error al procesar el libro: " + e.getMessage());
        }
    }


    @Override
    public void destroy() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}

