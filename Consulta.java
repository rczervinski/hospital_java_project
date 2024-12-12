import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate data;
    private LocalTime horario;
    private Medico medico;
    private Paciente paciente;
    public Object date;
    private static final String CONSULTAS_FILE = "ser/consultas.ser";

    public Consulta(LocalDate data, LocalTime horario, Medico medico, Paciente paciente) {
        this.data = data;
        this.horario = horario;
        this.medico = medico;
        this.paciente = paciente;

        paciente.addConsulta(this);
        if (!medico.verificaPaciente(paciente)) {
            medico.addPaciente(this.paciente);
        }
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public Medico getMedico() {
        return medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public static void salvarConsultas(ArrayList<Consulta> Consultas, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(Consultas);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Consulta> carregarConsultas(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (ArrayList<Consulta>) ois.readObject();
        }
    }

    public static void adicionarNovaConsulta(ArrayList<Consulta> consultas, ArrayList<Medico> medicos, ArrayList<Paciente> pacientes, JFrame frame, JTextArea resultArea) {
        String cpfPacienteConsulta = JOptionPane.showInputDialog(frame, "Digite o CPF do paciente:");
        if (cpfPacienteConsulta == null || cpfPacienteConsulta.trim().isEmpty()) {
            resultArea.append("CPF não fornecido.\n");
            return;
        }

        Paciente paciente = Paciente.getPacienteByCpf(cpfPacienteConsulta, pacientes);

        String codigoMedicoStr = JOptionPane.showInputDialog(frame, "Digite o código do médico:");
        if (codigoMedicoStr == null || codigoMedicoStr.trim().isEmpty()) {
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

        Medico medico = Medico.getMedicoByCode(codigoMedico, medicos);

        String dataConsultaStr = JOptionPane.showInputDialog(frame, "Digite a data da consulta (dd/MM/yyyy):");
        if (dataConsultaStr == null || dataConsultaStr.trim().isEmpty()) {
            resultArea.append("Data não fornecida.\n");
            return;
        }

        LocalDate dataConsulta;
        try {
            dataConsulta = LocalDate.parse(dataConsultaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            resultArea.append("Data inválida. Use o formato dd/MM/yyyy.\n");
            return;
        }

        String horarioConsultaStr = JOptionPane.showInputDialog(frame, "Digite o horário da consulta (HH:MM):");
        if (horarioConsultaStr == null || horarioConsultaStr.trim().isEmpty()) {
            resultArea.append("Horário não fornecido.\n");
            return;
        }

        LocalTime horarioConsulta;
        try {
            horarioConsulta = LocalTime.parse(horarioConsultaStr);
        } catch (Exception e) {
            resultArea.append("Horário inválido. Use o formato HH:MM.\n");
            return;
        }

        Consulta novaConsulta = new Consulta(dataConsulta, horarioConsulta, medico, paciente );
        consultas.add(novaConsulta);
        try {
            salvarConsultas(consultas, CONSULTAS_FILE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        resultArea.append("Nova consulta adicionada com sucesso. Consulta do paciente: " + paciente.getNome() + " com o médico: " +  medico.getNome() +" na data " + dataConsulta + " e no horário " + horarioConsulta );
    }

    public static void Printar(ArrayList<Consulta> consultas) {
        for (Consulta c : consultas) {
            System.out.println(c.data);
            System.out.println(c.horario);
            System.out.println(c.medico);
            System.out.println(c.paciente.getNome());
        }
    }

}