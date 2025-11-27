package Application;

import Model.*;
import services.NavigationService;
import services.ChatService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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


        boolean dbEnabled = Utils.Database.isEnabled();

        if (dbEnabled) {
            try (java.sql.Connection conn = Utils.Database.getConnection()) {

                boolean usersTableExists = false;
                try (java.sql.ResultSet rs = conn.getMetaData().getTables(null, null, "users", null)) {
                    usersTableExists = rs.next();
                }
                if (!usersTableExists) {
                    Utils.Database.runSqlScriptFromResource("/db/schema.sql");
                    System.out.println("Schema do banco criado.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            carregarDadosDoBanco();

        } else {
            System.out.println("Banco de dados não disponível. Usando dados em memória (Seed).");
            seedData();
        }

        navigationService.showLoginScene();
    }

    private void carregarDadosDoBanco() {
        users.clear();
        contratos.clear();

        try {

            List<dao.UserRecord> records = dao.UserDAO.findAllRecords();
            for (dao.UserRecord r : records) {
                if ("CLIENTE".equalsIgnoreCase(r.getRole())) {
                    Model.Cliente c = new Model.Cliente(r.getName(), r.getEmail(), r.getPasswordHash(), "");
                    c.setId(r.getId());
                    c.setFotoPerfilPath(r.getFotoPerfilPath());

                    if (r.getCpf() != null) c.setCpf(r.getCpf());
                    if (r.getEndereco() != null) c.setEndereco(r.getEndereco());
                    if (r.getTelefone() != null) c.setTelefone(r.getTelefone());
                    users.add(c);
                } else {
                    Model.Prestador p = new Model.Prestador(r.getName(), r.getEmail(), r.getPasswordHash(),
                            r.getCnpj(), r.getDescricaoServico(), r.getEndereco(), r.getTelefone(),
                            (r.getCategoria() == null ? "Outro" : r.getCategoria()), r.getFotoPerfilPath());
                    p.setId(r.getId());

                    users.add(p);
                }
            }
            System.out.println("Usuários carregados: " + users.size());


            for (User u : users) {
                if (u instanceof Prestador) {
                    Prestador p = (Prestador) u;
                    List<dao.AvaliacaoRecord> avRecs = dao.AvaliacaoDAO.findByPrestadorId(p.getId());
                    for (dao.AvaliacaoRecord ar : avRecs) {
                        User cliente = findUserById(ar.getClienteId());
                        if (cliente instanceof Cliente) {
                            Avaliacao av = new Avaliacao((Cliente) cliente, p, ar.getPontuacao(), ar.getComentario(), ar.getDataAvaliacao().toLocalDateTime());
                            p.addAvaliacao(av);
                        }
                    }
                }
            }
            System.out.println("Avaliações carregadas.");


            try {
                List<dao.ProjetoRecord> projRecs = dao.ProjetoDAO.findAll();
                for (dao.ProjetoRecord pr : projRecs) {
                    User cliente = findUserById(pr.getClienteId());
                    User prestador = pr.getPrestadorId() == null ? null : findUserById(pr.getPrestadorId());
                    if (!(cliente instanceof Cliente)) continue;

                    ProjetoConstrucao projeto = null;
                    if (prestador instanceof Prestador) {
                        projeto = new ProjetoConstrucao((Cliente) cliente, (Prestador) prestador, pr.getTitulo(), pr.getTipoServico(), pr.getDescricao(), pr.getDataInicioEstimada() == null ? null : pr.getDataInicioEstimada().toLocalDate(), pr.getDataFimEstimada() == null ? null : pr.getDataFimEstimada().toLocalDate());
                    } else {
                        projeto = new ProjetoConstrucao((Cliente) cliente, pr.getTitulo(), pr.getTipoServico(), pr.getDescricao(), pr.getDataInicioEstimada() == null ? null : pr.getDataInicioEstimada().toLocalDate(), pr.getDataFimEstimada() == null ? null : pr.getDataFimEstimada().toLocalDate());
                    }


                    try {
                        java.lang.reflect.Field idField = ProjetoConstrucao.class.getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(projeto, pr.getId());
                    } catch (Exception ignore) {}


                    try { projeto.setStatus(ProjetoConstrucao.StatusProjeto.valueOf(pr.getStatus())); } catch (Exception ignore) {}


                    try { if (pr.getCustoEstimadoMateriais() != null) projeto.setCustoEstimadoMateriais(pr.getCustoEstimadoMateriais().doubleValue()); } catch (Exception ignore) {}
                    try { if (pr.getCustoEstimadoMaoDeObra() != null) projeto.setCustoEstimadoMaoDeObra(pr.getCustoEstimadoMaoDeObra().doubleValue()); } catch (Exception ignore) {}
                    projeto.setObservacoesPrestador(pr.getObservacoesPrestador());
                    try { if (pr.getValorMaoDeObra() != null) projeto.setValorMaoDeObra(pr.getValorMaoDeObra().doubleValue()); } catch (Exception ignore) {}
                    try { if (pr.getCustoRealMateriais() != null) projeto.setCustoRealMateriais(pr.getCustoRealMateriais().doubleValue()); } catch (Exception ignore) {}
                    try { if (pr.getCustoRealMaoDeObra() != null) projeto.setCustoRealMaoDeObra(pr.getCustoRealMaoDeObra().doubleValue()); } catch (Exception ignore) {}


                    try {
                        List<dao.ItemOrcamentoRecord> items = dao.ItemOrcamentoDAO.findByProjetoId(pr.getId());
                        for (dao.ItemOrcamentoRecord ir : items) {
                            Model.ItemOrcamento it = new Model.ItemOrcamento(ir.getNome(), ir.getUnidade(), ir.getQuantidade() == null ? 0.0 : ir.getQuantidade().doubleValue(), ir.getValorUnitario() == null ? 0.0 : ir.getValorUnitario().doubleValue());

                            try { java.lang.reflect.Field idField = Model.ItemOrcamento.class.getDeclaredField("id"); idField.setAccessible(true); idField.set(it, ir.getId()); } catch (Exception ignore) {}
                            projeto.getItensOrcamento().add(it);
                        }
                    } catch (Exception ex) {
                        System.err.println("Erro ao carregar itens de orçamento: " + ex.getMessage());
                    }

                    projetosConstrucao.add(projeto);
                }
                System.out.println("Projetos carregados: " + projetosConstrucao.size());
            } catch (Exception ex) {
                System.err.println("Erro ao carregar projetos do DB: " + ex.getMessage());
                ex.printStackTrace();
            }

            for (User u : users) {
                if (u instanceof Prestador) {
                    List<dao.ContratoRecord> cRecs = dao.ContratoDAO.findByPrestadorId(u.getId());
                    for (dao.ContratoRecord cr : cRecs) {
                        User cliente = findUserById(cr.getClienteId());
                        User prestador = findUserById(cr.getPrestadorId());

                        if (cliente instanceof Cliente && prestador instanceof Prestador) {

                            if (contratos.stream().noneMatch(c -> c.getId().equals(cr.getId()))) {
                                Contrato.ContratoStatus status = Contrato.ContratoStatus.valueOf(cr.getStatus());

                                java.time.LocalDateTime data = java.time.LocalDateTime.now();

                                Contrato contratoModel = new Contrato((Cliente) cliente, (Prestador) prestador, cr.getDescricao(), data, status);
                                contratoModel.setProjetoId(cr.getProjetoId());
                                        try {

                                            java.math.BigDecimal vt = cr.getValorTotal();
                                            java.math.BigDecimal vr = cr.getValorRecebido();
                                            if (vt != null) contratoModel.setValorTotal(vt.doubleValue());
                                            if (vr != null) contratoModel.setValorRecebido(vr.doubleValue());
                                        } catch (Exception ex) {

                                        }

                                try {
                                    java.lang.reflect.Field idField = Contrato.class.getDeclaredField("id");
                                    idField.setAccessible(true);
                                    idField.set(contratoModel, cr.getId());
                                } catch (Exception e) { e.printStackTrace(); }

                                contratos.add(contratoModel);
                            }
                        }
                    }
                }
            }
            System.out.println("Contratos carregados: " + contratos.size());


            ChatService chatService = ChatService.getInstance();

            List<String> loadedConversaIds = new ArrayList<>();

            for (User u : users) {
                List<dao.ConversaRecord> convRecs = dao.ConversaDAO.findByUserId(u.getId());
                for (dao.ConversaRecord cr : convRecs) {
                    if (loadedConversaIds.contains(cr.getId())) continue;

                    User u1 = findUserById(cr.getUser1Id());
                    User u2 = findUserById(cr.getUser2Id());

                    if (u1 != null && u2 != null) {

                        chatService.carregarConversaDoBanco(cr.getId(), u1, u2, cr.getUltimaAtividade().toLocalDateTime());


                        List<dao.MensagemRecord> msgRecs = dao.MensagemDAO.findByConversaId(cr.getId());
                        Conversa conversaObj = chatService.encontrarConversa(u1, u2).orElse(null);

                        if (conversaObj != null) {
                            for (dao.MensagemRecord mr : msgRecs) {
                                User remetente = findUserById(mr.getRemetenteId());
                                User destinatario = findUserById(mr.getDestinatarioId());
                                if (remetente != null && destinatario != null) {
                                    Mensagem m = new Mensagem(remetente, destinatario, mr.getConteudo());
                                    m.setId(mr.getId());
                                    m.setTimestamp(mr.getDataEnvio().toLocalDateTime());
                                    m.setLida(mr.isLida());
                                    conversaObj.addMensagem(m);
                                }
                            }
                        }
                        loadedConversaIds.add(cr.getId());
                    }
                }
            }
            System.out.println("Conversas carregadas: " + loadedConversaIds.size());

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados do banco: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static User findUserById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }


    public static Stage getPrimaryStage() { return primaryStage; }
    public static List<User> getUsers() { return users; }
    public static List<Contrato> getContratos() { return contratos; }
    public static User getLoggedUser() { return loggedUser; }
    public static void setLoggedUser(User user) { loggedUser = user; }
    public static NavigationService getNavigationService() { return navigationService; }

    public static List<Prestador> getPrestadores() {
        return users.stream().filter(u -> u instanceof Prestador).map(u -> (Prestador) u).collect(Collectors.toList());
    }

    public static List<Prestador> getPrestadoresPorCategoria(String categoria) {
        List<Prestador> todos = getPrestadores();
        if (categoria == null || "Todas as Categorias".equals(categoria) || categoria.isEmpty()) return todos;
        return todos.stream().filter(p -> p.getCategoria().equalsIgnoreCase(categoria)).collect(Collectors.toList());
    }

    public static void addUser(User newUser) { users.add(newUser); }
    public static void addContrato(Contrato newContrato) { contratos.add(newContrato); }
    public static List<ProjetoConstrucao> getProjetosConstrucao() { return projetosConstrucao; }
    public static void addProjetoConstrucao(ProjetoConstrucao projeto) { projetosConstrucao.add(projeto); }
    public static List<ProjetoConstrucao> getProjetosConstrucaoByCliente(Cliente cliente) {
        return projetosConstrucao.stream().filter(p -> p.getCliente().equals(cliente)).collect(Collectors.toList());
    }
    public static List<ProjetoConstrucao> getProjetosConstrucaoByPrestador(Prestador prestador) {
        return projetosConstrucao.stream().filter(p -> p.getPrestador() != null && p.getPrestador().equals(prestador)).collect(Collectors.toList());
    }

    private void seedData() { /* ... (código original de seed) ... */ }

    public static void main(String[] args) { launch(); }
}