import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Leitor {
    private String arquivoMedicos;
    private String arquivoPacientes;
    private String arquivoConsultas;

    public Leitor(String arquivoMedicos, String arquivoPacientes, String arquivoConsultas) {
        this.arquivoMedicos = arquivoMedicos;
        this.arquivoPacientes = arquivoPacientes;
        this.arquivoConsultas = arquivoConsultas;
    }

    public void carregarConsultas(ArrayList<Consulta> consultas, ArrayList<Medico> medicos,
            ArrayList<Paciente> pacientes) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.arquivoConsultas))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                LocalDate data = LocalDate.parse(dados[0]);
                LocalTime horario = LocalTime.parse(dados[1]);
                int codigoMedico = Integer.parseInt(dados[2]);
                String cpfPaciente = dados[3];

                Paciente paciente = Paciente.getPacienteByCpf(cpfPaciente, pacientes);
                Medico medico = Medico.getMedicoByCode(codigoMedico, medicos);
                Consulta consulta = new Consulta(data, horario, medico, paciente);

                consultas.add(consulta);
            }

        } catch (IOException | NumberFormatException e) {
            throw e;
        }

    }

    public void carregarPacientes(ArrayList<Paciente> pacientes) {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.arquivoPacientes))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                String cpf = dados[1];
                String nome = dados[0];
                Paciente paciente = new Paciente(nome, cpf);
                pacientes.add(paciente);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public void carregarMedicos(ArrayList<Medico> medicos) {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.arquivoMedicos))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                String nome = dados[0];
                int codigo = Integer.parseInt(dados[1]);
                new Medico(nome, codigo);
                Medico medico = new Medico(nome, codigo);
                medicos.add(medico);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void ler(ArrayList<Medico> medicos, ArrayList<Paciente> pacientes, ArrayList<Consulta> consultas)
            throws IOException {
        this.carregarPacientes(pacientes);
        this.carregarMedicos(medicos);
        carregarConsultas(consultas, medicos, pacientes);
    }
}