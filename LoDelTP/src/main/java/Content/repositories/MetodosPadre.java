package Content.repositories;

import Content.models.Solicitud;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class MetodosPadre<T, ID> {

    @PersistenceContext
    EntityManager em;
    protected Class<T> entityClass;

    public MetodosPadre(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(query, entityClass).getResultList();
    }

}



