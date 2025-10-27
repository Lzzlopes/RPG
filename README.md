# Old Dragon Character Creator - Black Bulls Theme 🐂⚔️

Um aplicativo Android moderno para criação de personagens de RPG Old Dragon, com tema visual inspirado no esquadrão Touros Negros (Black Bulls) do anime Black Clover.

## 🎨 Características Visuais

### Paleta de Cores Black Bulls
- **Preto (#0A0A0A)**: Background principal
- **Vermelho (#DC143C)**: Ações principais e destaques
- **Dourado (#FFD700)**: Títulos e elementos importantes
- **Cinza (#2D2D2D)**: Cards e superfícies
- **Prata (#C0C0C0)**: Textos secundários

### Design Moderno
- Animações suaves e transições elegantes
- Gradientes e efeitos visuais imersivos
- Cards com elevação e bordas arredondadas
- Ícones e emojis temáticos
- Interface responsiva e intuitiva

## 📱 Funcionalidades

### 1. **Tela Inicial (Home)**
- Design impactante com animações pulsantes
- Navegação clara para criar ou ver personagens
- Citação motivacional: "Surpass your limits!"

### 2. **Criar Personagem (4 Etapas)**

#### Etapa 1: Nome
- Campo de texto estilizado
- Validação em tempo real
- Sugestões de nomes épicos

#### Etapa 2: Atributos
- Três estratégias de geração:
  - **Clássica**: 3d6 para cada atributo
  - **Heróica**: 4d6, descarta menor
  - **Aventureiro**: 3d6, distribuição livre
- Visualização clara dos valores rolados
- Sistema de atribuição intuitivo

#### Etapa 3: Raça
- Cards visuais para cada raça:
  - 👤 Humano
  - 🧝 Elfo
  - 🪓 Anão
  - 🌿 Halfling
- Informações de movimento e alinhamento
- Seleção com feedback visual

#### Etapa 4: Classe
- Cards para cada classe:
  - ⚔️ Guerreiro
  - ✝️ Clérigo
  - 🗡️ Ladrão
- Informações de armas permitidas
- Indicador visual de seleção

### 3. **Lista de Personagens**
- Lista scrollável de todos os personagens
- Cards informativos com:
  - Nome e badges de classe/raça
  - Estatísticas principais (HP, CA, BA, MV)
  - Opção de deletar
- Estado vazio com mensagem motivacional
- FAB (Floating Action Button) para criar novo

### 4. **Navegação**
- Sistema de navegação por steps na criação
- Barra de progresso visual
- Botões "Anterior" e "Próximo"
- Confirmação ao criar personagem
- Redirecionamento automático para lista

## 🔧 Tecnologias Utilizadas

- **Kotlin**: Linguagem principal
- **Jetpack Compose**: UI moderna e declarativa
- **Material Design 3**: Componentes e tema
- **Room Database**: Persistência de dados local
- **Navigation Compose**: Navegação entre telas
- **ViewModel & StateFlow**: Gerenciamento de estado
- **Coroutines**: Operações assíncronas

## 📦 Estrutura do Projeto

```
app/
├── src/main/java/com/olddragon/charactercreator/
│   ├── controller/
│   │   └── CharacterCreationViewModel.kt
│   ├── data/
│   │   ├── CharacterDao.kt
│   │   ├── CharacterDatabase.kt
│   │   ├── CharacterEntity.kt
│   │   ├── AttributeEntity.kt
│   │   ├── RaceEntity.kt
│   │   └── CharacterClassEntity.kt
│   ├── model/
│   │   ├── Character.kt
│   │   ├── Race.kt
│   │   ├── CharacterClass.kt
│   │   ├── Ability.kt
│   │   ├── attributes/
│   │   ├── classes/
│   │   └── races/
│   ├── navigation/
│   │   └── Screen.kt
│   ├── ui/theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── view/
│       ├── MainActivity.kt
│       ├── HomeScreen.kt
│       ├── CharacterListScreen.kt
│       └── CreateCharacterScreen.kt
```

## 🚀 Como Executar

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd MyApplication
```

2. **Abra no Android Studio**
- File → Open → Selecione a pasta do projeto

3. **Sincronize as dependências**
- O Gradle irá baixar automaticamente todas as dependências

4. **Execute o app**
- Conecte um dispositivo Android ou inicie um emulador
- Clique em "Run" (▶️) ou pressione Shift+F10

## 📝 Requisitos

- **Android Studio**: Arctic Fox ou superior
- **Kotlin**: 2.0.0
- **Android SDK**: Mínimo API 24 (Android 7.0)
- **Target SDK**: API 36

## 🎮 Como Usar o App

1. **Tela Inicial**: Escolha entre criar um novo personagem ou ver seus personagens existentes

2. **Criar Personagem**:
   - Digite um nome épico
   - Escolha uma estratégia e role os atributos
   - Selecione uma raça
   - Escolha uma classe
   - Confirme a criação

3. **Lista de Personagens**:
   - Veja todos os seus personagens
   - Toque para ver detalhes (em desenvolvimento)
   - Use o ícone de lixeira para deletar
   - Toque no botão + para criar novo

## 🎨 Personalização

### Alterar Cores
Edite o arquivo `Color.kt` para modificar a paleta:
```kotlin
val BlackBullsRed = Color(0xFFDC143C) // Cor primária
val BlackBullsGold = Color(0xFFFFD700) // Cor de destaque
```

### Adicionar Novas Raças/Classes
1. Crie uma nova classe em `model/races/` ou `model/classes/`
2. Implemente a interface `Race` ou `CharacterClass`
3. Adicione à lista no ViewModel

## 🐛 Resolução de Problemas

### Erro de compilação com KSP
Se encontrar erro `IncompatibleClassChangeError`:
1. Limpe o projeto: Build → Clean Project
2. Reconstrua: Build → Rebuild Project
3. Invalide caches: File → Invalidate Caches / Restart

### Room Database não funciona
- Verifique se as versões do Room estão alinhadas
- Sincronize o Gradle novamente
- Limpe e reconstrua o projeto

## 🔮 Funcionalidades Futuras

- [ ] Tela de detalhes do personagem
- [ ] Edição de personagens existentes
- [ ] Exportar/Importar personagens
- [ ] Calculadora de combate
- [ ] Sistema de níveis e progressão
- [ ] Inventário de itens
- [ ] Histórico de aventuras
- [ ] Compartilhamento de personagens

## 📄 Licença

Este projeto é de código aberto e está disponível sob a licença MIT.

## 🙏 Créditos

- **Old Dragon RPG**: Sistema de RPG brasileiro
- **Black Clover**: Inspiração visual do anime/mangá
- **Material Design**: Guidelines de design do Google

## 📧 Contato

Para dúvidas, sugestões ou contribuições, abra uma issue no repositório.

---

**"Supere seus limites! Surpass your limits!" 🐂⚔️**
