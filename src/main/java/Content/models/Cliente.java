package Content.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CLIENTES")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE")
    private Integer idCliente;

    @Column(name = "DNI", nullable = false, unique = true)
    private Integer dni;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "APELLIDO", nullable = false, length = 100)
    private String apellido;

    @Column(name = "DIRECCION", nullable = false, length = 200)
    private String direccion;

    @Column(name = "TELEFONO", length = 15)
    private String telefono;

    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;
}
