package com.brinquedostore.api.controller;

// import com.brinquedostore.api.model.Brinquedo;
// import com.brinquedostore.api.service.BrinquedoService;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;
//
// import java.util.Arrays;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
//
// @Controller
public class HomeController {
//
//    private final BrinquedoService brinquedoService;
//
//    public HomeController(BrinquedoService brinquedoService) {
//        this.brinquedoService = brinquedoService;
//    }
//
//    @GetMapping("/")
//    public String index(Model model) {
//        List<Brinquedo> brinquedos = brinquedoService.listarTodos();
//        model.addAttribute("brinquedos", brinquedos);
//        return "index";
//    }
//
//    @GetMapping("/catalogo")
//    public String catalogo(Model model) {
//        List<Brinquedo> todosBrinquedos = brinquedoService.listarTodos();
//        
//        Map<String, Brinquedo> categoriasMap = todosBrinquedos.stream()
//                .collect(Collectors.toMap(
//                        Brinquedo::getCategoria,
//                        brinquedo -> brinquedo,
//                        (existing, replacement) -> existing
//                ));
//
//        model.addAttribute("categorias", categoriasMap.values());
//        return "catalogo";
//    }
//    
//    @GetMapping("/catalogo/{categoria}")
//    public String catalogoCategoria(@PathVariable String categoria, Model model) {
//        List<Brinquedo> brinquedos = brinquedoService.buscarPorCategoria(categoria);
//        model.addAttribute("brinquedos", brinquedos);
//        model.addAttribute("nomeCategoria", categoria);
//        return "categoria";
//    }
//
//    @GetMapping("/detalhes/{id}")
//    public String detalhes(@PathVariable Long id, Model model) {
//        Brinquedo brinquedo = brinquedoService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Brinquedo inválido: " + id));
//        model.addAttribute("brinquedo", brinquedo);
//        return "detalhes";
//    }
//
//    // --- Rotas de Administração ---
//
//    @GetMapping("/administracao")
//    public String administracao(Model model) {
//        List<Brinquedo> brinquedos = brinquedoService.listarTodos();
//        model.addAttribute("brinquedos", brinquedos);
//        return "administracao";
//    }
//
//    @GetMapping("/administracao/novo")
//    public String novoBrinquedo(Model model) {
//        model.addAttribute("brinquedo", new Brinquedo());
//        return "formulario-brinquedo";
//    }
//
//    @PostMapping("/administracao/salvar")
//    public String salvarBrinquedo(@ModelAttribute Brinquedo brinquedo) {
//        brinquedoService.salvar(brinquedo);
//        return "redirect:/administracao";
//    }
//
//    @GetMapping("/administracao/editar/{id}")
//    public String editarBrinquedo(@PathVariable Long id, Model model) {
//        Brinquedo brinquedo = brinquedoService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Brinquedo inválido: " + id));
//        model.addAttribute("brinquedo", brinquedo);
//        return "formulario-brinquedo";
//    }
//
//    @GetMapping("/administracao/excluir/{id}")
//    public String excluirBrinquedo(@PathVariable Long id) {
//        brinquedoService.deletar(id);
//        return "redirect:/administracao";
//    }
//
//    // --- Rota Sobre a Equipe ---
//
//    @GetMapping("/sobre")
//    public String sobre(Model model) {
//        List<Map<String, String>> equipe = Arrays.asList(
//            Map.of("nome", "Integrante 1", "rgm", "12345678"),
//            Map.of("nome", "Integrante 2", "rgm", "87654321"),
//            Map.of("nome", "Integrante 3", "rgm", "11223344"),
//            Map.of("nome", "Integrante 4", "rgm", "44332211"),
//            Map.of("nome", "Integrante 5", "rgm", "55667788"),
//            Map.of("nome", "Integrante 6", "rgm", "99887766")
//        );
//        model.addAttribute("equipe", equipe);
//        return "sobre";
//    }
}
