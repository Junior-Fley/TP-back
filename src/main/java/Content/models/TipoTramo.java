package Content.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "TIPO_TRAMO")
public class TipoTramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO_TRAMO")
    private Integer idTipoTramo;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private NombreTramo nombre;

}
