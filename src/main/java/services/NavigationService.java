package services;

import Application.App;
import Controller.ChatController;
import Controller.PerfilPrestadorController;
import Controller.PerfilClienteController;
import Model.Prestador;
import Model.Cliente;
import Model.Contrato;
import Model.User;
import Model.ProjetoConstrucao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class NavigationService {

    private Stage primaryStage;
    private Stack<SceneInfo> history = new Stack<>();
    private java.util.Map<String, Object> sceneModelCache = new java.util.HashMap<>();
    private boolean isGoingBack = false;


    private static class SceneInfo {
        String fxmlPath;
        String title;

        SceneInfo(String fxmlPath, String title) {
            this.fxmlPath = fxmlPath;
            this.title = title;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SceneInfo sceneInfo = (SceneInfo) o;
            return fxmlPath.equals(sceneInfo.fxmlPath);
        }

        @Override
        public int hashCode() {
            return fxmlPath.hashCode();
        }
    }

    public NavigationService(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void navigateTo(String fxmlPath, String title) throws IOException {
        if (!isGoingBack) {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                String currentFxmlPath = primaryStage.getScene().getUserData().toString();
                if (history.isEmpty() || !history.peek().fxmlPath.equals(currentFxmlPath)) {
                    history.push(new SceneInfo(currentFxmlPath, primaryStage.getTitle()));
                }
            }
        }
        isGoingBack = false;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Application/" + fxmlPath));
        Parent root = loader.load();


        Object cachedModel = sceneModelCache.get(fxmlPath);
        Object controller = loader.getController();
        if (cachedModel != null && controller != null) {
            try {
                if (controller instanceof PerfilPrestadorController && cachedModel instanceof Model.Prestador) {
                    ((PerfilPrestadorController) controller).setPrestador((Model.Prestador) cachedModel);
                } else if (controller instanceof PerfilClienteController && cachedModel instanceof Model.Cliente) {
                    ((PerfilClienteController) controller).setCliente((Model.Cliente) cachedModel);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        Scene scene = new Scene(root);
        scene.setUserData(fxmlPath);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }


    public void goBack() throws IOException {
        if (!history.isEmpty()) {
            isGoingBack = true;
            SceneInfo previous = history.pop();
            navigateTo(previous.fxmlPath, previous.title);
        } else {

            if (App.getLoggedUser() != null) {
                if (App.getLoggedUser() instanceof Cliente) showPainelClienteScene();
                else if (App.getLoggedUser() instanceof Prestador) showPainelPrestadorScene();
                else showLoginScene();
            } else {
                showLoginScene();
            }
        }
    }



    public void showLoginScene() {
        try {
            history.clear();
            navigateTo("login-view.fxml", "Conecta - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegistroScene() {
        try {
            navigateTo("registro-view.fxml", "Conecta - Registro");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegistroPerfilClienteScene() {
        try {
            navigateTo("registro-perfil-cliente-view.fxml", "Conecta - Completar Perfil Cliente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegistroPerfilPrestadorScene() {
        try {
            navigateTo("registro-perfil-prestador-view.fxml", "Conecta - Completar Perfil Prestador");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPerfilPrestadorScene(Prestador prestador) {
        try {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                history.push(new SceneInfo(primaryStage.getScene().getUserData().toString(), primaryStage.getTitle()));
            }

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Application/perfil-prestador-view.fxml"));
            Parent root = loader.load();
            PerfilPrestadorController controller = loader.getController();
            controller.setPrestador(prestador);


            sceneModelCache.put("perfil-prestador-view.fxml", prestador);

            Scene scene = new Scene(root);
            scene.setUserData("perfil-prestador-view.fxml");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Perfil de " + prestador.getName());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPerfilClienteScene(Cliente cliente) {
        try {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                history.push(new SceneInfo(primaryStage.getScene().getUserData().toString(), primaryStage.getTitle()));
            }

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Application/perfil-cliente-view.fxml"));
            Parent root = loader.load();
            PerfilClienteController controller = loader.getController();
            controller.setCliente(cliente);


            sceneModelCache.put("perfil-cliente-view.fxml", cliente);

            Scene scene = new Scene(root);
            scene.setUserData("perfil-cliente-view.fxml");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Perfil de " + cliente.getName());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPainelClienteScene() {
        try {
            navigateTo("painel-clientes-view.fxml", "Painel do Cliente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPainelPrestadorScene() {
        try {
            navigateTo("painel-prestador-view.fxml", "Painel do Prestador");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showChatScene(User otherUser) {
        try {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                history.push(new SceneInfo(primaryStage.getScene().getUserData().toString(), primaryStage.getTitle()));
            }

            ChatService.getInstance().selecionarConversaOuCriar(App.getLoggedUser(), otherUser);

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Application/chat-view.fxml"));
            Parent root = loader.load();


            Scene scene = new Scene(root);
            scene.setUserData("chat-view.fxml");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Chat com " + otherUser.getName());
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showContratoDetalhes(Contrato contrato) {
        try {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                history.push(new SceneInfo(primaryStage.getScene().getUserData().toString(), primaryStage.getTitle()));
            }

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Application/contrato-detalhes-view.fxml"));
            Parent root = loader.load();
            Controller.ContratoDetalhesController controller = loader.getController();
            controller.setContrato(contrato);

            Scene scene = new Scene(root);
            scene.setUserData("contrato-detalhes-view.fxml");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Detalhes do Contrato");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMeusContratosScene(Cliente cliente) {
        try {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                history.push(new SceneInfo(primaryStage.getScene().getUserData().toString(), primaryStage.getTitle()));
            }

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Application/meus-contratos-view.fxml"));
            Parent root = loader.load();
            Controller.MeusContratosController controller = loader.getController();
            controller.setCliente(cliente);

            Scene scene = new Scene(root);
            scene.setUserData("meus-contratos-view.fxml");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Meus Contratos - " + (cliente != null ? cliente.getName() : ""));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMeusContratosDialog(Cliente cliente) {

        try {
            showMeusContratosScene(cliente);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGerenciamentoProjetosScene(String projetoId) {
        try {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                history.push(new SceneInfo(primaryStage.getScene().getUserData().toString(), primaryStage.getTitle()));
            }

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Application/gerenciamento-projetos-view.fxml"));
            Parent root = loader.load();
            Controller.GerenciamentoProjetosController controller = loader.getController();

            if (projetoId != null) {
                controller.selectProjectById(projetoId);
            }

            Scene scene = new Scene(root);
            scene.setUserData("gerenciamento-projetos-view.fxml");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gerenciamento de Projetos");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showOrcamentoScene(Model.ProjetoConstrucao projeto) {
        try {
            if (primaryStage.getScene() != null && primaryStage.getScene().getUserData() != null) {
                history.push(new SceneInfo(primaryStage.getScene().getUserData().toString(), primaryStage.getTitle()));
            }

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Application/orcamento-view.fxml"));
            Parent root = loader.load();
            Controller.OrcamentoController controller = loader.getController();
            controller.setProjeto(projeto);

            Scene scene = new Scene(root);
            scene.setUserData("orcamento-view.fxml");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Orçamento - " + (projeto != null ? projeto.getNomeProjeto() : ""));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}