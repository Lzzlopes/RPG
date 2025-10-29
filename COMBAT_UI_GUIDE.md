# 🎮 Interface Visual de Combate - Guia Completo

## ✨ O que foi criado

### 1. **CombatScreen.kt** - Tela Principal de Combate
Interface completa e imersiva para combates em tempo real.

#### Características:
- ✅ Status visual dos combatentes (jogador vs inimigo)
- ✅ Barras de HP animadas e coloridas
- ✅ Log de combate em tempo real (auto-scroll)
- ✅ Controles de combate (Auto/Manual/Parar)
- ✅ Dialog de seleção de dificuldade
- ✅ Dialog de resultado final
- ✅ Animações e feedback visual
- ✅ Tema Black Bulls completo

### 2. **CombatHistoryScreen.kt** - Histórico de Combates
Visualização do histórico de combates do personagem.

#### Características:
- ✅ Lista de combates anteriores
- ✅ Vitórias e derrotas destacadas
- ✅ Data e hora de cada combate
- ✅ Informações dos oponentes
- ✅ Número de rodadas

### 3. **Integração Completa**
- ✅ Botão "Iniciar Combate" nos cards de personagens
- ✅ Navegação entre todas as telas
- ✅ ViewModels compartilhados
- ✅ Bind/unbind automático do serviço

## 🎨 Design Visual

### Tela de Combate

```
╔══════════════════════════════════════╗
║  ← Arena de Combate           🕐     ║
╠══════════════════════════════════════╣
║                                      ║
║  ┌────────────────────────────────┐  ║
║  │  Rodada 3                     │  ║
║  │                                │  ║
║  │  🛡️ Asta          HP: 7/10    │  ║
║  │  ████████░░                    │  ║
║  │  CA 16  BA +4  Espada Longa   │  ║
║  │                                │  ║
║  │         ⚔️ VS ⚔️              │  ║
║  │                                │  ║
║  │  👹 Orc           HP: 3/10    │  ║
║  │  ███░░░░░░░                    │  ║
║  │  CA 14  BA +3  Machado        │  ║
║  └────────────────────────────────┘  ║
║                                      ║
║  ┌────────────────────────────────┐  ║
║  │  Log de Combate          ⚙️   │  ║
║  ├────────────────────────────────┤  ║
║  │  ⚔️ RODADA 3                  │  ║
║  │  Asta ataca Orc                │  ║
║  │  🎲 Rolou 18 + 4 = 22 vs 14   │  ║
║  │  ✓ ACERTOU! Dano: 7 (5+2)    │  ║
║  │  ❤️ Orc: 3/10 HP              │  ║
║  │                                 │  ║
║  │  Orc ataca Asta                │  ║
║  │  🎲 Rolou 8 + 3 = 11 vs 16    │  ║
║  │  ✗ ERROU!                     │  ║
║  └────────────────────────────────┘  ║
║                                      ║
║  ┌─────────┬─────────┬─────┐        ║
║  │  Auto   │ Próxima │  ⏹️  │       ║
║  └─────────┴─────────┴─────┘        ║
╚══════════════════════════════════════╝
```

### Fluxo de Uso

```
1. Lista de Personagens
   ↓ (clica "Iniciar Combate")
   
2. Dialog: Escolha Dificuldade
   🟢 Fácil
   🟡 Médio  ← [Selecionado]
   🔴 Difícil
   ↓ (seleciona)
   
3. Tela de Combate
   - Mostra personagem vs inimigo gerado
   - Status visual com barras de HP
   - Opção: Auto ou Manual
   ↓ (clica "Auto")
   
4. Combate em Execução
   - Log atualiza em tempo real
   - Delay de 1.5s entre rodadas
   - Animações nas barras de HP
   - Auto-scroll do log
   ↓ (combate termina)
   
5. Dialog: Resultado
   🏆 VITÓRIA!
   Rodadas: 5
   HP Final: 7/10
   [Novo Combate] [Histórico] [Fechar]
   ↓
   
6. Histórico (opcional)
   - Lista de combates anteriores
   - Vitórias/derrotas
   - Estatísticas
```

## 🎯 Componentes Principais

### 1. CombatScreen

```kotlin
@Composable
fun CombatScreen(
    navController: NavController,
    characterId: Long,
    characterViewModel: CharacterCreationViewModel,
    combatViewModel: CombatViewModel
)
```

**Seções:**
- `NoCombatSection` - Quando não há combate ativo
- `CombatantsStatusSection` - Status dos lutadores
- `CombatLogMessage` - Mensagens do log
- `CombatControls` - Botões de controle
- `DifficultySelectionDialog` - Escolher dificuldade
- `CombatResultDialog` - Resultado final

### 2. CombatantStatusCard

```kotlin
@Composable
private fun CombatantStatusCard(
    combatant: Combatant,
    isPlayer: Boolean
)
```

**Features:**
- HP bar animada e colorida
- Status "AGONIZANDO" pulsante
- Ícones diferentes (🛡️ player, 👹 enemy)
- Badges de stats (CA, BA, Arma)

### 3. CombatLogMessage

```kotlin
@Composable
private fun CombatLogMessage(message: String)
```

**Cores dinâmicas:**
- 💥 CRÍTICO → Vermelho
- ✓ ACERTOU → Verde
- ✗ ERROU → Cinza
- ⚔️ RODADA → Dourado
- 🏆 VITÓRIA → Dourado
- 💀 MORTE → Vermelho

### 4. CombatControls

```kotlin
@Composable
private fun CombatControls(
    isProcessing: Boolean,
    combat: Combat?,
    onAutoCombat: () -> Unit,
    onNextRound: () -> Unit,
    onStopCombat: () -> Unit
)
```

**Botões:**
- ▶️ Auto - Executa automaticamente
- ⏭️ Próxima - Avança uma rodada
- ⏹️ Parar - Cancela combate

## 🎨 Paleta de Cores (Black Bulls)

```kotlin
// Status HP
HP > 60% → Verde (#4CAF50)
HP 30-60% → Laranja (#FF6B35)
HP < 30% → Vermelho (#CF6679)

// Elementos UI
Fundo → Preto (#0A0A0A)
Cards → Cinza Escuro (#1E1E1E)
Primário → Vermelho (#DC143C)
Destaque → Dourado (#FFD700)
Texto → Branco (#F5F5F5)
Secundário → Prata (#C0C0C0)
```

## ⚡ Animações Implementadas

### 1. HP Bar
- Transição suave ao mudar valor
- Cores dinâmicas baseadas em porcentagem
- Gradiente horizontal

### 2. Status Agonizando
- Pulsação contínua (scale 1.0 → 1.1)
- Duração: 500ms
- Modo: Reverse (vai e volta)

### 3. Auto-scroll do Log
- Scrolla automaticamente para a última mensagem
- Animação suave
- Acionada a cada nova mensagem

### 4. Loading States
- CircularProgressIndicator durante processamento
- Botões desabilitados enquanto processa
- Feedback visual claro

## 📱 Estados da Tela

### Estado 1: Sem Combate
```
- Card do personagem
- Estatísticas (HP, CA, BA)
- Botão "Iniciar Combate"
```

### Estado 2: Combate Ativo
```
- Status dos combatentes (2 cards)
- Log de combate (scroll)
- Controles (3 botões)
```

### Estado 3: Processando
```
- Todos controles desabilitados
- Indicador de loading visível
- Log atualizando em tempo real
```

### Estado 4: Combate Finalizado
```
- Dialog de resultado aparece
- Opções: Novo / Histórico / Fechar
- Stats finais exibidas
```

## 🔧 Integração com Backend

### Observação de Estados

```kotlin
// Combat state
val combat by combatViewModel.activeCombat.collectAsStateWithLifecycle()

// Log messages
val combatLog by combatViewModel.combatLog.collectAsStateWithLifecycle()

// Processing status
val isProcessing by combatViewModel.isProcessing.collectAsStateWithLifecycle()
```

### Ações Disponíveis

```kotlin
// Iniciar combate
combatViewModel.startNewCombat(character, CombatDifficulty.MEDIUM)

// Executar automaticamente
combatViewModel.runAutoCombat(delayBetweenRounds = 1500L)

// Avançar manualmente
combatViewModel.processNextRound()

// Limpar combate
combatViewModel.clearCombat()

// Carregar histórico
combatViewModel.loadRecentCombats()
```

## 🎮 Controles do Usuário

### Modo Automático
1. Clica "Iniciar Combate"
2. Seleciona dificuldade
3. Clica "Auto"
4. Assiste o combate executar
5. Vê o resultado

### Modo Manual
1. Clica "Iniciar Combate"
2. Seleciona dificuldade
3. Clica "Próxima" para cada rodada
4. Controla o ritmo
5. Vê o resultado

## 📊 Informações Exibidas

### Durante o Combate
- Nome dos combatentes
- HP atual / HP máximo
- Barra visual de HP
- CA (Classe de Armadura)
- BA (Base de Ataque)
- Arma equipada
- Rodada atual
- Log completo de ações

### No Resultado
- Vencedor (com emoji 🏆 ou 💀)
- Total de rodadas
- HP final do jogador
- Opções de próxima ação

### No Histórico
- Data e hora do combate
- Nome do oponente
- Resultado (Vitória/Derrota)
- Número de rodadas
- Emoji de status

## 🚀 Como Usar

### Para Testar
1. Execute o app
2. Crie ou selecione um personagem
3. Clique em "Iniciar Combate" no card
4. Selecione a dificuldade
5. Clique em "Auto" ou "Próxima"
6. Assista o combate!

### Navegação
```
Home → Lista → [Card] → Combate
                  ↓
              Histórico
```

## ✨ Destaques da UI

✅ **Design Imersivo** - Tema Black Bulls completo
✅ **Animações Suaves** - Transições e feedback visual
✅ **Real-time Updates** - StateFlow para reatividade
✅ **Auto-scroll** - Log sempre visível
✅ **Status Visual** - Barras de HP coloridas
✅ **Controles Intuitivos** - Auto e Manual
✅ **Dialogs Informativos** - Dificuldade e resultado
✅ **Responsive** - Adapta a diferentes tamanhos
✅ **Loading States** - Feedback durante processamento
✅ **Histórico Completo** - Reveja combates anteriores

---

## 🎯 Resultado Final

A interface visual está **100% funcional e integrada** com o backend de combate. 

O usuário pode:
- ✅ Iniciar combates com um clique
- ✅ Escolher dificuldade do oponente
- ✅ Executar automaticamente ou manualmente
- ✅ Ver animações em tempo real
- ✅ Acompanhar pelo log detalhado
- ✅ Ver resultado final com stats
- ✅ Consultar histórico de combates

**Sistema de Combate Visual Completo! ⚔️🎮🐂**
