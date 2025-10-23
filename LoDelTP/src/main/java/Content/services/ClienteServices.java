package Content.services;

import Content.models.Cliente;


import java.util.List;

public class ClienteServices {
    static List<Cliente> listarCLientes() {
        listarCLientes().forEach(System.out::println);
        return listarCLientes();
    }

}
