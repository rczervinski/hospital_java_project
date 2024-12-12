import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AplicativoSwing {
    private JFrame frame;
    private JTextArea resultArea;
    private JComboBox<String> optionsComboBox;
    private ArrayList<Medico> medicos;
    private ArrayList<Consulta> consultas;
    private ArrayList<Paciente> pacientes;
    private static final String MEDICOS_FILE = "ser/medicos.ser";
    private static final String PACIENTES_FILE = "ser/pacientes.ser";
    private static final String CONSULTAS_FILE = "ser/consultas.ser";

    public static void main(String[] args) {
        ArrayList<Consulta> consultas = new ArrayList<>();
        ArrayList<Medico> medicos = new ArrayList<>();
        ArrayList<Paciente> pacientes = new ArrayList<>();

        // Tenta carregar os dados dos arquivos serializados
        boolean dataLoadedFromSerializedFiles = false;
        try {
            medicos = Medico.carregarMedicos(MEDICOS_FILE);
            pacientes = Paciente.carregarPacientes(PACIENTES_FILE);
            consultas = Consulta.carregarConsultas(CONSULTAS_FILE);
            dataLoadedFromSerializedFiles = true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar dados serializados: " + e.getMessage());
        }

        // Se não conseguiu carregar dos arquivos serializados, carrega dos CSVs
        if (!dataLoadedFromSerializedFiles) {
            try {
                Leitor leitor = new Leitor("csv/medicos.csv", "csv/pacientes.csv", "csv/consultas.csv");
                leitor.ler(medicos, pacientes, consultas);

                Medico.salvarMedicos(medicos, MEDICOS_FILE);
                Paciente.salvarPacientes(pacientes, PACIENTES_FILE);
                Consulta.salvarConsultas(consultas, CONSULTAS_FILE);
            } catch (IOException e) {
                System.out.println("Erro ao carregar dados dos CSVs: " + e.getMessage());
            }
        }

        ArrayList<Medico> finalMedicos = medicos;
        ArrayList<Paciente> finalPacientes = pacientes;
        ArrayList<Consulta> finalConsultas = consultas;

        SwingUtilities.invokeLater(() -> new AplicativoSwing(finalMedicos, finalPacientes, finalConsultas));
    }

    public AplicativoSwing(ArrayList<Medico> medicos, ArrayList<Paciente> pacientes, ArrayList<Consulta> consultas) {
        this.medicos = medicos;
        this.pacientes = pacientes;
        this.consultas = consultas;

        frame = new JFrame("Aplicativo Médico");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        createInterface();

        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void createInterface() {
        // Criação dos componentes
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout());

        String[] options = {
                "Quais são todos os pacientes de um determinado médico",
                "Quais são todas as consultas agendadas para um determinado médico em determinado período",
                "Quais são todos os médicos que um determinado paciente já consultou ou tem consulta agendada?",
                "Quais são todas as consultas que um determinado paciente realizou com determinado médico?",
                "Quais são todas as consultas agendadas que um determinado paciente possui?",
                "Quais são os pacientes de um determinado médico que não o consulta há mais que um determinado tempo (em meses)?",
                "Adicionar médico",
                "Adicionar paciente",
                "Adicionar consulta"
        };

        optionsComboBox = new JComboBox<>(options);
        menuPanel.add(optionsComboBox);

        JButton button = new JButton("Selecionar");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOptionSelected(optionsComboBox.getSelectedIndex() + 1);
            }
        });
        menuPanel.add(button);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        frame.add(menuPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
    }

    private void handleOptionSelected(int option) {
        resultArea.setText(""); // Limpa a área de resultado
        switch (option) {
            case 1:
                Medico.pegarPacientesPorMedicoSwing(frame, resultArea, medicos);
                break;
            case 2:
                Medico.pegarConsultasPorPeriodoSwing(frame, resultArea, medicos);
                break;
            case 3:
                try {
                    Paciente.pegarMedicosPorPaciente(pacientes, frame, resultArea);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            break;
            case 4:
                try {
                    Paciente.pegarConsultasRealizadasPeloPacienteEspecifico(pacientes,medicos, frame, resultArea);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                try {
                    Paciente.pegarConsultasAgendadasParaPaciente(pacientes, frame, resultArea);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 6:
                Medico.pacientesQueNaoConsultamHaMaisDePeriodo(medicos, frame, resultArea);
                break;
            case 7:
                Medico.adicionarNovoMedico(medicos, frame, resultArea);
                break;
            case 8: 
                Paciente.adicionarNovoPaciente(pacientes, frame, resultArea);
                break;
            case 9:
                Consulta.adicionarNovaConsulta(consultas, medicos, pacientes, frame, resultArea);
                break;
            default:
                resultArea.setText("Opção inválida");
                break;
        }
        
    }
    
}
