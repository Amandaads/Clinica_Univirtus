# Clínica Univirtus 🏥

A **Clínica Univirtus** é um aplicativo Android desenvolvido para facilitar o agendamento de consultas médicas. O sistema permite que os pacientes escolham especialidades, selecionem médicos disponíveis, escolham datas e horários, e gerenciem seus agendamentos de forma simples e intuitiva.

---

## 🚀 Funcionalidades

- **Autenticação de Usuários:** Login e cadastro de pacientes via Firebase Authentication.
- **Agendamento Inteligente:**
  - Escolha de especialidade (Cardiologia, Clínico Geral, Ortopedia, Nutrição, etc.).
  - Filtro automático de médicos que possuem agenda disponível.
  - Seleção dinâmica de datas e horários baseada na disponibilidade em tempo real.
- **Validação de Agendamentos:** O sistema impede que um paciente marque mais de uma consulta ativa para a mesma especialidade.
- **Gestão de Disponibilidade:** Assim que um horário é agendado, ele é removido da lista de disponíveis automaticamente.
- **Interface Moderna:** Utiliza Material Design, View Binding e navegação simplificada com BottomNavigationView.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **Android SDK:** Modernas práticas com Fragments e ViewBinding.
- **Banco de Dados:** [Firebase Realtime Database](https://firebase.google.com/docs/database) (NoSQL).
- **Autenticação:** [Firebase Auth](https://firebase.google.com/docs/auth).
- **UI/Layout:** XML, Material Design Components, RecyclerView para listas dinâmicas.

---

## 📂 Estrutura do Projeto

- `app/src/main/java/.../fragments/`: Contém a lógica das telas (Agendamento, Meus Agendamentos, etc.).
- `app/src/main/java/.../models/`: Classes de dados (Data Transfer Objects) para Médicos, Pacientes e Agendamentos.
- `app/src/main/java/.../adapters/`: Adaptadores para RecyclerView (listagem de horários, etc.).
- `app/src/main/res/layout/`: Arquivos de interface (XML).

---

## 🗄️ Estrutura do Banco de Dados (Firebase)

O projeto utiliza uma estrutura NoSQL organizada da seguinte forma:

- **`medicos/`**: Lista de médicos separados por especialidade e seu status de agenda.
- **`agendas/`**: Mapeamento de `ID_MEDICO -> DATA -> HORARIOS` (Boolean indicando disponibilidade).
- **`pacientes/`**: Dados dos usuários e seu histórico de `agendamentos/`.

---

## 🔧 Como executar o projeto

1. Clone o repositório.
2. Abra o projeto no **Android Studio**.
3. Certifique-se de configurar o arquivo `google-services.json` (necessário para conexão com o Firebase).
4. Sincronize o Gradle.
5. Execute em um emulador ou dispositivo físico com Android 7.0 (API 24) ou superior.

---

## 📄 Licença

Este projeto é para fins acadêmicos/demonstrativos.
