package BibliotecaN.Modelos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EjemplarDAO {
    private final EntityManager entityManager;

    public EjemplarDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void crearEjemplar(Ejemplar ejemplar) {
        entityManager.getTransaction().begin();
        entityManager.persist(ejemplar);
        entityManager.getTransaction().commit();
    }

    public Ejemplar buscarPorId(int id) {
        return entityManager.find(Ejemplar.class, id);
    }

    public List<Ejemplar> listarEjemplares() {
        return entityManager.createQuery("SELECT e FROM Ejemplar e", Ejemplar.class).getResultList();
    }

    public List<Ejemplar> listarEjemplaresPorNombre() {
        return entityManager.createQuery("SELECT e FROM Ejemplar e JOIN Libro l WHERE e.libro = l.isbn", Ejemplar.class
        ).getResultList();
    }

    public List<Ejemplar> listarEjemplaresPorLibro(String isbn) {
        TypedQuery<Ejemplar> query = entityManager.createQuery(
                "SELECT e FROM Ejemplar e WHERE e.libro.isbn = :isbn", Ejemplar.class
        );
        query.setParameter("isbn", isbn);
        return query.getResultList();
    }

    public Ejemplar listarEjemplaresPorLibrov2(String isbn) {
        TypedQuery<Ejemplar> query = entityManager.createQuery(
                "SELECT e FROM Ejemplar e WHERE e.libro.isbn = :isbn", Ejemplar.class
        );
        query.setParameter("isbn", isbn);
        List<Ejemplar> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public long contarEjemplaresDisponibles(String isbn) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(e) FROM Ejemplar e JOIN e.libro l WHERE l.isbn = :isbn AND e.estado = :estado", Long.class
        );
        query.setParameter("isbn", isbn);
        query.setParameter("estado", Ejemplar.EstadoEjemplar.Disponible);
        return query.getSingleResult();
    }

    public void actualizarEjemplar(Ejemplar ejemplar) {
        entityManager.getTransaction().begin();
        entityManager.merge(ejemplar);
        entityManager.getTransaction().commit();
    }

    public void eliminarEjemplar(int id) {
        entityManager.getTransaction().begin();
        Ejemplar ejemplar = buscarPorId(id);
        if (ejemplar != null) {
            entityManager.remove(ejemplar);
        }
        entityManager.getTransaction().commit();
    }

}
