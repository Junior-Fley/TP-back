package Content.repositories;

import Content.models.Solicitud;
import jakarta.persistence.EntityManager;

public class SolicitudRepository extends MetodosPadre<Solicitud,Integer>{

    public SolicitudRepository (EntityManager em) {
        super(em, Solicitud.class);
    }
}
