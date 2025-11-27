package Controller; // O pacote está correto aqui

import Model.*;
import services.NavigationService;
import services.ChatService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class App extends Application {

    private static Stage primaryStage;
    private static List<User> users = new ArrayList<>();
    private static List<Contrato> contratos = new ArrayList<>();
    private static List<ProjetoConstrucao> projetosConstrucao = new ArrayList<>();
    private static User loggedUser;
    private static NavigationService navigationService;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        navigationService = new NavigationService(primaryStage);
        seedData();


        navigationService.showLoginScene();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static List<Contrato> getContratos() {
        return contratos;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(User user) {
        loggedUser = user;
    }

    public static NavigationService getNavigationService() {
        return navigationService;
    }

    public static List<Prestador> getPrestadores() {
        return users.stream()
                .filter(u -> u instanceof Prestador)
                .map(u -> (Prestador) u)
                .collect(Collectors.toList());
    }

    public static List<Prestador> getPrestadoresPorCategoria(String categoria) {
        List<Prestador> todosPrestadores = getPrestadores();
        if (categoria == null || "Todas as Categorias".equals(categoria) || categoria.isEmpty()) {
            return todosPrestadores;
        } else {
            return todosPrestadores.stream()
                    .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }
    }

    public static void addUser(User newUser) {
        users.add(newUser);
    }

    public static void addContrato(Contrato newContrato) {
        contratos.add(newContrato);
    }


    public static List<ProjetoConstrucao> getProjetosConstrucao() {
        return projetosConstrucao;
    }

    public static void addProjetoConstrucao(ProjetoConstrucao projeto) {
        projetosConstrucao.add(projeto);
    }


    public static List<ProjetoConstrucao> getProjetosConstrucaoByCliente(Cliente cliente) {
        return projetosConstrucao.stream()
                .filter(p -> p.getCliente().equals(cliente))
                .collect(Collectors.toList());
    }


    public static List<ProjetoConstrucao> getProjetosConstrucaoByPrestador(Prestador prestador) {
        return projetosConstrucao.stream()
                .filter(p -> p.getPrestador() != null && p.getPrestador().equals(prestador))
                .collect(Collectors.toList());
    }


    private void seedData() {

        Cliente cliente1 = new Cliente("João Silva", "joao@example.com", "Senha123!", "11122233344", "Rua A, 10", "(11) 98765-4321");
        cliente1.setFotoPerfilPath("joao.png");
        Cliente cliente2 = new Cliente("Maria Oliveira", "maria@example.com", "Senha123!", "55566677788", "Av. B, 20", "(21) 91234-5678");
        cliente2.setFotoPerfilPath("maria.png");
        Cliente cliente3 = new Cliente("Carlos Alberto", "carlos@example.com", "Senha123!", "99988877766", "Rua do Sol, 123", "(11) 9876-1234");
        cliente3.setFotoPerfilPath("carlos.png");


        Prestador prestador1 = new Prestador("Ana Souza", "ana@example.com", "Senha123!", "11222333000144", "Encanamento residencial e comercial", "Rua C, 30", "(31) 99887-6543", "Encanador", "ana.png");
        Prestador prestador2 = new Prestador("Pedro Costa", "pedro@example.com", "Senha123!", "55666777000188", "Instalações elétricas, reparos e projetos", "Av. D, 40", "(41) 97766-5544", "Eletricista", "pedro.png");
        Prestador prestador3 = new Prestador("Carla Lima", "carla@example.com", "Senha123!", "99888777000166", "Pintura de interiores e exteriores, grafiato", "Rua E, 50", "(51) 96655-4433", "Pintor", "carla.png");
        Prestador prestador4 = new Prestador("Lucas Martins", "lucas@example.com", "Senha123!", "12345678000190", "Desenvolvimento de Websites e Aplicativos", "Online", "(11) 91122-3344", "Programador", "lucas.png");
        Prestador prestador5 = new Prestador("Julia Campos", "julia@example.com", "Senha123!", "98765432000110", "Serviços de Jardinagem e paisagismo", "Bairro F, 60", "(11) 95566-7788", "Jardineiro", "julia.png");
        Prestador prestador6 = new Prestador("Fernanda Alves", "fer@example.com", "Senha123!", "22333444000155", "Limpeza residencial e comercial, pós-obra", "Rua G, 70", "(11) 92233-4455", "Diarista", "fernanda.png");


        users.add(cliente1);
        users.add(cliente2);
        users.add(cliente3);
        users.add(prestador1);
        users.add(prestador2);
        users.add(prestador3);
        users.add(prestador4);
        users.add(prestador5);
        users.add(prestador6);



        Avaliacao avaliacao1 = new Avaliacao(cliente1, prestador1, 5, "Ótimo serviço de encanamento! Resolveu o problema rapidamente.", LocalDateTime.now().minusDays(10));
        Avaliacao avaliacao2 = new Avaliacao(cliente2, prestador1, 4, "Encanador muito competente. Rápido e eficiente.", LocalDateTime.now().minusDays(5));
        prestador1.addAvaliacao(avaliacao1);
        prestador1.addAvaliacao(avaliacao2);

        Avaliacao avaliacao3 = new Avaliacao(cliente1, prestador2, 3, "Instalação ok, mas demorou um pouco mais que o previsto.", LocalDateTime.now().minusDays(7));
        prestador2.addAvaliacao(avaliacao3);

        Avaliacao avaliacao4 = new Avaliacao(cliente2, prestador3, 5, "Pintura impecável! Adorei o resultado final e a limpeza.", LocalDateTime.now().minusDays(2));
        prestador3.addAvaliacao(avaliacao4);

        Avaliacao avaliacao5 = new Avaliacao(cliente3, prestador4, 5, "Fez um site incrível para minha empresa. Super recomendo!", LocalDateTime.now().minusDays(1));
        prestador4.addAvaliacao(avaliacao5);

        Avaliacao avaliacao6 = new Avaliacao(cliente1, prestador5, 4, "Jardinagem bem feita, meu jardim está lindo.", LocalDateTime.now().minusDays(3));
        prestador5.addAvaliacao(avaliacao6);


        Contrato contrato1 = new Contrato(cliente1, prestador1, "Conserto de vazamento na cozinha", LocalDateTime.now().minusDays(15), Contrato.ContratoStatus.FINALIZADO);
        Contrato contrato2 = new Contrato(cliente2, prestador2, "Instalação de tomadas no quarto", LocalDateTime.now().minusDays(10), Contrato.ContratoStatus.EM_ANDAMENTO);
        Contrato contrato3 = new Contrato(cliente1, prestador3, "Pintura quarto infantil", LocalDateTime.now().minusDays(3), Contrato.ContratoStatus.PENDENTE);
        Contrato contrato4 = new Contrato(cliente2, prestador1, "Troca de torneira do banheiro", LocalDateTime.now().minusDays(20), Contrato.ContratoStatus.FINALIZADO);
        Contrato contrato5 = new Contrato(cliente3, prestador4, "Desenvolvimento de Landing Page", LocalDateTime.now().minusDays(2), Contrato.ContratoStatus.EM_ANDAMENTO);
        Contrato contrato6 = new Contrato(cliente1, prestador5, "Manutenção de jardim", LocalDateTime.now().minusDays(4), Contrato.ContratoStatus.PENDENTE);
        Contrato contrato7 = new Contrato(cliente2, prestador6, "Limpeza completa do apartamento", LocalDateTime.now().minusDays(1), Contrato.ContratoStatus.PENDENTE);


        contratos.add(contrato1);
        contratos.add(contrato2);
        contratos.add(contrato3);
        contratos.add(contrato4);
        contratos.add(contrato5);
        contratos.add(contrato6);
        contratos.add(contrato7);


        ChatService chatService = ChatService.getInstance();
        chatService.addMensagemInicial(cliente1, prestador1, "Olá Ana, você está disponível para um vazamento urgente?");
        chatService.addMensagemInicial(prestador1, cliente1, "Olá João! Posso ir agora, qual o endereço?");
        chatService.addMensagemInicial(cliente1, prestador1, "Rua A, 10. Te espero!");

        chatService.addMensagemInicial(cliente2, prestador2, "Pedro, quero instalar umas tomadas novas. Qual seu valor?");
        chatService.addMensagemInicial(prestador2, cliente2, "Olá Maria! Posso fazer um orçamento no local. Que tal amanhã?");

        chatService.addMensagemInicial(cliente3, prestador4, "Lucas, preciso de um site profissional para minha empresa.");
        chatService.addMensagemInicial(prestador4, cliente3, "Claro, Carlos! Podemos agendar uma reunião para discutir os detalhes?");
        chatService.addMensagemInicial(cliente3, prestador4, "Ótimo! Que dia você tem disponível?");


        ProjetoConstrucao projeto1 = new ProjetoConstrucao(
                cliente1, prestador1, "Reforma da Cozinha", "Encanador",
                "Troca de pisos, armários e bancadas",
                LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 30));
        projeto1.setStatus(ProjetoConstrucao.StatusProjeto.EM_ANDAMENTO);
        projeto1.setCustoEstimadoMateriais(2500.00);
        projeto1.setCustoEstimadoMaoDeObra(1500.00);
        projeto1.setObservacoesPrestador("Aguardando entrega do granito para bancada.");
        projeto1.adicionarMaterialUtilizado(new ItemOrcamento("Piso Porcelanato", "m2", 20, 50.00));
        projeto1.adicionarMaterialUtilizado(new ItemOrcamento("Argamassa", "kg", 25, 10.00));
        projeto1.setCustoRealMateriais(1000.00);
        projeto1.setCustoRealMaoDeObra(500.00);

        ProjetoConstrucao projeto2 = new ProjetoConstrucao(
                cliente2, prestador3, "Pintura Externa da Casa", "Pintor",
                "Pintura completa da fachada e muros",
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 20));
        projeto2.setStatus(ProjetoConstrucao.StatusProjeto.ORCAMENTO_PENDENTE);
        projeto2.setCustoEstimadoMateriais(1000.00);
        projeto2.setCustoEstimadoMaoDeObra(800.00);

        ProjetoConstrucao projeto3 = new ProjetoConstrucao(
                cliente3, prestador4, "Site E-commerce", "Programador",
                "Criação de plataforma de e-commerce com catálogo de produtos e carrinho.",
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 15));
        projeto3.setStatus(ProjetoConstrucao.StatusProjeto.ORCAMENTO_PENDENTE);
        projeto3.setCustoEstimadoMateriais(0.00);
        projeto3.setCustoEstimadoMaoDeObra(5000.00);
        projeto3.setObservacoesPrestador("Aguardando aprovação do layout inicial pelo cliente.");

        projetosConstrucao.add(projeto1);
        projetosConstrucao.add(projeto2);
        projetosConstrucao.add(projeto3);
    }

    public static void main(String[] args) {
        launch();
    }
}