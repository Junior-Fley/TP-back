package Content.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRAMOS")
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRAMO")
    private Integer idTramo;

    @Column(name = "LATITUD_ORIGEN", nullable = false)
    private Integer latitudOrigen;
    @Column(name = "LONGITUD_ORIGEN", nullable = false)
    private Integer longitudOrigen;
    @Column(name = "LATITUD_DESTINO", nullable = false)
    private Integer latitudDestino;
    @Column(name = "LONGITUD_DESTINO", nullable = false)
    private Integer longitudDestino;

    @Column(name = "FECHA_HORA_INICIO", nullable = false)
    private String fechaHoraInicio;

    @Column(name = "FECHA_HORA_FIN", nullable = true)
    private String fechaHoraFin;

    @OneToOne
    @JoinColumn(name = "ID_CAMION" )
    private Camion camion;

    @OneToOne
    @JoinColumn(name = "ID_ESTADO")
    private Estado estado;

    @OneToOne
    @JoinColumn(name = "ID_DEPOSITO_DESTINO", nullable = true)
    private Deposito depositoDestino;
}
