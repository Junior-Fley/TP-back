package Content.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CAMIONES")
public class Camion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CAMION")
    private Integer idCamion;

    @Column(name = "PATENTE", nullable = false, length = 20, unique = true)
    private String patente;

    @Column(name = "CAPACIDAD_KG", nullable = false)
    private Integer capacidadKg;

    @Column(name = "CAPACIDAD_VOL", nullable = false)
    private Double volumenM3;

    @Column(name = "DISPONIBILIDAD", nullable = false)
    private Boolean disponibilidad;

    @Column(name = ("CONSUMO_COMBUSTIBLE"), nullable = false)
    private Integer consumoCombustible;

    @OneToOne
    @JoinColumn(name = "ID_TRANSPORTISTA")
    private Transportista transportista;
}
