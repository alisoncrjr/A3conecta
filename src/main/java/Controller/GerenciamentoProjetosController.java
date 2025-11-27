package Controller;

import Application.App;
import Model.Cliente;
import Model.ItemOrcamento;
import Model.Prestador;
import Model.ProjetoConstrucao;
import Model.User;
import services.NavigationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import Model.Contrato;
import Model.Contrato.ContratoStatus;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;

public class GerenciamentoProjetosController implements Initializable {

    @FXML private TabPane tabPaneProjetos;
    @FXML private Tab novoProjetoTab;
    @FXML private Tab meusProjetosTab;
    @FXML private Label labelTituloProjetos;

    @FXML private ListView<ProjetoConstrucao> listViewProjetos;
    private ObservableList<ProjetoConstrucao> projetosObservableList;
    private ProjetoConstrucao projetoSelecionado;

    @FXML private VBox viewCliente;
    @FXML private Label lblNomeProjetoCliente;
    @FXML private Label lblPrestadorCliente;
    @FXML private Label lblTipoServicoCliente;
    @FXML private Label lblPrazoCliente;
    @FXML private Label lblStatusCliente;
    @FXML private Label lblOrcamentoTotalCliente;
        @FXML private javafx.scene.control.ProgressBar progressBarCliente;
    @FXML private TableView<ItemOrcamento> tableViewItensCliente;
    @FXML private TextArea txtObservacoesCliente;
    @FXML private Button btnAprovarOrcamento;


    @FXML private VBox viewPrestador;
    @FXML private Label lblNomeProjetoPrestador;
    @FXML private Label lblClientePrestador;
    @FXML private Label lblTipoServicoPrestador;
    @FXML private Label lblPrazoPrestador;
    @FXML private ComboBox<ProjetoConstrucao.StatusProjeto> comboStatusPrestador;
    @FXML private TableView<ItemOrcamento> tableViewItensPrestador;
    @FXML private TextField txtNomeItem;
    @FXML private TextField txtQtdItem;
    @FXML private TextField txtValorUnitItem;
    @FXML private HBox hboxAddItens;
    @FXML private TextField txtValorMaoDeObra;
    @FXML private TextField txtValorContratoTotal;
    @FXML private TextField txtValorRecebido;
    @FXML private Label lblSaldoPrestador;
    @FXML private Label lblOrcamentoTotalPrestador;
        @FXML private javafx.scene.control.ProgressBar progressBarPrestador;
    @FXML private TextArea txtObservacoesPrestador;
    @FXML private Button btnSalvarOrcamento;

    @FXML private TableColumn<ItemOrcamento, String> colNomeItem;
    @FXML private TableColumn<ItemOrcamento, Double> colQuantidade;
    @FXML private TableColumn<ItemOrcamento, Double> colValorUnitario;
    @FXML private TableColumn<ItemOrcamento, Double> colValorTotal;

    @FXML private TextField newProjectNameField;
    @FXML private TextField newProjectTypeField;
    @FXML private TextArea newProjectDescriptionArea;
    @FXML private DatePicker newProjectStartDatePicker;
    @FXML private DatePicker newProjectEndDatePicker;
    @FXML private ListView<Prestador> prestadoresDisponiveisListView;
    @FXML private Label newProjectFeedbackLabel;

    private NavigationService navigationService;
    private User loggedUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navigationService = App.getNavigationService();
        loggedUser = App.getLoggedUser();

        if (loggedUser == null) {
            navigationService.showLoginScene();
            return;
        }

        setupProjetosListView();
        setupTableView(tableViewItensCliente);
        setupTableView(tableViewItensPrestador);

        carregarProjetos();
        configurarInterfacePorTipoUsuario();
    }

    private void setupProjetosListView() {
        projetosObservableList = FXCollections.observableArrayList();
        listViewProjetos.setItems(projetosObservableList);

        listViewProjetos.setCellFactory(lv -> new ListCell<ProjetoConstrucao>() {
            @Override
            protected void updateItem(ProjetoConstrucao item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNomeProjeto() + " (Status: " + item.getStatus().name().replace("_", " ") + ")");
            }
        });

        listViewProjetos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    projetoSelecionado = newSelection;
                    if (newSelection != null) {
                        exibirDetalhesProjeto(newSelection);
                    } else {
                        limparDetalhesProjeto();
                    }
                });
    }

    private void setupTableView(TableView<ItemOrcamento> tableView) {
        if (colNomeItem != null) {
            colNomeItem.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
            colValorUnitario.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
            colValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        } else {

            System.err.println("Colunas da TableView não injetadas. Verifique o FXML.");

            if (tableView.getColumns().size() >= 4) {
                TableColumn<ItemOrcamento, String> nomeCol = (TableColumn<ItemOrcamento, String>) tableView.getColumns().get(0);
                TableColumn<ItemOrcamento, Double> qtdCol = (TableColumn<ItemOrcamento, Double>) tableView.getColumns().get(1);
                TableColumn<ItemOrcamento, Double> valUniCol = (TableColumn<ItemOrcamento, Double>) tableView.getColumns().get(2);
                TableColumn<ItemOrcamento, Double> valTotCol = (TableColumn<ItemOrcamento, Double>) tableView.getColumns().get(3);

                nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
                qtdCol.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
                valUniCol.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
                valTotCol.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
            } else {

                TableColumn<ItemOrcamento, String> nomeCol = new TableColumn<>("Nome");
                TableColumn<ItemOrcamento, Double> qtdCol = new TableColumn<>("Qtd");
                TableColumn<ItemOrcamento, Double> valUniCol = new TableColumn<>("Valor Unit.");
                TableColumn<ItemOrcamento, Double> valTotCol = new TableColumn<>("Valor Total");

                nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
                qtdCol.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
                valUniCol.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
                valTotCol.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));

                tableView.getColumns().setAll(nomeCol, qtdCol, valUniCol, valTotCol);
            }
        }


        if (tableView == tableViewItensPrestador) {
            tableView.setEditable(true);
            if (colNomeItem != null) {
                colNomeItem.setCellFactory(TextFieldTableCell.forTableColumn());
                colQuantidade.setCellFactory(TextFieldTableCell.forTableColumn(new CommaFriendlyDoubleStringConverter()));
                colValorUnitario.setCellFactory(TextFieldTableCell.forTableColumn(new CommaFriendlyDoubleStringConverter()));

                colNomeItem.setOnEditCommit(event -> {
                    ItemOrcamento item = event.getRowValue();
                    item.setNome(event.getNewValue());
                    atualizarTotais();
                });
                colQuantidade.setOnEditCommit(event -> {
                    ItemOrcamento item = event.getRowValue();
                    item.setQuantidade(event.getNewValue());
                    atualizarTotais();
                });
                colValorUnitario.setOnEditCommit(event -> {
                    ItemOrcamento item = event.getRowValue();
                    item.setValorUnitario(event.getNewValue());
                    atualizarTotais();
                });
            }
        }
    }

    private static class CommaFriendlyDoubleStringConverter extends DoubleStringConverter {
        @Override
        public Double fromString(String value) {
            if (value == null) return null;
            String trimmed = value.trim();
            if (trimmed.isEmpty()) return null;
            trimmed = trimmed.replace(',', '.');
            try {
                return Double.valueOf(trimmed);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private void carregarProjetos() {
        if (loggedUser instanceof Cliente) {
            projetosObservableList.setAll(App.getProjetosConstrucaoByCliente((Cliente) loggedUser));
        } else if (loggedUser instanceof Prestador) {
            List<ProjetoConstrucao> atribuídos = App.getProjetosConstrucaoByPrestador((Prestador) loggedUser);
            List<ProjetoConstrucao> abertos = App.getProjetosConstrucao().stream()
                    .filter(p -> p.getPrestador() == null)
                    .filter(p -> p.getStatus() != ProjetoConstrucao.StatusProjeto.CONCLUIDO)
                    .collect(java.util.stream.Collectors.toList());
            java.util.ArrayList<ProjetoConstrucao> combined = new java.util.ArrayList<>();
            combined.addAll(atribuídos);
            for (ProjetoConstrucao p : abertos) if (combined.stream().noneMatch(x -> x.getId().equals(p.getId()))) combined.add(p);
            projetosObservableList.setAll(combined);
        }
        listViewProjetos.refresh();
    }

    public void selectProjectById(String projetoId) {
        carregarProjetos();
        if (projetoId == null || projetoId.isEmpty()) return;
        ProjetoConstrucao encontrado = App.getProjetosConstrucao().stream()
                .filter(p -> projetoId.equals(p.getId()))
                .findFirst()
                .orElse(null);
        if (encontrado != null) {
            listViewProjetos.getSelectionModel().select(encontrado);
            exibirDetalhesProjeto(encontrado);
            if (meusProjetosTab != null && tabPaneProjetos != null) tabPaneProjetos.getSelectionModel().select(meusProjetosTab);
        }
    }

    private void configurarInterfacePorTipoUsuario() {
        if (loggedUser instanceof Cliente) {
            labelTituloProjetos.setText("Meus Projetos (Cliente)");
            if (novoProjetoTab != null && !tabPaneProjetos.getTabs().contains(novoProjetoTab)) {
            }
            ObservableList<Prestador> prestadoresObservableList = FXCollections.observableArrayList(App.getPrestadores());
            prestadoresDisponiveisListView.setItems(prestadoresObservableList);
            prestadoresDisponiveisListView.setCellFactory(lv -> new ListCell<Prestador>() {
                @Override
                protected void updateItem(Prestador item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String categoria = item.getCategoria();
                        boolean hasCategoria = categoria != null && !categoria.trim().isEmpty() && !"Outro".equalsIgnoreCase(categoria);
                        String secondary = hasCategoria ? categoria : (item.getDescricaoServico() != null ? item.getDescricaoServico() : "");
                        setText(item.getName() + (secondary.isEmpty() ? "" : " (" + secondary + ")"));
                    }
                }
            });
            viewCliente.setVisible(true);
            viewCliente.setManaged(true);
            viewPrestador.setVisible(false);
            viewPrestador.setManaged(false);
        } else if (loggedUser instanceof Prestador) {
            labelTituloProjetos.setText("Meus Projetos (Prestador)");
            if (novoProjetoTab != null && tabPaneProjetos.getTabs().contains(novoProjetoTab)) {
                tabPaneProjetos.getTabs().remove(novoProjetoTab);
            }
            viewCliente.setVisible(false);
            viewCliente.setManaged(false);
            viewPrestador.setVisible(true);
            viewPrestador.setManaged(true);
            comboStatusPrestador.setItems(FXCollections.observableArrayList(ProjetoConstrucao.StatusProjeto.values()));
        }
    }

    private void exibirDetalhesProjeto(ProjetoConstrucao projeto) {
        if (projeto == null) {
            limparDetalhesProjeto();
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String prazo = String.format("Prazos: %s a %s",
                projeto.getDataInicio().format(formatter),
                projeto.getDataFim().format(formatter));

        if (loggedUser instanceof Cliente) {
            viewCliente.setVisible(true);
            viewPrestador.setVisible(false);

            lblNomeProjetoCliente.setText("Projeto: " + projeto.getTitulo());
            lblPrestadorCliente.setText("Prestador: " + (projeto.getPrestador() != null ? projeto.getPrestador().getName() : "Aguardando prestador..."));
            lblTipoServicoCliente.setText("Serviço: " + projeto.getTipoServico());
            lblPrazoCliente.setText(prazo);
            lblStatusCliente.setText("Status: " + projeto.getStatus().name().replace("_", " "));
            double materiais = projeto.getCustoTotalMateriais();
            double mao = projeto.getValorMaoDeObra();
            double total = projeto.getCustoTotalOrcamento();
            lblOrcamentoTotalCliente.setText(String.format("(R$ %.2f materiais + R$ %.2f mão de obra) Total: R$ %.2f", materiais, mao, total));

            if (progressBarCliente != null) {
                switch (projeto.getStatus()) {
                    case SOLICITADO:
                    case ORCAMENTO_PENDENTE:
                        progressBarCliente.setProgress(0.1);
                        break;
                    case AGUARDANDO_CLIENTE:
                    case APROVADO:
                        progressBarCliente.setProgress(0.4);
                        break;
                    case EM_ANDAMENTO:
                        progressBarCliente.setProgress(0.7);
                        break;
                    case CONCLUIDO:
                        progressBarCliente.setProgress(1.0);
                        break;
                    default:
                        progressBarCliente.setProgress(0.0);
                }
            }

            tableViewItensCliente.setItems(projeto.getItensOrcamento());
            txtObservacoesCliente.setText(projeto.getObservacoesPrestador());

            btnAprovarOrcamento.setVisible(projeto.getStatus() == ProjetoConstrucao.StatusProjeto.AGUARDANDO_CLIENTE);

        } else if (loggedUser instanceof Prestador) {
            viewCliente.setVisible(false);
            viewPrestador.setVisible(true);

            lblNomeProjetoPrestador.setText("Projeto: " + projeto.getTitulo());
            lblClientePrestador.setText("Cliente: " + projeto.getCliente().getName());
            lblTipoServicoPrestador.setText("Serviço: " + projeto.getTipoServico());
            lblPrazoPrestador.setText(prazo);

            comboStatusPrestador.setValue(projeto.getStatus());

            tableViewItensPrestador.setItems(projeto.getItensOrcamento());
            txtValorMaoDeObra.setText(String.format("%.2f", projeto.getValorMaoDeObra()));
            txtObservacoesPrestador.setText(projeto.getObservacoesPrestador());

            atualizarTotais();

            Contrato contratoVinculado = App.getContratos().stream()
                    .filter(c -> projetoSelecionado.getId().equals(c.getProjetoId()))
                    .findFirst().orElse(null);

            if (contratoVinculado != null) {
                double novoTotal = projetoSelecionado.getCustoTotalOrcamento();
                contratoVinculado.setValorTotal(novoTotal);
                if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                    try {
                        dao.ContratoDAO.updateValores(contratoVinculado.getId(), novoTotal, contratoVinculado.getValorRecebido());
                    } catch (Exception ex) {
                        System.err.println("Failed to update contrato valores after adding item: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void limparDetalhesProjeto() {
        viewCliente.setVisible(false);
        viewCliente.setManaged(false);
        viewPrestador.setVisible(false);
        viewPrestador.setManaged(false);
        projetoSelecionado = null;
    }

    @FXML
    private void handleCriarNovoProjeto() {
        if (!(loggedUser instanceof Cliente)) {
            newProjectFeedbackLabel.setText("Apenas clientes podem criar novos projetos.");
            return;
        }

        String nome = newProjectNameField.getText();
        String tipo = newProjectTypeField.getText();
        String descricao = newProjectDescriptionArea.getText();
        LocalDate dataInicio = newProjectStartDatePicker.getValue();
        LocalDate dataFim = newProjectEndDatePicker.getValue();
        Prestador prestadorSelecionado = prestadoresDisponiveisListView.getSelectionModel().getSelectedItem();

        if (nome.isEmpty() || tipo.isEmpty() || descricao.isEmpty() || dataInicio == null || dataFim == null) {
            newProjectFeedbackLabel.setText("Por favor, preencha todos os campos do projeto.");
            return;
        }
        if (dataInicio.isAfter(dataFim)) {
            newProjectFeedbackLabel.setText("A data de início não pode ser depois da data de fim.");
            return;
        }

        ProjetoConstrucao novoProjeto = new ProjetoConstrucao(
                (Cliente) loggedUser, nome, tipo, descricao, dataInicio, dataFim);

        if (prestadorSelecionado != null) {
            novoProjeto.setPrestador(prestadorSelecionado);
            novoProjeto.setStatus(ProjetoConstrucao.StatusProjeto.ORCAMENTO_PENDENTE);
            newProjectFeedbackLabel.setText("Solicitação de orçamento enviada para " + prestadorSelecionado.getName());
        } else {
            novoProjeto.setStatus(ProjetoConstrucao.StatusProjeto.SOLICITADO);
            newProjectFeedbackLabel.setText("Projeto criado e aberto para orçamentos.");
        }

        if (Utils.Database.isEnabled()) {
            try {
                dao.ProjetoRecord rec = new dao.ProjetoRecord(
                    novoProjeto.getId(),
                    ((Cliente) loggedUser).getId(),
                    prestadorSelecionado == null ? null : prestadorSelecionado.getId(),
                    novoProjeto.getTitulo(),
                    novoProjeto.getTipoServico(),
                    novoProjeto.getDescricao(),
                    dataInicio == null ? null : Date.valueOf(dataInicio),
                    dataFim == null ? null : Date.valueOf(dataFim),
                    novoProjeto.getStatus().name(),
                    BigDecimal.valueOf(novoProjeto.getCustoEstimadoMateriais()),
                    BigDecimal.valueOf(novoProjeto.getCustoEstimadoMaoDeObra()),
                    novoProjeto.getObservacoesPrestador(),
                    null,
                    null,
                    null
                );
                dao.ProjetoDAO.insert(rec);
            } catch (SQLException ex) {
                newProjectFeedbackLabel.setText("Erro ao salvar projeto no DB: " + ex.getMessage());
            }
        }

        App.addProjetoConstrucao(novoProjeto);
        carregarProjetos();
        tabPaneProjetos.getSelectionModel().select(meusProjetosTab);


        newProjectNameField.clear();
        newProjectTypeField.clear();
        newProjectDescriptionArea.clear();
        newProjectStartDatePicker.setValue(null);
        newProjectEndDatePicker.setValue(null);
        prestadoresDisponiveisListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAdicionarItem() {
        if (projetoSelecionado == null || !(loggedUser instanceof Prestador)) return;

        try {
            String nome = txtNomeItem.getText();
            double qtd = parseDoubleLenient(txtQtdItem.getText());
            double valorUnit = parseDoubleLenient(txtValorUnitItem.getText());

            if (nome.isEmpty() || qtd <= 0 || valorUnit < 0) {
                showAlert("Erro", "Dados do item inválidos.");
                return;
            }

            ItemOrcamento novoItem = new ItemOrcamento(nome, "un", qtd, valorUnit);
            projetoSelecionado.getItensOrcamento().add(novoItem);


            if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                try {
                    dao.ItemOrcamentoDAO.insert(new dao.ItemOrcamentoRecord(novoItem.getId(), projetoSelecionado.getId(), novoItem.getNome(), novoItem.getUnidade(), java.math.BigDecimal.valueOf(novoItem.getQuantidade()), java.math.BigDecimal.valueOf(novoItem.getValorUnitario()), java.math.BigDecimal.valueOf(novoItem.getValorTotal())));
                } catch (Exception ex) {
                    System.err.println("Failed to persist item orcamento: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }


            if (projetoSelecionado.getStatus() == ProjetoConstrucao.StatusProjeto.EM_ANDAMENTO) {
                double novoCustoReal = projetoSelecionado.getCustoRealMateriais() + novoItem.getValorTotal();
                projetoSelecionado.setCustoRealMateriais(novoCustoReal);


                if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                    try {
                        dao.ProjetoDAO.updateFinancials(projetoSelecionado.getId(), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoEstimadoMateriais()), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoEstimadoMaoDeObra()), projetoSelecionado.getObservacoesPrestador(), java.math.BigDecimal.valueOf(projetoSelecionado.getValorMaoDeObra()), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoRealMateriais()), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoRealMaoDeObra()));

                        services.ChatService.getInstance().notifyListeners();
                    } catch (Exception ex) {
                        System.err.println("Failed to update projeto real materials in DB: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }


                Contrato contratoVinculado = null;
                try {
                    contratoVinculado = App.getContratos().stream().filter(c -> projetoSelecionado.getId().equals(c.getProjetoId())).findFirst().orElse(null);
                } catch (Exception ignored) {}

                if (contratoVinculado != null) {
                    double novoTotalContrato = contratoVinculado.getValorTotal() + novoItem.getValorTotal();
                    contratoVinculado.setValorTotal(novoTotalContrato);
                    if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                        try {
                            dao.ContratoDAO.updateValores(contratoVinculado.getId(), contratoVinculado.getValorTotal(), contratoVinculado.getValorRecebido());
                            services.ChatService.getInstance().notifyListeners();
                        } catch (Exception ex) {
                            System.err.println("Failed to update contrato valores after adding material: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }

                atualizarTotais();
                listViewProjetos.refresh();
                showAlert("Material Adicionado", String.format("Material '%s' adicionado. Orçamento e saldo atualizados (R$ %.2f).", novoItem.getNome(), novoItem.getValorTotal()));
            }

            txtNomeItem.clear();
            txtQtdItem.clear();
            txtValorUnitItem.clear();

            atualizarTotais();

        } catch (NumberFormatException e) {
            showAlert("Erro", "Quantidade e Valor Unitário devem ser números.");
        }
    }

    @FXML
    private void handleAtualizarOrcamento() {
        if (projetoSelecionado == null || !(loggedUser instanceof Prestador)) return;

        try {
            double maoDeObra = parseDoubleLenient(txtValorMaoDeObra.getText());
            projetoSelecionado.setValorMaoDeObra(maoDeObra);
            projetoSelecionado.setObservacoesPrestador(txtObservacoesPrestador.getText());

            ProjetoConstrucao.StatusProjeto statusSelecionado = comboStatusPrestador.getValue();
            if (statusSelecionado != null) {
                projetoSelecionado.setStatus(statusSelecionado);
                if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                    try {
                        dao.ProjetoDAO.updateStatus(projetoSelecionado.getId(), statusSelecionado.name());
                    } catch (Exception ex) {
                        System.err.println("Failed to update projeto status in DB: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }

            if (projetoSelecionado.getStatus() == ProjetoConstrucao.StatusProjeto.ORCAMENTO_PENDENTE) {
                projetoSelecionado.setStatus(ProjetoConstrucao.StatusProjeto.AGUARDANDO_CLIENTE);
                comboStatusPrestador.setValue(ProjetoConstrucao.StatusProjeto.AGUARDANDO_CLIENTE);
                    if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                        try {
                            dao.ProjetoDAO.updateStatus(projetoSelecionado.getId(), ProjetoConstrucao.StatusProjeto.AGUARDANDO_CLIENTE.name());
                        } catch (Exception ex) {
                            System.err.println("Failed to update projeto status in DB: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
            }

            atualizarTotais();
            showAlert("Sucesso", "Orçamento e status atualizados.");
            if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                try {
                    dao.ProjetoDAO.updateFinancials(projetoSelecionado.getId(), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoEstimadoMateriais()), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoEstimadoMaoDeObra()), projetoSelecionado.getObservacoesPrestador(), java.math.BigDecimal.valueOf(projetoSelecionado.getValorMaoDeObra()), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoRealMateriais()), java.math.BigDecimal.valueOf(projetoSelecionado.getCustoRealMaoDeObra()));
                        services.ChatService.getInstance().notifyListeners();
                } catch (Exception ex) {
                    System.err.println("Failed to update projeto financials in DB: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            Model.Contrato contratoVinculado = App.getContratos().stream()
                    .filter(c -> projetoSelecionado.getId().equals(c.getProjetoId()))
                    .findFirst().orElse(null);

            if (contratoVinculado != null) {
                double novoTotal = projetoSelecionado.getCustoTotalOrcamento();
                contratoVinculado.setValorTotal(novoTotal);
                if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                    try {
                        dao.ContratoDAO.updateValores(contratoVinculado.getId(), contratoVinculado.getValorTotal(), contratoVinculado.getValorRecebido());
                            services.ChatService.getInstance().notifyListeners();
                    } catch (Exception ex) {
                        System.err.println("Failed to update contrato valores after atualizar orcamento: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }

            atualizarTotais();
            listViewProjetos.refresh();

        } catch (NumberFormatException e) {
            showAlert("Erro", "Valor da Mão de Obra deve ser um número válido.");
        }
    }

    @FXML
    private void handleSalvarOrcamento() {
        handleAtualizarOrcamento();
    }

    @FXML
    private void handleAprovarOrcamento() {
        if (projetoSelecionado == null || !(loggedUser instanceof Cliente)) return;

        if (projetoSelecionado.getStatus() == ProjetoConstrucao.StatusProjeto.AGUARDANDO_CLIENTE) {
            projetoSelecionado.setStatus(ProjetoConstrucao.StatusProjeto.APROVADO);
            if (projetoSelecionado.getPrestador() != null) {
                double materiaisContrato = projetoSelecionado.getCustoTotalMateriais();
                double maoContrato = projetoSelecionado.getValorMaoDeObra();
                double totalContrato = projetoSelecionado.getCustoTotalOrcamento();
                String descricao = String.format("Projeto: %s - Orçamento: R$ %.2f", projetoSelecionado.getTitulo(), totalContrato);
                StringBuilder itensDesc = new StringBuilder();
                projetoSelecionado.getItensOrcamento().forEach(i -> itensDesc.append(i.getNome()).append(" (x").append(i.getQuantidade()).append("), "));
                if (itensDesc.length() > 0) descricao += " - Itens: " + itensDesc.substring(0, Math.max(0, itensDesc.length() - 2));
                String breakdown = String.format(" (R$ %.2f materiais + R$ %.2f mão de obra)", materiaisContrato, maoContrato);
                descricao = descricao + breakdown;

                Contrato novoContrato = new Contrato((Cliente) loggedUser, projetoSelecionado.getPrestador(), descricao, LocalDateTime.now(), Contrato.ContratoStatus.PENDENTE);
                novoContrato.setProjetoId(projetoSelecionado.getId());
                novoContrato.setValorTotal(projetoSelecionado.getCustoTotalOrcamento());
                novoContrato.setValorRecebido(0.0);
                App.addContrato(novoContrato);
                if (Utils.Database.isEnabled()) {
                    try {
                        dao.ContratoDAO.insert(novoContrato);
                        services.ChatService.getInstance().notifyListeners();
                    } catch (SQLException ex) {
                        showAlert("Erro ao salvar contrato", "Erro ao persistir contrato no banco: " + ex.getMessage());
                    }
                }
                projetoSelecionado.setStatus(ProjetoConstrucao.StatusProjeto.APROVADO);
                if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                    try {
                        dao.ProjetoDAO.updateStatus(projetoSelecionado.getId(), ProjetoConstrucao.StatusProjeto.APROVADO.name());
                    } catch (Exception ex) {
                        System.err.println("Failed to update projeto status in DB: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                lblStatusCliente.setText("Status: " + projetoSelecionado.getStatus().name());
                showAlert("Sucesso", "Orçamento aprovado e contrato criado. O prestador será notificado.");
                btnAprovarOrcamento.setVisible(false);
                listViewProjetos.refresh();
            } else {
                lblStatusCliente.setText("Status: " + projetoSelecionado.getStatus().name());
                showAlert("Sucesso", "Orçamento aprovado! Aguarde definição de prestador.");
                btnAprovarOrcamento.setVisible(false);
                listViewProjetos.refresh();
            }
        }
    }

    private void atualizarTotais() {
        if (projetoSelecionado == null) return;
        Contrato contratoVinculado = App.getContratos().stream()
            .filter(c -> projetoSelecionado.getId().equals(c.getProjetoId()))
            .findFirst().orElse(null);

        double valorContratado = contratoVinculado != null ? contratoVinculado.getValorTotal() : projetoSelecionado.getCustoTotalMateriais();
        double valorMao = projetoSelecionado.getValorMaoDeObra();
        double totalOrcamento = valorContratado + valorMao;

        if (lblOrcamentoTotalPrestador != null)
            lblOrcamentoTotalPrestador.setText(String.format("(R$ %.2f contratado + R$ %.2f mão de obra) Total: R$ %.2f", valorContratado, valorMao, totalOrcamento));

        if (progressBarPrestador != null) {
            switch (projetoSelecionado.getStatus()) {
                case SOLICITADO:
                case ORCAMENTO_PENDENTE:
                    progressBarPrestador.setProgress(0.1);
                    break;
                case AGUARDANDO_CLIENTE:
                case APROVADO:
                    progressBarPrestador.setProgress(0.4);
                    break;
                case EM_ANDAMENTO:
                    progressBarPrestador.setProgress(0.7);
                    break;
                case CONCLUIDO:
                    progressBarPrestador.setProgress(1.0);
                    break;
                default:
                    progressBarPrestador.setProgress(0.0);
            }
        }

        if(lblOrcamentoTotalCliente != null)
            lblOrcamentoTotalCliente.setText(String.format("(R$ %.2f contratado + R$ %.2f mão de obra) Total: R$ %.2f", valorContratado, valorMao, totalOrcamento));


        if (contratoVinculado != null) {
            if (lblOrcamentoTotalPrestador != null)
                lblOrcamentoTotalPrestador.setText(String.format("Total Orçamento: R$ %.2f", contratoVinculado.getValorTotal()));

            if (txtValorContratoTotal != null)
                txtValorContratoTotal.setText(String.format("%.2f", contratoVinculado.getValorTotal()));
            if (txtValorRecebido != null)
                txtValorRecebido.setText(String.format("%.2f", contratoVinculado.getValorRecebido()));
            if (lblSaldoPrestador != null)
                lblSaldoPrestador.setText(String.format("Saldo: R$ %.2f", contratoVinculado.getValorRestante()));
        } else {
            if (txtValorContratoTotal != null) txtValorContratoTotal.setText(String.format("%.2f", totalOrcamento));
            if (txtValorRecebido != null) txtValorRecebido.setText("0.00");
            if (lblSaldoPrestador != null) lblSaldoPrestador.setText(String.format("Saldo: R$ %.2f", totalOrcamento));
        }
    }

    @FXML
    private void handleAtualizarPagamento() {
        if (projetoSelecionado == null || !(loggedUser instanceof Prestador)) return;
        try {
            double valorTotal = parseDoubleLenient(txtValorContratoTotal.getText());
            double recebido = parseDoubleLenient(txtValorRecebido.getText());

            Contrato contratoVinculado = App.getContratos().stream()
                    .filter(c -> projetoSelecionado.getId().equals(c.getProjetoId()))
                    .findFirst().orElse(null);

            if (contratoVinculado == null) {
                showAlert("Atenção", "Nenhum contrato vinculado a este projeto. Aguarde o cliente aprovar/contratar para que o contrato seja criado.");
                return;
            }

            contratoVinculado.setValorTotal(valorTotal);
            contratoVinculado.setValorRecebido(recebido);

            if (java.lang.Boolean.parseBoolean(System.getenv().getOrDefault("USE_DB", "false")) && Utils.Database.isEnabled()) {
                try {
                    dao.ContratoDAO.updateValores(contratoVinculado.getId(), valorTotal, recebido);
                    services.ChatService.getInstance().notifyListeners();
                } catch (Exception ex) {
                    System.err.println("Failed to update contrato valores in DB: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            if (lblSaldoPrestador != null)
                lblSaldoPrestador.setText(String.format("Saldo: R$ %.2f", contratoVinculado.getValorRestante()));

            showAlert("Sucesso", "Dados de pagamento atualizados.");
        } catch (NumberFormatException e) {
            showAlert("Erro", "Valores inválidos para Total ou Recebido.");
        }
    }

    private double parseDoubleLenient(String text) {
        if (text == null) return 0.0;
        String s = text.trim();
        if (s.isEmpty()) return 0.0;
        s = s.replace(',', '.');
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @FXML
    private void handleVoltar() throws IOException {
        navigationService.goBack();
    }

    @FXML
    private void handleVoltarParaPainelPrestador() {
        try {
            navigationService.showPainelPrestadorScene();
        } catch (Exception e) {
            try { navigationService.goBack(); } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleVoltarDetalhes() {
        if (listViewProjetos != null) {
            listViewProjetos.getSelectionModel().clearSelection();
        }
        limparDetalhesProjeto();
    }

    @FXML
    private void handleAbrirOrcamento() {
        if (projetoSelecionado == null) {
            showAlert("Aviso", "Selecione um projeto primeiro.");
            return;
        }
        try {
            navigationService.showOrcamentoScene(projetoSelecionado);
        } catch (Exception e) {
            showAlert("Erro", "Não foi possível abrir a tela de orçamento: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}