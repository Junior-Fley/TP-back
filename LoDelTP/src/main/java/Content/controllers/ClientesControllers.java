package Content.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientesControllers {

    @GetMapping({"/holamundo", "/HelloWorld"})
    public String holamundo() {
        return "Hola mundo";
    }
}
