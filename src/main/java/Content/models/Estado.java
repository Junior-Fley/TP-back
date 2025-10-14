package Content.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ESTADOS")
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private Integer idEstado;

    @Column(name = "NOMBRE", nullable = false, length = 50, unique = true)
    private NombreEstado nombre;
}
