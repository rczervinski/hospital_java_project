
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Medico implements Serializable{
    private static final long serialVersionUID = 1L;
    private String nome;
    private int codigo;
    private ArrayList<Paciente> pacientes = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String MEDICOS_FILE = "ser/medicos.ser";

    public Medico(String nome, int codigo) {
        this.nome = nome;
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public ArrayList<Paciente> getPaciente() {
        return pacientes;
    }

    public void addPaciente(Paciente paciente) {
        pacientes.add(paciente);
    }

    public boolean verificaPaciente(Paciente paciente) {
        return this.pacientes.contains(paciente);
    }

    public static void salvarMedicos(ArrayList<Medico> medicos, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(medicos);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Medico> carregarMedicos(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (ArrayList<Medico>) ois.readObject();
        }
    }

    public static void adicionarNovoMedico(ArrayList<Medico> medicos, JFrame frame, JTextArea resultArea) {
        String codigoMedicoStr = JOptionPane.showInputDialog(frame, "Digite o código do novo médico:");
        if(codigoMedicoStr == null || codigoMedicoStr.trim().isEmpty()){
            resultArea.append("Código não fornecido.\n");
            return;
        }
    
        int codigoMedico;
        try {
            codigoMedico = Integer.parseInt(codigoMedicoStr);
        } catch (NumberFormatException e) {
            resultArea.append("Código inválido.\n");
            return;
        }
    
        String nomeMedico = JOptionPane.showInputDialog(frame, "Digite o nome do novo médico:");
        if(nomeMedico == null || nomeMedico.trim().isEmpty()){
            resultArea.append("Nome não fornecido.\n");
            return;
        }
    
        Medico novoMedico = new Medico(nomeMedico, codigoMedico);
        medicos.add(novoMedico);
        try {
            salvarMedicos(medicos, MEDICOS_FILE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        resultArea.append("Novo médico adicionado com sucesso: " + nomeMedico + " (Código: " + codigoMedico + ")\n");
}
    


    public static Medico getMedicoByCode(int codigo, ArrayList<Medico> medicos) {
        Medico medicoRetornar = new Medico("", 0);

        for (Medico doctor : medicos) {
            if (doctor.codigo == codigo) {
                medicoRetornar = doctor;
            }
        }

        return medicoRetornar;
    }

    public static void pegarTodosOsMedicos(ArrayList<Medico> medicos) {
        System.out.println("\nEscolha um medico:");
        for (Medico m : medicos) {
            System.out.println("Médico: " + m.getNome());
            System.out.println("Codigo: " + m.getCodigo());
            System.out.println("------------------------------");

        }

    }

    public static void pegarConsultasPorMedico(ArrayList<Medico> medicos,
            ArrayList<Consulta> consultas) throws FileNotFoundException {
        System.out.println("Digite o codigo do medico: ");
        int codigo = scanner.nextInt();

        ArrayList<Consulta> consultasPorMedico = new ArrayList<Consulta>();
        Medico medicoCodigo = getMedicoByCode(codigo, medicos);
        Saida saida = Saida.getImpressoraDeSaida("Pacientes por médico");

        try {
            for (Consulta consulta : consultas) {
                if (consulta.getMedico() == medicoCodigo && (consulta.getData().isBefore(LocalDate.now())
                        || (consulta.getData().isEqual(LocalDate.now())
                                && consulta.getHorario().isBefore(LocalTime.now())))) {
                    consultasPorMedico.add(consulta);
                }
            }

            for (Consulta c : consultasPorMedico) {
                saida.out.println("Paciente: " + c.getPaciente().getNome() + "\n" + "Data:" + c.getData() + "\n"
                        + "Horário: " + c.getHorario() + "\n" + "---------------------------------------");
            }
        } finally {
            if (saida.deveFechar) {
                saida.out.close();
            }
        }

    }

    static void pegarConsultasPorPeriodoSwing(JFrame frame, JTextArea resultArea, ArrayList<Medico> medicos) {
        try {
            String codigoMedicoStr = JOptionPane.showInputDialog(frame, "Digite o código do médico:");
            int codigoMedico = Integer.parseInt(codigoMedicoStr);

            String dataInicialStr = JOptionPane.showInputDialog(frame, "Digite a data inicial (dd/MM/yyyy):");
            LocalDate dataInicial = LocalDate.parse(dataInicialStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String dataFinalStr = JOptionPane.showInputDialog(frame, "Digite a data final (dd/MM/yyyy):");
            LocalDate dataFinal = LocalDate.parse(dataFinalStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            ArrayList<Consulta> consultasPorPeriodo = new ArrayList<>();

            for (Medico medico : medicos) {
                if (medico.getCodigo() == codigoMedico) {
                    for (Paciente paciente : medico.getPaciente()) {
                        for (Consulta consulta : paciente.getConsultas()) {
                            LocalDate dataConsulta = consulta.getData();
                            if (consulta.getMedico().getCodigo() == codigoMedico &&
                                    !dataConsulta.isBefore(dataInicial) &&
                                    !dataConsulta.isAfter(dataFinal)) {
                                consultasPorPeriodo.add(consulta);
                            }
                        }
                    }
                    break;
                }
            }

            resultArea.append("Consultas agendadas para o médico no período especificado:\n");
            for (Consulta c : consultasPorPeriodo) {
                resultArea.append("---------------------------------------\n");
                resultArea.append("Paciente: " + c.getPaciente().getNome() + "\n");
                resultArea.append("Data: " + c.getData() + "\n");
                resultArea.append("Horário: " + c.getHorario() + "\n");
                resultArea.append("---------------------------------------\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Erro ao processar dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void pegarPacientesPorMedicoSwing(JFrame frame, JTextArea resultArea, ArrayList<Medico> medicos) {
        String codigoMedicoStr = JOptionPane.showInputDialog(frame, "Digite o código do médico:");
        int codigoMedico = Integer.parseInt(codigoMedicoStr);
        ArrayList<Paciente> pacientesDoMedico = new ArrayList<>();

        for (Medico m : medicos) {
            if (m.getCodigo() == codigoMedico) {
                pacientesDoMedico = m.getPaciente();
                break;
            }
        }

        for (Paciente p : pacientesDoMedico) {
            resultArea.append("---------------------------------------\n");
            resultArea.append("Paciente: " + p.getNome() + "\n");
            resultArea.append("---------------------------------------\n");
        }
    }

    public static void pacientesQueNaoConsultamHaMaisDePeriodo(ArrayList<Medico> medicos, JFrame frame, JTextArea resultArea) {
        int codigoMedico;
        try {
            codigoMedico = Integer.parseInt(JOptionPane.showInputDialog(frame, "Digite o código do médico:"));
        } catch (NumberFormatException e) {
            resultArea.append("Código do médico inválido.\n");
            return;
        }

        Medico medico = Medico.getMedicoByCode(codigoMedico, medicos);

        if (medico == null) {
            resultArea.append("Médico não encontrado.\n");
            return;
        }

        int periodo;
        try {
            periodo = Integer.parseInt(JOptionPane.showInputDialog(frame, "Digite o período em meses:"));
        } catch (NumberFormatException e) {
            resultArea.append("Período inválido.\n");
            return;
        }

        LocalDate dataLimite = LocalDate.now().minusMonths(periodo);

        ArrayList<Paciente> pacientesSemConsultas = new ArrayList<>();
        for (Paciente paciente : medico.getPaciente()) {
            Consulta ultimaConsulta = paciente.getUltimaConsultaComMedico(medico);
            if (ultimaConsulta == null || ultimaConsulta.getData().isBefore(dataLimite)) {
                pacientesSemConsultas.add(paciente);
            }
        }

        if (pacientesSemConsultas.isEmpty()) {
            resultArea.append("Todos os pacientes do médico " + medico.getNome() + " consultaram nos últimos " + periodo + " meses.\n");
        } else {
            resultArea.append("Pacientes do médico " + medico.getNome() + " que não consultaram nos últimos " + periodo + " meses:\n");
            for (Paciente paciente : pacientesSemConsultas) {
                resultArea.append("- " + paciente.getNome() + "\n");
            }
        }
    }

    public static void pegarPacientesPorMedico(ArrayList<Medico> medicos) throws FileNotFoundException {
        System.out.print("Digiite o codigo do medico >>");
        int codigoMedico = scanner.nextInt();
        ArrayList<Paciente> pacientesDoMedico = new ArrayList<>();
        Saida saida = Saida.getImpressoraDeSaida("Pacientes do medico");
        for (Medico m : medicos) {
            ArrayList<Paciente> pacientesMedico = m.getPaciente();
            if (m.getCodigo() == codigoMedico) {
                for (Paciente p : pacientesMedico) {
                    pacientesDoMedico.add(p);
                }

            }
        }

        for (Paciente p : pacientesDoMedico) {
            saida.out.println("---------------------------------------");
            saida.out.println("Paciente: " + p.getNome());
            saida.out.println("---------------------------------------");
        }

        if (saida.deveFechar) {
            saida.out.close();
        }

    }

    public static void pegarConsultasPorPeriodo(ArrayList<Medico> medicos)
            throws FileNotFoundException {
        ArrayList<Consulta> consultasPorPeriodo = new ArrayList<Consulta>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.print("Digite o codigo do medico >> ");
        int codigo = scanner.nextInt();

        System.out.print("Digite a data inicial (dd/MM/yyyy): ");

        String dataInicialString = scanner.next();

        System.out.print("Digite a data final(dd/MM/yyyy): ");
        String dataFinalString = scanner.next();

        LocalDate dataFinal = LocalDate.parse(dataFinalString, formatter);
        LocalDate dataInicial = LocalDate.parse(dataInicialString, formatter);
        Saida saida = Saida.getImpressoraDeSaida("Consulta por periodo");

        for (Medico medico : medicos) {
            if (medico.getCodigo() == codigo) {
                for (Paciente paciente : medico.getPaciente()) {
                    for (Consulta consulta : paciente.getConsultas()) {
                        LocalDate dataConsulta = consulta.getData();
                        if (consulta.getMedico().getCodigo() == codigo &&
                                !dataConsulta.isBefore(dataInicial) &&
                                !dataConsulta.isAfter(dataFinal)) {
                            consultasPorPeriodo.add(consulta);
                        }
                    }
                }
                break;
            }
        }

        Collections.sort(consultasPorPeriodo, new Comparator<Consulta>() {
            public int compare(Consulta consulta1, Consulta consulta2) {
                int comparacaoData = consulta1.getData().compareTo(consulta2.getData());
                if (comparacaoData != 0) {
                    return comparacaoData;
                } else {
                    return consulta1.getHorario().compareTo(consulta2.getHorario());
                }
            }
        });

        for (Consulta c : consultasPorPeriodo) {
            saida.out.println("---------------------------------------");
            saida.out.println("Paciente: " + c.getPaciente().getNome());
            saida.out.println("Data: " + c.getData());
            saida.out.println("Horário: " + c.getHorario());
            saida.out.println("---------------------------------------");
        }

        if (saida.deveFechar) {
            saida.out.close();
        }

    }

    public static List<Consulta> pegarConsultasDoMedico(int codigo, List<Consulta> consultas) {
        List<Consulta> consultasDoMedico = new ArrayList<>();

        for (Consulta consulta : consultas) {
            if (consulta.getMedico().getCodigo() == codigo) {
                consultasDoMedico.add(consulta);
            }
        }

        return consultasDoMedico;
    }

    public static void pacientesQueNaoConsultaram(ArrayList<Medico> medicos) throws FileNotFoundException {
        System.out.println("Digite o código do médico >>");
        int codigoMed = scanner.nextInt();
        System.out.println("Digite a quantidade de meses >>");
        int meses = scanner.nextInt();
        ArrayList<Consulta> pacientesNaoConsultaram = new ArrayList<Consulta>();
        Saida saida = Saida.getImpressoraDeSaida("Pacientes que não consultam");
        LocalDate dataAgora = LocalDate.now();

        LocalDate dataLimite = dataAgora.minusMonths(meses);

        for (Medico m : medicos) {
            if (m.codigo == codigoMed) {
                for (Paciente p : m.pacientes) {
                    for (Consulta c : p.getConsultas()) {
                        LocalDate dataConsulta = c.getData();

                        if (dataConsulta.isBefore(dataLimite)) {
                            pacientesNaoConsultaram.add(c);
                        }
                    }
                }
            }
        }

        for (Consulta c : pacientesNaoConsultaram) {
            saida.out.println("Paciente: " + c.getPaciente().getNome());
            saida.out.println("Data: " + c.getData());
            saida.out.println("---------------------------------------");
        }

        if (saida.deveFechar) {
            saida.out.close();
        }

    }

}
