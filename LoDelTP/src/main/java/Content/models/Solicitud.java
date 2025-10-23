package Content.models;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_solicitud")
    @EqualsAndHashCode.Include
    private Long numeroSolicitud;

    @Column(name = "id_contenedor", nullable = false)
    private Long idContenedor;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "costo_estimado", precision = 19, scale = 2)
    private BigDecimal costoEstimado;

    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;

    @Column(name = "costo_final", precision = 19, scale = 2)
    private BigDecimal costoFinal;

    @Column(name = "tiempo_real")
    private Integer tiempoReal;

    @Column(name = "id_tarifa")
    private Long idTarifa;

    @Column(name = "estado_solicitud")
    private Long estadoSolicitud;
}
