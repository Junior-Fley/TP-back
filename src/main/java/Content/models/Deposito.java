package Content.models;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.boot.context.properties.bind.Name;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DEPOSITOS")
public class Deposito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEPOSITO")
    private Integer idDeposito;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "DIRECCION", nullable = false, length = 200)
    private String direccion;

    //Ahora hacemos coordenadas con latitud y longitud (Corregir en MIRO)
    @Column(name = "LATITUD")
    private Double latitud;

    @Column(name = "LONGITUD")
    private Double longitud;
}
