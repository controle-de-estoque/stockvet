package ufrpe.stockvet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ufrpe.stockvet.models.Produto;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.service.ProdutoService;

import java.util.UUID;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

}