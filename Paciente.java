
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String cpf;
    private ArrayList<Consulta> consultas = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    private static final String PACIENTES_FILE = "ser/pacientes.ser";


    public Paciente(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;

    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void addConsulta(Consulta consulta) {
        consultas.add(consulta);
    }

    public ArrayList<Consulta> getConsultas() {
        return consultas;
    }

    public static void salvarPacientes(ArrayList<Paciente> pacientes, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(pacientes);
        }
    }

    public static ArrayList<Paciente> carregarPacientes(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (ArrayList<Paciente>) ois.readObject();
        }
    }

    public static Paciente getPacienteByCpf(String cpf, ArrayList<Paciente> pacientes) {
        Paciente patientToReturn = new Paciente("", "");

        for (Paciente paciente : pacientes) {
            if (paciente.cpf.equals(cpf)) {
                patientToReturn = paciente;
            }
        }

        return patientToReturn;
    }

    public static void adicionarNovoPaciente(ArrayList<Paciente> Pacientes, JFrame frame, JTextArea resultArea) {
        String cpfPaciente = JOptionPane.showInputDialog(frame, "Digite o CPF do novo paciente:");
        if(cpfPaciente == null || cpfPaciente.trim().isEmpty()){
            resultArea.append("CPF não fornecido.\n");
            return;
        }
    
        String nomePaciente = JOptionPane.showInputDialog(frame, "Digite o nome do novo paciente:");
        if(nomePaciente == null || nomePaciente.trim().isEmpty()){
            resultArea.append("Nome não fornecido.\n");
            return;
        }
    
        Paciente novoPaciente = new Paciente(nomePaciente, cpfPaciente);
        Pacientes.add(novoPaciente);
        try {
            salvarPacientes(Pacientes, PACIENTES_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultArea.append("Novo médico adicionado com sucesso: " + nomePaciente + " (Código: " + cpfPaciente + ")\n");
    }


    public static void pegarMedicosPorPaciente(ArrayList<Paciente> pacientes, JFrame frame, JTextArea resultArea)
            throws FileNotFoundException {
        String cpfPaciente = JOptionPane.showInputDialog(frame, "Digite o CPF do paciente:");
        if (cpfPaciente == null || cpfPaciente.trim().isEmpty()) {
            resultArea.append("CPF não fornecido.\n");
            return;
        }

        Paciente paciente = Paciente.getPacienteByCpf(cpfPaciente, pacientes);

        if (paciente == null) {
            resultArea.append("Paciente não encontrado.\n");
            return;
        }

        ArrayList<Medico> medicosConsultados = new ArrayList<>();

        for (Consulta consulta : paciente.getConsultas()) {
            Medico medico = consulta.getMedico();
            if (!medicosConsultados.contains(medico)) {
                medicosConsultados.add(medico);
            }
        }

        if (medicosConsultados.isEmpty()) {
            resultArea.append("O paciente " + paciente.getNome() + " não consultou nenhum médico.\n");
        } else {
            resultArea.append("Médicos consultados pelo paciente " + paciente.getNome() + ":\n");
            for (Medico medico : medicosConsultados) {
                resultArea.append("Nome: " + medico.getNome() + " | Código: " + medico.getCodigo() + "\n");
            }
        }
    }

    public Consulta getUltimaConsultaComMedico(Medico medico) {
        Consulta ultimaConsulta = null;
        for (Consulta consulta : consultas) {
            if (consulta.getMedico().equals(medico)) {
                if (ultimaConsulta == null || consulta.getData().isAfter(ultimaConsulta.getData())) {
                    ultimaConsulta = consulta;
                }
            }
        }
        return ultimaConsulta;
    }

    public static void pegarConsultasAgendadasParaPaciente(ArrayList<Paciente> pacientes, JFrame frame, JTextArea resultArea)
            throws FileNotFoundException {
        String cpfPaciente = JOptionPane.showInputDialog(frame, "Digite o CPF do paciente:");
        if (cpfPaciente == null || cpfPaciente.trim().isEmpty()) {
            resultArea.append("CPF não fornecido.\n");
            return;
        }

        Paciente paciente = Paciente.getPacienteByCpf(cpfPaciente, pacientes);

        if (paciente == null) {
            resultArea.append("Paciente não encontrado.\n");
            return;
        }

        ArrayList<Consulta> consultasAgendadas = new ArrayList<>();

        for (Consulta consulta : paciente.getConsultas()) {
            LocalDate dataConsulta = consulta.getData();
            LocalTime horarioConsulta = consulta.getHorario();
            if (dataConsulta.isAfter(LocalDate.now()) || (dataConsulta.isEqual(LocalDate.now()) && horarioConsulta.isAfter(LocalTime.now()))) {
                consultasAgendadas.add(consulta);
            }
        }

        if (consultasAgendadas.isEmpty()) {
            resultArea.append("O paciente " + paciente.getNome() + " não possui consultas agendadas.\n");
        } else {
            resultArea.append("Consultas agendadas para o paciente " + paciente.getNome() + ":\n");
            for (Consulta consulta : consultasAgendadas) {
                resultArea.append("Data: " + consulta.getData() + " | Horário: " + consulta.getHorario() + " | Médico: " + consulta.getMedico().getNome() + "\n");
            }
        }
    }

    public static void pegarConsultasRealizadasPeloPacienteEspecifico(ArrayList<Paciente> pacientes, ArrayList<Medico> medicos, JFrame frame, JTextArea resultArea)
            throws FileNotFoundException {
        String cpfPaciente = JOptionPane.showInputDialog(frame, "Digite o CPF do paciente:");
        if (cpfPaciente == null || cpfPaciente.trim().isEmpty()) {
            resultArea.append("CPF não fornecido.\n");
            return;
        }

        Paciente paciente = Paciente.getPacienteByCpf(cpfPaciente, pacientes);

        if (paciente == null) {
            resultArea.append("Paciente não encontrado.\n");
            return;
        }

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

        ArrayList<Consulta> consultasRealizadas = new ArrayList<>();

        for (Consulta consulta : paciente.getConsultas()) {
            if (consulta.getMedico().getCodigo() == codigoMedico) {
                LocalDate dataConsulta = consulta.getData();
                LocalTime horarioConsulta = consulta.getHorario();
                if (dataConsulta.isBefore(LocalDate.now()) || (dataConsulta.isEqual(LocalDate.now()) && horarioConsulta.isBefore(LocalTime.now()))) {
                    consultasRealizadas.add(consulta);
                }
            }
        }

        if (consultasRealizadas.isEmpty()) {
            resultArea.append("O paciente " + paciente.getNome() + " não realizou consultas com o médico de código " + codigoMedico + ".\n");
        } else {
            resultArea.append("Consultas realizadas pelo paciente " + paciente.getNome() + " com o médico " + medico.getNome() + " (Código " + medico.getCodigo() + "):\n");
            for (Consulta consulta : consultasRealizadas) {
                resultArea.append("Data: " + consulta.getData() + " | Horário: " + consulta.getHorario() + "\n");
            }
        }
    }

    
    public static String getNomeByCPF(String cpf, List<Paciente> pacientes) {
        for (Paciente paciente : pacientes) {
            if (paciente.getCpf() == cpf) {
                return paciente.getNome();
            }
        }
        return null;
    }

    public static void pegarFuturasConsultas(ArrayList<Paciente> pacientes)
            throws FileNotFoundException {
        System.out.print("Digite o cpf do Paciente: ");

        String cpf = scanner.next();
        Saida saida = Saida.getImpressoraDeSaida("Consultas Futuras");

        ArrayList<Consulta> consultasFuturas = new ArrayList<Consulta>();
        ArrayList<Consulta> consultasPaciente = new ArrayList<>();

        for (Paciente paciente : pacientes) {
            if (paciente.getCpf().equals(cpf)) {
                consultasPaciente.addAll(paciente.getConsultas());
                for (Consulta c : consultasPaciente) {
                    if (c.getData().isAfter(LocalDate.now())
                            || (c.getData().isEqual(LocalDate.now())
                                    && c.getHorario().isAfter(LocalTime.now()))) {
                        consultasFuturas.add(c);
                    }
                }
            }
        }
        for (Consulta c : consultasFuturas) {
            saida.out.println("Medico: " + c.getMedico().getNome());
            saida.out.println("Data: " + c.getData());
            saida.out.println("Horário: " + c.getHorario());
            saida.out.println("---------------------------------------");
        }

        if (saida.deveFechar) {
            saida.out.close();
        }

    }

    public static void pegarMedicosPorPaciente(ArrayList<Paciente> pacientes)
            throws FileNotFoundException {

        System.out.println("Digite o cpf do paciente: ");
        String cpfPaciente = scanner.next();
        Paciente paciente = Paciente.getPacienteByCpf(cpfPaciente, pacientes);
        Saida saida = Saida.getImpressoraDeSaida("Medicos por paciente");

        System.out.println("\nMédicos de " + paciente.getNome() + " :");
        ArrayList<Medico> medicosPorPaciente = new ArrayList<Medico>();

        for (Consulta c : paciente.consultas) {
            if (paciente == c.getPaciente()) {
                if (!medicosPorPaciente.contains(c.getMedico())) {
                    medicosPorPaciente.add(c.getMedico());
                }
            }
        }

        for (Medico m : medicosPorPaciente) {
            saida.out.println("Nome: " + m.getNome() + " | Código " + m.getCodigo());
        }

        if (saida.deveFechar) {
            saida.out.close();
        }
    }

    public static void pegarMedicosEspecificiosDePacientes(ArrayList<Paciente> pacientes,
            String cpf)
            throws FileNotFoundException {

        Paciente paciente = Paciente.getPacienteByCpf(cpf, pacientes);

        System.out.println("\nMédicos de " + paciente.getNome() + " :");

        ArrayList<Integer> medicosImpressos = new ArrayList<>();

        for (Paciente p : pacientes) {
            if (p.getCpf().equals(cpf)) {
                for (Consulta consulta : p.consultas) {
                    int codigoMedico = consulta.getMedico().getCodigo();
                    if (!medicosImpressos.contains(codigoMedico) && consulta.getData().isBefore(LocalDate.now())
                            && consulta.getHorario().isBefore(LocalTime.now())) {
                        System.out.println("Nome: " + consulta.getMedico().getNome() + " | Código: " + codigoMedico);
                        medicosImpressos.add(codigoMedico);
                    }
                }
            }
        }

    }

    public static void consultasEspecificasPacienteComMedico(ArrayList<Paciente> pacientes, ArrayList<Medico> medicos)
            throws FileNotFoundException {

        System.out.print("Digite o cpf do paciente >>");
        String cpf = scanner.next();
        Paciente paciente = Paciente.getPacienteByCpf(cpf, pacientes);
        pegarMedicosEspecificiosDePacientes(pacientes, cpf);

        System.out.print("Digite o código do médico >>");
        int codMedico = scanner.nextInt();
        Medico medico = Medico.getMedicoByCode(codMedico, medicos);
        LocalDate dataAtual = LocalDate.now();

        Saida saida = Saida.getImpressoraDeSaida("Paciente por medico");
        ArrayList<Consulta> consultasRealizadas = new ArrayList<Consulta>();

        for (Paciente p : pacientes) {
            for (Consulta c : p.consultas) {
                if (p.cpf.equals(cpf)) {
                    if (c.getData().isBefore(dataAtual) && c.getMedico().equals(medico)) {
                        consultasRealizadas.add(c);
                    }
                }
            }
        }

        if (!consultasRealizadas.isEmpty()) {
            saida.out.println("\nO paciente " + paciente.getNome() + " realizou as seguintes consultas com o médico "
                    + medico.getNome() + " :");
            for (Consulta c : consultasRealizadas) {
                saida.out.println(
                        "Consulta realizada na data: " + c.getData() + " com o médico(a) " + c.getMedico().getNome());
            }
        } else {
            System.out
                    .println(("o paciente não realizou consultas com o médico " + medico.getNome() + " antes de hoje.")
                            .toLowerCase());
        }

        if (saida != null && saida.deveFechar) {
            saida.out.close();
        }
    }
}
